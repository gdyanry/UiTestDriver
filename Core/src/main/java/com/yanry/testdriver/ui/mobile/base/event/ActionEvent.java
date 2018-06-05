/**
 *
 */
package com.yanry.testdriver.ui.mobile.base.event;

import com.yanry.testdriver.ui.mobile.base.Presentable;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author yanry
 *         <p>
 *         Jan 6, 2017
 */
@Presentable
public class ActionEvent<T, R> implements Event {
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

    public ActionEvent() {}

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
}
