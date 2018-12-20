package com.yanry.driver.mobile.sample.listview;

import com.yanry.driver.core.model.base.StateSpace;
import com.yanry.driver.mobile.sample.Tester;
import com.yanry.driver.mobile.window.Application;

public class ListViewTest extends Application {
    public ListViewTest(StateSpace stateSpace) {
        super(stateSpace);
        MainPage mainPage = new MainPage(stateSpace, this);
        EditPage editPage = new EditPage(stateSpace, this);
        FilterPage filterPage = new FilterPage(stateSpace, this);
        DetailPage detailPage = new DetailPage(stateSpace, this);
        registerWindows(mainPage, editPage, filterPage, detailPage);
    }

    public static void main(String... args) {
        Tester.test(true, graph -> new ListViewTest(graph));
    }
}
