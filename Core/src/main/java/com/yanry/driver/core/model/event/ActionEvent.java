/**
 *
 */
package com.yanry.driver.core.model.event;

import com.yanry.driver.core.model.base.Property;
import com.yanry.driver.core.model.runtime.Presentable;
import lib.common.entity.HashAndEquals;

import java.util.ArrayList;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author yanry
 * <p>
 * Jan 6, 2017
 */
@Presentable
public class ActionEvent<T extends ActionEvent<T, TAR, RET>, TAR, RET> extends HashAndEquals<T> implements Event {
    private TAR target;
    private RET preActionResult;
    private Supplier<TAR> targetSupplier;
    private Function<TAR, RET> preAction;

    public ActionEvent(Supplier<TAR> targetSupplier) {
        this.targetSupplier = targetSupplier;
    }

    public ActionEvent(TAR target) {
        this.target = target;
    }

    public void setPreAction(Function<TAR, RET> preAction) {
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
    public TAR getTarget() {
        return target;
    }

    public RET getPreActionResult() {
        return preActionResult;
    }

    @Override
    protected void addHashFields(ArrayList<Object> hashFields) {
        hashFields.add(target);
    }

    @Override
    protected boolean equalsWithSameClass(T t) {
        return getTarget() != null ? getTarget().equals(t.getTarget()) : t.getTarget() == null;
    }

    @Override
    public boolean matches(Property property, Object fromValue, Object toValue) {
        return false;
    }
}
