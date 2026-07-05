package net.solace.api.plugins.config;

import java.io.File;
import java.lang.reflect.Type;
import java.util.List;
import java.util.function.Consumer;
import net.solace.api.plugins.Plugin;
import net.solace.api.plugins.config.Config;
import net.solace.api.plugins.config.ConfigDescriptor;
import net.solace.api.plugins.config.ConfigProfile;
import net.solace.api.plugins.config.ProfileLock;
import net.solace.api.plugins.config.RuneScapeProfile;

public interface ConfigManager {
    public static final String RSPROFILE_GROUP = "rsprofile";
    public static final String RSPROFILE_DISPLAY_NAME = "displayName";
    public static final String RSPROFILE_TYPE = "type";

    public void load();

    public <T extends Config> T getConfig(Class<T> var1);

    public List<String> getConfigurationKeys(String var1);

    public List<String> getRSProfileConfigurationKeys(String var1, String var2, String var3);

    public String getConfiguration(String var1, String var2);

    public String getConfiguration(String var1, String var2, String var3);

    public <T> T getConfiguration(String var1, String var2, Type var3);

    public <T> T getRSProfileConfiguration(String var1, String var2, Type var3);

    public <T> T getRSProfileConfiguration(String var1, String var2);

    public <T> T getConfiguration(String var1, String var2, String var3, Type var4);

    public void setConfiguration(String var1, String var2, String var3, String var4);

    public void setConfiguration(String var1, String var2, String var3);

    public <T> void setConfiguration(String var1, String var2, String var3, T var4);

    public <T> void setConfiguration(String var1, String var2, T var3);

    public <T> void setRSProfileConfiguration(String var1, String var2, T var3);

    public void unsetConfiguration(String var1, String var2, String var3);

    public void unsetConfiguration(String var1, String var2);

    public void unsetRSProfileConfiguration(String var1, String var2);

    public ConfigDescriptor getConfigDescriptor(Config var1);

    public <T extends Config> void setDefaultConfiguration(T var1, boolean var2);

    public Object stringToObject(String var1, Type var2);

    public String objectToString(Object var1);

    public List<RuneScapeProfile> getRSProfiles();

    public Consumer<? super Plugin> getConsumer(String var1, String var2);

    public ConfigProfile getProfile();

    public String getRSProfileKey();

    public void switchProfile(ConfigProfile var1);

    public void sendConfig();

    public void importAndMigrate(ProfileLock var1, File var2, ConfigProfile var3);
}

