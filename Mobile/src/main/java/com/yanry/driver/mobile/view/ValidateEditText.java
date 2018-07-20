package com.yanry.driver.mobile.view;

import com.yanry.driver.core.model.Graph;
import com.yanry.driver.core.model.Path;
import com.yanry.driver.core.model.event.Event;
import com.yanry.driver.core.model.expectation.Expectation;
import com.yanry.driver.core.model.property.Property;
import com.yanry.driver.core.model.runtime.Presentable;
import com.yanry.driver.mobile.view.container.ViewContainer;
import com.yanry.driver.mobile.view.selector.ViewSelector;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by rongyu.yan on 5/11/2017.
 */
public class ValidateEditText extends EditText {
    private ValidityState validity;
    private Set<String> validContents;
    private Set<String> invalidContents;

    public ValidateEditText(Graph graph, ViewContainer parent, ViewSelector selector) {
        super(graph, parent, selector);
        validity = new ValidityState(graph);
        validContents = new HashSet<>();
        invalidContents = new HashSet<>();
    }

    public void addPositiveCases(String... contents) {
        for (String content : contents) {
            validContents.add(content);
        }
    }

    public Path addNegativeCase(String content, Event event, Expectation expectation, Property<Boolean>...
            preValidities) {
        invalidContents.add(content);
        Path path = getWindow().createPath(event, expectation).addInitState(getContent(), content);
        getParent().present(path);
        for (Property<Boolean> preValidity : preValidities) {
            path.addInitState(preValidity, true);
        }
        return path;
    }

    public Path setEmptyValidationCase(Event event, Expectation expectation, Property<Boolean>... preValidities) {
        return addNegativeCase("", event, expectation, preValidities);
    }

    public ValidityState getValidity() {
        return validity;
    }

    public Set<String> getValidContents() {
        return validContents;
    }

    public class ValidityState extends Property<Boolean> {

        public ValidityState(Graph graph) {
            super(graph);
        }

        @Presentable
        public EditText getEditText() {
            return ValidateEditText.this;
        }

        @Override
        public void handleExpectation(Boolean expectedValue, boolean needCheck) {

        }

        @Override
        protected boolean selfSwitch(Boolean to) {
            if (to) {
                return validContents.stream().anyMatch(c -> getContent().switchTo(c));
            }
            return invalidContents.stream().anyMatch(c -> getContent().switchTo(c));
        }

        @Override
        protected boolean equalsWithSameClass(Property<Boolean> property) {
            ValidityState validityState = (ValidityState) property;
            return getEditText().equals(validityState.getEditText());
        }

        @Override
        public Boolean getCurrentValue() {
            return validContents.contains(getContent().getCurrentValue());
        }
    }
}
