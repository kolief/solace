package net.solace.api.plugins;

import com.google.inject.Binder;
import com.google.inject.Injector;
import com.google.inject.Module;
import net.runelite.client.eventbus.EventBus;
import net.solace.api.events.PluginToggleHiddenChanged;
import net.solace.api.plugins.PluginDescriptor;
import net.solace.api.plugins.PluginMetaData;
import org.pf4j.Extension;
import org.pf4j.ExtensionPoint;

@Extension
public abstract class Plugin
implements Module,
ExtensionPoint {
    public Injector injector;
    private boolean toggleHidden;
    private PluginMetaData pluginMetaData;

    public final int hashCode() {
        return super.hashCode();
    }

    public final boolean equals(Object obj) {
        return super.equals(obj);
    }

    public void configure(Binder binder) {
    }

    public void startUp() throws Exception {
    }

    public void shutDown() throws Exception {
    }

    public void resetConfiguration() {
    }

    public String getName() {
        return this.getClass().getAnnotation(PluginDescriptor.class).name();
    }

    public boolean isSdn() {
        return this.pluginMetaData != null && this.pluginMetaData.isSdn();
    }

    public void setToggleHidden(boolean hidden) {
        this.toggleHidden = hidden;
        ((EventBus)this.injector.getInstance(EventBus.class)).post((Object)new PluginToggleHiddenChanged(hidden));
    }

    public Injector getInjector() {
        return this.injector;
    }

    public boolean isToggleHidden() {
        return this.toggleHidden;
    }

    public PluginMetaData getPluginMetaData() {
        return this.pluginMetaData;
    }

    public void setInjector(Injector injector) {
        this.injector = injector;
    }

    public void setPluginMetaData(PluginMetaData pluginMetaData) {
        this.pluginMetaData = pluginMetaData;
    }
}

