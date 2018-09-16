/**
 *
 */
package com.yanry.driver.core.model.event;

import com.yanry.driver.core.model.base.Property;
import com.yanry.driver.core.model.runtime.Presentable;

import java.util.function.Function;

/**
 * @author yanry
 * <p>
 * Jan 6, 2017
 */
@Presentable
public class ActionEvent<T extends ActionEvent<T, TAR, RET>, TAR, RET> extends Event<T> {
    private TAR target;
    private RET preActionResult;
    private Function<TAR, RET> preAction;

    public ActionEvent(Function<T, Object>... concernedFields) {
        super(concernedFields);
    }

    public void setTarget(TAR target) {
        this.target = target;
    }

    public void setPreAction(Function<TAR, RET> preAction) {
        this.preAction = preAction;
    }

    public void processPreAction() {
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
    public boolean matches(Property property, Object fromValue, Object toValue) {
        return false;
    }
}
