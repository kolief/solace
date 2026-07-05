package net.solace.api.commons;

import java.util.function.BooleanSupplier;

public interface ITime {
    public static final int DEFAULT_POLLING_RATE = 10;

    public boolean sleep(long var1);

    public boolean sleep(int var1, int var2);

    public boolean sleepUntil(BooleanSupplier var1, BooleanSupplier var2, int var3, int var4);

    default public boolean sleepUntil(BooleanSupplier supplier, BooleanSupplier resetSupplier, int timeOut) {
        return this.sleepUntil(supplier, resetSupplier, 10, timeOut);
    }

    default public boolean sleepUntil(BooleanSupplier supplier, int pollingRate, int timeOut) {
        return this.sleepUntil(supplier, () -> false, pollingRate, timeOut);
    }

    default public boolean sleepUntil(BooleanSupplier supplier, int timeOut) {
        return this.sleepUntil(supplier, 10, timeOut);
    }

    public boolean sleepTicks(int var1);

    default public boolean sleepTick() {
        return this.sleepTicks(1);
    }

    public boolean sleepTicksUntil(BooleanSupplier var1, int var2);
}

