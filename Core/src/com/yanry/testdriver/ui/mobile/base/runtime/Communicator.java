package com.yanry.testdriver.ui.mobile.base.runtime;

import com.yanry.testdriver.ui.mobile.base.event.ActionEvent;
import com.yanry.testdriver.ui.mobile.base.expectation.Expectation;
import com.yanry.testdriver.ui.mobile.base.property.QueryableProperty;

/**
 * Created by rongyu.yan on 3/2/2017.
 */
public interface Communicator {
    /**
     * @param stateToCheck
     * @param <V>
     * @return 返回null表示无法确定状态值。
     */
    <V> V checkState(StateToCheck<V> stateToCheck);

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

    /**
     *
     * @param property
     * @return 返回null表示无法校验，否则表示校验结果。
     */
    String queryValue(QueryableProperty property);
}
