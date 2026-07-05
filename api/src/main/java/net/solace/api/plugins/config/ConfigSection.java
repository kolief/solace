package net.solace.api.plugins.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.FIELD})
public @interface ConfigSection {
    public String name();

    public String description();

    public int position();

    public boolean closedByDefault() default false;

    public String keyName() default "";

    public String section() default "";

    public boolean hidden() default false;

    public String unhide() default "";
}

