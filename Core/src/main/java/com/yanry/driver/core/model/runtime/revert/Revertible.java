package com.yanry.driver.core.model.runtime.revert;

public interface Revertible {
    void proceed();

    void recover();
}
