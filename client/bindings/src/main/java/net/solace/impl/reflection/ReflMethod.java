package net.solace.impl.reflection;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.objectweb.asm.Type;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

@RequiredArgsConstructor
@Slf4j
public class ReflMethod {
    private final String ownerName;
    @Getter
    private final String ownerMappedName;
    private final String name;
    @Getter
    private final String mappedName;
    private final String descriptor;
    private final String garbageValue;
    @Getter
    private final boolean isStatic;

    private transient Class<?> owner;
    private transient Method method;

    public void initReflection(ClassLoader classLoader) throws ClassNotFoundException, NoSuchMethodException {
        this.owner = Class.forName(ownerName, false, classLoader);
        this.method = Arrays.stream(owner.getDeclaredMethods())
                .filter(m -> m.getName().equals(name))
                .filter(m -> Type.getMethodDescriptor(m).equals(descriptor))
                .filter(m -> isStatic == Modifier.isStatic(m.getModifiers()))
                .findFirst()
                .orElseThrow(this::throwNotFoundException);
    }

    public <T> T invoke(Object owner, Object[] params) {
        try {
            method.setAccessible(true);
            if (garbageValue != null) {
                Class<? extends Number> garbageParam = getGarbageParam();
                Object[] newParams;
                if (params == null) {
                    newParams = new Object[1];
                } else {
                    newParams = new Object[params.length + 1];
                    System.arraycopy(params, 0, newParams, 0, params.length);
                }

                Number number = garbageParam.getConstructor(String.class).newInstance(garbageValue);
                newParams[newParams.length - 1] = number;
                params = newParams;
            }

            Object value = method.invoke(owner, params);
            method.setAccessible(false);
            return (T) value;
        } catch (Exception e) {
            throw new RuntimeException("Failed to invoke method", e);
        }
    }

    public <T> T invoke(Object[] params) {
        return invoke(null, params);
    }

    public String getGarbageType() {
        int bracketIdx = descriptor.lastIndexOf(')');
        return descriptor.substring(bracketIdx - 1, bracketIdx);
    }

    public Class<? extends Number> getGarbageParam() {
        String garbageType = getGarbageType();
        switch (garbageType) {
            case "I":
                return Integer.class;
            case "J":
                return Long.class;
            case "F":
                return Float.class;
            case "D":
                return Double.class;
            case "S":
                return Short.class;
            case "B":
                return Byte.class;
            default:
                throw new RuntimeException("Unknown garbage param type: " + garbageType);
        }
    }

    private NoSuchMethodException throwNotFoundException() {
        var fieldType = isStatic ? "Static" : "Instance";
        var sb = new StringBuilder();
        sb.append(fieldType).append(" method not found: ")
                .append(descriptor).append(' ')
                .append(ownerName).append('.').append(name).append(" (");

        if (ownerMappedName != null) {
            sb.append(ownerMappedName).append('.');
        }

        sb.append(mappedName).append(')');

        return new NoSuchMethodException(sb.toString());
    }
}
