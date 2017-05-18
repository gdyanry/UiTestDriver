package com.yanry.testdriver.ui.mobile.base.expectation;

import com.yanry.testdriver.ui.mobile.base.Path;
import com.yanry.testdriver.ui.mobile.base.Presentable;
import com.yanry.testdriver.ui.mobile.base.property.SearchableProperty;

import java.util.LinkedList;
import java.util.List;
import java.util.function.BiPredicate;

/**
 * Created by rongyu.yan on 4/24/2017.
 */
@Presentable
public abstract class AbstractExpectation implements Expectation {
    private Timing timing;
    private List<Expectation> followingExpectations;

    protected abstract boolean verify(List<Path> superPathContainer);

    protected abstract boolean selfSwitchTest(BiPredicate<SearchableProperty, Object> predicate);

    public AbstractExpectation(Timing timing) {
        this.timing = timing;
        followingExpectations = new LinkedList<>();
    }

    public AbstractExpectation addFollowingExpectation(Expectation expectation) {
        followingExpectations.add(expectation);
        return this;
    }

    @Presentable
    public Timing getTiming() {
        return timing;
    }

    @Override
    public boolean verifyBunch(List<Path> superPathContainer) {
        if (verify(superPathContainer)) {
            followingExpectations.forEach(e -> e.verifyBunch(superPathContainer));
            return true;
        }
        return false;
    }

    @Override
    public boolean switchTest(BiPredicate<SearchableProperty, Object> predicate) {
        if (selfSwitchTest(predicate)) {
            return true;
        }
        return followingExpectations.stream().anyMatch(e -> e.switchTest(predicate));
    }
}
