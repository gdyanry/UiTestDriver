package com.yanry.testdriver.ui.mobile.base.expectation;

import com.yanry.testdriver.ui.mobile.base.Path;
import com.yanry.testdriver.ui.mobile.base.Presentable;
import com.yanry.testdriver.ui.mobile.base.property.SearchableSwitchableProperty;

import java.util.LinkedList;
import java.util.List;
import java.util.function.BiPredicate;

/**
 * Expectation that has a timing and following expectations. Following expectations are verified only when this expectation passes its verification at runtime.
 * Created by rongyu.yan on 4/24/2017.
 */
@Presentable
public abstract class AbstractExpectation implements Expectation {
    private Timing timing;
    private List<Expectation> followingExpectations;

    public AbstractExpectation(Timing timing) {
        this.timing = timing;
        followingExpectations = new LinkedList<>();
    }

    protected abstract boolean selfVerify(List<Path> superPathContainer);

    /**
     *
     * @param endStatePredicate
     * @return whether this expectation itself isSatisfied the given end state predicate, excluding its following
     * expectations.
     */
    protected abstract boolean isSelfSatisfied(BiPredicate<SearchableSwitchableProperty, Object> endStatePredicate);

    public AbstractExpectation addFollowingExpectation(Expectation expectation) {
        followingExpectations.add(expectation);
        return this;
    }

    @Presentable
    public Timing getTiming() {
        return timing;
    }

    @Override
    public boolean verify(List<Path> superPathContainer) {
        if (selfVerify(superPathContainer)) {
            followingExpectations.forEach(e -> e.verify(superPathContainer));
            return true;
        }
        return false;
    }

    @Override
    public boolean isSatisfied(BiPredicate<SearchableSwitchableProperty, Object> endStatePredicate) {
        if (isSelfSatisfied(endStatePredicate)) {
            return true;
        }
        return followingExpectations.stream().anyMatch(e -> e.isSatisfied(endStatePredicate));
    }
}
