package net.solace.loader.local;

import com.google.gson.Gson;
import net.solace.sdn.plugins.version.VersionPackage;
import net.solace.sdn.plugins.version.mappings.ClassMapping;
import net.solace.sdn.plugins.version.mappings.MappingContainer;
import net.solace.sdn.plugins.version.mappings.StaticFieldMapping;
import net.solace.sdn.plugins.version.mappings.StaticMethodMapping;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class LocalVersionPackageLoader {
    private static final String DEFAULT_RESOURCE = "/net/solace/loader/version-package.json";
    private static final Gson GSON = new Gson();

    private LocalVersionPackageLoader() {
    }

    public static VersionPackage load() {
        var override = System.getenv("SOLACE_VP_PATH");
        if (override != null && !override.isBlank()) {
            return loadFromPath(Path.of(override));
        }
        return loadFromClasspath(DEFAULT_RESOURCE);
    }

    private static VersionPackage loadFromPath(Path path) {
        try (var reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            return toVersionPackage(GSON.fromJson(reader, VersionPackageJson.class));
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load version package from " + path, e);
        }
    }

    private static VersionPackage loadFromClasspath(String resource) {
        try (var in = LocalVersionPackageLoader.class.getResourceAsStream(resource)) {
            if (in == null) {
                throw new IllegalStateException("Missing bundled version package: " + resource);
            }
            try (var reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
                return toVersionPackage(GSON.fromJson(reader, VersionPackageJson.class));
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load version package from " + resource, e);
        }
    }

    private static VersionPackage toVersionPackage(VersionPackageJson json) {
        if (json == null || json.classMappings == null) {
            throw new IllegalStateException("Invalid version package JSON");
        }
        var mappings = new MappingContainer(
                json.classMappings,
                json.staticFields != null ? json.staticFields : java.util.List.of(),
                json.staticMethods != null ? json.staticMethods : java.util.List.of(),
                json.garbage != null ? json.garbage : java.util.Map.of()
        );
        return new VersionPackage(mappings, json.runeLiteVersion, json.runeLiteCommit);
    }

    private static class VersionPackageJson {
        java.util.List<ClassMapping> classMappings;
        java.util.List<StaticFieldMapping> staticFields;
        java.util.List<StaticMethodMapping> staticMethods;
        java.util.Map<String, String> garbage;
        String runeLiteVersion;
        String runeLiteCommit;
    }
}
