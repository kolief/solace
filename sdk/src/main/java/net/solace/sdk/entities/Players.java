package net.solace.sdk.entities;

import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.runelite.api.coords.WorldPoint;
import net.solace.api.Static;
import net.solace.api.domain.actors.IPlayer;
import net.solace.api.entities.IPlayers;
import net.solace.api.query.entities.PlayerQuery;

public class Players {
    private static final IPlayers PLAYERS = Static.getPlayers();

    public static PlayerQuery query() {
        return new PlayerQuery(() -> Players.getAll(x -> true));
    }

    public static PlayerQuery query(Supplier<List<IPlayer>> supplier) {
        return new PlayerQuery(supplier);
    }

    public static IPlayer get(int index) {
        return PLAYERS.get(index);
    }

    public static IPlayer getLocal() {
        return PLAYERS.getLocal();
    }

    public static List<IPlayer> getAll() {
        return PLAYERS.getAll(x -> true);
    }

    public static List<IPlayer> getAll(Predicate<? super IPlayer> filter) {
        return PLAYERS.getAll(filter);
    }

    public static List<IPlayer> getAll(int ... ids) {
        return PLAYERS.getAll(ids);
    }

    public static List<IPlayer> getAll(String ... names) {
        return PLAYERS.getAll(names);
    }

    public static IPlayer getNearest(WorldPoint worldPoint, Predicate<? super IPlayer> filter) {
        return (IPlayer)PLAYERS.getNearest(worldPoint, filter);
    }

    public static IPlayer getNearest(WorldPoint worldPoint, int ... ids) {
        return (IPlayer)PLAYERS.getNearest(worldPoint, ids);
    }

    public static IPlayer getNearest(WorldPoint worldPoint, String ... names) {
        return (IPlayer)PLAYERS.getNearest(worldPoint, names);
    }

    public static IPlayer getNearest(Predicate<? super IPlayer> filter) {
        return Players.getNearest(Players.getLocal().getWorldLocation(), filter);
    }

    public static IPlayer getNearest(int ... ids) {
        return Players.getNearest(Players.getLocal().getWorldLocation(), ids);
    }

    public static IPlayer getNearest(String ... names) {
        return Players.getNearest(Players.getLocal().getWorldLocation(), names);
    }

    public static IPlayer getHintArrowed() {
        return PLAYERS.getHintArrowed();
    }
}

