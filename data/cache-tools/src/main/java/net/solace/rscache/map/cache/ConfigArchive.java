package net.solace.rscache.map.cache;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.solace.rscache.map.cache.config.ObjectConfig;

@RequiredArgsConstructor
public class ConfigArchive {
    @Getter
    private final ObjectConfig objects;

    public static ConfigArchive load(GameCache cache) {
        return new ConfigArchive(ObjectConfig.load(cache));
    }
}
