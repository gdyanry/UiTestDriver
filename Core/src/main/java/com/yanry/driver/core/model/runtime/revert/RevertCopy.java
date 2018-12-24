package com.yanry.driver.core.model.runtime.revert;

public abstract class RevertCopy<T> implements RevertStep {
    private T origin;

    public RevertCopy(T origin) {
        this.origin = origin;
    }

    @Override
    public void proceed() {
        T copy = getCopy(origin);
        setReference(copy);
    }

    @Override
    public void recover() {
        setReference(origin);
    }

    protected abstract T getCopy(T origin);

    protected abstract void setReference(T object);
}
