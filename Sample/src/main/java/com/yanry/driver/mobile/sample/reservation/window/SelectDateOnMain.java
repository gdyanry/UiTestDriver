package com.yanry.driver.mobile.sample.reservation.window;

import com.yanry.driver.core.model.base.Graph;
import com.yanry.driver.core.model.expectation.Timing;
import com.yanry.driver.mobile.window.Window;
import com.yanry.driver.mobile.window.WindowManager;
import com.yanry.driver.mobile.action.Click;
import com.yanry.driver.mobile.view.container.ListView;
import com.yanry.driver.mobile.view.container.ListViewItem;

/**
 * Created by rongyu.yan on 5/12/2017.
 */
public class SelectDateOnMain extends Window {
    public SelectDateOnMain(WindowManager manager) {
        super(manager);
    }

    @Override
    protected void addCases(Graph graph, WindowManager manager) {
        ListView lvDate = new ListView(graph, this, null);
        // 随机点击一个item返回主页
        Click<ListViewItem, String[]> clickItem = new Click<>(lvDate.getRandomItem());
//        clickItem.setPreAction(item -> {
//            TextView dateView = new TextView(item, null);
//            QueryableProperty month = new QueryableProperty(getGraph(), "所选月份");
//            QueryableProperty year = new QueryableProperty(getGraph(), "所选年份");
//            return new String[]{dateView.getText().getCurrentValue(), month.getCurrentValue(), year.getCurrentValue()};
//        });
        // 校验日期标签是否正确显示
        Main main = new Main(manager);
        close(clickItem, Timing.IMMEDIATELY, main.getTxtDate().getDynamicExpectation(Timing.IMMEDIATELY, true, () -> {
            String[] result = clickItem.getPreActionResult();
            return String.format("%s-%s-%s", result[2], result[1], result[0]);
        }));
    }
}
