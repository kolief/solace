package net.solace.sdn.plugins.version.mappings;

import lombok.Value;

@Value
public class StaticMethodMapping {
    String name;
    String obfName;
    String owner;
    String expectedTypeDesc;
    String typeDesc;
}
