package net.solace.sdk.commons;

import java.time.Duration;
import java.time.Instant;
import java.util.function.Supplier;
import net.solace.sdk.commons.Time;

public class StopWatch {
    private Instant start;
    private Instant end;

    private StopWatch(Supplier<Instant> supplier, Duration duration) {
        this.start = supplier.get();
        if (duration != null) {
            this.end = this.start.plus(duration);
        }
    }

    public static StopWatch start(Supplier<Instant> supplier) {
        return new StopWatch(supplier, null);
    }

    public static StopWatch start() {
        return StopWatch.start(Instant::now);
    }

    public boolean exceeds(Duration duration) {
        return this.getElapsed().getSeconds() > duration.getSeconds();
    }

    public void setEndIn(Duration duration) {
        this.end = Instant.now().plus(duration);
    }

    public boolean isRunning() {
        return this.end == null || Instant.now().isBefore(this.end);
    }

    public Duration getElapsed() {
        return Duration.between(this.start, Instant.now());
    }

    public Duration getRemaining() {
        return this.end != null ? Duration.between(this.end, Instant.now()) : Duration.ZERO;
    }

    public String toElapsedString() {
        return Time.format(this.getElapsed());
    }

    public String toRemainingString() {
        return Time.format(this.getRemaining());
    }

    public void reset() {
        Instant prevStart = this.start;
        this.start = Instant.now();
        if (this.end != null) {
            Duration duration = Duration.between(prevStart, this.end);
            this.setEndIn(duration);
        }
    }

    public double getRate(long value, Duration rate) {
        long elapsed = this.getElapsed().toMillis();
        if (elapsed == 0L) {
            return 0.0;
        }
        return value * rate.toMillis() / this.getElapsed().toMillis();
    }

    public double getHourlyRate(long value) {
        return this.getRate(value, Duration.ofHours(1L));
    }
}

