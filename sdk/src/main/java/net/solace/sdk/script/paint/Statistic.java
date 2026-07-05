package net.solace.sdk.script.paint;

import java.util.function.Supplier;
import net.solace.sdk.commons.NumericFormat;
import net.solace.sdk.commons.StopWatch;

public final class Statistic {
    private final String key;
    private final boolean header;
    private final Supplier<String> supplier;
    private boolean empty = false;

    public Statistic(String key, boolean header, Supplier<String> supplier) {
        this.key = key;
        this.header = header;
        this.supplier = supplier;
    }

    public Statistic(String key, Supplier<String> supplier) {
        this(key, false, supplier);
    }

    public Statistic(String key, StopWatch timer, Supplier<Integer> rate, boolean format) {
        this(key, false, () -> {
            int value = (Integer)rate.get();
            long hourly = (long)timer.getHourlyRate(value);
            String valueText = format ? NumericFormat.apply(value) : String.valueOf(value);
            String hourlyText = format ? NumericFormat.apply(hourly) : String.valueOf(hourly);
            return valueText + " (" + hourlyText + " / hr)";
        });
    }

    public Statistic(String key, StopWatch timer, Supplier<Integer> rate) {
        this(key, timer, rate, true);
    }

    public static Statistic empty() {
        Statistic stat = new Statistic(null, null);
        stat.setEmpty(true);
        return stat;
    }

    public boolean isHeader() {
        return this.header;
    }

    public Supplier<String> getSupplier() {
        return this.supplier;
    }

    public String toString() {
        return this.supplier.get();
    }

    public String getKey() {
        return this.key;
    }

    public boolean isEmpty() {
        return this.empty;
    }

    public void setEmpty(boolean empty) {
        this.empty = empty;
    }
}

