package com.yanry.testdriver.ui.mobile.extend.view;

import com.yanry.testdriver.ui.mobile.base.Path;
import com.yanry.testdriver.ui.mobile.base.event.Event;
import com.yanry.testdriver.ui.mobile.base.expectation.Expectation;
import com.yanry.testdriver.ui.mobile.base.property.Property;
import com.yanry.testdriver.ui.mobile.extend.view.container.ViewContainer;
import com.yanry.testdriver.ui.mobile.extend.view.selector.ViewSelector;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Created by rongyu.yan on 5/11/2017.
 */
public class ValidateEditText extends EditText {
    private ValidityState validity;
    private Set<String> validContents;
    private Set<String> invalidContents;

    public ValidateEditText(ViewContainer parent, ViewSelector selector, Supplier<Boolean> defaultVisibility) {
        super(parent, selector, defaultVisibility);
        validity = new ValidityState();
        validContents = new HashSet<>();
        invalidContents = new HashSet<>();
    }

    public ValidateEditText(ViewContainer parent, ViewSelector selector) {
        this(parent, selector, null);
    }

    public void addPositiveCases(String... contents) {
        for (String content : contents) {
            validContents.add(content);
        }
    }

    public Path addNegativeCase(String content, Event event, Expectation expectation, Property<Boolean>...
            preValidities) {
        invalidContents.add(content);
        Path path = getWindow().createPath(event, expectation).addInitState(getInputContent(), content);
        getParent().present(path);
        for (Property<Boolean> preValidity : preValidities) {
            path.addInitState(preValidity, true);
        }
        return path;
    }

    public Path setEmptyValidationCase(Event event, Expectation expectation, Property<Boolean>...preValidities) {
        return addNegativeCase("", event, expectation, preValidities);
    }

    public ValidityState getValidity() {
        return validity;
    }

    public Set<String> getValidContents() {
        return validContents;
    }

    public class ValidityState extends Property<Boolean> {

        @Override
        protected boolean doSwitch(Boolean to, List<Path> superPathContainer) {
            if (to) {
                return validContents.stream().anyMatch(c -> getInputContent().switchTo(c, superPathContainer));
            }
            return invalidContents.stream().anyMatch(c -> getInputContent().switchTo(c, superPathContainer));
        }

        @Override
        public Boolean getCurrentValue() {
            return validContents.contains(getInputContent().getCurrentValue());
        }
    }
}
