package com.yanry.testdriver.sample.reservation.window;

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

    protected abstract String getTextViewTag();

    protected abstract String getValidityTag();

    protected abstract String getExpectedText(String selectedText);

    @Override
    protected void addCases() {
        closeOnTouchOutside();
        ListView listView = new ListView(this, null);
        TextView tv = getWindow(PeriodicReserve.class).getView(getTextViewTag());
        PeriodicReserve.Validity validity = getWindow(PeriodicReserve.class).getProperty(getValidityTag());
        Click<ListViewItem, String> click = new Click<>(listView.getRandomItem());
        click.setPreAction(item -> new TextView(item, null).getText().getCurrentValue());
        close(click, Timing.IMMEDIATELY, tv.getText().getExpectation(Timing.IMMEDIATELY, getExpectedText(click.getPreActionResult()))
                .addFollowingExpectation(validity.getExpectation(Timing.IMMEDIATELY, true)));
    }
}
