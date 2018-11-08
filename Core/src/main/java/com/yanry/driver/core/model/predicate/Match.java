package com.yanry.driver.core.model.predicate;

import lib.common.util.object.EqualsPart;
import lib.common.util.object.Visible;

import java.util.regex.Pattern;

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
    public boolean test(V value) {
        return pattern.matcher(value).matches();
    }
}
