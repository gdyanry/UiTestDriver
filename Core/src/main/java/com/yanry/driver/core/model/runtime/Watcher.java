package com.yanry.driver.core.model.runtime;

import com.yanry.driver.core.model.base.Property;

public interface Watcher {
    /**
     * 状态机处于待命状态的回调。待命状态是指某个动作执行后所有相关属性变化都已经处理完毕，等待执行下一个动作指令时的稳定状态。
     */
    void onTransitionComplete();

    <V> void onStateChange(Property<V> property, V fromVal, V toVal);
}
