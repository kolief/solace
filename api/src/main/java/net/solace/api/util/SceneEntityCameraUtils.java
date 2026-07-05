package net.solace.api.util;

import java.awt.Rectangle;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Player;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.solace.api.Static;
import net.solace.api.domain.SceneEntity;
import net.solace.api.domain.game.IClient;
import net.solace.api.domain.game.IClientThread;

public final class SceneEntityCameraUtils {
    private SceneEntityCameraUtils() {
    }

    public static CameraSnapshot captureSnapshot() {
        IClient client = Static.getClient();
        IClientThread clientThread = Static.getGameThread();
        if (client == null || clientThread == null) {
            return null;
        }
        try {
            if (client.isClientThread()) {
                return SceneEntityCameraUtils.captureSnapshot(client);
            }
            return clientThread.invokeAndWait(() -> SceneEntityCameraUtils.captureSnapshot(client));
        }
        catch (Exception ignored) {
            return null;
        }
    }

    private static CameraSnapshot captureSnapshot(IClient client) {
        Client wrapped = (Client)client.getWrapped();
        if (wrapped == null) {
            return null;
        }
        Player localPlayer = wrapped.getLocalPlayer();
        if (localPlayer == null || localPlayer.getLocalLocation() == null) {
            return null;
        }
        return new CameraSnapshot(wrapped, wrapped.getCameraYaw(), localPlayer.getLocalLocation(), localPlayer.getWorldLocation(), new Rectangle(wrapped.getViewportXOffset(), wrapped.getViewportYOffset(), wrapped.getViewportWidth(), wrapped.getViewportHeight()));
    }

    public static boolean isOnScreen(SceneEntity entity, CameraSnapshot snapshot) {
        if (entity == null || snapshot == null) {
            return false;
        }
        LocalPoint localLocation = entity.getLocalLocation();
        if (localLocation == null) {
            return false;
        }
        Point canvasPoint = Perspective.localToCanvas((Client)snapshot.getClient(), (LocalPoint)localLocation, (int)entity.getPlane());
        return canvasPoint != null && snapshot.getViewport().contains(canvasPoint.getX(), canvasPoint.getY());
    }

    public static int getYawDelta(SceneEntity entity, CameraSnapshot snapshot) {
        if (entity == null || snapshot == null) {
            return Integer.MAX_VALUE;
        }
        LocalPoint entityLocal = entity.getLocalLocation();
        LocalPoint playerLocal = snapshot.getPlayerLocalLocation();
        if (entityLocal == null || playerLocal == null) {
            return Integer.MAX_VALUE;
        }
        int dx = entityLocal.getX() - playerLocal.getX();
        int dy = entityLocal.getY() - playerLocal.getY();
        double radians = Math.atan2(dx, dy);
        int gameAngle = (int)(radians / (Math.PI * 2) * 2048.0);
        int targetYaw = (gameAngle + 2048) % 2048;
        return Math.abs(SceneEntityCameraUtils.shortestYawDelta(snapshot.getCameraYaw(), targetYaw));
    }

    private static int shortestYawDelta(int current, int target) {
        int delta = (target - current + 2048) % 2048;
        if (delta > 1024) {
            delta -= 2048;
        }
        return delta;
    }

    public static final class CameraSnapshot {
        private final Client client;
        private final int cameraYaw;
        private final LocalPoint playerLocalLocation;
        private final WorldPoint playerWorldLocation;
        private final Rectangle viewport;

        public CameraSnapshot(Client client, int cameraYaw, LocalPoint playerLocalLocation, WorldPoint playerWorldLocation, Rectangle viewport) {
            this.client = client;
            this.cameraYaw = cameraYaw;
            this.playerLocalLocation = playerLocalLocation;
            this.playerWorldLocation = playerWorldLocation;
            this.viewport = viewport;
        }

        public Client getClient() {
            return this.client;
        }

        public int getCameraYaw() {
            return this.cameraYaw;
        }

        public LocalPoint getPlayerLocalLocation() {
            return this.playerLocalLocation;
        }

        public WorldPoint getPlayerWorldLocation() {
            return this.playerWorldLocation;
        }

        public Rectangle getViewport() {
            return this.viewport;
        }
    }
}

