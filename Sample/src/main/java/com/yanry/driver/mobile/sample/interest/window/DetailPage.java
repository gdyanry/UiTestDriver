package com.yanry.driver.mobile.sample.interest.window;

import com.yanry.driver.core.model.base.Graph;
import com.yanry.driver.core.model.expectation.Timing;
import com.yanry.driver.mobile.property.Text;
import com.yanry.driver.mobile.view.View;
import com.yanry.driver.mobile.view.selector.ById;
import com.yanry.driver.mobile.window.Window;
import com.yanry.driver.mobile.window.WindowManager;

public abstract class DetailPage extends Window {
    private Text tvPrincipal;

    public DetailPage(WindowManager manager) {
        super(manager);
        tvPrincipal = new Text(new View(getGraph(), this, new ById("tv_principal")));
    }

    @Override
    protected void addCases(Graph graph, WindowManager manager) {
        closeOnPressBack();
        createPath(getCreateEvent(), tvPrincipal.getDynamicExpectation(Timing.IMMEDIATELY, true, () -> getMainPage().getClickItem().getPreActionResult().getTvMoney().getCurrentValue()));
    }

    protected abstract MainPage getMainPage();
}
