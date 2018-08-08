package com.yanry.driver.mobile.property;

import com.yanry.driver.core.model.base.CacheProperty;
import com.yanry.driver.core.model.runtime.Presentable;
import com.yanry.driver.mobile.view.View;

public abstract class ViewProperty<V> extends CacheProperty<V> {
    private View view;

    public ViewProperty(View view) {
        super(view.getGraph());
        this.view = view;
    }

    @Presentable
    public View getView() {
        return view;
    }

    @Override
    protected final V checkValue() {
        if (view.isVisible()) {
            return doCheckValue();
        }
        return null;
    }

    protected abstract V doCheckValue();
}