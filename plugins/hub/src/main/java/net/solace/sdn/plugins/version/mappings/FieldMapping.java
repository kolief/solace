package net.solace.sdn.plugins.version.mappings;

import lombok.Value;

@Value
public class FieldMapping {
    String name;
    String obfName;
    String typeDesc;
    boolean isStatic;
}
