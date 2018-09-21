package com.yanry.driver.mobile.sample.model;

import com.yanry.driver.core.model.base.Property;
import com.yanry.driver.core.model.base.Graph;
import com.yanry.driver.core.model.base.Path;
import com.yanry.driver.core.model.runtime.GraphWatcher;
import lib.common.model.log.Logger;

import java.util.Map;
import java.util.Set;

public class ConsoleGraphWatcher implements GraphWatcher {
    @Override
    public void onStandby(Map<Property, Object> cacheProperties, Set<Path> unprocessedPaths, Set<Path> successTemp, Set<Path> failedPaths, Path rollingPath) {
        Logger.getDefault().d("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        Logger.getDefault().d("rolling path: %s", Graph.getPresentation(rollingPath));
        Logger.getDefault().d("unprocessed paths: %s", unprocessedPaths.size());
        for (Property property : cacheProperties.keySet()) {
            Logger.getDefault().v(">>>>%s - %s", Graph.getPresentation(property), Graph.getPresentation(property.getCurrentValue()));
        }
        Logger.getDefault().v("success temp:");
        for (Path path : successTemp) {
            Logger.getDefault().v("    %s", Graph.getPresentation(path));
        }
        Logger.getDefault().v("failed paths:");
        for (Path failedPath : failedPaths) {
            Logger.getDefault().v("    %s", Graph.getPresentation(failedPath));
        }
        Logger.getDefault().d("------------------------------------------------------------------------------------------");
    }

}
