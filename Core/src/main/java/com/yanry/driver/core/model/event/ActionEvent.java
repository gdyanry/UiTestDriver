/**
 *
 */
package com.yanry.driver.core.model.event;

import com.yanry.driver.core.model.base.Property;
import com.yanry.driver.core.model.runtime.Presentable;
import lib.common.model.EqualsProxy;

import java.util.function.Function;

/**
 * @author yanry
 * <p>
 * Jan 6, 2017
 */
@Presentable
public class ActionEvent<T, R> implements Event {
    private T target;
    private R preActionResult;
    private Function<T, R> preAction;
    private EqualsProxy<ActionEvent<T, R>> equalsProxy;

    public ActionEvent(T target) {
        this.target = target;
        equalsProxy = new EqualsProxy<>(this, e -> e.target);
    }

    public void setPreAction(Function<T, R> preAction) {
        this.preAction = preAction;
    }

    public void processPreAction() {
        if (preAction != null) {
            preActionResult = preAction.apply(target);
        }
    }

    @Presentable
    public T getTarget() {
        return target;
    }

    public R getPreActionResult() {
        return preActionResult;
    }

    @Override
    public boolean matches(Property property, Object fromValue, Object toValue) {
        return false;
    }

    @Override
    public int hashCode() {
        return equalsProxy.getHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return equalsProxy.checkEquals(obj);
    }
}
