package net.solace.mappings.generator.dto;

public class JMethod {
    private String name;
    private String obfuscatedName;
    private String owner;
    private String ownerObfuscatedName;
    private String descriptor;
    private Number garbageValue;
    private boolean isStatic;

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

    public Number getGarbageValue() {
        return garbageValue;
    }

    public void setGarbageValue(Number garbageValue) {
        this.garbageValue = garbageValue;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public void setStatic(boolean aStatic) {
        isStatic = aStatic;
    }

    @Override
    public String toString() {
        return name + " [" + ownerObfuscatedName + "." + obfuscatedName + "] " + descriptor;
    }
}
