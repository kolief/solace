package net.solace.api.plugins;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.TYPE})
@Documented
public @interface PluginDescriptor {
    public String name();

    public String configName() default "";

    public String description() default "";

    public String[] tags() default {};

    public String[] conflicts() default {};

    public boolean hidden() default false;

    public boolean enabledByDefault() default false;
}

