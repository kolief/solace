package net.solace.sdn.plugins.version;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.solace.sdn.plugins.version.mappings.MappingContainer;

import java.util.Map;

@RequiredArgsConstructor
@Getter
public class VersionPackage {
    private final MappingContainer mappings;
    private final String runeLiteVersion;
    private final String runeLiteCommit;
}
