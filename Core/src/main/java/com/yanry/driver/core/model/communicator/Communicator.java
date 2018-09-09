package com.yanry.driver.core.model.communicator;

import com.yanry.driver.core.model.base.Expectation;
import com.yanry.driver.core.model.base.Property;
import com.yanry.driver.core.model.event.ActionEvent;
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
    <V> V checkState(Obtainable<V> stateToCheck);

    /**
     *
     * @param actionEvent
     * @return 表示是否执行动作。
     */
    boolean performAction(ActionEvent actionEvent);

    /**
     *
     * @param expectation
     * @return 返回null表示无法校验，否则表示校验结果。
     */
    Boolean verifyExpectation(Expectation expectation);
}
