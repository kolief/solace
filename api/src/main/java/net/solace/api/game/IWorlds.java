package net.solace.api.game;

import java.util.List;
import java.util.function.Predicate;
import net.runelite.api.World;

public interface IWorlds {
    public boolean inMembersWorld();

    public List<World> getAll(Predicate<World> var1);

    default public World getFirst(Predicate<World> filter) {
        return this.getAll(filter).stream().findFirst().orElse(null);
    }

    default public World getFirst(int id) {
        return this.getFirst(w -> w.getId() == id);
    }

    public World getRandom(Predicate<World> var1);

    public int getCurrentId();

    default public World getCurrent() {
        return this.getFirst(w -> w.getId() == this.getCurrentId());
    }

    public boolean isHopperOpen();

    public void hopTo(World var1);

    public void openHopper();

    public boolean isMembers(World var1);

    public boolean isAllPkWorld(World var1);

    public boolean isSkillTotal(World var1);

    public boolean isTournament(World var1);

    public boolean isSpeedrunning(World var1);

    public boolean isFreshStart(World var1);

    public boolean isLeague(World var1);

    public boolean isNormal(World var1);

    public boolean isPvpArena(World var1);

    public boolean isQuestSpeedRunning(World var1);

    public boolean isBeta(World var1);

    public boolean isHighRisk(World var1);

    public void loadWorlds();

    public void openLobbyWorlds();

    public void closeLobbyWorlds();
}

