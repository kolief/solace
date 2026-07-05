package net.solace.sdn.pf4j;

import lombok.Getter;
import org.pf4j.DefaultPluginDescriptor;
import org.pf4j.PluginDescriptor;

@Getter
public class SdnPluginDescriptor extends DefaultPluginDescriptor {
    private final String commitHash;

    public SdnPluginDescriptor(PluginDescriptor descriptor, String commitHash) {
        super(
                descriptor.getPluginId(),
                descriptor.getPluginDescription(),
                descriptor.getPluginClass(),
                descriptor.getVersion(),
                descriptor.getRequires(),
                descriptor.getProvider(),
                descriptor.getLicense()
        );
        this.commitHash = commitHash;

        descriptor.getDependencies().forEach(this::addDependency);
    }
}
