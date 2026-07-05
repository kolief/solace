package net.solace.sdk.entities;

import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.runelite.api.coords.WorldPoint;
import net.solace.api.Static;
import net.solace.api.domain.actors.INPC;
import net.solace.api.entities.INPCs;
import net.solace.api.query.entities.NPCQuery;

public class NPCs {
    private static final INPCs NPCS = Static.getNpcs();

    public static NPCQuery query() {
        return new NPCQuery(() -> NPCs.getAll(x -> true));
    }

    public static NPCQuery query(Supplier<List<INPC>> supplier) {
        return new NPCQuery(supplier);
    }

    public static INPC get(int index) {
        return NPCS.get(index);
    }

    public static List<INPC> getAll() {
        return NPCS.getAll(x -> true);
    }

    public static List<INPC> getAll(Predicate<? super INPC> filter) {
        return NPCS.getAll(filter);
    }

    public static List<INPC> getAll(int ... ids) {
        return NPCS.getAll(ids);
    }

    public static List<INPC> getAll(String ... names) {
        return NPCS.getAll(names);
    }

    public static INPC getNearest(WorldPoint worldPoint, Predicate<? super INPC> filter) {
        return (INPC)NPCS.getNearest(worldPoint, filter);
    }

    public static INPC getNearest(WorldPoint worldPoint, int ... ids) {
        return (INPC)NPCS.getNearest(worldPoint, ids);
    }

    public static INPC getNearest(WorldPoint worldPoint, String ... names) {
        return (INPC)NPCS.getNearest(worldPoint, names);
    }

    public static INPC getNearest(Predicate<? super INPC> filter) {
        return (INPC)NPCS.getNearest(filter);
    }

    public static INPC getNearest(String ... names) {
        return (INPC)NPCS.getNearest(names);
    }

    public static INPC getNearest(int ... ids) {
        return (INPC)NPCS.getNearest(ids);
    }

    public static INPC getHintArrowed() {
        return NPCS.getHintArrowed();
    }
}

