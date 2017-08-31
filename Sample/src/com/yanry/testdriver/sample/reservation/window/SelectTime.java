package com.yanry.testdriver.sample.reservation.window;

import com.yanry.testdriver.sample.reservation.window.PeriodicReserve.Validity;
import com.yanry.testdriver.ui.mobile.base.expectation.Timing;
import com.yanry.testdriver.ui.mobile.extend.TestManager;
import com.yanry.testdriver.ui.mobile.extend.action.Click;
import com.yanry.testdriver.ui.mobile.extend.view.TextView;
import com.yanry.testdriver.ui.mobile.extend.view.container.ListView;
import com.yanry.testdriver.ui.mobile.extend.view.container.ListViewItem;

/**
 * Created by rongyu.yan on 5/19/2017.
 */
public abstract class SelectTime extends TestManager.Window {
    public SelectTime(TestManager manager) {
        manager.super();
    }

    protected abstract TextView getTextView(PeriodicReserve reserve);

    protected abstract Validity getValidity(PeriodicReserve reserve);

    protected abstract String getExpectedText(String selectedText);

    @Override
    protected void addCases() {
        closeOnTouchOutside();
        ListView listView = new ListView(this, null);
        Click<ListViewItem, String> click = new Click<>(listView.getRandomItem());
        click.setPreAction(item -> new TextView(item, null).getText().getCurrentValue());
        PeriodicReserve periodicReserve = getWindow(PeriodicReserve.class);
        close(click, Timing.IMMEDIATELY, getTextView(periodicReserve).getText().getExpectation(Timing.IMMEDIATELY, getExpectedText(click.getPreActionResult()))
                .addFollowingExpectation(getValidity(periodicReserve).getExpectation(Timing.IMMEDIATELY, true)));
    }
}
