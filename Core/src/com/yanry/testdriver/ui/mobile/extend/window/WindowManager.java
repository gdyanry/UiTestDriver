package com.yanry.testdriver.ui.mobile.extend.window;

import com.yanry.testdriver.ui.mobile.Util;
import com.yanry.testdriver.ui.mobile.base.expectation.DynamicExpectation;
import com.yanry.testdriver.ui.mobile.base.Graph;

import java.util.LinkedList;

/**
 * Created by rongyu.yan on 4/17/2017.
 */
public class WindowManager extends LinkedList<Window> {
    public WindowManager(Graph graph) {
        Util.createPath(graph, null, graph.getProcessState().getStopProcessEvent(), new DynamicExpectation() {
            @Override
            public void run() {
                clear();
            }
        });
    }

    public Visibility checkWindowVisibility(Window window) {
        int index = indexOf(window);
        if (index == -1) {
            return Visibility.NotCreated;
        } else if (index == size() - 1) {
            return Visibility.Foreground;
        } else {
            return Visibility.Background;
        }
    }
}
