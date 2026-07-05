package net.solace.sdn.plugins.version.mappings;

import lombok.Value;

import java.util.List;

@Value
public class ClassMapping {
    String name;
    String obfName;
    List<FieldMapping> fields;
    List<MethodMapping> methods;
}
