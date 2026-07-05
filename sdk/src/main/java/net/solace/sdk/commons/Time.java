package net.solace.sdk.commons;

import java.time.Duration;
import java.util.function.BooleanSupplier;
import net.solace.api.Static;
import net.solace.api.commons.ITime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Time {
    private static final Logger log = LoggerFactory.getLogger(Time.class);
    private static final ITime TIME = Static.getTime();
    private static final int DEFAULT_POLLING_RATE = 10;

    public static boolean sleep(long ms) {
        return TIME.sleep(ms);
    }

    public static boolean sleep(int min, int max) {
        return TIME.sleep(min, max);
    }

    public static boolean sleepUntil(BooleanSupplier supplier, BooleanSupplier resetSupplier, int pollingRate, int timeOut) {
        return TIME.sleepUntil(supplier, resetSupplier, pollingRate, timeOut);
    }

    public static boolean sleepUntil(BooleanSupplier supplier, BooleanSupplier resetSupplier, int timeOut) {
        return TIME.sleepUntil(supplier, resetSupplier, timeOut);
    }

    public static boolean sleepUntil(BooleanSupplier supplier, int pollingRate, int timeOut) {
        return TIME.sleepUntil(supplier, pollingRate, timeOut);
    }

    public static boolean sleepUntil(BooleanSupplier supplier, int timeOut) {
        return TIME.sleepUntil(supplier, timeOut);
    }

    public static boolean sleepTicks(int ticks) {
        return TIME.sleepTicks(ticks);
    }

    public static boolean sleepTick() {
        return TIME.sleepTick();
    }

    public static boolean sleepTicksUntil(BooleanSupplier supplier, int ticks) {
        return TIME.sleepTicksUntil(supplier, ticks);
    }

    public static String format(Duration duration) {
        long secs = Math.abs(duration.getSeconds());
        return String.format("%02d:%02d:%02d", secs / 3600L, secs % 3600L / 60L, secs % 60L);
    }
}

