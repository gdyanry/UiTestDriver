package com.yanry.driver.mobile.sample.model;

import com.yanry.driver.core.Utils;
import com.yanry.driver.core.model.base.Path;
import com.yanry.driver.core.model.base.CacheProperty;
import com.yanry.driver.core.model.runtime.GraphWatcher;
import com.yanry.driver.mobile.window.PreviousWindow;
import com.yanry.driver.mobile.window.VisibilityState;
import lib.common.model.log.Logger;
import lib.common.util.ConsoleUtil;

import java.util.Map;
import java.util.Set;

public class ConsoleGraphWatcher implements GraphWatcher {
    @Override
    public void onStandby(Map<CacheProperty, Object> cacheProperties, Set<Path> unprocessedPaths, Set<Path> successTemp, Set<Path> failedPaths, Path rollingPath) {
        Logger.getDefault().d("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        Logger.getDefault().d("rolling path: %s", Utils.getPresentation(rollingPath));
        Logger.getDefault().d("unprocessed paths: %s", unprocessedPaths.size());
        for (CacheProperty property : cacheProperties.keySet()) {
            Logger.getDefault().v(">>>>%s - %s", Utils.getPresentation(property), Utils.getPresentation(property.getCurrentValue()));
        }
        Logger.getDefault().v("success temp:");
        for (Path path : successTemp) {
            Logger.getDefault().v("    %s", Utils.getPresentation(path));
        }
        Logger.getDefault().v("failed paths:");
        for (Path failedPath : failedPaths) {
            Logger.getDefault().v("    %s", Utils.getPresentation(failedPath));
        }
        Logger.getDefault().d("------------------------------------------------------------------------------------------");
    }

}
