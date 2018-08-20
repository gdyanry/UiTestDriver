package com.yanry.driver.core.model.base;

import com.yanry.driver.core.model.expectation.Timing;
import com.yanry.driver.core.model.runtime.Presentable;
import com.yanry.driver.core.model.state.State;
import com.yanry.driver.core.model.state.ValuePredicate;

import java.util.LinkedList;
import java.util.List;

/**
 * Expectation that has a timing and following expectations. Following expectations are verified only when this expectation passes its verification at runtime.
 * Created by rongyu.yan on 4/24/2017.
 */
@Presentable
public abstract class Expectation {
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

    @Presentable
    public List<Expectation> getFollowingExpectations() {
        return followingExpectations;
    }

    @Presentable
    public State getTrigger() {
        return trigger;
    }

    private void recursiveOnVerify() {
        onVerify();
        followingExpectations.forEach(e -> e.recursiveOnVerify());
    }

    private VerifyResult recursiveDoVerify(Graph graph) {
        if (trigger == null || trigger.getValuePredicate().test(trigger.getProperty())) {
            if (doVerify()) {
                followingExpectations.forEach(e -> e.recursiveDoVerify(graph));
                return VerifyResult.Success;
            }
            return VerifyResult.Failed;
        }
        graph.addPendingExpectation(this);
        return VerifyResult.Pending;
    }

    final VerifyResult verify(Graph graph) {
        recursiveOnVerify();
        return recursiveDoVerify(graph);
    }

    /**
     * @return 该期望是否为用户关注（需要输出到测试结果中）的。
     */
    public final boolean isNeedCheck() {
        return needCheck;
    }

    @Presentable
    public Timing getTiming() {
        return timing;
    }

    protected abstract void onVerify();

    protected abstract boolean doVerify();

    public enum VerifyResult {
        Success, Failed, Pending
    }
}
