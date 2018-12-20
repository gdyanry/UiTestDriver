package com.yanry.driver.mobile.window;

import com.yanry.driver.core.model.base.ExternalEvent;
import com.yanry.driver.core.model.base.Property;
import com.yanry.driver.core.model.base.StateSpace;
import lib.common.util.ReflectionUtil;
import lib.common.util.object.EqualsPart;
import lib.common.util.object.Visible;

import java.util.Set;
import java.util.stream.Stream;

public class WindowState extends Property<String> {
    static {
        ReflectionUtil.initStaticStringFields(WindowState.class);
    }

    public static String NOT_CREATED;
    public static String FOREGROUND;
    public static String BACKGROUND;

    private Window window;

    WindowState(StateSpace stateSpace, Window window) {
        super(stateSpace);
        this.window = window;
    }

    @Visible
    @EqualsPart
    public Window getWindow() {
        return window;
    }

    @Override
    protected String checkValue() {
        Window current = window.getApplication().getCurrentValue();
        if (current == null) {
            return NOT_CREATED;
        }
        if (current.equals(window)) {
            return FOREGROUND;
        }
        if (checkExist(current.getPreviousWindow())) {
            return BACKGROUND;
        }
        return NOT_CREATED;
    }

    @Override
    protected ExternalEvent doSelfSwitch(String to) {
        return null;
    }

    @Override
    protected Stream<String> getValueStream(Set<String> collectedValues) {
        return ReflectionUtil.getStaticStringFieldNames(getClass()).stream();
    }

    private boolean checkExist(PreviousWindow previousWindow) {
        Window previous = previousWindow.getCurrentValue();
        if (previous == null) {
            return false;
        }
        if (window.equals(previous)) {
            return true;
        }
        return checkExist(previous.getPreviousWindow());
    }
}
