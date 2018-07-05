package com.yanry.testdriver.sample.reservation.window;

import com.yanry.testdriver.sample.reservation.server.Config;
import com.yanry.testdriver.ui.mobile.base.Graph;
import com.yanry.testdriver.ui.mobile.base.expectation.Timing;
import com.yanry.testdriver.ui.mobile.base.property.CacheProperty;
import com.yanry.testdriver.ui.mobile.base.property.Property;
import com.yanry.testdriver.ui.mobile.extend.WindowManager;
import com.yanry.testdriver.ui.mobile.extend.action.Click;
import com.yanry.testdriver.ui.mobile.extend.expectation.Toast;
import com.yanry.testdriver.ui.mobile.extend.view.TextView;
import com.yanry.testdriver.ui.mobile.extend.view.ValidateEditText;
import com.yanry.testdriver.ui.mobile.extend.view.View;
import com.yanry.testdriver.ui.mobile.extend.view.selector.ByDesc;

/**
 * Created by rongyu.yan on 5/12/2017.
 */
public class PeriodicReserve extends WindowManager.Window {
    public static String DESC_IC_QUIT;
    public static String DESC_ET_TOPIC;
    public static String DESC_V_SUBMIT;
    public static String DESC_ITEM_ROOM;
    public static String TV_ROOM;
    public static String TV_START_TIME;
    public static String DESC_ITEM_START_TIME;
    public static String DESC_ITEM_END_TIME;
    public static String TV_END_TIME;
    public static String DESC_ITEM_DAY_OF_WEEK;
    public static String TV_DAY_OF_WEEK;

    private TextView tvRoom;
    private TextView tvStartTime;
    private TextView tvEndTime;
    private TextView tvDayOfWeek;

    private Validity roomValidity;
    private Validity startTimeValidity;
    private Validity endTimeValidity;
    private DayOfWeekValue dayOfWeekValue;
    private DayOfWeekValidity dayOfWeekValidity;

    public PeriodicReserve(WindowManager manager) {
        manager.super();
    }

    public TextView getTvRoom() {
        return tvRoom;
    }

    public TextView getTvStartTime() {
        return tvStartTime;
    }

    public TextView getTvEndTime() {
        return tvEndTime;
    }

    public TextView getTvDayOfWeek() {
        return tvDayOfWeek;
    }

    public Validity getRoomValidity() {
        return roomValidity;
    }

    public Validity getStartTimeValidity() {
        return startTimeValidity;
    }

    public Validity getEndTimeValidity() {
        return endTimeValidity;
    }

    public DayOfWeekValue getDayOfWeekValue() {
        return dayOfWeekValue;
    }

    public DayOfWeekValidity getDayOfWeekValidity() {
        return dayOfWeekValidity;
    }

    @Override
    protected void addCases() {
        ValidateEditText etTopic = new ValidateEditText(getManager(), this, new ByDesc(DESC_ET_TOPIC));
        Click clickSubmit = new Click(new View(getManager(), this, new ByDesc(DESC_V_SUBMIT)));
        tvRoom = new TextView(getManager(), this, new ByDesc(TV_ROOM));
        roomValidity = new Validity(getManager());
        tvStartTime = new TextView(getManager(), this, new ByDesc(TV_START_TIME));
        startTimeValidity = new Validity(getManager());
        endTimeValidity = new Validity(getManager());
        tvEndTime = new TextView(getManager(), this, new ByDesc(TV_END_TIME));
        dayOfWeekValue = new DayOfWeekValue(getManager());
        dayOfWeekValidity = new DayOfWeekValidity(getManager(), dayOfWeekValue);
        tvDayOfWeek = new TextView(getManager(), this, new ByDesc(TV_DAY_OF_WEEK));

        close(new Click<>(new View(getManager(), this, new ByDesc(DESC_IC_QUIT))), Timing.IMMEDIATELY);

        etTopic.setEmptyValidationCase(clickSubmit, new Toast(Timing.IMMEDIATELY, getManager(), Config.TOAST_DURATION,
                "会议主题不可为空"));
        etTopic.addPositiveCases(String.format("test topic<%tR>", System.currentTimeMillis()));

        popWindow(new SelectRoom(getManager()), new Click<>(new View(getManager(), this, new ByDesc(DESC_ITEM_ROOM))), Timing
                .IMMEDIATELY, false, false);
        createPath(getCreateEvent(), roomValidity.getStaticExpectation(Timing.IMMEDIATELY, false, false));
        createPath(clickSubmit, new Toast(Timing.IMMEDIATELY, getManager(), Config.TOAST_DURATION, "必须选择会议室"))
                .addInitState(etTopic.getValidity(), true).addInitState(roomValidity, false);

        popWindow(new SelectStartTime(getManager()), new Click<>(new View(getManager(), this, new ByDesc(DESC_ITEM_START_TIME))), Timing
                .IMMEDIATELY, false, false);
        createPath(getCreateEvent(), startTimeValidity.getStaticExpectation(Timing.IMMEDIATELY, false, false));
        createPath(clickSubmit, new Toast(Timing.IMMEDIATELY, getManager(), Config.TOAST_DURATION, "必须选择会议开始时间"))
                .addInitState(etTopic.getValidity(), true).addInitState(roomValidity, true).addInitState
                (startTimeValidity, false);

        popWindow(new SelectEndTime(getManager()), new Click<>(new View(getManager(), this, new ByDesc(DESC_ITEM_END_TIME))),
                Timing.IMMEDIATELY, false, false);
        createPath(getCreateEvent(), endTimeValidity.getStaticExpectation(Timing.IMMEDIATELY, false, false));
        createPath(clickSubmit, new Toast(Timing.IMMEDIATELY, getManager(), Config.TOAST_DURATION, "必须选择会议结束时间"))
                .addInitState(etTopic.getValidity(), true).addInitState(roomValidity, true).addInitState
                (startTimeValidity, true).addInitState(endTimeValidity, false);

        popWindow(new SelectDayOfWeek(getManager()), new Click<>(new View(getManager(), this, new ByDesc(DESC_ITEM_DAY_OF_WEEK))),
                Timing.IMMEDIATELY, false, false);
        createPath(getCreateEvent(), dayOfWeekValidity.getStaticExpectation(Timing.IMMEDIATELY, false, false));
        createPath(getCreateEvent(), dayOfWeekValue.getStaticExpectation(Timing.IMMEDIATELY, false, new boolean[]{false, false,
                false, false, false, false, false}));
        createPath(clickSubmit, new Toast(Timing.IMMEDIATELY, getManager(), Config.TOAST_DURATION, "必须选择星期"))
                .addInitState(etTopic.getValidity(), true).addInitState(roomValidity, true).addInitState
                (startTimeValidity, true).addInitState(endTimeValidity, true).addInitState(dayOfWeekValidity, false);
    }

    public class Validity extends CacheProperty<Boolean> {

        public Validity(Graph graph) {
            super(graph);
        }

        @Override
        protected Boolean checkValue() {
            return false;
        }

        @Override
        protected boolean doSelfSwitch(Boolean to) {
            return false;
        }

        @Override
        protected boolean equalsWithSameClass(Property<Boolean> property) {
            return false;
        }
    }

    public class DayOfWeekValue extends CacheProperty<boolean[]> {

        public DayOfWeekValue(Graph graph) {
            super(graph);
        }

        @Override
        protected boolean[] checkValue() {
            return new boolean[]{false, false, false, false, false, false, false};
        }

        @Override
        protected boolean doSelfSwitch(boolean[] to) {
            return false;
        }

        @Override
        protected boolean equalsWithSameClass(Property<boolean[]> property) {
            return false;
        }
    }

    public class DayOfWeekValidity extends Property<Boolean> {
        private DayOfWeekValue value;

        public DayOfWeekValidity(Graph graph, DayOfWeekValue value) {
            super(graph);
            this.value = value;
        }

        public DayOfWeekValidity(Graph graph) {
            super(graph);
        }

        @Override
        public void handleExpectation(Boolean expectedValue, boolean needCheck) {

        }

        @Override
        protected boolean selfSwitch(Boolean to) {
            return false;
        }

        @Override
        protected boolean equalsWithSameClass(Property<Boolean> property) {
            return false;
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
    }
}
