package net.solace.loader.plugins.arceuuslibrary.domain;

import lombok.Getter;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.solace.api.coords.Area;

import java.util.HashMap;
import java.util.Map;

@Getter
public enum Room {
    TNW(0, new WorldPoint(1612, 3824, 2)), // Top North West
    TNE(1, new WorldPoint(1649, 3825, 2)), // Top North East
    TC(2, new WorldPoint(1632, 3813, 2)),  // Top Center
    TSW(3, new WorldPoint(1614, 3792, 2)), // Top South West
    MNW(4, new WorldPoint(1614, 3824, 1)), // Middle North West
    MNE(5, new WorldPoint(1650, 3823, 1)), // Middle North East
    MC(6, new WorldPoint(1638, 3809, 1)),  // Middle Center
    MSW(7, new WorldPoint(1617, 3792, 1)), // Middle South West
    BNW(8, new WorldPoint(1617, 3821, 0)), // Bottom North West
    BNE(9, new WorldPoint(1647, 3821, 0)), // Bottom North East
    BC(10, new WorldPoint(1632, 3808, 0), Area.fromCorners(new WorldPoint(1623, 3798, 0), new WorldPoint(1642, 3817, 0))), // Bottom Center
    BSW(11, new WorldPoint(1617, 3794, 0));// Bottom South West

    private static final int NORTH_BORDER = 3815;
    private static final int NORTH_BORDER_GROUND_FLOOR = 3813;
    private static final int WEST_BORDER_GROUND_FLOOR = 1627;
    private static final int WEST_BORDER = 1625;
    private static Map<Integer, Room> byIndex = new HashMap<>();
    private static Map<WorldPoint, Room> byWorldPoint = new HashMap<>();

    static {
        for (Room r : Room.values()) {
            byIndex.put(r.index, r);
            byWorldPoint.put(r.WorldPoint, r);
        }
    }

    private int index;
    private WorldPoint WorldPoint;
    private WorldArea area;

    Room(int index, WorldPoint WorldPoint) {
        this.index = index;
        this.WorldPoint = WorldPoint;
        area = null;
    }

    Room(int index, WorldPoint WorldPoint, WorldArea area) {
        this.index = index;
        this.WorldPoint = WorldPoint;
        this.area = area;
    }

    public static Room getRoom(int index) {
        return byIndex.get(index);
    }

    public static Room getRoom(WorldPoint pos) {
        return byWorldPoint.get(pos);
    }

    public static Stair getStair(Room r, boolean isGoingUp) {
        String stairName = r.name() + "_" + (isGoingUp ? "UP" : "DOWN");
        for (Stair s : Stair.values()) {
            if (s.name().equals(stairName)) {
                return s;
            }
        }
        return null;
    }

    public static Stair getStair(int room, boolean isGoingUp) {
        String stairName = getRoom(room).name() + "_" + (isGoingUp ? "UP" : "DOWN");
        for (Stair s : Stair.values()) {
            if (s.name().equals(stairName)) {
                return s;
            }
        }
        return null;
    }

    public static int getRoomByWorldPoint(WorldPoint WorldPoint) {
        if (BC.getArea().contains(WorldPoint) && WorldPoint.getPlane() == 0) return BC.index;
        boolean north = WorldPoint.getY() > NORTH_BORDER;
        boolean west = WorldPoint.getX() < WEST_BORDER;
        if (WorldPoint.getPlane() == 0) {
            north = WorldPoint.getY() > NORTH_BORDER_GROUND_FLOOR;
            west = WorldPoint.getX() < WEST_BORDER_GROUND_FLOOR;
        }
        int room = 11;
        room -= WorldPoint.getPlane() * 4;
        if (north && west)
            room -= 3;
        else if (north)
            room -= 2;
        else if (!west)
            room -= 1;
        return room;
    }

    public static boolean isSameLibraryRoom(WorldPoint pos1, WorldPoint pos2) {
        return getRoomByWorldPoint(pos1) == getRoomByWorldPoint(pos2);
    }

    public static boolean isRoomSameFloor(int room, WorldPoint WorldPoint) {
        return Room.getRoom(room).getWorldPoint().getPlane() == WorldPoint.getPlane();
    }
}
