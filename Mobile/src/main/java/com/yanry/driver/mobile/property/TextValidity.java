package com.yanry.driver.mobile.property;

import com.yanry.driver.core.model.Path;
import com.yanry.driver.core.model.event.Event;
import com.yanry.driver.core.model.expectation.Expectation;
import com.yanry.driver.core.model.property.Property;
import com.yanry.driver.mobile.view.View;

import java.util.HashSet;
import java.util.Set;

public class TextValidity extends Property<Boolean> {
    private Set<String> validContents;
    private Set<String> invalidContents;
    private View view;
    private Text text;

    public TextValidity(View view, Text text) {
        super(view.getGraph());
        this.view = view;
        this.text = text;
        validContents = new HashSet<>();
        invalidContents = new HashSet<>();
    }

    public void addPositiveCases(String... contents) {
        for (String content : contents) {
            validContents.add(content);
        }
    }

    public Path addNegativeCase(String content, Event event, Expectation expectation, Property<Boolean>...
            preValidity) {
        invalidContents.add(content);
        Path path = view.getWindow().createPath(event, expectation)
                .addInitState(text, content);
        for (Property<Boolean> v : preValidity) {
            path.addInitState(v, true);
        }
        return path;
    }

    public Text getText() {
        return text;
    }

    public Set<String> getValidContents() {
        return validContents;
    }

    @Override
    public void handleExpectation(Boolean expectedValue, boolean needCheck) {

    }

    @Override
    public Boolean getCurrentValue() {
        return validContents.contains(text.getCurrentValue());
    }

    @Override
    protected boolean selfSwitch(Boolean to) {
        if (to) {
            return view.switchToVisible() || validContents.stream().anyMatch(c -> text.switchTo(c));
        }
        return view.switchToVisible() || invalidContents.stream().anyMatch(c -> text.switchTo(c));
    }
}
