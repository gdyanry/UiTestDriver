package com.yanry.driver.core.model.libtemp.revert;

public class RevertibleLong {
    private RevertManager manager;
    private long value;

    public RevertibleLong(RevertManager manager) {
        this.manager = manager;
    }

    public long get() {
        return value;
    }

    public void set(long value) {
        manager.proceed(new SetLong(value));
    }

    public void increment() {
        set(value + 1);
    }

    private class SetLong implements Revertible {
        private long copy;
        private long valueToSet;

        public SetLong(long valueToSet) {
            this.valueToSet = valueToSet;
            copy = value;
        }

        @Override
        public void proceed() {
            value = valueToSet;
        }

        @Override
        public void recover() {
            value = copy;
        }
    }

}
