package net.solace.impl.game;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.GameState;
import net.runelite.api.MenuAction;
import net.runelite.api.World;
import net.runelite.api.WorldType;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.client.game.WorldService;
import net.solace.api.domain.game.IClient;
import net.solace.api.game.IGame;
import net.solace.api.game.IWorlds;
import net.solace.api.input.IKeyboard;
import net.solace.api.interact.AutomatedMenu;
import net.solace.api.widgets.IWidgets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
public class WorldsImpl implements IWorlds {
    private final WorldService worldService;
    private final IClient client;
    private final IGame game;
    private final IWidgets widgets;
    private final IKeyboard keyboard;

    private final int[] BLACKLISTED_WORLDS = { 598, 401 };
    private final Predicate<World> BLACKLISTED_FILTER = w -> Arrays.stream(BLACKLISTED_WORLDS).noneMatch(id -> id == w.getId());


    @Override
    public boolean inMembersWorld() {
        return client.getWorldType().contains(WorldType.MEMBERS);
    }

    public List<World> getAll(Predicate<World> filter) {
        var worldList = client.getWorldList();
        if (worldList != null) {
            return Arrays.stream(worldList)
                    .filter(BLACKLISTED_FILTER)
                    .filter(filter)
                    .collect(Collectors.toList());
        }

        return fetchWorlds().stream()
                .filter(BLACKLISTED_FILTER)
                .filter(filter)
                .collect(Collectors.toList());
    }

    public World getFirst(Predicate<World> filter) {
        return getAll(filter).stream().findFirst().orElse(null);
    }

    public World getFirst(int id) {
        return getFirst(w -> w.getId() == id);
    }

    public World getRandom(Predicate<World> filter) {
        var worlds = getAll(filter);
        return worlds.isEmpty() ? null : worlds.get((int) (Math.random() * worlds.size()));
    }

    public int getCurrentId() {
        return client.getWorld();
    }

    public World getCurrent() {
        return getFirst(w -> w.getId() == getCurrentId());
    }

    public boolean isHopperOpen() {
        var worldSwitcher = widgets.get(InterfaceID.Worldswitcher.BUTTONS);
        return worldSwitcher != null;
    }

    public void hopTo(World world) {
        if (world == null) {
            return;
        }

        if (game.getState() == GameState.HOPPING) {
            return;
        }

        if (!isHopperOpen()) {
            openHopper();
            return;
        }

        var ask = widgets.get(InterfaceID.OBJECTBOX, 0, 3);
        if (widgets.isVisible(ask)) {
            keyboard.type(2);
            return;
        }

        client.interact(
                AutomatedMenu.builder()
                        .identifier(1)
                        .opcode(MenuAction.CC_OP)
                        .param0(world.getId())
                        .param1(InterfaceID.Worldswitcher.BUTTONS)
                        .build()
        );
    }

    public void openHopper() {
        client.interact(
                AutomatedMenu.builder()
                        .identifier(1)
                        .opcode(MenuAction.CC_OP)
                        .param0(-1)
                        .param1(InterfaceID.Logout.WORLD_SWITCHER)
                        .build()
        );
    }

    public boolean isMembers(World world) {
        return world.getTypes().contains(WorldType.MEMBERS);
    }

    public boolean isAllPkWorld(World world) {
        return world.getTypes().contains(WorldType.DEADMAN) || world.getTypes().contains(WorldType.PVP);
    }

    public boolean isSkillTotal(World world) {
        return world.getTypes().contains(WorldType.SKILL_TOTAL);
    }

    public boolean isTournament(World world) {
        return world.getTypes().contains(WorldType.NOSAVE_MODE) || world.getTypes().contains(WorldType.TOURNAMENT_WORLD);
    }

    public boolean isSpeedrunning(World world) {
        return world.getTypes().contains(WorldType.QUEST_SPEEDRUNNING);
    }

    public boolean isFreshStart(World world) {
        return world.getTypes().contains(WorldType.FRESH_START_WORLD);
    }

    public boolean isLeague(World world) {
        return world.getTypes().contains(WorldType.SEASONAL);
    }

    public boolean isNormal(World world) {
        return !isAllPkWorld(world) && !isSkillTotal(world) && !isTournament(world) && !isLeague(world) && !isPvpArena(world) && !isSpeedrunning(world) && !isFreshStart(world) && !isQuestSpeedRunning(world) && !isBeta(world) && !isHighRisk(world) && !isBountyHunter(world);
    }

    public boolean isPvpArena(World world) {
        return world.getTypes().contains(WorldType.PVP_ARENA);
    }

    public boolean isQuestSpeedRunning(World world) {
        return world.getTypes().contains(WorldType.QUEST_SPEEDRUNNING);
    }

    public boolean isBeta(World world) {
        return world.getTypes().contains(WorldType.BETA_WORLD);
    }

    public boolean isHighRisk(World world) {
        return world.getTypes().contains(WorldType.HIGH_RISK);
    }

    public boolean isBountyHunter(World world) {
        return world.getTypes().contains(WorldType.BOUNTY);
    }

    public void loadWorlds() {
        if (game.isOnLoginScreen()) {
            openLobbyWorlds();
            closeLobbyWorlds();
            return;
        }

        if (game.isLoggedIn()) {
            openHopper();
        }
    }

    public void openLobbyWorlds() {
        client.loadWorlds();
        client.setWorldSelectOpen(true);
    }

    public void closeLobbyWorlds() {
        client.setWorldSelectOpen(false);
    }

    private List<World> fetchWorlds() {
        log.debug("Fetching worlds from RuneLite API");
        var result = worldService.getWorlds();

        if (result == null || result.getWorlds().isEmpty()) {
            log.error("Failed to fetch worlds from RuneLite API");
            return Collections.emptyList();
        }

        var out = new ArrayList<World>();
        var worlds = result.getWorlds();
        for (var httpWorld : worlds) {
            var world = client.createWorld();
            world.setActivity(httpWorld.getActivity());
            world.setAddress(httpWorld.getAddress());
            world.setId(httpWorld.getId());
            world.setPlayerCount(httpWorld.getPlayers());
            world.setLocation(httpWorld.getLocation());
            var types = httpWorld.getTypes().stream()
                    .map(this::toApiWorldType)
                    .collect(Collectors.toCollection(() -> EnumSet.noneOf(WorldType.class)));
            world.setTypes(types);
            out.add(world);
        }

        return out;
    }

    private WorldType toApiWorldType(net.runelite.http.api.worlds.WorldType httpWorld) {
        if (httpWorld == net.runelite.http.api.worlds.WorldType.TOURNAMENT) {
            return WorldType.TOURNAMENT_WORLD;
        }

        return WorldType.valueOf(httpWorld.name());
    }
}
