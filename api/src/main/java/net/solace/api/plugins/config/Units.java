package net.solace.api.plugins.config;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.METHOD})
@Documented
public @interface Units {
    public static final String MILLISECONDS = "ms";
    public static final String MINUTES = " mins";
    public static final String PERCENT = "%";
    public static final String PIXELS = "px";
    public static final String POINTS = "pt";
    public static final String SECONDS = "s";
    public static final String TICKS = " ticks";
    public static final String LEVELS = " lvls";
    public static final String FPS = " fps";
    public static final String GP = " GP";

    public String value();
}

