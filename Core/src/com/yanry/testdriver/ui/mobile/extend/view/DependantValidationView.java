package com.yanry.testdriver.ui.mobile.extend.view;

import com.yanry.testdriver.ui.mobile.base.*;
import com.yanry.testdriver.ui.mobile.base.StateProperty;
import com.yanry.testdriver.ui.mobile.extend.property.NoCacheProperty;
import com.yanry.testdriver.ui.mobile.extend.view.selector.ViewSelector;

import java.util.List;
import java.util.function.Predicate;

/**
 * Created by rongyu.yan on 2/28/2017.
 */
public abstract class DependantValidationView extends View {
    private DependantValidationView dependantValidationView;
    private StateProperty<Boolean> clientValidity;

    public DependantValidationView(ViewContainer parent, ViewSelector selector, DependantValidationView dependantValidationView) {
        super(parent, selector);
        this.dependantValidationView = dependantValidationView;
        clientValidity = new ClientValidity();
    }

    public void allClientDependantValidationPass(boolean includeSelf, Path path) {
        DependantValidationView dependant = includeSelf ? this : dependantValidationView;
        while (dependant != null) {
            path.put(dependant.clientValidity, true);
            dependant = dependant.dependantValidationView;
        }
        present(path);
    }

    public StateProperty<Boolean> getClientValidity() {
        return clientValidity;
    }

    protected abstract boolean checkClientValidity();

    protected abstract boolean transitToValidity(boolean validity, List<Path> superPathContainer);

    protected abstract Graph getGraph();

    @Presentable
    public class ClientValidity extends NoCacheProperty<Boolean> {
        @Override
        public Boolean checkValue() {
            return checkClientValidity();
        }

        @Override
        protected Graph getGraph() {
            return DependantValidationView.this.getGraph();
        }

        @Override
        public boolean transitTo(Predicate<Boolean> to, List<Path> superPathContainer) {
            return transitToValidity(to.test(true), superPathContainer);
        }

        @Presentable
        public DependantValidationView getView() {
            return DependantValidationView.this;
        }
    }
}
