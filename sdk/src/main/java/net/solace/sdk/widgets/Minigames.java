package net.solace.sdk.widgets;

import java.time.Instant;
import net.solace.api.Static;
import net.solace.api.widgets.IMinigames;
import net.solace.api.widgets.MinigameTeleport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Minigames {
    private static final Logger log = LoggerFactory.getLogger(Minigames.class);
    private static final IMinigames MINIGAMES = Static.getMinigames();

    public static boolean canTeleport() {
        return MINIGAMES.canTeleport();
    }

    public static boolean teleport(MinigameTeleport destination) {
        return MINIGAMES.teleport(destination);
    }

    public static boolean open() {
        return MINIGAMES.open();
    }

    public static boolean isOpen() {
        return MINIGAMES.isOpen();
    }

    public static boolean isTabOpen() {
        return MINIGAMES.isTabOpen();
    }

    public static Instant getLastMinigameTeleportUsage() {
        return MINIGAMES.getLastMinigameTeleportUsage();
    }
}

