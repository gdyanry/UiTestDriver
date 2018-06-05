package com.yanry.testdriver.ui.mobile.base.expectation;

import com.yanry.testdriver.ui.mobile.base.Path;
import com.yanry.testdriver.ui.mobile.base.Presentable;
import com.yanry.testdriver.ui.mobile.base.property.SwitchBySearchProperty;

import java.util.LinkedList;
import java.util.List;
import java.util.function.BiPredicate;

/**
 * Expectation that has a timing and following expectations. Following expectations are verified only when this expectation passes its verification at runtime.
 * Created by rongyu.yan on 4/24/2017.
 */
@Presentable
public abstract class Expectation {
    private Timing timing;
    private List<Expectation> followingExpectations;

    public Expectation(Timing timing) {
        this.timing = timing;
        followingExpectations = new LinkedList<>();
    }

    protected abstract boolean selfVerify(List<Path> superPathContainer);

    /**
     * @param endStatePredicate
     * @return whether this expectation itself isSatisfied the given end state predicate, excluding its following
     * expectations.
     */
    protected abstract boolean isSelfSatisfied(BiPredicate<SwitchBySearchProperty, Object> endStatePredicate);

    public Expectation addFollowingExpectation(Expectation expectation) {
        followingExpectations.add(expectation);
        return this;
    }

    @Presentable
    public Timing getTiming() {
        return timing;
    }

    public boolean verify(List<Path> superPathContainer) {
        if (selfVerify(superPathContainer)) {
            followingExpectations.forEach(e -> e.verify(superPathContainer));
            return true;
        }
        return false;
    }

    /**
     * @param endStatePredicate
     * @return whether this expectation isSatisfied the given end state predicate.
     */
    public boolean isSatisfied(BiPredicate<SwitchBySearchProperty, Object> endStatePredicate) {
        if (isSelfSatisfied(endStatePredicate)) {
            return true;
        }
        return followingExpectations.stream().anyMatch(e -> e.isSatisfied(endStatePredicate));
    }

    /**
     * @return 该期望是否为用户关注（需要输出到测试结果中）的。
     */
    public abstract boolean ifRecord();
}
