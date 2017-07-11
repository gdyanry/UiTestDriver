package com.yanry.testdriver.sample.reservation.window;

import com.yanry.testdriver.sample.reservation.server.Config;
import com.yanry.testdriver.ui.mobile.base.Graph;
import com.yanry.testdriver.ui.mobile.base.expectation.Timing;
import com.yanry.testdriver.ui.mobile.base.property.SwitchBySearchProperty;
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
    public static String TV_START_TIME;
    public static String PROP_START_TIME_VALIDITY;
    public static String DESC_ITEM_START_TIME;
    public static String DESC_ITEM_END_TIME;
    public static String PROP_END_TIME_VALIDITY;
    public static String TV_END_TIME;
    public static String PROP_DAY_OF_WEEK_VALUE;
    public static String PROP_DAY_OF_WEEK_VALIDITY;
    public static String DESC_ITEM_DAY_OF_WEEK;
    public static String TV_DAY_OF_WEEK;

    public PeriodicReserve(TestManager manager) {
        manager.super();
    }

    @Override
    protected void addCases() {
        ValidateEditText etTopic = new ValidateEditText(this, new ByDesc(DESC_ET_TOPIC));
        Click clickSubmit = new Click(new View(this, new ByDesc(DESC_V_SUBMIT)));
        TextView tvRoom = new TextView(this, new ByDesc(TV_ROOM));
        Validity roomValidity = new Validity();
        TextView tvStartTime = new TextView(this, new ByDesc(TV_START_TIME));
        Validity startTimeValidity = new Validity();
        Validity endTimeValidity = new Validity();
        TextView tvEndTime = new TextView(this, new ByDesc(TV_END_TIME));
        DayOfWeekValue dayOfWeekValue = new DayOfWeekValue();
        DayOfWeekValidity dayOfWeekValidity = new DayOfWeekValidity(dayOfWeekValue);
        TextView tvDayOfWeed = new TextView(this, new ByDesc(TV_DAY_OF_WEEK));

        registerView(TV_ROOM, tvRoom);
        registerProperty(PROP_ROOM_VALIDITY, roomValidity);
        registerView(TV_START_TIME, tvStartTime);
        registerProperty(PROP_START_TIME_VALIDITY, startTimeValidity);
        registerProperty(PROP_END_TIME_VALIDITY, endTimeValidity);
        registerView(TV_END_TIME, tvEndTime);
        registerProperty(PROP_DAY_OF_WEEK_VALUE, dayOfWeekValue);
        registerProperty(PROP_DAY_OF_WEEK_VALIDITY, dayOfWeekValidity);
        registerView(TV_DAY_OF_WEEK, tvDayOfWeed);

        close(new Click<>(new View(this, new ByDesc(DESC_IC_QUIT))), Timing.IMMEDIATELY);

        etTopic.setEmptyValidationCase(clickSubmit, new Toast(Timing.IMMEDIATELY, Config.TOAST_DURATION, getGraph(),
                "会议主题不可为空"));
        etTopic.addPositiveCases(String.format("test topic<%tR>", System.currentTimeMillis()));

        popWindow(getWindow(SelectRoom.class), new Click<>(new View(this, new ByDesc(DESC_ITEM_ROOM))), Timing
                .IMMEDIATELY, false, false);
        createPath(getCreateEvent(), roomValidity.getExpectation(Timing.IMMEDIATELY, false));
        createPath(clickSubmit, new Toast(Timing.IMMEDIATELY, Config.TOAST_DURATION, getGraph(), "必须选择会议室"))
                .addInitState(etTopic.getValidity(), true).addInitState(roomValidity, false);

        popWindow(getWindow(SelectTime.class), new Click<>(new View(this, new ByDesc(DESC_ITEM_START_TIME))), Timing
                .IMMEDIATELY, false, false);
        createPath(getCreateEvent(), startTimeValidity.getExpectation(Timing.IMMEDIATELY, false));
        createPath(clickSubmit, new Toast(Timing.IMMEDIATELY, Config.TOAST_DURATION, getGraph(), "必须选择会议开始时间"))
                .addInitState(etTopic.getValidity(), true).addInitState(roomValidity, true).addInitState
                (startTimeValidity, false);

        popWindow(getWindow(SelectEndTime.class), new Click<>(new View(this, new ByDesc(DESC_ITEM_END_TIME))),
                Timing.IMMEDIATELY, false, false);
        createPath(getCreateEvent(), endTimeValidity.getExpectation(Timing.IMMEDIATELY, false));
        createPath(clickSubmit, new Toast(Timing.IMMEDIATELY, Config.TOAST_DURATION, getGraph(), "必须选择会议结束时间"))
                .addInitState(etTopic.getValidity(), true).addInitState(roomValidity, true).addInitState
                (startTimeValidity, true).addInitState(endTimeValidity, false);

        popWindow(getWindow(SelectDayOfWeek.class), new Click<>(new View(this, new ByDesc(DESC_ITEM_DAY_OF_WEEK))),
                Timing.IMMEDIATELY, false, false);
        createPath(getCreateEvent(), dayOfWeekValidity.getExpectation(Timing.IMMEDIATELY, false));
        createPath(getCreateEvent(), dayOfWeekValue.getExpectation(Timing.IMMEDIATELY, new boolean[]{false, false,
                false, false, false, false, false}));
        createPath(clickSubmit, new Toast(Timing.IMMEDIATELY, Config.TOAST_DURATION, getGraph(), "必须选择星期"))
                .addInitState(etTopic.getValidity(), true).addInitState(roomValidity, true).addInitState
                (startTimeValidity, true).addInitState(endTimeValidity, true).addInitState(dayOfWeekValidity, false);
    }

    public class Validity extends SwitchBySearchProperty<Boolean> {

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

    public class DayOfWeekValue extends SwitchBySearchProperty<boolean[]> {

        @Override
        protected boolean[] checkValue() {
            return new boolean[] {false, false, false, false, false, false, false};
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

    public class DayOfWeekValidity extends SwitchBySearchProperty<Boolean> {
        private DayOfWeekValue value;

        public DayOfWeekValidity(DayOfWeekValue value) {
            this.value = value;
        }

        @Override
        protected Boolean checkValue() {
            return null;
        }

        @Override
        public Boolean getCurrentValue() {
            boolean[] booleans = value.getCurrentValue();
            for (boolean b : booleans) {
                if (b) {
                    return true;
                }
            }
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
