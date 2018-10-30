package com.yanry.driver.mobile.property;

import com.yanry.driver.core.model.base.ExternalEvent;
import com.yanry.driver.core.model.base.Property;
import com.yanry.driver.core.model.event.TransitionEvent;
import com.yanry.driver.core.model.expectation.Timing;
import com.yanry.driver.core.model.state.Within;
import com.yanry.driver.mobile.view.View;
import lib.common.util.object.EqualsPart;
import lib.common.util.object.Visible;

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
        Within<String> valid = new Within<>(validContents);
        Within<String> invalid = new Within<>(invalidContents);
        // to valid
        view.getWindow().createForegroundPath(new TransitionEvent<>(text, invalid, valid), getStaticExpectation(Timing.IMMEDIATELY, false, true))
                .addContextState(view, true);
        // to invalid
        view.getWindow().createForegroundPath(new TransitionEvent<>(text, valid, invalid), getStaticExpectation(Timing.IMMEDIATELY, false, false))
                .addContextState(view, true);
    }

    public void addPositiveCases(String... contents) {
        for (String content : contents) {
            validContents.add(content);
        }
    }

    public void addNegativeCase(String... contents) {
        for (String content : contents) {
            invalidContents.add(content);
        }
    }

    public Set<String> getValidContents() {
        return validContents;
    }

    public Text getText() {
        return text;
    }

    @Visible
    @EqualsPart
    public View getView() {
        return view;
    }

    @Override
    protected Boolean checkValue() {
        return validContents.contains(text.getCurrentValue());
    }

    @Override
    protected ExternalEvent doSelfSwitch(Boolean to) {
        return null;
    }
}
