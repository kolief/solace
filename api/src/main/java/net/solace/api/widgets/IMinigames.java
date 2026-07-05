package net.solace.api.widgets;

import java.time.Instant;
import net.solace.api.widgets.MinigameTeleport;

public interface IMinigames {
    public boolean canTeleport();

    public boolean teleport(MinigameTeleport var1);

    public boolean open();

    public boolean isOpen();

    public boolean isTabOpen();

    public Instant getLastMinigameTeleportUsage();
}

