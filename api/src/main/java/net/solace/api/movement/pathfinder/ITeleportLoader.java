package net.solace.api.movement.pathfinder;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import net.runelite.api.coords.WorldPoint;
import net.solace.api.movement.pathfinder.model.Teleport;
import net.solace.api.movement.pathfinder.model.TeleportItem;
import net.solace.api.movement.pathfinder.model.poh.HousePortal;
import net.solace.api.movement.pathfinder.model.poh.PortalNexus;

public interface ITeleportLoader {
    public List<Teleport> getCustomTeleports();

    public void refreshTeleports(List<Integer> var1);

    default public void refreshTeleports() {
        this.refreshTeleports(Collections.emptyList());
    }

    public List<Teleport> buildTeleports(Boolean var1);

    default public List<Teleport> buildTeleports() {
        return this.buildTeleports(false);
    }

    public List<Teleport> buildTimedTeleports();

    public List<Teleport> teleportItems(List<Integer> var1);

    public List<Teleport> duelingRing(List<Integer> var1);

    public List<Teleport> gamesNecklace(List<Integer> var1);

    public List<Teleport> necklaceOfPassage(List<Integer> var1);

    public List<Teleport> xericsTalisman(List<Integer> var1);

    public List<Teleport> digsitePendant(List<Integer> var1);

    public List<Teleport> skillCapes(List<Integer> var1);

    public List<Teleport> kharedstMemoirs(List<Integer> var1);

    public List<Teleport> diaryItems(List<Integer> var1);

    public List<Teleport> ringOfTheElements(List<Integer> var1);

    public List<Teleport> ringOfShadows(List<Integer> var1);

    public List<Teleport> questItems(List<Integer> var1);

    public List<Teleport> combatBracelet(List<Integer> var1);

    public List<Teleport> skillsNecklace(List<Integer> var1);

    public List<Teleport> ringOfWealth(List<Integer> var1);

    public List<Teleport> amuletOfGlory(List<Integer> var1);

    public List<Teleport> burningAmulet(List<Integer> var1);

    public List<Teleport> slayerRing(List<Integer> var1);

    public List<Teleport> pohMountedTeleports();

    public List<Teleport> pohJewelryBox();

    public List<Teleport> pohPortals();

    public List<Teleport> pohSpiritFairyTree(List<Integer> var1);

    public Teleport equipableTeleport(WorldPoint var1, String var2, String var3, int ... var4);

    public Teleport pohPortalTeleport(HousePortal var1);

    public Teleport pohNexusTeleport(PortalNexus var1);

    public Teleport constructionCapeTeleport(WorldPoint var1, String var2);

    public Teleport mountedAdventureTeleport(WorldPoint var1, String var2, int var3);

    public Teleport itemTeleport(TeleportItem var1);

    public Teleport pohWidgetTeleport(WorldPoint var1, char var2, String var3);

    public Teleport mountedPohTeleport(WorldPoint var1, int var2, String var3);

    public Collection<Teleport> getSpiritTreeTeleports(Boolean var1);

    default public Collection<Teleport> getSpiritTreeTeleports() {
        return this.getSpiritTreeTeleports(true);
    }

    public Collection<Teleport> getFairyRingTeleports(Boolean var1);

    default public Collection<Teleport> getFairyRingTeleports() {
        return this.getFairyRingTeleports(true);
    }

    public Map<HousePortal, Teleport> getNexusTeleports();

    public void enterHouse();
}

