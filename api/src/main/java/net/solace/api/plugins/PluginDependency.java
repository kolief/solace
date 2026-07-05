package net.solace.api.plugins;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import net.solace.api.plugins.Plugin;
import net.solace.api.plugins.PluginDependencies;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.TYPE})
@Documented
@Repeatable(value=PluginDependencies.class)
public @interface PluginDependency {
    public Class<? extends Plugin> value();
}

