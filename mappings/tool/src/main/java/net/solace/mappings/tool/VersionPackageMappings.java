package net.solace.mappings.tool;

import net.solace.sdn.plugins.version.mappings.ClassMapping;
import net.solace.sdn.plugins.version.mappings.FieldMapping;
import net.solace.sdn.plugins.version.mappings.MethodMapping;
import net.solace.sdn.plugins.version.mappings.StaticFieldMapping;
import net.solace.sdn.plugins.version.mappings.StaticMethodMapping;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class VersionPackageMappings {
    private final List<ClassMapping> classMappings;
    private final List<StaticFieldMapping> staticFields;
    private final List<StaticMethodMapping> staticMethods;
    private final Map<String, String> garbage;

    public VersionPackageMappings(
            List<ClassMapping> classMappings,
            List<StaticFieldMapping> staticFields,
            List<StaticMethodMapping> staticMethods,
            Map<String, String> garbage
    ) {
        this.classMappings = List.copyOf(classMappings);
        this.staticFields = List.copyOf(staticFields);
        this.staticMethods = List.copyOf(staticMethods);
        this.garbage = Map.copyOf(garbage);
    }

    public static VersionPackageMappings empty() {
        return new VersionPackageMappings(List.of(), List.of(), List.of(), Map.of());
    }

    public List<ClassMapping> getClassMappings() {
        return classMappings;
    }

    public List<StaticFieldMapping> getStaticFields() {
        return staticFields;
    }

    public List<StaticMethodMapping> getStaticMethods() {
        return staticMethods;
    }

    public Map<String, String> getGarbage() {
        return garbage;
    }

    public static VersionPackageMappings merge(VersionPackageMappings... sources) {
        var classByName = new LinkedHashMap<String, ClassMapping>();
        var staticFields = new LinkedHashMap<String, StaticFieldMapping>();
        var staticMethods = new LinkedHashMap<String, StaticMethodMapping>();
        var garbage = new LinkedHashMap<String, String>();

        for (var source : sources) {
            for (var cls : source.classMappings) {
                var existing = classByName.get(cls.getName());
                if (existing == null) {
                    classByName.put(cls.getName(), cls);
                } else {
                    classByName.put(cls.getName(), new ClassMapping(
                            cls.getName(),
                            cls.getObfName().isBlank() ? existing.getObfName() : cls.getObfName(),
                            mergeFields(existing.getFields(), cls.getFields()),
                            mergeMethods(existing.getMethods(), cls.getMethods())
                    ));
                }
            }

            for (var field : source.staticFields) {
                staticFields.put(field.getName(), field);
            }
            for (var method : source.staticMethods) {
                staticMethods.put(method.getName(), method);
            }
            garbage.putAll(source.garbage);
        }

        return new VersionPackageMappings(
                new ArrayList<>(classByName.values()),
                new ArrayList<>(staticFields.values()),
                new ArrayList<>(staticMethods.values()),
                garbage
        );
    }

    private static List<FieldMapping> mergeFields(List<FieldMapping> existing, List<FieldMapping> incoming) {
        var byName = new LinkedHashMap<String, FieldMapping>();
        for (var field : existing) {
            byName.put(field.getName(), field);
        }
        for (var field : incoming) {
            byName.put(field.getName(), field);
        }
        return new ArrayList<>(byName.values());
    }

    private static List<MethodMapping> mergeMethods(List<MethodMapping> existing, List<MethodMapping> incoming) {
        var byName = new LinkedHashMap<String, MethodMapping>();
        for (var method : existing) {
            byName.put(method.getName(), method);
        }
        for (var method : incoming) {
            byName.put(method.getName(), method);
        }
        return new ArrayList<>(byName.values());
    }
}
