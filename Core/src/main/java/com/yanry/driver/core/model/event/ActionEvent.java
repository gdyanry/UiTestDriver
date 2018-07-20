/**
 *
 */
package com.yanry.driver.core.model.event;

import com.yanry.driver.core.model.property.Property;
import com.yanry.driver.core.model.runtime.Presentable;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author yanry
 * <p>
 * Jan 6, 2017
 */
@Presentable
public class ActionEvent<T, R> implements Event<Object> {
    private T target;
    private R preActionResult;
    private Supplier<T> targetSupplier;
    private Function<T, R> preAction;

    public ActionEvent(Supplier<T> targetSupplier) {
        this.targetSupplier = targetSupplier;
    }

    public ActionEvent(T target) {
        this.target = target;
    }

    public void setPreAction(Function<T, R> preAction) {
        this.preAction = preAction;
    }

    public void processPreAction() {
        if (targetSupplier != null) {
            target = targetSupplier.get();
        }
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
    public boolean matches(Property<Object> property, Object fromValue, Object toValue) {
        return false;
    }
}
