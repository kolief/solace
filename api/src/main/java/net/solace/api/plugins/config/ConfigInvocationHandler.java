package net.solace.api.plugins.config;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Objects;
import net.runelite.client.util.ReflectUtil;
import net.solace.api.plugins.config.ConfigGroup;
import net.solace.api.plugins.config.ConfigItem;
import net.solace.api.plugins.config.ConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigInvocationHandler
implements InvocationHandler {
    private static final Logger log = LoggerFactory.getLogger(ConfigInvocationHandler.class);
    private static final Object NULL = new Object();
    private final ConfigManager manager;
    private final Cache<Method, Object> cache = CacheBuilder.newBuilder().maximumSize(256L).build();

    public ConfigInvocationHandler(ConfigManager manager) {
        this.manager = manager;
    }

    public static Object callDefaultMethod(Object proxy, Method method, Object[] args) throws Throwable {
        Class<?> declaringClass = method.getDeclaringClass();
        return ReflectUtil.privateLookupIn(declaringClass).unreflectSpecial(method, declaringClass).bindTo(proxy).invokeWithArguments(args);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object defaultValue;
        Object cachedValue;
        if (args == null && (cachedValue = this.cache.getIfPresent(method)) != null) {
            return cachedValue == NULL ? null : cachedValue;
        }
        Class<?> iface = proxy.getClass().getInterfaces()[0];
        if ("toString".equals(method.getName()) && args == null) {
            return iface.getSimpleName();
        }
        if ("hashCode".equals(method.getName()) && args == null) {
            return System.identityHashCode(proxy);
        }
        if ("equals".equals(method.getName()) && args != null && args.length == 1) {
            return proxy == args[0];
        }
        ConfigGroup group = iface.getAnnotation(ConfigGroup.class);
        ConfigItem item = method.getAnnotation(ConfigItem.class);
        if (group == null) {
            log.warn("Configuration proxy class {} has no @ConfigGroup!", proxy.getClass());
            return null;
        }
        if (item == null) {
            log.warn("Configuration method {} has no @ConfigItem!", (Object)method);
            return null;
        }
        if (args == null) {
            log.trace("cache miss (size: {}, group: {}, key: {})", new Object[]{this.cache.size(), group.value(), item.keyName()});
            String value = this.manager.getConfiguration(group.value(), item.keyName());
            if (value == null) {
                if (method.isDefault()) {
                    Object defaultValue2 = ConfigInvocationHandler.callDefaultMethod(proxy, method, null);
                    this.cache.put(method, defaultValue2 == null ? NULL : defaultValue2);
                    return defaultValue2;
                }
                this.cache.put(method, NULL);
                return null;
            }
            try {
                Object objectValue = this.manager.stringToObject(value, method.getGenericReturnType());
                this.cache.put(method, objectValue == null ? NULL : objectValue);
                return objectValue;
            }
            catch (Exception e) {
                log.warn("Unable to unmarshal {}.{} ", new Object[]{group.value(), item.keyName(), e});
                if (method.isDefault()) {
                    return ConfigInvocationHandler.callDefaultMethod(proxy, method, null);
                }
                return null;
            }
        }
        if (args.length != 1) {
            throw new RuntimeException("Invalid number of arguments to configuration method");
        }
        Object newValue = args[0];
        Class<?> type = method.getParameterTypes()[0];
        Object oldValue = this.manager.getConfiguration(group.value(), item.keyName(), type);
        if (Objects.equals(oldValue, newValue)) {
            return null;
        }
        if (method.isDefault() && Objects.equals(newValue, defaultValue = ConfigInvocationHandler.callDefaultMethod(proxy, method, args))) {
            this.manager.unsetConfiguration(group.value(), item.keyName());
            return null;
        }
        if (newValue == null) {
            this.manager.unsetConfiguration(group.value(), item.keyName());
        } else {
            String newValueStr = this.manager.objectToString(newValue);
            this.manager.setConfiguration(group.value(), item.keyName(), newValueStr);
        }
        return null;
    }

    public void invalidate() {
        log.trace("cache invalidate");
        this.cache.invalidateAll();
    }
}

