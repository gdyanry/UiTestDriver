package com.yanry.driver.mobile.sample.model;

import com.yanry.driver.core.model.Graph;
import com.yanry.driver.core.model.Path;
import com.yanry.driver.core.model.property.CacheProperty;
import com.yanry.driver.core.model.runtime.GraphWatcher;
import lib.common.util.ConsoleUtil;

import java.util.Map;
import java.util.Set;

public class ConsoleGraphWatcher implements GraphWatcher {
    @Override
    public void onStandby(Map<CacheProperty, Object> cacheProperties, Set<Path> unprocessedPaths, Set<Path> successTemp, Set<Path> failedPaths, Path rollingPath) {
        ConsoleUtil.debug("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        ConsoleUtil.debug("rolling path: %s.", Graph.getPresentation(rollingPath));
        ConsoleUtil.debug("unprocessed paths: %s.", unprocessedPaths.size());
        for (CacheProperty property : cacheProperties.keySet()) {
            ConsoleUtil.debug(">>>>%s - %s", Graph.getPresentation(property), Graph.getPresentation(property.getCurrentValue()));
//            if (property instanceof WindowManager.Window.PreviousWindow) {
//                WindowManager.Window.PreviousWindow previousWindow = (WindowManager.Window.PreviousWindow) property;
//                WindowManager.Window.VisibilityState visibilityState = previousWindow.getWindow().getVisibility();
//                ConsoleUtil.debug(">>>>%s - %s", Graph.getPresentation(visibilityState), visibilityState.getCurrentValue());
//            }
        }
        ConsoleUtil.debug("success temp:");
        for (Path path : successTemp) {
            ConsoleUtil.debug("    %s", Graph.getPresentation(path));
        }
        ConsoleUtil.debug("failed paths:");
        for (Path failedPath : failedPaths) {
            ConsoleUtil.debug("    %s", Graph.getPresentation(failedPath));
        }
        ConsoleUtil.debug("------------------------------------------------------------------------------------------");
    }

}
