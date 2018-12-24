package com.yanry.driver.core.model.runtime.revert;

public interface RevertStep {
    void proceed();

    void recover();
}
