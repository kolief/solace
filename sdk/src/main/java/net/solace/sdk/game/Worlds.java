package net.solace.sdk.game;

import java.util.List;
import java.util.function.Predicate;
import net.runelite.api.World;
import net.solace.api.Static;
import net.solace.api.game.IWorlds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Worlds {
    private static final Logger log = LoggerFactory.getLogger(Worlds.class);
    private static final IWorlds WORLDS = Static.getWorlds();

    public static boolean inMembersWorld() {
        return WORLDS.inMembersWorld();
    }

    public static List<World> getAll(Predicate<World> filter) {
        return WORLDS.getAll(filter);
    }

    public static World getFirst(Predicate<World> filter) {
        return WORLDS.getFirst(filter);
    }

    public static World getFirst(int id) {
        return WORLDS.getFirst(id);
    }

    public static World getRandom(Predicate<World> filter) {
        return WORLDS.getRandom(filter);
    }

    public static int getCurrentId() {
        return WORLDS.getCurrentId();
    }

    public static World getCurrent() {
        return WORLDS.getCurrent();
    }

    public static boolean isHopperOpen() {
        return WORLDS.isHopperOpen();
    }

    public static void hopTo(World world) {
        WORLDS.hopTo(world);
    }

    public static void openHopper() {
        WORLDS.openHopper();
    }

    public static boolean isMembers(World world) {
        return WORLDS.isMembers(world);
    }

    public static boolean isAllPkWorld(World world) {
        return WORLDS.isAllPkWorld(world);
    }

    public static boolean isSkillTotal(World world) {
        return WORLDS.isSkillTotal(world);
    }

    public static boolean isTournament(World world) {
        return WORLDS.isTournament(world);
    }

    public static boolean isSpeedrunning(World world) {
        return WORLDS.isSpeedrunning(world);
    }

    public static boolean isFreshStart(World world) {
        return WORLDS.isFreshStart(world);
    }

    public static boolean isLeague(World world) {
        return WORLDS.isLeague(world);
    }

    public static boolean isNormal(World world) {
        return WORLDS.isNormal(world);
    }

    public static boolean isPvpArena(World world) {
        return WORLDS.isPvpArena(world);
    }

    public static boolean isQuestSpeedRunning(World world) {
        return WORLDS.isQuestSpeedRunning(world);
    }

    public static boolean isBeta(World world) {
        return WORLDS.isBeta(world);
    }

    public static boolean isHighRisk(World world) {
        return WORLDS.isHighRisk(world);
    }

    public static void loadWorlds() {
        WORLDS.loadWorlds();
    }

    public static void openLobbyWorlds() {
        WORLDS.openLobbyWorlds();
    }

    public static void closeLobbyWorlds() {
        WORLDS.closeLobbyWorlds();
    }
}

