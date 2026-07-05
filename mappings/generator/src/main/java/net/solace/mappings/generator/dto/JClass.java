package net.solace.mappings.generator.dto;

import java.util.ArrayList;
import java.util.List;

public class JClass {
    private String name;
    private String obfuscatedName;
    private final List<JField> fields = new ArrayList<>();
    private final List<JMethod> methods = new ArrayList<>();

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

    public List<JField> getFields() {
        return fields;
    }

    public List<JMethod> getMethods() {
        return methods;
    }

    @Override
    public String toString() {
        return name + " [" + obfuscatedName + ".class] fields: " + fields.size() + ", methods: " + methods.size();
    }
}
