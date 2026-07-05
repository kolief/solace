package net.solace.mappings.generator.dto;

public class JField {
    private String name;
    private String obfuscatedName;
    private String owner;
    private String ownerObfuscatedName;
    private String descriptor;
    private Number getter;
    private Number setter;
    private boolean isStatic;
    private boolean fieldHookAfter;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getObfuscatedName() {
        return obfuscatedName;
    }

    public void setObfuscatedName(String obfuscatedName) {
        this.obfuscatedName = obfuscatedName;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getOwnerObfuscatedName() {
        return ownerObfuscatedName;
    }

    public void setOwnerObfuscatedName(String ownerObfuscatedName) {
        this.ownerObfuscatedName = ownerObfuscatedName;
    }

    public String getDescriptor() {
        return descriptor;
    }

    public void setDescriptor(String descriptor) {
        this.descriptor = descriptor;
    }

    public Number getGetter() {
        return getter;
    }

    public void setGetter(Number getter) {
        this.getter = getter;
    }

    public Number getSetter() {
        return setter;
    }

    public void setSetter(Number setter) {
        this.setter = setter;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public void setStatic(boolean aStatic) {
        isStatic = aStatic;
    }

    public boolean isFieldHookAfter() {
        return fieldHookAfter;
    }

    public void setFieldHookAfter(boolean fieldHookAfter) {
        this.fieldHookAfter = fieldHookAfter;
    }

    @Override
    public String toString() {
        return name + " [" + ownerObfuscatedName + "." + obfuscatedName + " : " + owner + "." + name + "] " + descriptor;
    }
}
