package com.yanry.driver.core.model.base;

import com.yanry.driver.core.model.expectation.Timing;
import yanry.lib.java.model.log.LogLevel;
import yanry.lib.java.util.object.EqualsPart;
import yanry.lib.java.util.object.HandyObject;
import yanry.lib.java.util.object.Visible;

import java.util.LinkedList;
import java.util.List;

/**
 * Expectation that has a timing and following expectations. Following expectations are verified only when this expectation passes its verification at runtime.
 * Created by rongyu.yan on 4/24/2017.
 */
public abstract class Expectation extends HandyObject {
    private Timing timing;
    private List<Expectation> followingExpectations;
    private boolean needCheck;
    private State trigger;

    public Expectation(Timing timing, boolean needCheck) {
        this.timing = timing;
        this.needCheck = needCheck;
        followingExpectations = new LinkedList<>();
    }

    public Expectation addFollowingExpectation(Expectation expectation) {
        followingExpectations.add(expectation);
        return this;
    }

    public <V> Expectation setTrigger(Property<V> property, ValuePredicate<V> valuePredicate) {
        trigger = new State<>(property, valuePredicate);
        return this;
    }

    @Visible
    @EqualsPart
    public List<Expectation> getFollowingExpectations() {
        return followingExpectations;
    }

    @Visible
    @EqualsPart
    public State getTrigger() {
        return trigger;
    }

    @Visible
    @EqualsPart
    public Timing getTiming() {
        return timing;
    }

    final VerifyResult verify(StateSpace stateSpace) {
        if (needCheck) {
            stateSpace.enterMethod(this);
        }
        if (trigger == null || trigger.isSatisfied()) {
            if (doVerify()) {
                followingExpectations.forEach(e -> e.verify(stateSpace));
                if (needCheck) {
                    stateSpace.exitMethod(LogLevel.Verbose, VerifyResult.Success);
                }
                return VerifyResult.Success;
            }
            if (needCheck) {
                stateSpace.exitMethod(LogLevel.Warn, VerifyResult.Failed);
            }
            return VerifyResult.Failed;
        }
        stateSpace.addPendingExpectation(this);
        if (needCheck) {
            stateSpace.exitMethod(LogLevel.Info, VerifyResult.Pending);
        }
        return VerifyResult.Pending;
    }

    /**
     * @return 该期望是否为用户关注（需要输出到测试结果中）的。
     */
    public final boolean isNeedCheck() {
        return needCheck;
    }

    protected abstract boolean doVerify();

    public enum VerifyResult {
        Success, Failed, Pending
    }
}
