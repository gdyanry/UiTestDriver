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
    private Graph graph;
    /**
     * 缓存向客户端查询属性值时的graph的actionTimeFrame，用于防止非必要的重复查询。
     */
    long communicateTimeFrame;
    private HashSet<V> collectedValues;
    private State[] dependentStates;
    private LinkedList<Consumer<V>> onCheckValueListeners;
    private LinkedList<Runnable> onCleanListeners;

    public Property(Graph graph) {
        this.graph = graph;
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

    public void addOnCheckValueListener(Consumer<V> listener) {
        if (onCheckValueListeners == null) {
            onCheckValueListeners = new LinkedList<>();
        }
        onCheckValueListeners.add(listener);
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
    public Graph getGraph() {
        return graph;
    }

    public final ExternalEvent switchToValue(V toState) {
        return switchTo(Equals.of(toState));
    }

    public final ExternalEvent switchTo(ValuePredicate<V> toState) {
        if (toState.test(getCurrentValue())) {
            return null;
        }
        graph.enterMethod(String.format("%s > %s", this, toState));
        graph.addStateTrace(getState(toState));
        V cacheValue = (V) graph.propertyCache.get(this);
        if (cacheValue == null) {
            if (!graph.nullCache.contains(this)) {
                // 属性值未知,认为dependentStates未满足
                if (dependentStates != null) {
                    for (State dependentState : dependentStates) {
                        if (!dependentState.isSatisfied()) {
                            ExternalEvent externalEvent = dependentState.switchTo();
                            graph.exitMethod(LogLevel.Verbose, externalEvent);
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
                .filter(a -> a != null && graph.isValidAction(a))
                .findAny();
        if (any.isPresent()) {
            ExternalEvent externalEvent = any.get();
            graph.exitMethod(LogLevel.Verbose, externalEvent);
            return externalEvent;
        }
        ExternalEvent externalEvent = graph.findPathToRoll(e -> {
            if (e instanceof PropertyExpectation) {
                PropertyExpectation<V> exp = (PropertyExpectation) e;
                return equals(exp.getProperty()) && toState.test(exp.getExpectedValue());
            }
            return false;
        });
        graph.exitMethod(LogLevel.Verbose, externalEvent);
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
            if (!graph.isValueFresh(this)) {
                // 清空缓存，使得接下来调用getCurrentValue时触发向客户端查询并更新该属性最新的状态值
                clean();
            }
        } else {
            // 不查询客户端，直接通过验证并更新状态值
            updateCache(expectedValue);
        }
        V newValue = getCurrentValue();
        if (!Objects.equals(oldValue, newValue)) {
            graph.verifySuperPaths(this, oldValue, newValue);
        }
    }

    private void updateCache(V value) {
        if (value == null) {
            graph.nullCache.add(this);
            graph.propertyCache.remove(this);
        } else {
            graph.nullCache.remove(this);
            graph.propertyCache.put(this, value);
        }
    }

    public void clean() {
        graph.nullCache.remove(this);
        graph.propertyCache.remove(this);
        if (onCleanListeners != null) {
            for (Runnable listener : onCleanListeners) {
                listener.run();
            }
        }
    }

    public final V getCurrentValue() {
        if (graph.nullCache.contains(this)) {
            return null;
        }
        V cacheValue = (V) getGraph().propertyCache.get(this);
        if (cacheValue == null) {
            if (dependentStates != null) {
                for (State dependentState : dependentStates) {
                    if (!dependentState.isSatisfied()) {
                        return null;
                    }
                }
            }
            cacheValue = checkValue();
            updateCache(cacheValue);
            if (onCheckValueListeners != null) {
                for (Consumer<V> listener : onCheckValueListeners) {
                    listener.accept(cacheValue);
                }
            }
        }
        return cacheValue;
    }

    public Object[] getValues() {
        return getValueStream(collectedValues).toArray();
    }

    protected abstract V checkValue();

    protected abstract ExternalEvent doSelfSwitch(V to);

    protected abstract Stream<V> getValueStream(Set<V> collectedValues);
}
