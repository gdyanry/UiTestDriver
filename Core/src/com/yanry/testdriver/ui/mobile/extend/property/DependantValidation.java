package com.yanry.testdriver.ui.mobile.extend.property;

import com.yanry.testdriver.ui.mobile.base.Graph;
import com.yanry.testdriver.ui.mobile.base.Path;
import com.yanry.testdriver.ui.mobile.base.property.SwitchableProperty;

/**
 * Created by rongyu.yan on 5/11/2017.
 */
public abstract class DependantValidation extends SwitchableProperty<Boolean> {
    private DependantValidation dependant;

    public DependantValidation(DependantValidation dependant) {
        this.dependant = dependant;
    }

    public void addValidationPassToPath(Path path, boolean includeSelf) {
        DependantValidation d = includeSelf ? this : dependant;
        while (d != null) {
            path.addInitState(d, true);
            d = d.dependant;
        }
    }
}
