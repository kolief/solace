package net.solace.impl.reflection;

import lombok.RequiredArgsConstructor;
import net.solace.sdn.plugins.version.VersionPackage;
import org.objectweb.asm.Type;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class ReflectionManager {
    private static final Map<String, ReflField> FIELDS = new HashMap<>();
    private static final Map<String, ReflMethod> METHODS = new HashMap<>();

    public void load(ClassLoader classLoader, VersionPackage versionPackage) throws Exception {
        var mappingContainer = versionPackage.getMappings();
        var garbageValues = mappingContainer.getGarbage();

        var classes = mappingContainer.getClassMappings();
        for (var aClass : classes) {
            for (var field : aClass.getFields()) {
                var key = field.isStatic() ? field.getName() : aClass.getName() + "." + field.getName();
                var garbageValue = garbageValues.get(key);
                var decoder = mapDecoderValue(garbageValue, field.getTypeDesc());
                var reflField = new ReflField(
                        aClass.getObfName(),
                        aClass.getName(),
                        field.getObfName(),
                        field.getName(),
                        field.getTypeDesc(),
                        decoder,
                        field.isStatic()
                );

                reflField.initReflection(classLoader);

                if (reflField.isStatic()) {
                    FIELDS.put(reflField.getMappedName(), reflField);
                } else {
                    FIELDS.put(reflField.getOwnerMappedName() + "." + reflField.getMappedName(), reflField);
                }
            }

            for (var method : aClass.getMethods()) {
                if (method.isInit()) {
                    continue;
                }

                var key = method.isStatic() ? method.getName() : aClass.getName() + "." + method.getName();
                var garbageValue = garbageValues.get(key);
                var reflMethod = new ReflMethod(
                        aClass.getObfName(),
                        aClass.getName(),
                        method.getObfName(),
                        method.getName(),
                        method.getTypeDesc(),
                        garbageValue,
                        method.isStatic()
                );

                reflMethod.initReflection(classLoader);

                if (reflMethod.isStatic()) {
                    METHODS.put(reflMethod.getMappedName(), reflMethod);
                } else {
                    METHODS.put(reflMethod.getOwnerMappedName() + "." + reflMethod.getMappedName(), reflMethod);
                }
            }
        }

        var staticFields = mappingContainer.getStaticFields();
        for (var staticField : staticFields) {
            var decoder = mapDecoderValue(garbageValues.get(staticField.getName()), staticField.getTypeDesc());
            if (decoder != null) {
            }
            var reflField = new ReflField(
                    staticField.getOwner(),
                    staticField.getOwner(),
                    staticField.getObfName(),
                    staticField.getName(),
                    staticField.getTypeDesc(),
                    decoder,
                    true
            );

            reflField.initReflection(classLoader);
            FIELDS.put(reflField.getMappedName(), reflField);
        }

        var staticMethods = mappingContainer.getStaticMethods();
        for (var staticMethod : staticMethods) {
            var garbageValue = garbageValues.get(staticMethod.getName());
            var reflMethod = new ReflMethod(
                    staticMethod.getOwner(),
                    staticMethod.getOwner(),
                    staticMethod.getObfName(),
                    staticMethod.getName(),
                    staticMethod.getTypeDesc(),
                    garbageValue,
                    true
            );

            reflMethod.initReflection(classLoader);
            METHODS.put(reflMethod.getMappedName(), reflMethod);
        }
    }

    public static <T> T getStatic(String name) {
        var field = FIELDS.get(name);
        if (field == null) {
            throw new IllegalArgumentException("Field not found: " + name);
        }

        return field.getValue();
    }

    public static boolean hasStatic(String name) {
        return FIELDS.containsKey(name);
    }

    public static void putStatic(String name, Object value) {
        var field = FIELDS.get(name);
        if (field == null) {
            throw new IllegalArgumentException("Field not found: " + name);
        }

        field.setValue(value);
    }

    public static <T> T getField(Object instance, String owner, String name) {
        var field = FIELDS.get(owner + "." + name);
        if (field == null) {
            throw new IllegalArgumentException("Field not found: " + owner + "." + name);
        }

        return field.getValue(instance);
    }

    public static void putField(Object instance, String owner, String name, Object value) {
        var field = FIELDS.get(owner + "." + name);
        if (field == null) {
            throw new IllegalArgumentException("Field not found: " + owner + "." + name);
        }

        field.setValue(instance, value);
    }

    public static <T> T invokeStatic(String name, Object... params) {
        var method = METHODS.get(name);
        if (method == null) {
            throw new IllegalArgumentException("Method not found: " + name);
        }

        return method.invoke(params);
    }

    public static <T> T invoke(Object instance, String owner, String name, Object... params) {
        var method = METHODS.get(owner + "." + name);
        if (method == null) {
            throw new IllegalArgumentException("Method not found: " + owner + "." + name);
        }

        return method.invoke(instance, params);
    }

    private static Number mapDecoderValue(String value, String typeDesc) {
        if (value == null) {
            return null;
        }

        var type = Type.getType(typeDesc);

        switch (type.getSort()) {
            case Type.BYTE:
                return Byte.parseByte(value);
            case Type.SHORT:
                return Short.parseShort(value);
            case Type.INT:
                return Integer.parseInt(value);
            case Type.LONG:
                return Long.parseLong(value);
            default:
                throw new IllegalArgumentException("Unsupported type: " + type);
        }
    }
}