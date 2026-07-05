package net.solace.api.movement.pathfinder;

import java.util.List;
import net.runelite.api.coords.WorldPoint;
import net.solace.api.movement.pathfinder.model.BirdFlight;
import net.solace.api.movement.pathfinder.model.CharterShip;
import net.solace.api.movement.pathfinder.model.FairyRing;
import net.solace.api.movement.pathfinder.model.GnomeGlider;
import net.solace.api.movement.pathfinder.model.IgnoredDoor;
import net.solace.api.movement.pathfinder.model.MagicCarpet;
import net.solace.api.movement.pathfinder.model.Transport;
import net.solace.api.movement.pathfinder.model.requirement.Requirements;

public interface ITransportLoader {
    public static final int MAX_INTERACT_DISTANCE = 20;
    public static final int MAX_RADIUS = 10;

    public void init();

    public List<Transport> getCustomTransports();

    public List<Transport> buildTransports(boolean var1);

    default public List<Transport> buildTransports() {
        return this.buildTransports(false);
    }

    public void refreshTransports(List<Integer> var1);

    default public void refreshTransports() {
        this.refreshTransports(List.of());
    }

    public List<Transport> getLastTransports();

    public List<IgnoredDoor> getLastIgnoredDoors();

    public Transport trapDoorTransport(WorldPoint var1, WorldPoint var2, int var3, int var4);

    public Transport itemUseAndObjectTransport(WorldPoint var1, WorldPoint var2, int var3, int var4, int var5);

    public Transport fairyRingTransport(FairyRing var1, FairyRing var2);

    public Transport itemUseTransport(WorldPoint var1, WorldPoint var2, int var3, int var4, int var5);

    default public Transport itemUseTransport(WorldPoint source, WorldPoint destination, int itemId, int objId) {
        return this.itemUseTransport(source, destination, itemId, objId, 5);
    }

    public Transport itemWearTransport(int var1, WorldPoint var2, WorldPoint var3, int var4, String var5, int ... var6);

    default public Transport itemWearTransport(WorldPoint source, WorldPoint destination, int objId, String actions, int ... itemIds) {
        return this.itemWearTransport(10, source, destination, objId, actions, itemIds);
    }

    public Transport npcTransport(int var1, WorldPoint var2, WorldPoint var3, int var4, Requirements var5, String ... var6);

    public Transport npcTransport(int var1, WorldPoint var2, WorldPoint var3, String var4, Requirements var5, String ... var6);

    default public Transport npcTransport(WorldPoint source, WorldPoint destination, String npcName, String ... actions) {
        return this.npcTransport(10, source, destination, npcName, new Requirements(), actions);
    }

    default public Transport npcTransport(WorldPoint source, WorldPoint destination, int npcId, Requirements requirements, String ... actions) {
        return this.npcTransport(10, source, destination, npcId, requirements, actions);
    }

    default public Transport npcTransport(WorldPoint source, WorldPoint destination, int npcId, String ... actions) {
        return this.npcTransport(10, source, destination, npcId, new Requirements(), actions);
    }

    default public Transport npcTransport(int radius, WorldPoint source, WorldPoint destination, int npcId, String ... actions) {
        return this.npcTransport(radius, source, destination, npcId, new Requirements(), actions);
    }

    public Transport npcDialogTransport(int var1, WorldPoint var2, WorldPoint var3, int var4, Requirements var5, String ... var6);

    default public Transport npcDialogTransport(int radius, WorldPoint source, WorldPoint destination, int npcId, String ... chatOptions) {
        return this.npcDialogTransport(radius, source, destination, npcId, new Requirements(), chatOptions);
    }

    default public Transport npcDialogTransport(WorldPoint source, WorldPoint destination, int npcId, Requirements requirements, String ... chatOptions) {
        return this.npcDialogTransport(10, source, destination, npcId, requirements, chatOptions);
    }

    default public Transport npcDialogTransport(WorldPoint source, WorldPoint destination, int npcId, String ... chatOptions) {
        return this.npcDialogTransport(10, source, destination, npcId, new Requirements(), chatOptions);
    }

    public Transport objectTransport(WorldPoint var1, WorldPoint var2, int var3, String var4);

    public Transport objectTransport(int var1, WorldPoint var2, WorldPoint var3, int var4, String var5, Requirements var6);

    public Transport objectDialogTransport(WorldPoint var1, WorldPoint var2, int var3, String var4, Requirements var5, String ... var6);

    default public Transport objectDialogTransport(WorldPoint source, WorldPoint destination, int objId, String action, String ... chatOptions) {
        return this.objectDialogTransport(source, destination, objId, action, new Requirements(), chatOptions);
    }

    default public Transport objectTransport(WorldPoint source, WorldPoint destination, int objId, String actions, Requirements requirements) {
        return this.objectTransport(20, source, destination, objId, actions, requirements);
    }

    default public Transport objectTransport(int radius, WorldPoint source, WorldPoint destination, int objId, String actions) {
        return this.objectTransport(radius, source, destination, objId, actions, new Requirements());
    }

    public Transport slashWebTransport(WorldPoint var1, WorldPoint var2);

    public Transport spritTreeTransport(WorldPoint var1, WorldPoint var2, String var3);

    public Transport mushtreeTransport(WorldPoint var1, WorldPoint var2, int var3);

    public Transport charterShip(CharterShip var1);

    public Transport magicCarpet(MagicCarpet var1);

    public Transport birdFlight(BirdFlight var1, BirdFlight var2);

    public Transport gnomeGlider(GnomeGlider var1);

    public Transport minecartTransport(WorldPoint var1, WorldPoint var2, String var3);
}

