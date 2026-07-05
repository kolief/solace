package net.solace.api.movement.pathfinder;

import net.runelite.api.coords.WorldPoint;

public interface CollisionMap {
    public boolean n(int var1, int var2, int var3);

    public boolean e(int var1, int var2, int var3);

    default public boolean s(int x, int y, int z) {
        return this.n(x, y - 1, z);
    }

    default public boolean w(int x, int y, int z) {
        return this.e(x - 1, y, z);
    }

    default public boolean ne(int x, int y, int z) {
        return this.n(x, y, z) && this.e(x, y + 1, z) && this.e(x, y, z) && this.n(x + 1, y, z);
    }

    default public boolean nw(int x, int y, int z) {
        return this.n(x, y, z) && this.w(x, y + 1, z) && this.w(x, y, z) && this.n(x - 1, y, z);
    }

    default public boolean se(int x, int y, int z) {
        return this.s(x, y, z) && this.e(x, y - 1, z) && this.e(x, y, z) && this.s(x + 1, y, z);
    }

    default public boolean sw(int x, int y, int z) {
        return this.s(x, y, z) && this.w(x, y - 1, z) && this.w(x, y, z) && this.s(x - 1, y, z);
    }

    default public boolean fullBlock(int x, int y, int z) {
        return !this.n(x, y, z) && !this.s(x, y, z) && !this.w(x, y, z) && !this.e(x, y, z);
    }

    default public boolean n(WorldPoint worldPoint) {
        return this.n(worldPoint.getX(), worldPoint.getY(), worldPoint.getPlane());
    }

    default public boolean s(WorldPoint worldPoint) {
        return this.s(worldPoint.getX(), worldPoint.getY(), worldPoint.getPlane());
    }

    default public boolean w(WorldPoint worldPoint) {
        return this.w(worldPoint.getX(), worldPoint.getY(), worldPoint.getPlane());
    }

    default public boolean e(WorldPoint worldPoint) {
        return this.e(worldPoint.getX(), worldPoint.getY(), worldPoint.getPlane());
    }

    default public boolean ne(WorldPoint worldPoint) {
        return this.ne(worldPoint.getX(), worldPoint.getY(), worldPoint.getPlane());
    }

    default public boolean nw(WorldPoint worldPoint) {
        return this.nw(worldPoint.getX(), worldPoint.getY(), worldPoint.getPlane());
    }

    default public boolean se(WorldPoint worldPoint) {
        return this.se(worldPoint.getX(), worldPoint.getY(), worldPoint.getPlane());
    }

    default public boolean sw(WorldPoint worldPoint) {
        return this.sw(worldPoint.getX(), worldPoint.getY(), worldPoint.getPlane());
    }

    default public boolean fullBlock(WorldPoint worldPoint) {
        return !this.n(worldPoint) && !this.s(worldPoint) && !this.w(worldPoint) && !this.e(worldPoint);
    }
}

