package com.yanry.driver.core.model.runtime;

import com.yanry.driver.core.model.base.CacheProperty;
import com.yanry.driver.core.model.base.Path;

import java.util.Map;
import java.util.Set;

public interface GraphWatcher {
    /**
     * 状态机处于待命状态的回调。待命状态是指某个动作执行后所有相关属性变化都已经处理完毕，等待执行下一个动作指令的状态。
     *
     * @param cacheProperties
     * @param failedPaths
     * @param rollingPath
     */
    void onStandby(Map<CacheProperty, Object> cacheProperties, Set<Path> unprocessedPaths, Set<Path> successTemp, Set<Path> failedPaths, Path rollingPath);
}
