package net.solace.api.plugins.config;

import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;
import javax.swing.JMenuItem;
import net.runelite.client.util.LinkBrowser;
import net.solace.api.plugins.Plugin;
import net.solace.api.plugins.config.Config;
import net.solace.api.plugins.config.ConfigDescriptor;

public final class PluginConfigurationDescriptor {
    private final String name;
    private final String description;
    private final String[] tags;
    @Nullable
    private final Plugin plugin;
    @Nullable
    private final Config config;
    @Nullable
    private final ConfigDescriptor configDescriptor;
    @Nullable
    private final List<String> conflicts;

    public PluginConfigurationDescriptor(String name, String description, String[] tags, Config config, ConfigDescriptor configDescriptor) {
        this(name, description, tags, null, config, configDescriptor, null);
    }

    public boolean hasConfigurables() {
        return this.configDescriptor != null && !this.configDescriptor.getItems().stream().allMatch(item -> item.getItem().hidden());
    }

    public JMenuItem createSupportMenuItem() {
        String iname = this.getInternalPluginHubName();
        if (iname != null) {
            JMenuItem menuItem = new JMenuItem("Support");
            menuItem.addActionListener(e -> LinkBrowser.browse((String)("https://runelite.net/plugin-hub/show/" + iname)));
            return menuItem;
        }
        JMenuItem menuItem = new JMenuItem("Wiki");
        menuItem.addActionListener(e -> LinkBrowser.browse((String)("https://github.com/runelite/runelite/wiki/" + this.name.replace(' ', '-'))));
        return menuItem;
    }

    @Nullable
    public String getInternalPluginHubName() {
        return null;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public String[] getTags() {
        return this.tags;
    }

    @Nullable
    public Plugin getPlugin() {
        return this.plugin;
    }

    @Nullable
    public Config getConfig() {
        return this.config;
    }

    @Nullable
    public ConfigDescriptor getConfigDescriptor() {
        return this.configDescriptor;
    }

    @Nullable
    public List<String> getConflicts() {
        return this.conflicts;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof PluginConfigurationDescriptor)) {
            return false;
        }
        PluginConfigurationDescriptor other = (PluginConfigurationDescriptor)o;
        String this$name = this.getName();
        String other$name = other.getName();
        if (this$name == null ? other$name != null : !this$name.equals(other$name)) {
            return false;
        }
        String this$description = this.getDescription();
        String other$description = other.getDescription();
        if (this$description == null ? other$description != null : !this$description.equals(other$description)) {
            return false;
        }
        if (!Arrays.deepEquals(this.getTags(), other.getTags())) {
            return false;
        }
        Plugin this$plugin = this.getPlugin();
        Plugin other$plugin = other.getPlugin();
        if (this$plugin == null ? other$plugin != null : !((Object)this$plugin).equals(other$plugin)) {
            return false;
        }
        Config this$config = this.getConfig();
        Config other$config = other.getConfig();
        if (this$config == null ? other$config != null : !this$config.equals(other$config)) {
            return false;
        }
        ConfigDescriptor this$configDescriptor = this.getConfigDescriptor();
        ConfigDescriptor other$configDescriptor = other.getConfigDescriptor();
        if (this$configDescriptor == null ? other$configDescriptor != null : !this$configDescriptor.equals(other$configDescriptor)) {
            return false;
        }
        List<String> this$conflicts = this.getConflicts();
        List<String> other$conflicts = other.getConflicts();
        return !(this$conflicts == null ? other$conflicts != null : !((Object)this$conflicts).equals(other$conflicts));
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $name = this.getName();
        result = result * 59 + ($name == null ? 43 : $name.hashCode());
        String $description = this.getDescription();
        result = result * 59 + ($description == null ? 43 : $description.hashCode());
        result = result * 59 + Arrays.deepHashCode(this.getTags());
        Plugin $plugin = this.getPlugin();
        result = result * 59 + ($plugin == null ? 43 : ((Object)$plugin).hashCode());
        Config $config = this.getConfig();
        result = result * 59 + ($config == null ? 43 : $config.hashCode());
        ConfigDescriptor $configDescriptor = this.getConfigDescriptor();
        result = result * 59 + ($configDescriptor == null ? 43 : $configDescriptor.hashCode());
        List<String> $conflicts = this.getConflicts();
        result = result * 59 + ($conflicts == null ? 43 : ((Object)$conflicts).hashCode());
        return result;
    }

    public String toString() {
        return "PluginConfigurationDescriptor(name=" + this.getName() + ", description=" + this.getDescription() + ", tags=" + Arrays.deepToString(this.getTags()) + ", plugin=" + String.valueOf(this.getPlugin()) + ", config=" + String.valueOf(this.getConfig()) + ", configDescriptor=" + String.valueOf(this.getConfigDescriptor()) + ", conflicts=" + String.valueOf(this.getConflicts()) + ")";
    }

    public PluginConfigurationDescriptor(String name, String description, String[] tags, @Nullable Plugin plugin, @Nullable Config config, @Nullable ConfigDescriptor configDescriptor, @Nullable List<String> conflicts) {
        this.name = name;
        this.description = description;
        this.tags = tags;
        this.plugin = plugin;
        this.config = config;
        this.configDescriptor = configDescriptor;
        this.conflicts = conflicts;
    }
}

