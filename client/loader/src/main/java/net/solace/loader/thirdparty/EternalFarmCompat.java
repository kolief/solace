package net.solace.loader.thirdparty;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class EternalFarmCompat {
    private static final String MAIN_CLASS_NAME = "net.eternalfarm.agent.Agent";
    private static final String MAIN_METHOD_NAME = "main";

    public static void init(String arg, ClassLoader classLoader) {
        try {
            var split = arg.split(",");
            if (split.length != 2) {
                return;
            }

            var jarPath = split[0];
            var jarFile = new File(jarPath);
            if (!jarFile.exists() || !jarFile.isFile() || !jarFile.getName().endsWith(".jar")) {
                return;
            }

            var jarUrl = jarFile.toURI().toURL();

            try (var efClassLoader = new URLClassLoader(new URL[]{jarUrl}, classLoader)) {
                var clazz = efClassLoader.loadClass(MAIN_CLASS_NAME);
                var mainMethod = clazz.getMethod(MAIN_METHOD_NAME, String[].class);
                var jwt = split[1];
                mainMethod.invoke(null, (Object) new String[]{jwt});
            } catch (ClassNotFoundException | IOException ignored) {
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        } catch (MalformedURLException ignored) {
        }
    }
}
