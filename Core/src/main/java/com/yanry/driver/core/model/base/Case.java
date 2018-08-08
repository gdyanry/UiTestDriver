package com.yanry.driver.core.model.base;

import com.yanry.driver.core.model.runtime.Presentable;
import com.yanry.driver.core.model.state.StateEquals;
import com.yanry.driver.core.model.state.StatePredicate;

import java.util.HashMap;

@Presentable
public abstract class Case {
    private HashMap<Property, StatePredicate> initState;
    long timeFrame;
    int unsatisfiedDegree;

    public Case() {
        initState = new HashMap<>();
    }

    public <V> Case addInitState(Property<V> property, V value) {
        initState.put(property, new StateEquals(value));
        return this;
    }

    public <V> Case addInitStatePredicate(Property<V> property, StatePredicate<V> predicate) {
        initState.put(property, predicate);
        return this;
    }

    int getUnsatisfiedDegree(long timeFrame) {
        if (timeFrame == 0 || timeFrame != this.timeFrame) {
            unsatisfiedDegree = initState.keySet().stream()
                    .filter(property -> !initState.get(property).test(property.getCurrentValue()))
                    .mapToInt(prop -> 1).sum();
            this.timeFrame = timeFrame;
        }
        return unsatisfiedDegree;
    }

    @Presentable
    public HashMap<Property, StatePredicate> getInitState() {
        return initState;
    }

    protected abstract void execute(Graph graph);
}
