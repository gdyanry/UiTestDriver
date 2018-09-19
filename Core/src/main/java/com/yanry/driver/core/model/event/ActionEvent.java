/**
 *
 */
package com.yanry.driver.core.model.event;

import com.yanry.driver.core.model.base.Event;
import com.yanry.driver.core.model.base.Property;
import lib.common.util.object.HashAndEquals;
import lib.common.util.object.Presentable;

import java.util.function.Function;

/**
 * @author yanry
 * <p>
 * Jan 6, 2017
 */
@Presentable
public class ActionEvent<T, R> extends Event {
    private T target;
    private R preActionResult;
    private Function<T, R> preAction;

    public ActionEvent(T target) {
        this.target = target;
    }

    public void setPreAction(Function<T, R> preAction) {
        this.preAction = preAction;
    }

    public void processPreAction() {
        if (preAction != null) {
            preActionResult = preAction.apply(target);
        }
    }

    @HashAndEquals
    @Presentable
    public T getTarget() {
        return target;
    }

    public R getPreActionResult() {
        return preActionResult;
    }

    @Override
    protected boolean matches(Property property, Object fromValue, Object toValue) {
        return false;
    }
}
