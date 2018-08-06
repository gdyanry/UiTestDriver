package com.yanry.driver.core.model.communicator;

import com.yanry.driver.core.model.event.ActionEvent;
import com.yanry.driver.core.model.base.Expectation;
import com.yanry.driver.core.model.runtime.StateToCheck;

/**
 * Created by rongyu.yan on 3/13/2017.
 */
public abstract class SerializedCommunicator implements Communicator {
    /**
     * @param repeat
     * @param stateToCheck
     * @param <V>
     * @return 返回所选项的序号。返回null表示无法确定。
     */
    protected abstract <V> String checkState(int repeat, StateToCheck<V> stateToCheck);

    /**
     *
     * @param repeat
     * @param actionEvent
     * @return 返回1（已执行）或者0（未执行）。
     */
    protected abstract String performAction(int repeat, ActionEvent actionEvent);

    /**
     *
     * @param repeat
     * @param expectation
     * @return 返回1（校验成功）、0（校验失败）或-1（无法校验）。
     */
    protected abstract String verifyExpectation(int repeat, Expectation expectation);

    @Override
    public <V> V checkState(StateToCheck<V> stateToCheck) {
        return _checkState(0, stateToCheck);
    }

    private <V> V _checkState(int repeat, StateToCheck<V> req) {
        String resp = checkState(repeat, req);
        if (resp == null) {
            return null;
        }
        if (resp.matches("^([1-9]\\d*)|0$")) {
            int index = Integer.parseInt(resp);
            if (index < req.getOptions().length) {
                return req.getOptions()[index];
            }
        }
        return _checkState(++repeat, req);
    }

    @Override
    public boolean performAction(ActionEvent actionEvent) {
        return _performAction(0, actionEvent);
    }

    private boolean _performAction(int repeat, ActionEvent req) {
        String resp = performAction(repeat, req);
        if ("0".equals(resp)) {
            return false;
        } else if ("1".equals(resp)) {
            return true;
        }
        return _performAction(++repeat, req);
    }

    @Override
    public Boolean verifyExpectation(Expectation expectation) {
        return _verifyExpectation(0, expectation);
    }

    private Boolean _verifyExpectation(int repeat, Expectation req) {
        String resp = verifyExpectation(repeat, req);
        if ("0".equals(resp)) {
            return false;
        } else if ("1".equals(resp)) {
            return true;
        } else if ("-1".equals(resp)) {
            return null;
        }
        return _verifyExpectation(++repeat, req);
    }
}
