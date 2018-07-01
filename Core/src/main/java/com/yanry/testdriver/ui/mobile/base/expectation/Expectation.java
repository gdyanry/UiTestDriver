package com.yanry.testdriver.ui.mobile.base.expectation;

import com.yanry.testdriver.ui.mobile.base.Graph;
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

    public List<Expectation> getFollowingExpectations() {
        return followingExpectations;
    }

    public final int getTotalMatchDegree(Graph graph, Path path) {
        int degree = getMatchDegree(graph, path);
        for (Expectation expectation : followingExpectations) {
            degree += expectation.getTotalMatchDegree(graph, path);
        }
        return degree;
    }

    public final boolean verify(Graph graph) {
        if (selfVerify(graph)) {
            followingExpectations.forEach(e -> e.verify(graph));
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

    protected abstract boolean selfVerify(Graph graph);

    protected abstract int getMatchDegree(Graph graph, Path path);

}
