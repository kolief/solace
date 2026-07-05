package net.solace.rscache.map.engine.model.collision;

public final class CollisionFlag {
    public static final int WALL_NORTH_WEST = 0x1;
    public static final int WALL_NORTH = 0x2;
    public static final int WALL_NORTH_EAST = 0x4;
    public static final int WALL_EAST = 0x8;
    public static final int WALL_SOUTH_EAST = 0x10;
    public static final int WALL_SOUTH = 0x20;
    public static final int WALL_SOUTH_WEST = 0x40;
    public static final int WALL_WEST = 0x80;
    public static final int OBJECT = 0x100;
    public static final int WALL_NORTH_WEST_PROJECTILE_BLOCKER = 0x200;
    public static final int WALL_NORTH_PROJECTILE_BLOCKER = 0x400;
    public static final int WALL_NORTH_EAST_PROJECTILE_BLOCKER = 0x800;
    public static final int WALL_EAST_PROJECTILE_BLOCKER = 0x1000;
    public static final int WALL_SOUTH_EAST_PROJECTILE_BLOCKER = 0x2000;
    public static final int WALL_SOUTH_PROJECTILE_BLOCKER = 0x4000;
    public static final int WALL_SOUTH_WEST_PROJECTILE_BLOCKER = 0x8000;
    public static final int WALL_WEST_PROJECTILE_BLOCKER = 0x10000;
    public static final int OBJECT_PROJECTILE_BLOCKER = 0x20000;
    public static final int FLOOR_DECORATION = 0x40000;
    public static final int FLOOR = 0x200000;
    public static final int BLOCK_FULL = OBJECT | FLOOR_DECORATION | FLOOR;
    public static final int WALL_NORTH_WEST_ROUTE_BLOCKER = 0x400000;
    public static final int WALL_NORTH_ROUTE_BLOCKER = 0x800000;
    public static final int WALL_NORTH_EAST_ROUTE_BLOCKER = 0x1000000;
    public static final int WALL_EAST_ROUTE_BLOCKER = 0x2000000;
    public static final int WALL_SOUTH_EAST_ROUTE_BLOCKER = 0x4000000;
    public static final int WALL_SOUTH_ROUTE_BLOCKER = 0x8000000;
    public static final int WALL_SOUTH_WEST_ROUTE_BLOCKER = 0x10000000;
    public static final int WALL_WEST_ROUTE_BLOCKER = 0x20000000;
    public static final int OBJECT_ROUTE_BLOCKER = 0x40000000;

    private static final int FLOOR_BLOCKED = FLOOR | FLOOR_DECORATION;

    public static final int BLOCK_WEST = WALL_EAST |
                                         OBJECT |
                                         FLOOR_BLOCKED;

    public static final int BLOCK_EAST = WALL_WEST |
                                         OBJECT |
                                         FLOOR_BLOCKED;

    public static final int BLOCK_SOUTH = WALL_NORTH |
                                          OBJECT |
                                          FLOOR_BLOCKED;

    public static final int BLOCK_NORTH = WALL_SOUTH |
                                          OBJECT |
                                          FLOOR_BLOCKED;

    public static final int BLOCK_SOUTH_WEST = WALL_NORTH |
                                               WALL_NORTH_EAST |
                                               WALL_EAST |
                                               OBJECT |
                                               FLOOR_BLOCKED;

    public static final int BLOCK_SOUTH_EAST = WALL_NORTH_WEST |
                                               WALL_NORTH |
                                               WALL_WEST |
                                               OBJECT |
                                               FLOOR_BLOCKED;

    public static final int BLOCK_NORTH_WEST = WALL_EAST |
                                               WALL_SOUTH_EAST |
                                               WALL_SOUTH |
                                               OBJECT |
                                               FLOOR_BLOCKED;

    public static final int BLOCK_NORTH_EAST = WALL_SOUTH |
                                               WALL_SOUTH_WEST |
                                               WALL_WEST |
                                               OBJECT |
                                               FLOOR_BLOCKED;

    public static final int BLOCK_NORTH_AND_SOUTH_EAST = WALL_NORTH |
                                                         WALL_NORTH_EAST |
                                                         WALL_EAST |
                                                         WALL_SOUTH_EAST |
                                                         WALL_SOUTH |
                                                         OBJECT |
                                                         FLOOR_BLOCKED;

    public static final int BLOCK_NORTH_AND_SOUTH_WEST = WALL_NORTH_WEST |
                                                         WALL_NORTH |
                                                         WALL_SOUTH |
                                                         WALL_SOUTH_WEST |
                                                         WALL_WEST |
                                                         OBJECT |
                                                         FLOOR_BLOCKED;

    public static final int BLOCK_NORTH_EAST_AND_WEST = WALL_NORTH_WEST |
                                                        WALL_NORTH |
                                                        WALL_NORTH_EAST |
                                                        WALL_EAST |
                                                        WALL_WEST |
                                                        OBJECT |
                                                        FLOOR_BLOCKED;

    public static final int BLOCK_SOUTH_EAST_AND_WEST = WALL_EAST |
                                                        WALL_SOUTH_EAST |
                                                        WALL_SOUTH |
                                                        WALL_SOUTH_WEST |
                                                        WALL_WEST |
                                                        OBJECT |
                                                        FLOOR_BLOCKED;
}
