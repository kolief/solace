package net.solace.mappings.tool.canonical;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.solace.mappings.tool.VersionPackageMappings;
import net.solace.sdn.plugins.version.mappings.ClassMapping;
import net.solace.sdn.plugins.version.mappings.FieldMapping;
import net.solace.sdn.plugins.version.mappings.MethodMapping;
import net.solace.sdn.plugins.version.mappings.StaticFieldMapping;
import net.solace.sdn.plugins.version.mappings.StaticMethodMapping;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class CanonicalMappingConverter {
    private static final Gson GSON = new GsonBuilder().create();
    private static final Type CANONICAL_LIST = new TypeToken<List<CanonicalClass>>() {}.getType();

    private CanonicalMappingConverter() {
    }

    public static VersionPackageMappings fromJson(String json) {
        return convert(GSON.fromJson(json, CANONICAL_LIST));
    }

    public static VersionPackageMappings fromJsonFile(Path path) throws IOException {
        try (Reader reader = Files.newBufferedReader(path)) {
            return convert(GSON.fromJson(reader, CANONICAL_LIST));
        }
    }

    public static VersionPackageMappings convert(List<CanonicalClass> classes) {
        var classMappings = new ArrayList<ClassMapping>();
        var staticFields = new ArrayList<StaticFieldMapping>();
        var staticMethods = new ArrayList<StaticMethodMapping>();
        var garbage = new LinkedHashMap<String, String>();

        for (var canonicalClass : classes) {
            var className = resolveClassName(canonicalClass);
            var classObf = canonicalClass.obfuscatedName;
            if (classObf == null || classObf.isBlank()) {
                continue;
            }

            var fields = new ArrayList<FieldMapping>();
            var methods = new ArrayList<MethodMapping>();

            for (var field : canonicalClass.fields) {
                if (!isNamedMember(field.name)) {
                    continue;
                }

                if (field.isStatic && isCrossClassMember(field.owner, field.ownerObfuscatedName, className, classObf)) {
                    staticFields.add(new StaticFieldMapping(
                            field.name,
                            field.obfuscatedName,
                            blankTo(field.ownerObfuscatedName, classObf),
                            field.descriptor
                    ));
                } else {
                    fields.add(new FieldMapping(field.name, field.obfuscatedName, field.descriptor, field.isStatic));
                }

                addFieldGarbage(garbage, className, field);
            }

            for (var method : canonicalClass.methods) {
                if (!isNamedMember(method.name) || !isValidMethodMapping(method.name, method.obfuscatedName)) {
                    continue;
                }

                if (method.isStatic && isCrossClassMember(method.owner, method.ownerObfuscatedName, className, classObf)) {
                    staticMethods.add(new StaticMethodMapping(
                            method.name,
                            method.obfuscatedName,
                            blankTo(method.ownerObfuscatedName, classObf),
                            "",
                            method.descriptor
                    ));
                } else {
                    methods.add(new MethodMapping(method.name, method.obfuscatedName, method.descriptor, method.isStatic));
                }

                addMethodGarbage(garbage, className, method);
            }

            if (!fields.isEmpty() || !methods.isEmpty()) {
                classMappings.add(new ClassMapping(className, classObf, fields, methods));
            }
        }

        return new VersionPackageMappings(classMappings, staticFields, staticMethods, garbage);
    }

    private static String resolveClassName(CanonicalClass canonicalClass) {
        if (canonicalClass.name != null && !canonicalClass.name.isBlank()) {
            return canonicalClass.name;
        }

        String owner = null;
        for (var field : canonicalClass.fields) {
            if (isNamedMember(field.name) && field.owner != null && !field.owner.isBlank()) {
                owner = field.owner;
                break;
            }
        }
        if (owner == null) {
            for (var method : canonicalClass.methods) {
                if (isNamedMember(method.name) && method.owner != null && !method.owner.isBlank()) {
                    owner = method.owner;
                    break;
                }
            }
        }
        if (owner != null) {
            return owner;
        }

        return inferClassName(canonicalClass.obfuscatedName);
    }

    private static String inferClassName(String obfName) {
        var simple = obfName.substring(obfName.lastIndexOf('.') + 1);
        if (simple.isEmpty()) {
            return obfName;
        }
        return Character.toUpperCase(simple.charAt(0)) + simple.substring(1);
    }

    private static boolean isNamedMember(String name) {
        return name != null && !name.isBlank() && !"<init>".equals(name) && !"<clinit>".equals(name);
    }

    private static boolean isValidMethodMapping(String name, String obfuscatedName) {
        if (obfuscatedName == null || obfuscatedName.isBlank()) {
            return false;
        }
        if ("<init>".equals(obfuscatedName) || "<clinit>".equals(obfuscatedName)) {
            return "<init>".equals(name) || "<clinit>".equals(name);
        }
        return true;
    }

    private static boolean isCrossClassMember(String logicalOwner, String obfuscatedOwner, String className, String classObf) {
        if (logicalOwner != null && !logicalOwner.isBlank() && !logicalOwner.equals(className)) {
            return true;
        }
        return obfuscatedOwner != null && !obfuscatedOwner.isBlank() && !obfuscatedOwner.equals(classObf);
    }

    private static void addFieldGarbage(Map<String, String> garbage, String className, CanonicalField field) {
        Number decoder = field.setter != null ? field.setter : field.getter;
        if (decoder == null) {
            return;
        }
        var key = field.isStatic ? field.name : className + "." + field.name;
        garbage.put(key, decoder.toString());
    }

    private static void addMethodGarbage(Map<String, String> garbage, String className, CanonicalMethod method) {
        if (method.garbageValue == null) {
            return;
        }
        var key = method.isStatic ? method.name : className + "." + method.name;
        garbage.put(key, method.garbageValue.toString());
    }

    private static String blankTo(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }
}
