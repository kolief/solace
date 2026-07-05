package net.solace.rscache.map.engine.model.collision;

import net.solace.api.movement.pathfinder.BitSet4D;
import net.solace.api.movement.pathfinder.GlobalCollisionMap;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Objects;
import java.util.zip.GZIPOutputStream;

public class TranslatedMap {
    private final BitSet4D[] regions = new BitSet4D[256 * 256];

    public TranslatedMap(GlobalCollisionMap globalCollisionMap) {
        var mapRegions = globalCollisionMap.regions;
        for (int i = 0; i < mapRegions.length; i++) {
            if (mapRegions[i].getBits().cardinality() != 0) {
                regions[i] = mapRegions[i];
            }
        }
    }

    public byte[] gzipped() {
        try (var bos = new ByteArrayOutputStream();
             var gzip = new GZIPOutputStream(bos)) {
            gzip.write(toBytes());
            gzip.finish();
            return bos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to write gzipped collisionmap", e);
        }
    }

    public byte[] toBytes() {
        var regionCount = (int) Arrays.stream(regions).filter(Objects::nonNull).count();
        var buffer = ByteBuffer.allocate(regionCount * (2 + 64 * 64 * 4 * 2 / 8));

        for (int i = 0; i < regions.length; i++) {
            var region = regions[i];
            if (region != null) {
                buffer.putShort((short) i);
                region.write(buffer);
            }
        }

        return buffer.array();
    }
}
