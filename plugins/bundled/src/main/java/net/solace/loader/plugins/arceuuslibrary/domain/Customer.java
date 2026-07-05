package net.solace.loader.plugins.arceuuslibrary.domain;

import net.runelite.api.coords.WorldPoint;

import java.util.HashMap;
import java.util.Map;

public enum Customer {
    VILLIA(7047, "Villia", new WorldPoint(1626, 3814, 0)),
    PROFESSOR_GRACKLEBONE(7048, "Professor Gracklebone", new WorldPoint(1626, 3801, 0)),
    SAM(7049, "Sam", new WorldPoint(1639, 3801, 0)),
    HORPHIS(7046, "Horphis", new WorldPoint(1638, 3814, 0), false);

    private static final Map<Integer, Customer> byId = buildIdMap();
    private static final Map<String, Customer> byName = buildNameMap();
    private final int id;
    private final String name;
    private final WorldPoint WorldPoint;
    private final boolean isRegular;

    Customer(int id, String name, WorldPoint WorldPoint) {
        this.id = id;
        this.name = name;
        this.WorldPoint = WorldPoint;
        this.isRegular = true;
    }

    Customer(int id, String name, WorldPoint WorldPoint, boolean isRegular) {
        this.id = id;
        this.name = name;
        this.WorldPoint = WorldPoint;
        this.isRegular = isRegular;
    }

    public static Customer getById(int id) {
        return byId.get(id);
    }

    private static Map<Integer, Customer> buildIdMap() {
        Map<Integer, Customer> byId = new HashMap<>();
        for (Customer c : values()) {
            byId.put(c.id, c);
        }

        return byId;
    }

    public static Customer getByName(String name) {
        return byName.get(name);
    }

    private static Map<String, Customer> buildNameMap() {
        Map<String, Customer> byName = new HashMap<>();
        for (Customer c : values()) {
            byName.put(c.name, c);
        }

        return byName;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public WorldPoint getWorldPoint() {
        return WorldPoint;
    }

    public boolean isRegular() {
        return isRegular;
    }
}
