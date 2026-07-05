package net.solace.sdk.movement.pathfinder;

import java.util.List;
import net.runelite.api.coords.WorldPoint;
import net.solace.api.Static;
import net.solace.api.movement.pathfinder.ITransportLoader;
import net.solace.api.movement.pathfinder.model.Transport;
import net.solace.api.movement.pathfinder.model.requirement.Requirements;

public class TransportLoader {
    private static final ITransportLoader TRANSPORT_LOADER = Static.getTransportLoader();

    public static List<Transport> getCustomTransports() {
        return TRANSPORT_LOADER.getCustomTransports();
    }

    public static void addCustomTransport(Transport transport) {
        TransportLoader.getCustomTransports().add(transport);
    }

    public static void removeCustomTransport(Transport transport) {
        TransportLoader.getCustomTransports().remove(transport);
    }

    public static Transport trapDoorTransport(WorldPoint source, WorldPoint destination, int closedId, int openedId) {
        return TRANSPORT_LOADER.trapDoorTransport(source, destination, closedId, openedId);
    }

    public static Transport itemUseAndObjectTransport(WorldPoint source, WorldPoint destination, int beforeItem, int afterItem, int itemId) {
        return TRANSPORT_LOADER.itemUseAndObjectTransport(source, destination, beforeItem, afterItem, itemId);
    }

    public static Transport itemUseTransport(WorldPoint source, WorldPoint destination, int itemId, int objId, int radius) {
        return TRANSPORT_LOADER.itemUseTransport(source, destination, itemId, objId, radius);
    }

    public static Transport itemUseTransport(WorldPoint source, WorldPoint destination, int itemId, int objId) {
        return TransportLoader.itemUseTransport(source, destination, itemId, objId, 5);
    }

    public static Transport itemWearTransport(int radius, WorldPoint source, WorldPoint destination, int objId, String actions, int ... itemIds) {
        return TRANSPORT_LOADER.itemWearTransport(radius, source, destination, objId, actions, itemIds);
    }

    public static Transport itemWearTransport(WorldPoint source, WorldPoint destination, int objId, String actions, int ... itemIds) {
        return TransportLoader.itemWearTransport(10, source, destination, objId, actions, itemIds);
    }

    public static Transport npcTransport(int radius, WorldPoint source, WorldPoint destination, int npcId, Requirements requirements, String ... actions) {
        return TRANSPORT_LOADER.npcTransport(radius, source, destination, npcId, requirements, actions);
    }

    public static Transport npcTransport(int radius, WorldPoint source, WorldPoint destination, String npcName, Requirements requirements, String ... actions) {
        return TRANSPORT_LOADER.npcTransport(radius, source, destination, npcName, requirements, actions);
    }

    public static Transport npcTransport(WorldPoint source, WorldPoint destination, String npcName, String ... actions) {
        return TransportLoader.npcTransport(10, source, destination, npcName, new Requirements(), actions);
    }

    public static Transport npcTransport(WorldPoint source, WorldPoint destination, int npcId, Requirements requirements, String ... actions) {
        return TransportLoader.npcTransport(10, source, destination, npcId, requirements, actions);
    }

    public static Transport npcTransport(WorldPoint source, WorldPoint destination, int npcId, String ... actions) {
        return TransportLoader.npcTransport(10, source, destination, npcId, new Requirements(), actions);
    }

    public static Transport npcTransport(int radius, WorldPoint source, WorldPoint destination, int npcId, String ... actions) {
        return TransportLoader.npcTransport(radius, source, destination, npcId, new Requirements(), actions);
    }

    public static Transport npcDialogTransport(int radius, WorldPoint source, WorldPoint destination, int npcId, Requirements requirements, String ... chatOptions) {
        return TRANSPORT_LOADER.npcDialogTransport(radius, source, destination, npcId, requirements, chatOptions);
    }

    public static Transport npcDialogTransport(int radius, WorldPoint source, WorldPoint destination, int npcId, String ... chatOptions) {
        return TransportLoader.npcDialogTransport(radius, source, destination, npcId, new Requirements(), chatOptions);
    }

    public static Transport npcDialogTransport(WorldPoint source, WorldPoint destination, int npcId, Requirements requirements, String ... chatOptions) {
        return TransportLoader.npcDialogTransport(10, source, destination, npcId, requirements, chatOptions);
    }

    public static Transport npcDialogTransport(WorldPoint source, WorldPoint destination, int npcId, String ... chatOptions) {
        return TransportLoader.npcDialogTransport(10, source, destination, npcId, new Requirements(), chatOptions);
    }

    public static Transport objectTransport(WorldPoint source, WorldPoint destination, int objId, String actions) {
        return TransportLoader.objectTransport(20, source, destination, objId, actions, new Requirements());
    }

    public static Transport objectTransport(int radius, WorldPoint source, WorldPoint destination, int objId, String actions, Requirements requirements) {
        return TRANSPORT_LOADER.objectTransport(radius, source, destination, objId, actions, requirements);
    }

    public static Transport objectDialogTransport(WorldPoint source, WorldPoint destination, int objId, String action, Requirements requirements, String ... chatOptions) {
        return TRANSPORT_LOADER.objectDialogTransport(source, destination, objId, action, requirements, chatOptions);
    }

    public static Transport objectDialogTransport(WorldPoint source, WorldPoint destination, int objId, String action, String ... chatOptions) {
        return TransportLoader.objectDialogTransport(source, destination, objId, action, new Requirements(), chatOptions);
    }

    public static Transport objectTransport(WorldPoint source, WorldPoint destination, int objId, String actions, Requirements requirements) {
        return TransportLoader.objectTransport(20, source, destination, objId, actions, requirements);
    }

    public static Transport objectTransport(int radius, WorldPoint source, WorldPoint destination, int objId, String actions) {
        return TransportLoader.objectTransport(radius, source, destination, objId, actions, new Requirements());
    }

    public static Transport slashWebTransport(WorldPoint source, WorldPoint destination) {
        return TRANSPORT_LOADER.slashWebTransport(source, destination);
    }
}

