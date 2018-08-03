package com.yanry.driver.mobile.sample.model;

import lib.common.interfaces.Loggable;
import lib.common.util.ConsoleUtil;

public class ConsoleLoggable implements Loggable {
    @Override
    public void debug(String s, Object... objects) {
        ConsoleUtil.debug(2, s, objects);
    }

    @Override
    public void error(String s, Object... objects) {
        ConsoleUtil.error(2, s, objects);
    }
}
