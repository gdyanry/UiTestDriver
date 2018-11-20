package com.yanry.driver.mobile.sample.listview;

import com.yanry.driver.core.model.base.Graph;
import com.yanry.driver.mobile.sample.Tester;
import com.yanry.driver.mobile.window.Application;

public class ListViewTest extends Application {
    public ListViewTest(Graph graph) {
        super(graph);
        MainPage mainPage = new MainPage(graph, this);
        EditPage editPage = new EditPage(graph, this);
        FilterPage filterPage = new FilterPage(graph, this);
        DetailPage detailPage = new DetailPage(graph, this);
        registerWindows(mainPage, editPage, filterPage, detailPage);
    }

    public static void main(String... args) {
        Tester.test(true, graph -> new ListViewTest(graph));
    }
}
