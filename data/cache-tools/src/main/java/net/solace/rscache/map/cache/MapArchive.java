package net.solace.rscache.map.cache;

import lombok.extern.slf4j.Slf4j;
import net.runelite.cache.IndexType;
import net.runelite.cache.definitions.LocationsDefinition;
import net.runelite.cache.definitions.MapDefinition;
import net.runelite.cache.definitions.loaders.LocationsLoader;
import net.runelite.cache.definitions.loaders.MapLoader;
import net.solace.rscache.util.Pair;
import net.solace.rscache.map.xtea.XteaConfig;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class MapArchive extends HashMap<Integer, Pair<MapDefinition, LocationsDefinition>> {
    private static final int MAX_REGIONS = 32768;

    public MapArchive(Map<Integer, Pair<MapDefinition, LocationsDefinition>> entries) {
        super(entries);
    }

    public static MapArchive load(GameCache cache, XteaConfig xteaConfig) throws IOException {
        var entries = new HashMap<Integer, Pair<MapDefinition, LocationsDefinition>>();

        var archive = cache.getStore().getIndex(IndexType.MAPS);
        for (int regionId = 0; regionId < MAX_REGIONS; regionId++) {
            var x = regionId >> 8;
            var y = regionId & 0xFF;

            var map = archive.findArchiveByName("m%s_%s".formatted(x, y));
            if (map == null) {
                continue;
            }

            var loc = archive.findArchiveByName("l%s_%s".formatted(x, y));
            if (loc == null) {
                continue;
            }

            var mapCompressed = cache.getDisk().loadArchive(map);
            var mapData = map.decompress(mapCompressed);
            var mapDef = new MapLoader().load(x, y, mapData);

            var locCompressed = cache.getDisk().loadArchive(loc);
            var xtea = xteaConfig.getRegionKey(regionId);
            if (xtea == null) {
                continue;
            }

            byte[] locData;
            try {
                locData = loc.decompress(locCompressed, xtea);
            } catch (Exception e) {
                log.warn("Failed to decompress loc archive for region {}: {}", regionId, e.getMessage());
                continue;
            }

            var locDef = new LocationsLoader().load(x, y, locData);

            entries.put(regionId, new Pair<>(mapDef, locDef));
        }

        return new MapArchive(entries);
    }
}
