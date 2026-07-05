package net.solace.rscache.map.engine.model.obj;

public final class ObjectShape {
    public static final int WALL = 0;
    public static final int WALL_CORNER_DIAG = 1;
    public static final int UNFINISHED_WALL = 2;
    public static final int WALL_CORNER = 3;
    public static final int WALL_DECOR_STRAIGHT_XOFFSET = 4;
    public static final int WALL_DECOR_STRAIGHT_ZOFFSET = 5;
    public static final int WALL_DECOR_DIAGONAL_XOFFSET = 6;
    public static final int WALL_DECOR_DIAGONAL_ZOFFSET = 7;
    public static final int INTERIOR_WALL_DECOR_DIAG = 8;
    public static final int WALL_OPEN = 9;
    public static final int COMPLEX_GROUND_DECOR = 10;
    public static final int GROUND_DEFAULT = 11;
    public static final int ROOF_TOP_SIDE = 12;
    public static final int ROOF_TOP_CORNER_FLAT = 13;
    public static final int ROOF_TOP_FLAT_DOWNWARD_CREASE = 14;
    public static final int ROOF_TOP_SLANTED_UPWARD_CREASE = 15;
    public static final int ROOF_TOP_SLANTED_DOWNWARD_CREASE = 16;
    public static final int ROOF_TOP_FLAT = 17;
    public static final int ROOF_EDGE = 18;
    public static final int ROOF_EDGE_CORNER_FLAT = 19;
    public static final int ROOF_CONNECTING_EDGE = 20;
    public static final int ROOF_EDGE_CORNER_POINTED = 21;
    public static final int GROUND_DECOR = 22;

    public static final int[] WALL_SHAPES = new int[]{
            WALL,
            WALL_CORNER_DIAG,
            UNFINISHED_WALL,
            WALL_CORNER
    };

    public static final int[] WALL_DECOR_SHAPES = new int[]{
            WALL_DECOR_STRAIGHT_XOFFSET,
            WALL_DECOR_STRAIGHT_ZOFFSET,
            WALL_DECOR_DIAGONAL_XOFFSET,
            WALL_DECOR_DIAGONAL_ZOFFSET,
            INTERIOR_WALL_DECOR_DIAG
    };

    public static final int[] NORMAL_SHAPES = new int[]{
            WALL_OPEN,
            ROOF_TOP_SIDE,
            ROOF_TOP_CORNER_FLAT,
            ROOF_TOP_FLAT_DOWNWARD_CREASE,
            ROOF_TOP_SLANTED_UPWARD_CREASE,
            ROOF_TOP_SLANTED_DOWNWARD_CREASE,
            ROOF_TOP_FLAT,
            ROOF_EDGE,
            ROOF_EDGE_CORNER_FLAT,
            ROOF_CONNECTING_EDGE,
            ROOF_EDGE_CORNER_POINTED,
            COMPLEX_GROUND_DECOR,
            GROUND_DEFAULT
    };

    public static final int[] GROUND_DECOR_SHAPES = new int[]{
            GROUND_DECOR
    };
}
