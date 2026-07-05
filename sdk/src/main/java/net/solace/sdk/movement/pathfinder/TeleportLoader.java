package net.solace.sdk.movement.pathfinder;

import java.util.List;
import net.solace.api.Static;
import net.solace.api.movement.pathfinder.ITeleportLoader;
import net.solace.api.movement.pathfinder.model.Teleport;

public class TeleportLoader {
    private static final ITeleportLoader TELEPORT_LOADER = Static.getTeleportLoader();

    public static List<Teleport> getCustomTeleports() {
        return TELEPORT_LOADER.getCustomTeleports();
    }

    public static void addCustomTeleport(Teleport teleport) {
        TeleportLoader.getCustomTeleports().add(teleport);
    }

    public static void removeCustomTeleport(Teleport teleport) {
        TeleportLoader.getCustomTeleports().remove(teleport);
    }
}

