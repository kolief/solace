package net.solace.api.plugins;

import net.solace.api.plugins.Plugin;

public interface IPlugins {
    public boolean isEnabled(Plugin var1);

    public boolean stopPlugin(Plugin var1);

    public boolean startPlugin(Plugin var1);
}

