package net.solace.sdn.plugins.version.mappings;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

@Getter
@RequiredArgsConstructor
public class MappingContainer {
    private final List<ClassMapping> classMappings;
    private final List<StaticFieldMapping> staticFields;
    private final List<StaticMethodMapping> staticMethods;
    private final Map<String, String> garbage;
}