package com.yanry.driver.core.model.runtime.communicator;

import com.yanry.driver.core.model.base.Expectation;
import com.yanry.driver.core.model.base.ExternalEvent;
import com.yanry.driver.core.model.base.NonPropertyExpectation;
import com.yanry.driver.core.model.runtime.fetch.Obtainable;

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
    protected abstract <V> String checkState(int repeat, Obtainable<V> stateToCheck);

    /**
     * @param repeat
     * @param externalEvent
     * @return 返回1（已执行）或者0（未执行）。
     */
    protected abstract String performAction(int repeat, ExternalEvent externalEvent);

    /**
     * @param repeat
     * @param expectation
     * @return 返回1（校验成功）、0（校验失败）或-1（无法校验）。
     */
    protected abstract String verifyExpectation(int repeat, Expectation expectation);

    @Override
    public <V> V fetchState(Obtainable<V> stateToCheck) {
        return _checkState(0, stateToCheck);
    }

    private <V> V _checkState(int repeat, Obtainable<V> req) {
        String resp = checkState(repeat, req);
        if (resp == null || resp.equals("-1")) {
            return null;
        }
        V convert = req.convert(resp);
        if (convert == null) {
            return _checkState(++repeat, req);
        }
        return convert;
    }

    @Override
    public boolean performAction(ExternalEvent externalEvent) {
        return _performAction(0, externalEvent);
    }

    private boolean _performAction(int repeat, ExternalEvent req) {
        String resp = performAction(repeat, req);
        if ("0".equals(resp)) {
            return false;
        } else if ("1".equals(resp)) {
            return true;
        }
        return _performAction(++repeat, req);
    }

    @Override
    public boolean verifyExpectation(NonPropertyExpectation expectation) {
        return _verifyExpectation(0, expectation);
    }

    private boolean _verifyExpectation(int repeat, Expectation req) {
        String resp = verifyExpectation(repeat, req);
        if ("0".equals(resp)) {
            return false;
        } else if ("1".equals(resp)) {
            return true;
        }
        return _verifyExpectation(++repeat, req);
    }
}
