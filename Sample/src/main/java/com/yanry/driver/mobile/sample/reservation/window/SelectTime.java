package com.yanry.driver.mobile.sample.reservation.window;

import com.yanry.driver.core.extend.WindowManager;
import com.yanry.driver.core.extend.action.Click;
import com.yanry.driver.core.extend.view.TextView;
import com.yanry.driver.core.extend.view.container.ListView;
import com.yanry.driver.core.extend.view.container.ListViewItem;
import com.yanry.driver.core.model.expectation.Timing;
import com.yanry.driver.mobile.sample.reservation.window.PeriodicReserve.Validity;

/**
 * Created by rongyu.yan on 5/19/2017.
 */
public abstract class SelectTime extends WindowManager.Window {
    public SelectTime(WindowManager manager) {
        manager.super();
    }

    protected abstract TextView getTextView(PeriodicReserve reserve);

    protected abstract Validity getValidity(PeriodicReserve reserve);

    protected abstract String getExpectedText(String selectedText);

    @Override
    protected void addCases() {
        closeOnTouchOutside();
        ListView listView = new ListView(getManager(), this, null);
        Click<ListViewItem, String> click = new Click<>(listView.getRandomItem());
        click.setPreAction(item -> new TextView(getManager(), item, null).getText().getCurrentValue());
        PeriodicReserve periodicReserve = new PeriodicReserve(getManager());
        close(click, Timing.IMMEDIATELY, getTextView(periodicReserve).getText().getStaticExpectation(Timing.IMMEDIATELY, true, getExpectedText(click.getPreActionResult()))
                .addFollowingExpectation(getValidity(periodicReserve).getStaticExpectation(Timing.IMMEDIATELY, false, true)));
    }
}
