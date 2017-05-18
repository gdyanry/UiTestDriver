package com.yanry.testdriver.sample.reservation.window;

import com.yanry.testdriver.sample.reservation.server.Config;
import com.yanry.testdriver.ui.mobile.base.Graph;
import com.yanry.testdriver.ui.mobile.base.expectation.Timing;
import com.yanry.testdriver.ui.mobile.base.property.SearchableProperty;
import com.yanry.testdriver.ui.mobile.extend.TestManager;
import com.yanry.testdriver.ui.mobile.extend.action.Click;
import com.yanry.testdriver.ui.mobile.extend.expectation.Toast;
import com.yanry.testdriver.ui.mobile.extend.view.TextView;
import com.yanry.testdriver.ui.mobile.extend.view.ValidateEditText;
import com.yanry.testdriver.ui.mobile.extend.view.View;
import com.yanry.testdriver.ui.mobile.extend.view.selector.ByDesc;

/**
 * Created by rongyu.yan on 5/12/2017.
 */
public class PeriodicReserve extends TestManager.Window {
    public static String DESC_IC_QUIT;
    public static String DESC_ET_TOPIC;
    public static String DESC_V_SUBMIT;
    public static String DESC_ITEM_ROOM;
    public static String TV_ROOM;
    public static String PROP_ROOM_VALIDITY;

    public PeriodicReserve(TestManager manager) {
        manager.super();
    }

    @Override
    protected void addCases() {
        TextView tvRoom = new TextView(this, new ByDesc(TV_ROOM));
        ValidateEditText etTopic = new ValidateEditText(this, new ByDesc(DESC_ET_TOPIC));
        Click clickSubmit = new Click(new View(this, new ByDesc(DESC_V_SUBMIT)));
        Validity roomValidity = new Validity();

        registerView(TV_ROOM, tvRoom);
        registerProperty(PROP_ROOM_VALIDITY, roomValidity);

        close(new Click<>(new View(this, new ByDesc(DESC_IC_QUIT))), Timing.IMMEDIATELY);

        etTopic.setEmptyValidationCase(clickSubmit, new Toast(Timing.IMMEDIATELY, Config.TOAST_DURATION, getGraph(),
                "会议主题不可为空"));
        etTopic.addPositiveCases(String.format("test topic<%tR>", System.currentTimeMillis()));

        popWindow(getWindow(SelectRoom.class), new Click<>(new View(this, new ByDesc(DESC_ITEM_ROOM))), Timing
                .IMMEDIATELY, false, false);
        createPath(getCreateEvent(), roomValidity.getStaticExpectation(Timing.IMMEDIATELY, false));
        createPath(clickSubmit, new Toast(Timing.IMMEDIATELY, Config.TOAST_DURATION, getGraph(), "必须选择会议室"))
                .addInitState(etTopic.getValidity(), true);


    }

    public class Validity extends SearchableProperty<Boolean> {

        @Override
        protected Boolean checkValue() {
            return false;
        }

        @Override
        protected Graph getGraph() {
            return PeriodicReserve.this.getGraph();
        }

        @Override
        protected boolean isVisibleToUser() {
            return false;
        }
    }
}
