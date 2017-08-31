package com.yanry.testdriver.sample.reservation.window;

import com.yanry.testdriver.ui.mobile.base.expectation.Timing;
import com.yanry.testdriver.ui.mobile.extend.TestManager;
import com.yanry.testdriver.ui.mobile.extend.action.Click;
import com.yanry.testdriver.ui.mobile.extend.view.container.ListView;
import com.yanry.testdriver.ui.mobile.extend.view.container.ListViewItem;

/**
 * Created by rongyu.yan on 5/12/2017.
 */
public class SelectDateOnMain extends TestManager.Window {
    public SelectDateOnMain(TestManager manager) {
        manager.super();
    }

    @Override
    protected void addCases() {
        ListView lvDate = new ListView(this, null);
        // 随机点击一个item返回主页
        Click<ListViewItem, String[]> clickItem = new Click<>(lvDate.getRandomItem());
//        clickItem.setPreAction(item -> {
//            TextView dateView = new TextView(item, null);
//            QueryableProperty month = new QueryableProperty(getGraph(), "所选月份");
//            QueryableProperty year = new QueryableProperty(getGraph(), "所选年份");
//            return new String[]{dateView.getText().getCurrentValue(), month.getCurrentValue(), year.getCurrentValue()};
//        });
        // 校验日期标签是否正确显示
        Main main = getWindow(Main.class);
        close(clickItem, Timing.IMMEDIATELY, main.getTvDate().getText()
                .getExpectation(Timing.IMMEDIATELY, () -> {
                    String[] result = clickItem.getPreActionResult();
                    return String.format("%s-%s-%s", result[2], result[1], result[0]);
                }));
    }
}
