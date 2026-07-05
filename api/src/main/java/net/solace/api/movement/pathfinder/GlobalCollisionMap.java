package net.solace.api.movement.pathfinder;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Objects;
import net.solace.api.movement.pathfinder.BitSet4D;
import net.solace.api.movement.pathfinder.CollisionMap;

public class GlobalCollisionMap
implements CollisionMap {
    public final BitSet4D[] regions = new BitSet4D[65536];

    public GlobalCollisionMap() {
    }

    public GlobalCollisionMap(byte[] data) {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        while (buffer.hasRemaining()) {
            int region = buffer.getShort() & 0xFFFF;
            this.regions[region] = new BitSet4D(buffer, 64, 64, 4, 2);
        }
    }

    public byte[] toBytes() {
        int regionCount = (int)Arrays.stream(this.regions).filter(Objects::nonNull).count();
        ByteBuffer buffer = ByteBuffer.allocate(regionCount * 4098);
        for (int i = 0; i < this.regions.length; ++i) {
            if (this.regions[i] == null) continue;
            buffer.putShort((short)i);
            this.regions[i].write(buffer);
        }
        return buffer.array();
    }

    public void set(int x, int y, int z, int w, boolean value) {
        BitSet4D region = this.regions[x / 64 * 256 + y / 64];
        if (region == null) {
            return;
        }
        region.set(x % 64, y % 64, z, w, value);
    }

    public BitSet4D getRegion(int x, int y) {
        int regionId = x / 64 * 256 + y / 64;
        return this.regions[regionId];
    }

    public boolean get(int x, int y, int z, int w) {
        BitSet4D region = this.getRegion(x, y);
        if (region == null) {
            return false;
        }
        int regionX = x % 64;
        int regionY = y % 64;
        return region.get(regionX, regionY, z, w);
    }

    @Override
    public boolean n(int x, int y, int z) {
        return this.get(x, y, z, 0);
    }

    @Override
    public boolean e(int x, int y, int z) {
        return this.get(x, y, z, 1);
    }
}

