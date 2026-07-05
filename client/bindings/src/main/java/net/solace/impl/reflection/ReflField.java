package net.solace.impl.reflection;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.objectweb.asm.Type;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigInteger;
import java.util.Arrays;

@RequiredArgsConstructor
@Slf4j
public class ReflField {
    private final String ownerName;
    @Getter
    private final String ownerMappedName;
    private final String name;
    @Getter
    private final String mappedName;
    private final String descriptor;
    private final Number decoder;
    @Getter
    private final boolean isStatic;

    private transient Class<?> owner;
    private transient Field field;

    public void initReflection(ClassLoader classLoader) throws ClassNotFoundException, NoSuchFieldException {
        this.owner = Class.forName(ownerName, false, classLoader);
        this.field = Arrays.stream(owner.getDeclaredFields())
                .filter(f -> f.getName().equals(name))
                .filter(f -> Type.getType(f.getType()).getDescriptor().equals(descriptor))
                .filter(f -> isStatic == Modifier.isStatic(f.getModifiers()))
                .findFirst()
                .orElseThrow(this::throwNotFoundException);
    }

    public int inverse() {
        BigInteger a = BigInteger.valueOf(decoder.longValue());
        BigInteger modulus = BigInteger.ONE.shiftLeft(32);
        return a.modInverse(modulus).intValue();
    }

    public <T> T getValue() {
        return getValue(null);
    }

    public void setValue(Object value) {
        setValue(null, value);
    }

    public <T> T getValue(Object owner) {
        try {
            field.setAccessible(true);
            Object value = field.get(owner);
            field.setAccessible(false);
            if (decoder != null) {
                if (value instanceof Integer) {
                    value = (int) value * decoder.intValue();
                } else if (value instanceof Long) {
                    value = (long) value * decoder.longValue();
                }
            }

            return (T) value;
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to get field", e);
        }
    }

    public void setValue(Object owner, Object value) {
        try {
            field.setAccessible(true);
            if (decoder != null) {
                if (value instanceof Integer) {
                    value = (int) value * inverse();
                } else if (value instanceof Long) {
                    value = (long) value * inverse();
                }
            }
            field.set(owner, value);
            field.setAccessible(false);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to set field", e);
        }
    }

    public boolean isInteger() {
        return descriptor.equals("I");
    }

    public boolean isLong() {
        return descriptor.equals("J");
    }

    private NoSuchFieldException throwNotFoundException() {
        var fieldType = isStatic ? "Static" : "Instance";
        var sb = new StringBuilder();
        sb.append(fieldType).append(" field not found: ")
                .append(descriptor).append(' ')
                .append(ownerName).append('.').append(name).append(" (");

        if (ownerMappedName != null) {
            sb.append(ownerMappedName).append('.');
        }

        sb.append(mappedName).append(')');

        return new NoSuchFieldException(sb.toString());
    }
}
