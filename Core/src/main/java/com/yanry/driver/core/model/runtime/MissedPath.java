package com.yanry.driver.core.model.runtime;

import com.yanry.driver.core.model.base.Path;
import yanry.lib.java.util.object.EqualsPart;
import yanry.lib.java.util.object.HandyObject;
import yanry.lib.java.util.object.Visible;

/**
 * Created by rongyu.yan on 2/18/2017.
 */
public class MissedPath extends HandyObject {
    private Path path;
    private Object cause;

    public MissedPath(Path path, Object cause) {
        this.path = path;
        this.cause = cause;
    }

    @Visible
    @EqualsPart
    public Path getPath() {
        return path;
    }

    @Visible
    @EqualsPart
    public Object getCause() {
        return cause;
    }
}
