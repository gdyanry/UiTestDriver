package com.yanry.driver.core.model.runtime.communicator;

import com.yanry.driver.core.model.base.ExternalEvent;
import com.yanry.driver.core.model.base.NonPropertyExpectation;
import com.yanry.driver.core.model.runtime.fetch.Obtainable;

/**
 * Created by rongyu.yan on 3/2/2017.
 */
public interface Communicator {
    /**
     * @param stateToCheck
     * @param <V>
     * @return 返回null表示无法确定状态值。
     */
    <V> V fetchState(Obtainable<V> stateToCheck);

    /**
     * @param externalEvent
     * @return 表示是否执行动作。
     */
    boolean performAction(ExternalEvent externalEvent);

    boolean verifyExpectation(NonPropertyExpectation expectation);
}
