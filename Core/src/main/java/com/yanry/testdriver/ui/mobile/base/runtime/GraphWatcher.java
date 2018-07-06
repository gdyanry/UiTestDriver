package com.yanry.testdriver.ui.mobile.base.runtime;

import com.yanry.testdriver.ui.mobile.base.Path;
import com.yanry.testdriver.ui.mobile.base.property.CacheProperty;

import java.util.List;
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
    void onStandby(Map<CacheProperty, Object> cacheProperties, Set<Path> failedPaths, Path rollingPath);

    void onStartRolling(List<Path> rollingPaths, boolean verifySuperPaths);
}
