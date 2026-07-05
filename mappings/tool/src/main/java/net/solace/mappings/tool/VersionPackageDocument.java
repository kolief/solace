package net.solace.mappings.tool;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.solace.sdn.plugins.version.mappings.ClassMapping;
import net.solace.sdn.plugins.version.mappings.StaticFieldMapping;
import net.solace.sdn.plugins.version.mappings.StaticMethodMapping;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public final class VersionPackageDocument {
    public List<ClassMapping> classMappings;
    public List<StaticFieldMapping> staticFields;
    public List<StaticMethodMapping> staticMethods;
    public Map<String, String> garbage;
    public String runeLiteVersion;
    public String runeLiteCommit;

    public static VersionPackageDocument fromMappings(
            VersionPackageMappings mappings,
            String runeLiteVersion,
            String runeLiteCommit
    ) {
        var document = new VersionPackageDocument();
        document.classMappings = mappings.getClassMappings();
        document.staticFields = mappings.getStaticFields();
        document.staticMethods = mappings.getStaticMethods();
        document.garbage = mappings.getGarbage();
        document.runeLiteVersion = runeLiteVersion;
        document.runeLiteCommit = runeLiteCommit;
        return document;
    }
}

final class MappingJsonCodec {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private MappingJsonCodec() {
    }

    static String toJson(VersionPackageDocument document) {
        return GSON.toJson(document);
    }

    static VersionPackageMappings fromJsonFile(Path path) throws IOException {
        var json = Files.readString(path);
        var document = GSON.fromJson(json, VersionPackageDocument.class);
        return new VersionPackageMappings(
                document.classMappings != null ? document.classMappings : List.of(),
                document.staticFields != null ? document.staticFields : List.of(),
                document.staticMethods != null ? document.staticMethods : List.of(),
                document.garbage != null ? document.garbage : Map.of()
        );
    }

    static VersionPackageDocument readDocument(Path path) throws IOException {
        var json = Files.readString(path);
        return GSON.fromJson(json, VersionPackageDocument.class);
    }
}
