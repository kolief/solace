package net.solace.sdn.plugins.version.mappings;

import lombok.Value;

@Value
public class StaticFieldMapping {
    String name;
    String obfName;
    String owner;
    String typeDesc;
}
