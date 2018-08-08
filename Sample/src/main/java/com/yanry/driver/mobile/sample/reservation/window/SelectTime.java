package com.yanry.driver.mobile.sample.reservation.window;

import com.yanry.driver.core.model.base.Graph;
import com.yanry.driver.core.model.expectation.Timing;
import com.yanry.driver.mobile.window.Window;
import com.yanry.driver.mobile.window.WindowManager;
import com.yanry.driver.mobile.action.Click;
import com.yanry.driver.mobile.property.Text;
import com.yanry.driver.mobile.sample.reservation.window.PeriodicReserve.Validity;
import com.yanry.driver.mobile.view.View;
import com.yanry.driver.mobile.view.ListView;
import com.yanry.driver.mobile.view.selector.ById;

/**
 * Created by rongyu.yan on 5/19/2017.
 */
public abstract class SelectTime extends Window {
    public SelectTime(WindowManager manager) {
        super(manager);
    }

    protected abstract Text getTextView(PeriodicReserve reserve);

    protected abstract Validity getValidity(PeriodicReserve reserve);

    protected abstract String getExpectedText(String selectedText);

    @Override
    protected void addCases(Graph graph, WindowManager manager) {
        closeOnTouchOutside();
        ListView listView = new ListView(getGraph(), this, null);
        Click<ListViewItem, String> click = new Click<>(listView.getRandomItem());
        click.setPreAction(item -> new Text(new View(graph, item, new ById("tv"))).getCurrentValue());
        PeriodicReserve periodicReserve = new PeriodicReserve(manager);
        close(click, Timing.IMMEDIATELY, getTextView(periodicReserve).getStaticExpectation(Timing.IMMEDIATELY, true, getExpectedText(click.getPreActionResult()))
                .addFollowingExpectation(getValidity(periodicReserve).getStaticExpectation(Timing.IMMEDIATELY, false, true)));
    }
}
