package com.yanry.driver.mobile.sample.reservation.window;

import com.yanry.driver.core.model.Graph;
import com.yanry.driver.core.model.Path;
import com.yanry.driver.core.model.expectation.Timing;
import com.yanry.driver.mobile.WindowManager;
import com.yanry.driver.mobile.action.Click;
import com.yanry.driver.mobile.view.CheckBox;
import com.yanry.driver.mobile.view.TextView;
import com.yanry.driver.mobile.view.View;
import com.yanry.driver.mobile.view.selector.ByIndex;
import com.yanry.driver.mobile.view.selector.ByText;

/**
 * Created by rongyu.yan on 5/19/2017.
 */
public class SelectDayOfWeek extends WindowManager.Window {

    public SelectDayOfWeek(WindowManager manager) {
        manager.super();
    }

    @Override
    protected void addCases(Graph graph, WindowManager manager) {
        closeOnTouchOutside();
        close(new Click<>(new View(graph, this, new ByText("取消"))), Timing.IMMEDIATELY);
        Click<View, Object> clickConfirm = new Click<>(new View(graph, this, new ByText("确定")));
        close(clickConfirm, Timing.IMMEDIATELY);
        CheckBox[] checkBoxes = new CheckBox[7];
        PeriodicReserve fromWindow = new PeriodicReserve(manager);
        PeriodicReserve.DayOfWeekValue dayOfWeekValue = fromWindow.getDayOfWeekValue();
        PeriodicReserve.DayOfWeekValidity dayOfWeekValidity = fromWindow.getDayOfWeekValidity();
        for (int i = 0; i < 7; i++) {
            checkBoxes[i] = new CheckBox(graph, this, new ByIndex(i));
            int finalIndex = i;
            // init state
            createPath(getCreateEvent(), checkBoxes[i].getCheckState().getDynamicExpectation(Timing.IMMEDIATELY, true, () -> dayOfWeekValue.getCurrentValue()[finalIndex]));
            // day of week validity on true
            createPath(clickConfirm, dayOfWeekValidity.getStaticExpectation(Timing.IMMEDIATELY, false, true)).addInitState
                    (checkBoxes[i].getCheckState(), true).addInitState(dayOfWeekValidity, false);
        }
        // day of week validity on false
        Path path = createPath(clickConfirm, dayOfWeekValidity.getStaticExpectation(Timing.IMMEDIATELY, true, false));
        for (CheckBox checkBox : checkBoxes) {
            path.addInitState(checkBox.getCheckState(), false);
        }
        // day of week value
        TextView tvDayOfWeek = fromWindow.getTvDayOfWeek();
        createPath(clickConfirm, dayOfWeekValue.getDynamicExpectation(Timing.IMMEDIATELY, false, () -> {
            for (int i = 0; i < checkBoxes.length; i++) {
                CheckBox checkBox = checkBoxes[i];
                dayOfWeekValue.getCurrentValue()[i] = checkBox.getCheckState().getCurrentValue();
            }
            return dayOfWeekValue.getCurrentValue();
        }).addFollowingExpectation(tvDayOfWeek.getText().getDynamicExpectation(Timing.IMMEDIATELY, true, () -> {
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
