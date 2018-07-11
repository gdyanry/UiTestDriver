package com.yanry.testdriver.ui.mobile.base.expectation;

import com.yanry.testdriver.ui.mobile.base.Path;
import com.yanry.testdriver.ui.mobile.base.Presentable;

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

    public void preVerify() {
        onVerify();
        followingExpectations.forEach(e -> e.preVerify());
    }

    public final boolean verify(boolean verifySuperPaths) {
        if (doVerify(verifySuperPaths)) {
            followingExpectations.forEach(e -> e.verify(true));
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

    protected abstract boolean doVerify(boolean verifySuperPaths);

    protected abstract int getMatchDegree(Path path);

}
