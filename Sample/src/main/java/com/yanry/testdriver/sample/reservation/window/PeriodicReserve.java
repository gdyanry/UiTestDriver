package com.yanry.testdriver.sample.reservation.window;

import com.yanry.testdriver.sample.reservation.server.Config;
import com.yanry.testdriver.ui.mobile.base.Graph;
import com.yanry.testdriver.ui.mobile.base.expectation.Timing;
import com.yanry.testdriver.ui.mobile.base.property.CacheProperty;
import com.yanry.testdriver.ui.mobile.base.property.Property;
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

    public PeriodicReserve(TestManager manager) {
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
        ValidateEditText etTopic = new ValidateEditText(this, new ByDesc(DESC_ET_TOPIC));
        Click clickSubmit = new Click(new View(this, new ByDesc(DESC_V_SUBMIT)));
        tvRoom = new TextView(this, new ByDesc(TV_ROOM));
        roomValidity = new Validity();
        tvStartTime = new TextView(this, new ByDesc(TV_START_TIME));
        startTimeValidity = new Validity();
        endTimeValidity = new Validity();
        tvEndTime = new TextView(this, new ByDesc(TV_END_TIME));
        dayOfWeekValue = new DayOfWeekValue();
        dayOfWeekValidity = new DayOfWeekValidity(dayOfWeekValue);
        tvDayOfWeek = new TextView(this, new ByDesc(TV_DAY_OF_WEEK));

        close(new Click<>(new View(this, new ByDesc(DESC_IC_QUIT))), Timing.IMMEDIATELY);

        etTopic.setEmptyValidationCase(clickSubmit, new Toast(Timing.IMMEDIATELY, Config.TOAST_DURATION,
                "会议主题不可为空"));
        etTopic.addPositiveCases(String.format("test topic<%tR>", System.currentTimeMillis()));

        popWindow(getWindow(SelectRoom.class), new Click<>(new View(this, new ByDesc(DESC_ITEM_ROOM))), Timing
                .IMMEDIATELY, false, false);
        createPath(getCreateEvent(), roomValidity.getExpectation(Timing.IMMEDIATELY, false));
        createPath(clickSubmit, new Toast(Timing.IMMEDIATELY, Config.TOAST_DURATION, "必须选择会议室"))
                .addInitState(etTopic.getValidity(), true).addInitState(roomValidity, false);

        popWindow(getWindow(SelectTime.class), new Click<>(new View(this, new ByDesc(DESC_ITEM_START_TIME))), Timing
                .IMMEDIATELY, false, false);
        createPath(getCreateEvent(), startTimeValidity.getExpectation(Timing.IMMEDIATELY, false));
        createPath(clickSubmit, new Toast(Timing.IMMEDIATELY, Config.TOAST_DURATION, "必须选择会议开始时间"))
                .addInitState(etTopic.getValidity(), true).addInitState(roomValidity, true).addInitState
                (startTimeValidity, false);

        popWindow(getWindow(SelectEndTime.class), new Click<>(new View(this, new ByDesc(DESC_ITEM_END_TIME))),
                Timing.IMMEDIATELY, false, false);
        createPath(getCreateEvent(), endTimeValidity.getExpectation(Timing.IMMEDIATELY, false));
        createPath(clickSubmit, new Toast(Timing.IMMEDIATELY, Config.TOAST_DURATION, "必须选择会议结束时间"))
                .addInitState(etTopic.getValidity(), true).addInitState(roomValidity, true).addInitState
                (startTimeValidity, true).addInitState(endTimeValidity, false);

        popWindow(getWindow(SelectDayOfWeek.class), new Click<>(new View(this, new ByDesc(DESC_ITEM_DAY_OF_WEEK))),
                Timing.IMMEDIATELY, false, false);
        createPath(getCreateEvent(), dayOfWeekValidity.getExpectation(Timing.IMMEDIATELY, false));
        createPath(getCreateEvent(), dayOfWeekValue.getExpectation(Timing.IMMEDIATELY, new boolean[]{false, false,
                false, false, false, false, false}));
        createPath(clickSubmit, new Toast(Timing.IMMEDIATELY, Config.TOAST_DURATION, "必须选择星期"))
                .addInitState(etTopic.getValidity(), true).addInitState(roomValidity, true).addInitState
                (startTimeValidity, true).addInitState(endTimeValidity, true).addInitState(dayOfWeekValidity, false);
    }

    public class Validity extends CacheProperty<Boolean> {

        @Override
        protected Boolean checkValue(Graph graph) {
            return false;
        }

        @Override
        public boolean isCheckedByUser() {
            return false;
        }

        @Override
        protected boolean doSelfSwitch(Graph graph, Boolean to) {
            return false;
        }
    }

    public class DayOfWeekValue extends CacheProperty<boolean[]> {

        @Override
        protected boolean[] checkValue(Graph graph) {
            return new boolean[]{false, false, false, false, false, false, false};
        }

        @Override
        public boolean isCheckedByUser() {
            return false;
        }

        @Override
        protected boolean doSelfSwitch(Graph graph, boolean[] to) {
            return false;
        }
    }

    public class DayOfWeekValidity extends Property<Boolean> {
        private DayOfWeekValue value;

        public DayOfWeekValidity(DayOfWeekValue value) {
            this.value = value;
        }

        @Override
        protected boolean selfSwitch(Graph graph, Boolean to) {
            return false;
        }

        @Override
        public Boolean getCurrentValue(Graph graph) {
            boolean[] booleans = value.getCurrentValue(graph);
            for (boolean b : booleans) {
                if (b) {
                    return true;
                }
            }
            return false;
        }
    }
}
