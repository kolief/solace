package net.solace.loader.plugins.config;

import com.google.common.base.Strings;
import com.google.common.collect.ComparisonChain;
import com.google.gson.Gson;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.AccountHashChanged;
import net.runelite.api.events.PlayerChanged;
import net.runelite.api.events.WorldChanged;
import net.runelite.client.config.Keybind;
import net.runelite.client.config.ModifierlessKeybind;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ClientShutdown;
import net.runelite.client.util.ColorUtil;
import net.runelite.client.util.RunnableExceptionLogger;
import net.solace.api.Static;
import net.solace.api.events.ConfigChanged;
import net.solace.api.events.ConfigSync;
import net.solace.api.events.ProfileChanged;
import net.solace.api.events.RuneScapeProfileChanged;
import net.solace.api.items.loadouts.ILoadoutFactory;
import net.solace.api.items.loadouts.Loadout;
import net.solace.api.items.loadouts.LoadoutBuilder;
import net.solace.api.plugins.config.Alpha;
import net.solace.api.plugins.config.Config;
import net.solace.api.plugins.config.ConfigData;
import net.solace.api.plugins.config.ConfigDescriptor;
import net.solace.api.plugins.config.ConfigGroup;
import net.solace.api.plugins.config.ConfigImageResource;
import net.solace.api.plugins.config.ConfigInvocationHandler;
import net.solace.api.plugins.config.ConfigItem;
import net.solace.api.plugins.config.ConfigItemDescriptor;
import net.solace.api.plugins.config.ConfigManager;
import net.solace.api.plugins.config.ConfigProfile;
import net.solace.api.plugins.config.ConfigSection;
import net.solace.api.plugins.config.ConfigSectionDescriptor;
import net.solace.api.plugins.config.ConfigSerializer;
import net.solace.api.plugins.config.ConfigTitle;
import net.solace.api.plugins.config.ConfigTitleDescriptor;
import net.solace.api.plugins.config.ItemConfig;
import net.solace.api.plugins.config.ProfileLock;
import net.solace.api.plugins.config.Range;
import net.solace.api.plugins.config.RuneScapeProfile;
import net.solace.api.plugins.config.RuneScapeProfileType;
import net.solace.api.plugins.config.Serializer;
import net.solace.api.plugins.config.Units;
import net.solace.impl.items.loadouts.LoadoutImpl;
import net.solace.api.plugins.Plugin;
import net.solace.ui.plugins.ProfileManager;

import javax.annotation.Nullable;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Stack;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Slf4j
public class ConfigManagerImpl implements ConfigManager {
    private static final String RSPROFILE_ACCOUNT_HASH = "accountHash";

    private static final long RSPROFILE_ID = -1L;
    private static final String RSPROFILE_NAME = "$rsprofile";

    private static final int KEY_SPLITTER_GROUP = 0;
    private static final int KEY_SPLITTER_PROFILE = 1;
    private static final int KEY_SPLITTER_KEY = 2;

    private final ConfigInvocationHandler handler = new ConfigInvocationHandler(this);
    private final Map<String, Consumer<? super Plugin>> consumers = new HashMap<>();
    private final Map<Type, Serializer<?>> serializers = Collections.synchronizedMap(new WeakHashMap<>());

    @Nullable
    private final String configProfileName;
    private final EventBus eventBus;
    private final ProfileManager profileManager;

    @Nullable
    private final Client client;
    private final Gson gson;
    private final ExecutorService shutDownExecutor;
    private final ILoadoutFactory loadoutFactory;

    @Getter
    private ConfigProfile profile;
    private ConfigProfile rsProfile;
    private ConfigData configProfile;
    private ConfigData rsProfileConfigProfile;
    @Nullable
    private String rsProfileKey;

    public ConfigManagerImpl(
            ScheduledExecutorService executorService,
            @Nullable String configProfileName,
            EventBus eventBus,
            ProfileManager profileManager,
            @Nullable Client client,
            Gson gson,
            ExecutorService shutDownExecutor,
            ILoadoutFactory loadoutFactory
    ) {
        this.configProfileName = configProfileName;
        this.eventBus = eventBus;
        this.profileManager = profileManager;
        this.client = client;
        this.gson = gson;
        this.shutDownExecutor = shutDownExecutor;
        this.loadoutFactory = loadoutFactory;

        executorService.scheduleWithFixedDelay(RunnableExceptionLogger.wrap(this::sendConfig), 30 + (int) (5 * 60 * Math.random()), 5 * 60, TimeUnit.SECONDS);
    }

    public static String getWholeKey(String groupName, String profile, String key) {
        if (profile == null) {
            return groupName + "." + key;
        } else {
            return groupName + "." + profile + "." + key;
        }
    }

    private static Class<? extends Enum> findEnumClass(String clasz, ArrayList<ClassLoader> classLoaders) {
        var transformedString = new StringBuilder();
        for (var cl : classLoaders) {
            try {
                var strings = clasz.substring(0, clasz.indexOf("{")).split("\\.");
                var i = 0;
                while (i != strings.length) {
                    if (i == 0) {
                        transformedString.append(strings[i]);
                    } else if (i == strings.length - 1) {
                        transformedString.append("$").append(strings[i]);
                    } else {
                        transformedString.append(".").append(strings[i]);
                    }
                    i++;
                }
                return (Class<? extends Enum>) cl.loadClass(transformedString.toString());
            } catch (Exception e2) {
                // Will likely fail a lot
            }
            try {
                return (Class<? extends Enum>) cl.loadClass(clasz.substring(0, clasz.indexOf("{")));
            } catch (Exception e) {
                // Will likely fail a lot
            }
            transformedString = new StringBuilder();
        }
        throw new RuntimeException("Failed to find Enum for " + clasz.substring(0, clasz.indexOf("{")));
    }

    private static String[] splitKey(String key) {
        var i = key.indexOf('.');
        if (i == -1) {
            // all keys must have a group and key
            return null;
        }

        var group = key.substring(0, i);
        String profile = null;
        key = key.substring(i + 1);
        if (key.startsWith(RSPROFILE_GROUP + ".")) {
            i = key.indexOf('.', RSPROFILE_GROUP.length() + 2); // skip . after RSPROFILE_GROUP
            profile = key.substring(0, i);
            key = key.substring(i + 1);
        }
        return new String[]{group, profile, key};
    }

    private static void removeDuplicateProfiles(ProfileManager.Lock lock) {
        var seen = new HashMap<Long, ConfigProfile>();
        for (var it = lock.getProfiles().iterator(); it.hasNext(); ) {
            var profile = it.next();
            if (seen.containsKey(profile.getId())) {
                var existing = seen.get(profile.getId());
                log.warn("Duplicate profiles detected: {} and {}. Removing the latter.",
                        existing, profile);
                it.remove();
                lock.dirty();
                continue;
            }

            seen.put(profile.getId(), profile);
        }
    }

    private static void fixRsProfileName(ProfileManager.Lock lock) {
        var rsProfile = lock.findProfile(RSPROFILE_ID);
        if (rsProfile != null && !rsProfile.getName().equals(RSPROFILE_NAME)) {
            log.warn("renaming {} to {}", rsProfile, RSPROFILE_NAME);
            rsProfile.setName(RSPROFILE_NAME);
            lock.dirty();
        }
    }

    private static ConfigProfile updateProfile(ProfileLock lock, ConfigProfile profile) {
        var p = lock.findProfile(profile.getId());
        if (p == null) {
            log.warn("Lost active profile {}!", profile.getName());

            // We just recreate it, with the same id, so that the ConfigData stays valid
            p = lock.createProfile(profile.getName(), profile.getId());
            p.setActive(profile.isActive());
        } else if (profile.getRev() != p.getRev()) {
            // I think this is okay because while the in memory config on this client will be outdated,
            // the version on disk and also the remote version will still be consistent
            log.debug("Profile {} changed on disk", p.getName());
        }
        return p;
    }

    @Override
    public void load() {
        try (var lock = profileManager.lock()) {
            removeDuplicateProfiles(lock);
            fixRsProfileName(lock);

            ConfigProfile profile = null, rsProfile = null;

            for (var p : lock.getProfiles()) {
                if (p.isInternal()) {
                    log.debug("Profile '{}' (sync: {}, active: {}, id: {}, internal)", p.getName(), p.isSync(), p.getId(), p.isActive());

                    if (p.getName().equals(RSPROFILE_NAME)) {
                        rsProfile = p;
                    }

                    continue;
                }

                log.info("Profile '{}' (sync: {}, active: {}, id: {})", p.getName(), p.isSync(), p.isActive(), p.getId());
            }

            if (rsProfile == null) {
                rsProfile = lock.createProfile(RSPROFILE_NAME, RSPROFILE_ID);
            }
            rsProfile.setSync(true);

            this.rsProfile = rsProfile;
            rsProfileConfigProfile = new ConfigData(ProfileManager.profileConfigFile(rsProfile));

            final var launcherDisplayName = client != null ? client.getLauncherDisplayName() : null;
            // --profile
            if (configProfileName != null) {
                profile = lock.findProfile(p -> !p.isInternal() && configProfileName.equals(p.getName()));
            } else {
                // select a config profile associated with the display name from the jagex launcher, if available
                if (launcherDisplayName != null) {
                    profile = lock.findProfile(p ->
                    {
                        if (p.isInternal()) {
                            return false;
                        }

                        final var defaultRsProfilesForProfile = p.getDefaultForRsProfiles();
                        if (defaultRsProfilesForProfile == null) {
                            return false;
                        }

                        // Calling getConfiguration before a profile has been loaded is usually invalid. Because
                        // rsProfile is loaded above before this is run and we are only attempting to load rsProfile
                        // keys, it is safe to be called.
                        for (final var defaultRsProfile : defaultRsProfilesForProfile) {
                            final RuneScapeProfileType rsProfileType = getConfiguration(RSPROFILE_GROUP, defaultRsProfile, RSPROFILE_TYPE, RuneScapeProfileType.class);
                            if (rsProfileType != RuneScapeProfileType.STANDARD) {
                                continue;
                            }

                            final var profileDisplayName = getConfiguration(RSPROFILE_GROUP, defaultRsProfile, RSPROFILE_DISPLAY_NAME);
                            if (launcherDisplayName.equals(profileDisplayName)) {
                                return true;
                            }
                        }

                        return false;
                    });
                }
                if (profile == null) {
                    profile = lock.findProfile(p -> !p.isInternal() && p.isActive());
                }
                if (profile == null) {
                    profile = lock.findProfile(p -> !p.isInternal());
                }
            }

            if (profile != null) {
                log.info("Using profile: {} ({})", profile.getName(), profile.getId());
            } else {
                profile = lock.createProfile(configProfileName != null ? configProfileName : "default");
                if (configProfileName == null) {
                    // if creating the initial default profile
                    lock.getProfiles().forEach(p -> p.setActive(false));
                    profile.setActive(true);
                }

                log.info("Creating profile: {} ({})", profile.getName(), profile.getId());
            }

            this.profile = profile;
            configProfile = new ConfigData(ProfileManager.profileConfigFile(profile));
        }

        eventBus.post(new ProfileChanged());
    }

    @Override
    public <T extends Config> T getConfig(Class<T> clazz) {
        if (!Modifier.isPublic(clazz.getModifiers())) {
            throw new RuntimeException("Non-public configuration classes can't have default methods invoked");
        }

        var t = (T) Proxy.newProxyInstance(
                clazz.getClassLoader(),
                new Class<?>[]{clazz},
                handler
        );

        return t;
    }

    @Override
    public List<String> getConfigurationKeys(String prefix) {
        return configProfile.keySet().stream()
                .filter(k -> k.startsWith(prefix))
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getRSProfileConfigurationKeys(String group, String profile, String keyPrefix) {
        if (profile == null) {
            return Collections.emptyList();
        }

        assert profile.startsWith(RSPROFILE_GROUP);

        var prefix = group + "." + profile + "." + keyPrefix;
        return rsProfileConfigProfile.keySet().stream()
                .filter(k -> k.startsWith(prefix))
                .map(k -> splitKey(k)[KEY_SPLITTER_KEY])
                .collect(Collectors.toList());
    }

    private String getConfiguration(ConfigData configData, String groupName, String rsProfile, String key) {
        return configData.getProperty(getWholeKey(groupName, rsProfile, key));
    }

    @Override
    public String getConfiguration(String groupName, String key) {
        return getConfiguration(configProfile, groupName, null, key);
    }

    @Override
    public String getConfiguration(String groupName, String profile, String key) {
        if (profile != null) {
            return getConfiguration(rsProfileConfigProfile, groupName, profile, key);
        } else {
            return getConfiguration(configProfile, groupName, null, key);
        }
    }

    @Override
    public <T> T getConfiguration(String groupName, String key, Type clazz) {
        return getConfiguration(groupName, null, key, clazz);
    }

    public String getRSProfileConfiguration(String groupName, String key) {
        var rsProfileKey = this.rsProfileKey;
        if (rsProfileKey == null) {
            return null;
        }

        return getConfiguration(rsProfileConfigProfile, groupName, rsProfileKey, key);
    }

    public <T> T getRSProfileConfiguration(String groupName, String key, Type clazz) {
        var rsProfileKey = this.rsProfileKey;
        if (rsProfileKey == null) {
            return null;
        }

        return getConfiguration(groupName, rsProfileKey, key, clazz);
    }

    @Override
    public <T> T getConfiguration(String groupName, String profile, String key, Type type) {
        var value = getConfiguration(groupName, profile, key);
        if (!Strings.isNullOrEmpty(value)) {
            try {
                return (T) stringToObject(value, type);
            } catch (Exception e) {
                log.warn("Unable to unmarshal {} ", getWholeKey(groupName, profile, key), e);
            }
        }
        return null;
    }

    private void setConfiguration(ConfigData configData, String groupName, String profile, String key, String value) {
        if (Strings.isNullOrEmpty(groupName) || Strings.isNullOrEmpty(key) || key.indexOf(':') != -1 || key.startsWith("$")) {
            throw new IllegalArgumentException();
        }

        assert !key.startsWith(RSPROFILE_GROUP + ".");
        var wholeKey = getWholeKey(groupName, profile, key);
        var oldValue = configData.setProperty(wholeKey, value);

        if (Objects.equals(oldValue, value)) {
            return;
        }

        log.debug("Setting configuration value for {} to {}", wholeKey, value);
        handler.invalidate();

        var configChanged = new ConfigChanged();
        configChanged.setGroup(groupName);
        configChanged.setProfile(profile);
        configChanged.setKey(key);
        configChanged.setOldValue(oldValue);
        configChanged.setNewValue(value);

        eventBus.post(configChanged);
    }

    @Override
    public void setConfiguration(String groupName, String profile, String key, String value) {
        if (profile != null) {
            setConfiguration(rsProfileConfigProfile, groupName, profile, key, value);
        } else {
            setConfiguration(configProfile, groupName, null, key, value);
        }
    }

    @Override
    public void setConfiguration(String groupName, String key, String value) {
        setConfiguration(configProfile, groupName, null, key, value);
    }

    @Override
    public <T> void setConfiguration(String groupName, String profile, String key, T value) {
        setConfiguration(groupName, profile, key, objectToString(value));
    }

    @Override
    public <T> void setConfiguration(String groupName, String key, T value) {
        if (value instanceof Consumer) {
            return;
        }

        setConfiguration(groupName, null, key, value);
    }

    @Override
    public <T> void setRSProfileConfiguration(String groupName, String key, T value) {
        var rsProfileKey = this.rsProfileKey;
        if (rsProfileKey == null) {
            if (client == null) {
                log.warn("trying to use profile without injected client");
                return;
            }

            String displayName = null;
            var p = client.getLocalPlayer();
            if (p == null) {
                log.warn("trying to create profile without display name");
            } else {
                displayName = p.getName();
            }

            var prof = findRSProfile(getRSProfiles(), client.getAccountHash(), RuneScapeProfileType.getCurrent(client), displayName, true);
            if (prof == null) {
                log.warn("trying to create a profile while not logged in");
                return;
            }

            rsProfileKey = prof.getKey();
            this.rsProfileKey = rsProfileKey;

            log.debug("RS profile changed to {}", rsProfileKey);
            eventBus.post(new RuneScapeProfileChanged());
        }

        setConfiguration(groupName, rsProfileKey, key, value);
    }

    private void unsetConfiguration(ConfigData configData, String groupName, String profile, String key) {
        assert !key.startsWith(RSPROFILE_GROUP + ".");
        var wholeKey = getWholeKey(groupName, profile, key);
        var oldValue = configData.unset(wholeKey);

        if (oldValue == null) {
            return;
        }

        log.debug("Unsetting configuration value for {}", wholeKey);
        handler.invalidate();

        var configChanged = new ConfigChanged();
        configChanged.setGroup(groupName);
        configChanged.setProfile(profile);
        configChanged.setKey(key);
        configChanged.setOldValue(oldValue);

        eventBus.post(configChanged);
    }

    @Override
    public void unsetConfiguration(String groupName, String profile, String key) {
        if (profile != null) {
            unsetConfiguration(rsProfileConfigProfile, groupName, profile, key);
        } else {
            unsetConfiguration(configProfile, groupName, null, key);
        }
    }

    @Override
    public void unsetConfiguration(String groupName, String key) {
        unsetConfiguration(configProfile, groupName, null, key);
    }

    @Override
    public void unsetRSProfileConfiguration(String groupName, String key) {
        var rsProfileKey = this.rsProfileKey;
        if (rsProfileKey == null) {
            return;
        }

        unsetConfiguration(rsProfileConfigProfile, groupName, rsProfileKey, key);
    }

    @Override
    public ConfigDescriptor getConfigDescriptor(Config configurationProxy) {
        var inter = configurationProxy.getClass().getInterfaces()[0];
        var group = inter.getAnnotation(ConfigGroup.class);

        if (group == null) {
            throw new IllegalArgumentException("Not a config group");
        }

        final var sections = getAllDeclaredInterfaceFields(inter).stream()
                .filter(m -> m.isAnnotationPresent(ConfigSection.class) && m.getType() == String.class)
                .map(m ->
                {
                    try {
                        return new ConfigSectionDescriptor(
                                String.valueOf(m.get(inter)),
                                m.getDeclaredAnnotation(ConfigSection.class)
                        );
                    } catch (IllegalAccessException e) {
                        log.warn("Unable to load section {}::{}", inter.getSimpleName(), m.getName());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .sorted((a, b) -> ComparisonChain.start()
                        .compare(a.getSection().position(), b.getSection().position())
                        .compare(a.getSection().name(), b.getSection().name())
                        .result())
                .collect(Collectors.toList());

        final var titles = getAllDeclaredInterfaceFields(inter).stream()
                .filter(m -> m.isAnnotationPresent(ConfigTitle.class) && m.getType() == String.class)
                .map(m ->
                {
                    try {
                        return new ConfigTitleDescriptor(
                                String.valueOf(m.get(inter)),
                                m.getDeclaredAnnotation(ConfigTitle.class)
                        );
                    } catch (IllegalAccessException e) {
                        log.warn("Unable to load title {}::{}", inter.getSimpleName(), m.getName());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .sorted((a, b) -> ComparisonChain.start()
                        .compare(a.getTitle().position(), b.getTitle().position())
                        .compare(a.getTitle().name(), b.getTitle().name())
                        .result())
                .collect(Collectors.toList());

        final var items = Arrays.stream(inter.getMethods())
                .filter(m -> m.getParameterCount() == 0 && m.isAnnotationPresent(ConfigItem.class))
                .map(m -> new ConfigItemDescriptor(
                        m.getDeclaredAnnotation(ConfigItem.class),
                        m.getGenericReturnType(),
                        m.getDeclaredAnnotation(Range.class),
                        m.getDeclaredAnnotation(Alpha.class),
                        m.getDeclaredAnnotation(Units.class)
                ))
                .sorted((a, b) -> ComparisonChain.start()
                        .compare(a.getItem().position(), b.getItem().position())
                        .compare(a.getItem().name(), b.getItem().name())
                        .result())
                .collect(Collectors.toList());

        return new ConfigDescriptor(group, sections, titles, items);
    }

    @Override
    public <T extends Config> void setDefaultConfiguration(T proxy, boolean override) {
        var clazz = proxy.getClass().getInterfaces()[0];
        var group = clazz.getAnnotation(ConfigGroup.class);

        if (group == null) {
            return;
        }

        for (var method : getAllDeclaredInterfaceMethods(clazz)) {
            var item = method.getAnnotation(ConfigItem.class);

            // only apply default configuration for methods which read configuration (0 args)
            if (item == null || method.getParameterCount() != 0) {
                continue;
            }

            if (method.getReturnType().isAssignableFrom(Consumer.class)) {
                Object defaultValue;
                try {
                    defaultValue = ConfigInvocationHandler.callDefaultMethod(proxy, method, null);
                } catch (Throwable ex) {
                    log.warn(null, ex);
                    continue;
                }

                log.debug("Registered consumer: {}.{}", group.value(), item.keyName());
                consumers.put(group.value() + "." + item.keyName(), (Consumer) defaultValue);
            } else {
                if (!method.isDefault()) {
                    if (override) {
                        var current = getConfiguration(group.value(), item.keyName());
                        // only unset if already set
                        if (current != null) {
                            unsetConfiguration(group.value(), item.keyName());
                        }
                    }
                    continue;
                }

                if (!override) {
                    // This checks if it is set and is also unmarshallable to the correct type; so
                    // we will overwrite invalid config values with the default
                    var current = getConfiguration(group.value(), item.keyName(), method.getGenericReturnType());
                    if (current != null) {
                        continue; // something else is already set
                    }
                }

                Object defaultValue;
                try {
                    defaultValue = ConfigInvocationHandler.callDefaultMethod(proxy, method, null);
                } catch (Throwable ex) {
                    log.warn(null, ex);
                    continue;
                }

                var current = getConfiguration(group.value(), item.keyName());
                var valueString = objectToString(defaultValue);
                // null and the empty string are treated identically in sendConfig and treated as an unset
                // If a config value defaults to "" and the current value is null, it will cause an extra
                // unset to be sent, so treat them as equal
                if (Objects.equals(current, valueString) || (Strings.isNullOrEmpty(current) && Strings.isNullOrEmpty(valueString))) {
                    continue; // already set to the default value
                }

                log.debug("Setting default configuration value for {}.{} to {}", group.value(), item.keyName(), defaultValue);

                if (Strings.isNullOrEmpty(valueString)) {
                    unsetConfiguration(group.value(), item.keyName());
                } else {
                    setConfiguration(group.value(), item.keyName(), valueString);
                }
            }
        }
    }

    @Override
    public Object stringToObject(String str, Type type) {
        if (type == boolean.class || type == Boolean.class) {
            return Boolean.parseBoolean(str);
        }
        if (type == int.class || type == Integer.class) {
            return Integer.parseInt(str);
        }
        if (type == long.class || type == Long.class) {
            return Long.parseLong(str);
        }
        if (type == double.class || type == Double.class) {
            return Double.parseDouble(str);
        }
        if (type == Color.class) {
            return ColorUtil.fromString(str);
        }
        if (type == Dimension.class) {
            var splitStr = str.split("x");
            var width = Integer.parseInt(splitStr[0]);
            var height = Integer.parseInt(splitStr[1]);
            return new Dimension(width, height);
        }
        if (type == Point.class) {
            var splitStr = str.split(":");
            var width = Integer.parseInt(splitStr[0]);
            var height = Integer.parseInt(splitStr[1]);
            return new Point(width, height);
        }
        if (type == Rectangle.class) {
            var splitStr = str.split(":");
            var x = Integer.parseInt(splitStr[0]);
            var y = Integer.parseInt(splitStr[1]);
            var width = Integer.parseInt(splitStr[2]);
            var height = Integer.parseInt(splitStr[3]);
            return new Rectangle(x, y, width, height);
        }
        if (type instanceof Class && ((Class<?>) type).isEnum()) {
            return Enum.valueOf((Class<? extends Enum>) type, str);
        }
        if (type == Instant.class) {
            return Instant.parse(str);
        }
        if (type == Keybind.class || type == ModifierlessKeybind.class) {
            var splitStr = str.split(":");
            var code = Integer.parseInt(splitStr[0]);
            var mods = Integer.parseInt(splitStr[1]);
            if (type == ModifierlessKeybind.class) {
                return new ModifierlessKeybind(code, mods);
            }
            return new Keybind(code, mods);
        }
        if (type == WorldPoint.class) {
            var splitStr = str.split(":");
            var x = Integer.parseInt(splitStr[0]);
            var y = Integer.parseInt(splitStr[1]);
            var plane = Integer.parseInt(splitStr[2]);
            return new WorldPoint(x, y, plane);
        }
        if (type == Duration.class) {
            return Duration.ofMillis(Long.parseLong(str));
        }
        if (type == byte[].class) {
            return Base64.getUrlDecoder().decode(str);
        }
        if (type == Loadout.class) {
            var loadoutCfg = gson.fromJson(str, LoadoutImpl.class);
            LoadoutBuilder builder;
            if (loadoutCfg == null) {
                builder = loadoutFactory.newBuilder();
            } else {
                builder = loadoutFactory.fromLoadout(loadoutCfg);
            }

            return builder.build();
        }
        if (type == ItemConfig.class) {
            return gson.fromJson(str, ItemConfig.class);
        }
        if (type instanceof ParameterizedType) {
            var parameterizedType = (ParameterizedType) type;
            if (parameterizedType.getRawType() == Set.class) {
                Set set = gson.fromJson(str, parameterizedType);
                return set.stream()
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet());
            } else {
                return gson.fromJson(str, type);
            }
        }
        if (type instanceof Class) {
            var clazz = (Class<?>) type;
            var configSerializer = clazz.getAnnotation(ConfigSerializer.class);
            if (configSerializer != null) {
                var serializerClass = configSerializer.value();
                var serializer = serializers.get(type);
                if (serializer == null) {
                    // Guice holds references to all jitted types.
                    // To allow class unloading, use a temporary child injector
                    // and use it to get the instance, and cache it a weak map.
                    serializer = Static.injector
                            .createChildInjector()
                            .getInstance(serializerClass);
                    serializers.put(type, serializer);
                }
                return serializer.deserialize(str);
            }
        }

        if (type == ConfigImageResource.class) {
            return gson.fromJson(str, ConfigImageResource.class);
        }

        return str;
    }

    @Override
    public String objectToString(Object object) {
        if (object instanceof Color) {
            return String.valueOf(((Color) object).getRGB());
        }
        if (object instanceof Enum) {
            return ((Enum) object).name();
        }
        if (object instanceof Dimension) {
            var d = (Dimension) object;
            return d.width + "x" + d.height;
        }
        if (object instanceof Point) {
            var p = (Point) object;
            return p.x + ":" + p.y;
        }
        if (object instanceof Rectangle) {
            var r = (Rectangle) object;
            return r.x + ":" + r.y + ":" + r.width + ":" + r.height;
        }
        if (object instanceof Instant) {
            return ((Instant) object).toString();
        }
        if (object instanceof Keybind) {
            var k = (Keybind) object;
            return k.getKeyCode() + ":" + k.getModifiers();
        }
        if (object instanceof WorldPoint) {
            var wp = (WorldPoint) object;
            return wp.getX() + ":" + wp.getY() + ":" + wp.getPlane();
        }
        if (object instanceof Duration) {
            return Long.toString(((Duration) object).toMillis());
        }
        if (object instanceof byte[]) {
            return Base64.getUrlEncoder().encodeToString((byte[]) object);
        }
        if (object instanceof Set) {
            return gson.toJson(object, Set.class);
        }
        if (object instanceof Loadout) {
            return gson.toJson(object, LoadoutImpl.class);
        }
        if (object instanceof ItemConfig) {
            return gson.toJson(object, ItemConfig.class);
        }
        if (object instanceof List) {
            return gson.toJson(object, List.class);
        }
        if (object instanceof ConfigImageResource) {
            return gson.toJson(object, ConfigImageResource.class);
        }
        if (object != null) {
            var configSerializer = object.getClass().getAnnotation(ConfigSerializer.class);
            if (configSerializer != null) {
                var serializerClass = configSerializer.value();
                Serializer serializer = serializers.get(serializerClass);
                if (serializer == null) {
                    serializer = Static.injector
                            .createChildInjector()
                            .getInstance(serializerClass);
                    serializers.put(serializerClass, serializer);
                }
                return serializer.serialize(object);
            }
        }
        return object == null ? null : object.toString();
    }

    public void switchProfile(ConfigProfile newProfile) {
        if (newProfile.getId() == profile.getId()) {
            log.warn("switching to already-active profile!");
            return;
        }

        sendConfig();

        var newData = new ConfigData(ProfileManager.profileConfigFile(newProfile));
        Set<String> allKeys = new HashSet<>(newData.keySet());

        ConfigData oldData;
        synchronized (this) {
            handler.invalidate();
            oldData = configProfile;
            profile = newProfile;
            configProfile = newData;
        }

        allKeys.addAll(oldData.keySet());

        for (var wholeKey : allKeys) {
            var split = splitKey(wholeKey);
            if (split == null) {
                continue;
            }

            var groupName = split[KEY_SPLITTER_GROUP];
            var profile = split[KEY_SPLITTER_PROFILE];
            var key = split[KEY_SPLITTER_KEY];
            var oldValue = oldData.getProperty(wholeKey);
            var newValue = newData.getProperty(wholeKey);

            if (Objects.equals(oldValue, newValue)) {
                continue;
            }

            log.debug("Loading configuration value {}: {}", wholeKey, newValue);

            var configChanged = new ConfigChanged();
            configChanged.setGroup(groupName);
            configChanged.setProfile(profile);
            configChanged.setKey(key);
            configChanged.setOldValue(oldValue);
            configChanged.setNewValue(newValue);
            eventBus.post(configChanged);
        }

        eventBus.post(new ProfileChanged());
    }

    private synchronized RuneScapeProfile findRSProfile(List<RuneScapeProfile> profiles, long accountHash, RuneScapeProfileType type, String displayName, boolean create) {
        if (accountHash == RuneScapeProfile.ACCOUNT_HASH_INVALID) {
            return null;
        }

        var matches = profiles.stream()
                .filter(p -> p.getType() == type && accountHash == p.getAccountHash())
                .collect(Collectors.toList());

        if (matches.size() > 1) {
            log.warn("multiple matching profiles, choosing {}, ignoring {}", matches.get(0), matches.subList(1, matches.size()));
        }

        if (!matches.isEmpty()) {
            return matches.get(0);
        }

        if (!create) {
            return null;
        }

        // generate the new key deterministically so if you "create" the same profile on 2 different clients it doesn't duplicate
        var keys = profiles.stream().map(RuneScapeProfile::getKey).collect(Collectors.toSet());
        var key = new byte[]{
                (byte) accountHash,
                (byte) (accountHash >> 8),
                (byte) (accountHash >> 16),
                (byte) (accountHash >> 24),
                (byte) (accountHash >> 32),
                (byte) (accountHash >> 40),
        };
        key[0] += (byte) type.ordinal();
        for (var i = 0; i < 0xFF; i++, key[1]++) {
            var keyStr = RSPROFILE_GROUP + "." + Base64.getUrlEncoder().encodeToString(key);
            if (!keys.contains(keyStr)) {
                log.info("creating new profile {} for account hash {} ({})", keyStr, accountHash, type);

                setConfiguration(RSPROFILE_GROUP, keyStr, RSPROFILE_ACCOUNT_HASH, accountHash);
                setConfiguration(RSPROFILE_GROUP, keyStr, RSPROFILE_TYPE, type);
                if (displayName != null) {
                    setConfiguration(RSPROFILE_GROUP, keyStr, RSPROFILE_DISPLAY_NAME, displayName);
                }
                return new RuneScapeProfile(displayName, type, accountHash, keyStr);
            }
        }
        throw new RuntimeException("too many rs profiles");
    }

    public List<RuneScapeProfile> getRSProfiles() {
        var prefix = RSPROFILE_GROUP + "." + RSPROFILE_GROUP + ".";
        Set<String> profileKeys = new HashSet<>();
        for (var key : rsProfileConfigProfile.keySet()) {
            if (!key.startsWith(prefix)) {
                continue;
            }

            var split = splitKey(key);
            if (split == null) {
                continue;
            }

            profileKeys.add(split[KEY_SPLITTER_PROFILE]);
        }

        return profileKeys.stream()
                .map(key ->
                {
                    Long accid = getConfiguration(RSPROFILE_GROUP, key, RSPROFILE_ACCOUNT_HASH, long.class);

                    return new RuneScapeProfile(
                            getConfiguration(RSPROFILE_GROUP, key, RSPROFILE_DISPLAY_NAME),
                            getConfiguration(RSPROFILE_GROUP, key, RSPROFILE_TYPE, RuneScapeProfileType.class),
                            accid == null ? RuneScapeProfile.ACCOUNT_HASH_INVALID : accid,
                            key
                    );
                })
                .sorted(Comparator.comparing(RuneScapeProfile::getKey))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public Consumer<? super Plugin> getConsumer(String configGroup, String keyName) {
        return consumers.getOrDefault(configGroup + "." + keyName, (p) -> log.error("Failed to retrieve consumer with name {}.{}", configGroup, keyName));
    }

    @Override
    public String getRSProfileKey() {
        return rsProfileKey;
    }

    @Override
    public void importAndMigrate(ProfileLock lock, File from, ConfigProfile targetProfile) {
        var migratingData = new ConfigData(from);
        var configData = new ConfigData(ProfileManager.profileConfigFile(targetProfile));

        log.debug("Importing profile from {}", from);

        Set<String> rsProfileKeys = new HashSet<>();
        List<Map.Entry<String, String>> rsProfileEntries = new ArrayList<>();

        var keys = 0;
        for (var entry : migratingData.get().entrySet()) {
            var split = splitKey(entry.getKey());
            if (split == null) {
                continue;
            }

            var profile = split[KEY_SPLITTER_PROFILE];

            if (profile != null) {
                rsProfileKeys.add(profile);
                rsProfileEntries.add(entry);
            } else {
                configData.setProperty(entry.getKey(), entry.getValue());
                ++keys;
            }
        }

        if (!rsProfileKeys.isEmpty()) {
            Map<String, String> oldToNewRSProfile = new HashMap<>();
            var existingProfiles = getRSProfiles();
            for (var oldKey : rsProfileKeys) {
                try {
                    var strHash = migratingData.getProperty(getWholeKey(RSPROFILE_GROUP, oldKey, RSPROFILE_ACCOUNT_HASH));
                    var strType = migratingData.getProperty(getWholeKey(RSPROFILE_GROUP, oldKey, RSPROFILE_TYPE));
                    if (!Strings.isNullOrEmpty(strHash) && !Strings.isNullOrEmpty(strType)) {
                        var accHash = Long.parseLong(strHash);
                        var type = RuneScapeProfileType.valueOf(strType);

                        var newProfile = findRSProfile(existingProfiles, accHash, type, null, true);
                        if (newProfile != null) {
                            existingProfiles.add(newProfile);
                            oldToNewRSProfile.put(oldKey, newProfile.getKey());
                            log.info("importing rsprofile \"{}\" as \"{}\"", oldKey, newProfile.getKey());
                            continue;
                        }
                    }
                    log.info("not importing rsprofile key \"{}\" (hash={} type={})", oldKey, strHash, strType);
                } catch (IllegalArgumentException e) {
                    log.info("failed to unmarshal imported rsprofile data for key \"{}\"", oldKey, e);
                }
            }

            for (var entry : rsProfileEntries) {
                var split = splitKey(entry.getKey());
                assert split != null;
                var profile = split[KEY_SPLITTER_PROFILE];
                profile = oldToNewRSProfile.get(profile);
                if (profile != null && getConfiguration(split[KEY_SPLITTER_GROUP], profile, split[KEY_SPLITTER_KEY]) == null) {
                    setConfiguration(split[KEY_SPLITTER_GROUP], profile, split[KEY_SPLITTER_KEY], entry.getValue());
                }
            }
        }

        configData.patch(configData.swapChanges());

        rsProfile = updateProfile(lock, rsProfile);
        saveConfiguration(rsProfile, rsProfileConfigProfile);

        log.info("Finished importing {} keys", keys);
    }

    private void updateRSProfile() {
        if (client == null) {
            return;
        }

        var profiles = getRSProfiles();
        var prof = findRSProfile(profiles, client.getAccountHash(), RuneScapeProfileType.getCurrent(client), null, false);

        var key = prof == null ? null : prof.getKey();
        if (Objects.equals(key, rsProfileKey)) {
            return;
        }
        rsProfileKey = key;

        log.debug("RS profile changed to {}", key);
        eventBus.post(new RuneScapeProfileChanged());
    }

    private Collection<Method> getAllDeclaredInterfaceMethods(Class<?> clazz) {
        Collection<Method> methods = new HashSet<>();
        var interfaces = new Stack<Class<?>>();
        interfaces.push(clazz);

        while (!interfaces.isEmpty()) {
            var interfaze = interfaces.pop();
            Collections.addAll(methods, interfaze.getDeclaredMethods());
            Collections.addAll(interfaces, interfaze.getInterfaces());
        }

        return methods;
    }

    private Collection<Field> getAllDeclaredInterfaceFields(Class<?> clazz) {
        Collection<Field> methods = new HashSet<>();
        var interfaces = new Stack<Class<?>>();
        interfaces.push(clazz);

        while (!interfaces.isEmpty()) {
            var interfaze = interfaces.pop();
            Collections.addAll(methods, interfaze.getDeclaredFields());
            Collections.addAll(interfaces, interfaze.getInterfaces());
        }

        return methods;
    }

    private void saveConfiguration(ConfigProfile profile, ConfigData data) {
        var patch = data.swapChanges();

        if (patch.isEmpty()) {
            log.debug("No changes to save for profile {}", profile.getName());
            return;
        }

        log.debug("Saving profile {} (patch size: {})", profile.getName(), patch.size());

        data.patch(patch);
    }

    @Override
    public void sendConfig() {
        eventBus.post(new ConfigSync());

        try (var lock = profileManager.lock()) {
            // since we hold references to profiles outside of the lock, they are stale.
            // fetch the latest version.
            profile = updateProfile(lock, profile);
            rsProfile = updateProfile(lock, rsProfile);

            saveConfiguration(profile, configProfile);
            saveConfiguration(rsProfile, rsProfileConfigProfile);
        }

        log.debug("Saved configuration");
    }

    @Subscribe
    private void onAccountHashChanged(AccountHashChanged ev) {
        updateRSProfile();
    }

    @Subscribe
    private void onWorldChanged(WorldChanged ev) {
        updateRSProfile();
    }

    @Subscribe
    private void onPlayerChanged(PlayerChanged ev) {
        if (ev.getPlayer() == client.getLocalPlayer()) {
            var name = ev.getPlayer().getName();
            setRSProfileConfiguration(RSPROFILE_GROUP, RSPROFILE_DISPLAY_NAME, name);
        }
    }

    @Subscribe
    private void onRuneScapeProfileChanged(RuneScapeProfileChanged ev) {
        ConfigProfile switchToProfile = null;
        try (var lock = profileManager.lock()) {
            for (final var lockProfile : lock.getProfiles()) {
                final var get = lockProfile.getDefaultForRsProfiles();
                if (get != null && get.contains(rsProfileKey)) {
                    switchToProfile = lockProfile;

                    // change active profile
                    lock.getProfiles().forEach(p -> p.setActive(false));
                    switchToProfile.setActive(true);
                    lock.dirty();
                    break;
                }
            }
        }

        if (switchToProfile != null) {
            log.debug("Switching to default profile {} for rsprofile {}", switchToProfile.getName(), rsProfileKey);
            switchProfile(switchToProfile);
        }
    }

    @Subscribe(
            // run after plugins, in the event they save config on shutdown
            priority = Integer.MIN_VALUE
    )
    private void onClientShutdown(ClientShutdown e) {
        e.waitFor(shutDownExecutor.submit(this::sendConfig));
    }
}
