package com.yanry.driver.core.model.runtime;

import com.yanry.driver.core.model.base.Path;

/**
 * Created by rongyu.yan on 2/18/2017.
 */
@Presentable
public class MissedPath {
    private Path path;
    private Object cause;

    public MissedPath(Path path, Object cause) {
        this.path = path;
        this.cause = cause;
    }

    @Presentable
    public Path getPath() {
        return path;
    }

    @Presentable
    public Object getCause() {
        return cause;
    }
}
