package com.yanry.driver.core.model.libtemp.revert;

public interface Revertible {
    void proceed();

    void recover();
}
