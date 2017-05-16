package com.yanry.testdriver.ui.mobile.extend.view;

import com.yanry.testdriver.ui.mobile.Util;
import com.yanry.testdriver.ui.mobile.base.Path;
import com.yanry.testdriver.ui.mobile.base.event.Event;
import com.yanry.testdriver.ui.mobile.base.expectation.Expectation;
import com.yanry.testdriver.ui.mobile.extend.property.DependantValidation;
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

    public ValidateEditText(ViewContainer parent, ViewSelector selector, DependantValidation dependant) {
        super(parent, selector);
        validity = new ValidityState(dependant);
        validContents = new HashSet<>();
        invalidContents = new HashSet<>();
    }

    public void addPositiveCase(String content) {
        validContents.add(content);
    }

    public Path addNegativeCase(String content, Event event, Expectation expectation) {
        invalidContents.add(content);
        Path path = getWindow().createPath(event, expectation).addInitState(getInputContent(), content);
        validity.addValidationPassToPath(path, false);
        getParent().present(path);
        return path;
    }

    public Path setEmptyValidationCase(Event event, Expectation expectation) {
        return addNegativeCase("", event, expectation);
    }

    public ValidityState getValidity() {
        return validity;
    }

    public Set<String> getValidContents() {
        return validContents;
    }

    public class ValidityState extends DependantValidation {

        public ValidityState(DependantValidation dependant) {
            super(dependant);
        }

        @Override
        protected boolean switchTo(Boolean to, List<Path> superPathContainer, Supplier<Boolean> finalCheck) {
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
