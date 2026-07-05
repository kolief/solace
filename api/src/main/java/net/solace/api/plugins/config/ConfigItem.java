package net.solace.api.plugins.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.METHOD})
public @interface ConfigItem {
    public int position() default -1;

    public String keyName();

    public String name();

    public String description();

    public boolean hidden() default false;

    public String warning() default "";

    public boolean secret() default false;

    public String section() default "";

    public String title() default "";

    public boolean parse() default false;

    public Class<?> clazz() default void.class;

    public String method() default "";

    public String unhide() default "";

    public String unhideValue() default "";

    public String hide() default "";

    public String hideValue() default "";

    public String enabledBy() default "";

    public String enabledByValue() default "";

    public String disabledBy() default "";

    public String disabledByValue() default "";

    public boolean collapsible() default false;

    public boolean wide() default false;

    public boolean editable() default true;

    public Class<? extends Enum> enumClass() default Enum.class;
}

