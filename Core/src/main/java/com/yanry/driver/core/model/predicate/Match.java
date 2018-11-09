package com.yanry.driver.core.model.predicate;

import com.yanry.driver.core.model.base.ValuePredicate;
import lib.common.util.object.EqualsPart;
import lib.common.util.object.Visible;

import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Match<V extends CharSequence> extends ValuePredicate<V> {
    private Pattern pattern;

    public Match(String regex) {
        pattern = Pattern.compile(regex);
    }

    @EqualsPart
    @Visible
    public String getRegex() {
        return pattern.pattern();
    }

    @Override
    public Stream<V> getConcreteValues() {
        return null;
    }

    @Override
    public boolean test(V value) {
        return pattern.matcher(value).matches();
    }
}
