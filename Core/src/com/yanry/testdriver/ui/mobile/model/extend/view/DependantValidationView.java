package com.yanry.testdriver.ui.mobile.model.extend.view;

import com.yanry.testdriver.ui.mobile.model.base.*;
import com.yanry.testdriver.ui.mobile.model.base.view.View;
import com.yanry.testdriver.ui.mobile.model.base.view.ViewContainer;
import com.yanry.testdriver.ui.mobile.model.extend.property.NoCacheProperty;

/**
 * Created by rongyu.yan on 2/28/2017.
 */
public abstract class DependantValidationView extends View {
    private DependantValidationView dependantValidationView;
    private ObjectProperty<Boolean> clientValidity;

    public DependantValidationView(ViewContainer parent, String name, DependantValidationView dependantValidationView) {
        super(parent, name);
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

    public ObjectProperty<Boolean> getClientValidity() {
        return clientValidity;
    }

    protected abstract boolean checkClientValidity();

    protected abstract boolean transitToValidity(Graph graph, boolean validity, boolean isTransitEvent);

    @Presentable
    public class ClientValidity extends NoCacheProperty<Boolean> {
        @Override
        public Boolean checkValue(Timing timing) {
            return checkClientValidity();
        }

        @Override
        public boolean transitTo(Graph graph, Boolean toValue, boolean isTransitEvent) {
            return transitToValidity(graph, toValue, isTransitEvent);
        }

        @Presentable
        public DependantValidationView getView() {
            return DependantValidationView.this;
        }
    }
}
