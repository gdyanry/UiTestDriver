package com.yanry.testdriver.sample.reservation.window;

import com.yanry.testdriver.ui.mobile.base.Path;
import com.yanry.testdriver.ui.mobile.base.expectation.Timing;
import com.yanry.testdriver.ui.mobile.base.expectation.DynamicPropertyExpectation;
import com.yanry.testdriver.ui.mobile.extend.TestManager;
import com.yanry.testdriver.ui.mobile.extend.action.Click;
import com.yanry.testdriver.ui.mobile.extend.view.CheckBox;
import com.yanry.testdriver.ui.mobile.extend.view.TextView;
import com.yanry.testdriver.ui.mobile.extend.view.View;
import com.yanry.testdriver.ui.mobile.extend.view.selector.ByIndex;
import com.yanry.testdriver.ui.mobile.extend.view.selector.ByText;

/**
 * Created by rongyu.yan on 5/19/2017.
 */
public class SelectDayOfWeek extends TestManager.Window {

    public SelectDayOfWeek(TestManager manager) {
        manager.super();
    }

    @Override
    protected void addCases() {
        closeOnTouchOutside();
        close(new Click<>(new View(this, new ByText("取消"))), Timing.IMMEDIATELY);
        Click<View, Object> clickConfirm = new Click<>(new View(this, new ByText("确定")));
        close(clickConfirm, Timing.IMMEDIATELY);
        CheckBox[] checkBoxes = new CheckBox[7];
        PeriodicReserve fromWindow = getWindow(PeriodicReserve.class);
        PeriodicReserve.DayOfWeekValue dayOfWeekValue = fromWindow.getDayOfWeekValue();
        PeriodicReserve.DayOfWeekValidity dayOfWeekValidity = fromWindow.getDayOfWeekValidity();
        for (int i = 0; i < 7; i++) {
            checkBoxes[i] = new CheckBox(this, new ByIndex(i));
            int finalIndex = i;
            // init state
            createPath(getCreateEvent(), new DynamicPropertyExpectation<Boolean>(Timing.IMMEDIATELY, checkBoxes[i]
                    .getCheckState(), () -> dayOfWeekValue.getCurrentValue()[finalIndex]));
            // day of week validity on true
            createPath(clickConfirm, dayOfWeekValidity.getExpectation(Timing.IMMEDIATELY, true)).addInitState
                    (checkBoxes[i].getCheckState(), true).addInitState(dayOfWeekValidity, false);
        }
        // day of week validity on false
        Path path = createPath(clickConfirm, dayOfWeekValidity.getExpectation(Timing.IMMEDIATELY, false));
        for (CheckBox checkBox : checkBoxes) {
            path.addInitState(checkBox.getCheckState(), false);
        }
        // day of week value
        TextView tvDayOfWeek = fromWindow.getTvDayOfWeek();
        createPath(clickConfirm, dayOfWeekValue.getExpectation(Timing.IMMEDIATELY, () -> {
            for (int i = 0; i < checkBoxes.length; i++) {
                CheckBox checkBox = checkBoxes[i];
                dayOfWeekValue.getCurrentValue()[i] = checkBox.getCheckState().getCurrentValue();
            }
            return dayOfWeekValue.getCurrentValue();
        }).addFollowingExpectation(tvDayOfWeek.getText().getExpectation(Timing.IMMEDIATELY, () -> {
            StringBuilder stringBuilder = new StringBuilder();
            boolean[] bArr = dayOfWeekValue.getCurrentValue();
            if (bArr[0]) {
                stringBuilder.append("星期一，");
            }
            if (bArr[1]) {
                stringBuilder.append("星期二，");
            }
            if (bArr[2]) {
                stringBuilder.append("星期三，");
            }
            if (bArr[3]) {
                stringBuilder.append("星期四，");
            }
            if (bArr[4]) {
                stringBuilder.append("星期五，");
            }
            if (bArr[5]) {
                stringBuilder.append("星期六，");
            }
            if (bArr[6]) {
                stringBuilder.append("星期日，");
            }
            if (stringBuilder.length() > 0) {
                stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            }
            return stringBuilder.toString();
        })));
    }
}
