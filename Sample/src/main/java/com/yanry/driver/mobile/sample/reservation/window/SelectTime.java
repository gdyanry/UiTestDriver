package com.yanry.driver.mobile.sample.reservation.window;

import com.yanry.driver.core.model.Graph;
import com.yanry.driver.core.model.expectation.Timing;
import com.yanry.driver.mobile.WindowManager;
import com.yanry.driver.mobile.action.Click;
import com.yanry.driver.mobile.property.Text;
import com.yanry.driver.mobile.sample.reservation.window.PeriodicReserve.Validity;
import com.yanry.driver.mobile.view.View;
import com.yanry.driver.mobile.view.container.ListView;
import com.yanry.driver.mobile.view.container.ListViewItem;
import com.yanry.driver.mobile.view.selector.ById;

/**
 * Created by rongyu.yan on 5/19/2017.
 */
public abstract class SelectTime extends WindowManager.Window {
    public SelectTime(WindowManager manager) {
        manager.super();
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
