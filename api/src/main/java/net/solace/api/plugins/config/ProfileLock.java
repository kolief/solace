package net.solace.api.plugins.config;

import java.util.List;
import java.util.function.Predicate;
import net.solace.api.plugins.config.ConfigProfile;

public interface ProfileLock
extends AutoCloseable {
    public List<ConfigProfile> getProfiles();

    public ConfigProfile createProfile(String var1, long var2);

    public ConfigProfile findProfile(String var1);

    public ConfigProfile findProfile(long var1);

    public ConfigProfile findProfile(Predicate<ConfigProfile> var1);

    public void removeProfile(long var1);

    public void renameProfile(ConfigProfile var1, String var2);

    public void dirty();
}

