/**
 *
 */
package com.yanry.driver.core.model.base;

import com.yanry.driver.core.model.expectation.SDPropertyExpectation;
import com.yanry.driver.core.model.expectation.Timing;
import com.yanry.driver.core.model.predicate.Equals;
import lib.common.model.log.LogLevel;
import lib.common.util.object.EqualsPart;
import lib.common.util.object.HandyObject;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * @author yanry
 * <p>
 * Jan 5, 2017
 */
public abstract class Property<V> extends HandyObject {
    private StateSpace stateSpace;
    /**
     * 缓存向客户端查询属性值时的graph的actionTimeFrame，用于防止非必要的重复查询。
     */
    long graphFrameMark;
    private HashSet<V> collectedValues;
    private State[] dependentStates;
    private LinkedList<Consumer<V>> onValueUpdateListeners;
    private LinkedList<Runnable> onCleanListeners;

    public Property(StateSpace stateSpace) {
        this.stateSpace = stateSpace;
        collectedValues = new HashSet<>();
    }

    public void setDependentStates(State... dependentStates) {
        this.dependentStates = dependentStates;
    }

    public void addValue(V... value) {
        for (V v : value) {
            if (v != null) {
                collectedValues.add(v);
            }
        }
    }

    public void addOnValueUpdateListener(Consumer<V> listener) {
        if (onValueUpdateListeners == null) {
            onValueUpdateListeners = new LinkedList<>();
        }
        onValueUpdateListeners.add(listener);
    }

    public void addOnCleanListener(Runnable listener) {
        if (onCleanListeners == null) {
            onCleanListeners = new LinkedList<>();
        }
        onCleanListeners.add(listener);
    }

    void findValueToAdd(ValuePredicate<V> predicate) {
        if (predicate != null) {
            Stream<V> concreteValues = predicate.getConcreteValues();
            if (concreteValues != null) {
                concreteValues.forEach(v -> addValue(v));
            }
        }
    }

    @EqualsPart
    public StateSpace getStateSpace() {
        return stateSpace;
    }

    public final ExternalEvent switchToValue(V toState) {
        return switchTo(Equals.of(toState));
    }

    public final ExternalEvent switchTo(ValuePredicate<V> toState) {
        findValueToAdd(toState);
        if (toState.test(getCurrentValue())) {
            return null;
        }
        stateSpace.enterMethod(String.format("%s > %s", this, toState));
        stateSpace.addStateTrace(getState(toState));
        V cacheValue = (V) stateSpace.propertyCache.get(this);
        if (cacheValue == null) {
            if (!stateSpace.nullCache.contains(this)) {
                // 属性值未知,认为dependentStates未满足
                if (dependentStates != null) {
                    for (State dependentState : dependentStates) {
                        if (!dependentState.isSatisfied()) {
                            ExternalEvent externalEvent = dependentState.trySatisfy();
                            stateSpace.exitMethod(LogLevel.Verbose, externalEvent);
                            return externalEvent;
                        }
                    }
                }
            }
        }
        Stream<V> stream = getValueStream(collectedValues);
        if (stream == null) {
            stream = collectedValues.stream();
        }
        Optional<ExternalEvent> any = stream.filter(v -> toState.test(v))
                .map(v -> doSelfSwitch(v))
                .filter(a -> a != null && stateSpace.isValidAction(a))
                .findAny();
        if (any.isPresent()) {
            ExternalEvent externalEvent = any.get();
            stateSpace.exitMethod(LogLevel.Verbose, externalEvent);
            return externalEvent;
        }
        ExternalEvent externalEvent = stateSpace.findPathToRoll(e -> {
            if (e instanceof PropertyExpectation) {
                PropertyExpectation<V> exp = (PropertyExpectation) e;
                return equals(exp.getProperty()) && toState.test(exp.getExpectedValue());
            }
            return false;
        });
        stateSpace.exitMethod(LogLevel.Verbose, externalEvent);
        return externalEvent;
    }

    public State<V> getState(ValuePredicate<V> predicate) {
        return new State<>(this, predicate);
    }

    public SSPropertyExpectation<V> getStaticExpectation(Timing timing, boolean needCheck, V value) {
        return new SSPropertyExpectation<>(timing, needCheck, this, value);
    }

    public SDPropertyExpectation<V> getDynamicExpectation(Timing timing, boolean needCheck, Supplier<V> valueSupplier) {
        return new SDPropertyExpectation<>(timing, needCheck, this, valueSupplier);
    }

    public void setInitValue(V initValue) {
        updateCache(initValue);
    }

    public final void handleExpectation(V expectedValue, boolean needCheck) {
        V oldValue = getCurrentValue();
        if (needCheck) {
            if (!isValueFresh()) {
                // 清空缓存，使得接下来调用getCurrentValue时触发向客户端查询并更新该属性最新的状态值
                cleanCache();
            }
        } else {
            // 不查询客户端，直接通过验证并更新状态值
            updateCache(expectedValue);
        }
        V newValue = getCurrentValue();
        handleChange(oldValue, newValue);
    }

    private void handleChange(V oldValue, V newValue) {
        if (!Objects.equals(oldValue, newValue)) {
            stateSpace.verifySuperPaths(this, oldValue, newValue);
        }
    }

    boolean isValueFresh() {
        return stateSpace.frameMark > 0 && graphFrameMark == stateSpace.frameMark;
    }

    private void updateCache(V value) {
        if (value == null) {
            if (stateSpace.nullCache.contains(this)) {
                return;
            }
            stateSpace.nullCache.add(this);
            stateSpace.propertyCache.remove(this);
        } else {
            if (Objects.equals(stateSpace.propertyCache.get(this), value)) {
                return;
            }
            stateSpace.nullCache.remove(this);
            stateSpace.propertyCache.put(this, value);
        }
        if (onValueUpdateListeners != null) {
            for (Consumer<V> listener : onValueUpdateListeners) {
                listener.accept(value);
            }
        }
    }

    public void refresh() {
        boolean hasCache = stateSpace.nullCache.contains(this) || stateSpace.propertyCache.containsKey(this);
        if (hasCache) {
            V oldValue = getCurrentValue();
            V newValue = doCheckValue();
            handleChange(oldValue, newValue);
        } else {
            doCheckValue();
        }
    }

    public void cleanCache() {
        stateSpace.nullCache.remove(this);
        stateSpace.propertyCache.remove(this);
        if (onCleanListeners != null) {
            for (Runnable listener : onCleanListeners) {
                listener.run();
            }
        }
    }

    public final V getCurrentValue() {
        if (stateSpace.nullCache.contains(this)) {
            return null;
        }
        V cacheValue = (V) getStateSpace().propertyCache.get(this);
        if (cacheValue == null) {
            cacheValue = doCheckValue();
        }
        return cacheValue;
    }

    private V doCheckValue() {
        if (dependentStates != null) {
            for (State dependentState : dependentStates) {
                if (!dependentState.isSatisfied()) {
                    return null;
                }
            }
        }
        V value = checkValue();
        updateCache(value);
        if (value != null) {
            collectedValues.add(value);
        }
        return value;
    }

    public Object[] getValues() {
        return getValueStream(collectedValues).toArray();
    }

    protected abstract V checkValue();

    protected abstract ExternalEvent doSelfSwitch(V to);

    protected abstract Stream<V> getValueStream(Set<V> collectedValues);
}
