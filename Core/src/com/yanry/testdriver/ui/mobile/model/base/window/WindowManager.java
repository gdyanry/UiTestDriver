package com.yanry.testdriver.ui.mobile.model.base.window;

import com.yanry.testdriver.ui.mobile.model.base.FollowingAction;
import com.yanry.testdriver.ui.mobile.model.base.Graph;
import com.yanry.testdriver.ui.mobile.model.base.Path;

import java.util.LinkedList;

/**
 * Created by rongyu.yan on 4/17/2017.
 */
public class WindowManager extends LinkedList<Window> {
    public WindowManager(Graph graph) {
        new Path(graph, null, graph.getProcessState().getStopProcessEvent(), (FollowingAction) () -> clear());
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
