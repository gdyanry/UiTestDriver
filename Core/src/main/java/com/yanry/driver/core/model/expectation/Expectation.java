package com.yanry.driver.core.model.expectation;

import com.yanry.driver.core.model.Path;
import com.yanry.driver.core.model.runtime.Presentable;

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

    public Expectation(Timing timing, boolean needCheck) {
        this.timing = timing;
        this.needCheck = needCheck;
        followingExpectations = new LinkedList<>();
    }

    public Expectation addFollowingExpectation(Expectation expectation) {
        followingExpectations.add(expectation);
        return this;
    }

    @Presentable
    public List<Expectation> getFollowingExpectations() {
        return followingExpectations;
    }

    public final int getTotalMatchDegree(Path path) {
        int degree = getMatchDegree(path);
        for (Expectation expectation : followingExpectations) {
            degree += expectation.getTotalMatchDegree(path);
        }
        return degree;
    }

    public void beforeVerify() {
        onVerify();
        followingExpectations.forEach(e -> e.beforeVerify());
    }

    public final boolean verify() {
        if (doVerify()) {
            followingExpectations.forEach(e -> e.verify());
            return true;
        }
        return false;
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

    protected abstract int getMatchDegree(Path path);

}
