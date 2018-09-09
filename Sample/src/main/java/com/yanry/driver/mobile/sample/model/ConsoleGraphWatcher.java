package com.yanry.driver.mobile.sample.model;

import com.yanry.driver.core.Utils;
import com.yanry.driver.core.model.base.Path;
import com.yanry.driver.core.model.base.CacheProperty;
import com.yanry.driver.core.model.runtime.GraphWatcher;
import com.yanry.driver.mobile.window.PreviousWindow;
import com.yanry.driver.mobile.window.VisibilityState;
import lib.common.util.ConsoleUtil;

import java.util.Map;
import java.util.Set;

public class ConsoleGraphWatcher implements GraphWatcher {
    @Override
    public void onStandby(Map<CacheProperty, Object> cacheProperties, Set<Path> unprocessedPaths, Set<Path> successTemp, Set<Path> failedPaths, Path rollingPath) {
        ConsoleUtil.debug("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        ConsoleUtil.debug("rolling path: %s.", Utils.getPresentation(rollingPath));
        ConsoleUtil.debug("unprocessed paths: %s.", unprocessedPaths.size());
        for (CacheProperty property : cacheProperties.keySet()) {
            ConsoleUtil.debug(">>>>%s - %s", Utils.getPresentation(property), Utils.getPresentation(property.getCurrentValue()));
            if (property instanceof PreviousWindow) {
                PreviousWindow previousWindow = (PreviousWindow) property;
                VisibilityState visibilityState = previousWindow.getWindow().getVisibility();
                ConsoleUtil.debug(">>>>%s - %s", Utils.getPresentation(visibilityState), visibilityState.getCurrentValue());
            }
        }
        ConsoleUtil.debug("success temp:");
        for (Path path : successTemp) {
            ConsoleUtil.debug("    %s", Utils.getPresentation(path));
        }
        ConsoleUtil.debug("failed paths:");
        for (Path failedPath : failedPaths) {
            ConsoleUtil.debug("    %s", Utils.getPresentation(failedPath));
        }
        ConsoleUtil.debug("------------------------------------------------------------------------------------------");
    }

}
