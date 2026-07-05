package net.solace.sdn.plugins.version.mappings;

import lombok.Value;

@Value
public class MethodMapping {
    String name;
    String obfName;
    String typeDesc;
    boolean isStatic;

    public boolean isInit() {
        return name.equals("<init>") || name.equals("<clinit>");
    }
}
