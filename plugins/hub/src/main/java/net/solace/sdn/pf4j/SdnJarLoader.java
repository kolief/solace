package net.solace.sdn.pf4j;

import org.pf4j.JarPluginLoader;
import org.pf4j.PluginDescriptor;
import org.pf4j.PluginManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static net.solace.loader.commons.Directories.CURRENT_TEMP_DIR;

public class SdnJarLoader extends JarPluginLoader {
    private final net.runelite.client.plugins.PluginManager runeLitePluginManager;

    public SdnJarLoader(PluginManager pluginManager, net.runelite.client.plugins.PluginManager runeLitePluginManager) {
        super(pluginManager);
        this.runeLitePluginManager = runeLitePluginManager;
    }

    public ClassLoader loadPlugin(Path pluginPath, PluginDescriptor pluginDescriptor) {
        var pluginClassLoader = new SdnClassLoader(
                pluginManager,
                pluginDescriptor,
                getClass().getClassLoader(),
                runeLitePluginManager
        );

        var file = pluginPath.toFile();
        Path tempPath;

        try {
            tempPath = createTempJar();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        var tempFile = tempPath.toFile();
        tempFile.deleteOnExit();

        try {
            Files.copy(file.toPath(), tempPath, REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        pluginClassLoader.addFile(tempFile);
        return pluginClassLoader;
    }

    private Path createTempJar() throws IOException {
        return Files.createTempFile(CURRENT_TEMP_DIR, "pf4j", ".jar");
    }
}