package net.solace.api.plugins.config;

import java.util.function.Predicate;
import net.runelite.api.Client;
import net.runelite.api.WorldType;

public enum RuneScapeProfileType {
    STANDARD(client -> true),
    BETA(client -> client.getWorldType().contains(WorldType.NOSAVE_MODE) || client.getWorldType().contains(WorldType.BETA_WORLD)),
    QUEST_SPEEDRUNNING(client -> client.getWorldType().contains(WorldType.QUEST_SPEEDRUNNING)),
    DEADMAN(client -> client.getWorldType().contains(WorldType.DEADMAN)),
    PVP_ARENA(client -> client.getWorldType().contains(WorldType.PVP_ARENA)),
    TRAILBLAZER_LEAGUE,
    DEADMAN_REBORN,
    SHATTERED_RELICS_LEAGUE,
    TRAILBLAZER_RELOADED_LEAGUE(client -> client.getWorldType().contains(WorldType.SEASONAL));

    private final Predicate<Client> test;

    private RuneScapeProfileType() {
        this(client -> false);
    }

    public static RuneScapeProfileType getCurrent(Client client) {
        RuneScapeProfileType[] types = RuneScapeProfileType.values();
        for (int i = types.length - 1; i >= 0; --i) {
            RuneScapeProfileType type = types[i];
            if (!types[i].test.test(client)) continue;
            return type;
        }
        return STANDARD;
    }

    public Predicate<Client> getTest() {
        return this.test;
    }

    private RuneScapeProfileType(Predicate<Client> test) {
        this.test = test;
    }
}

