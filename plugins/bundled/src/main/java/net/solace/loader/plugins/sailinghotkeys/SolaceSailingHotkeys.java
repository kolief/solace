package net.solace.loader.plugins.sailinghotkeys;

import com.google.inject.Provides;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.input.KeyListener;
import net.runelite.client.input.KeyManager;
import net.solace.api.Static;
import net.solace.api.movement.pathfinder.model.sailing.SailingDirection;
import net.solace.api.plugins.Plugin;
import net.solace.api.plugins.PluginDescriptor;
import net.solace.api.plugins.config.ConfigManager;
import net.solace.sdk.movement.sailing.Sailing;

import javax.inject.Inject;
import java.awt.event.KeyEvent;

@PluginDescriptor(
        name = "Solace Sailing Hotkeys"
)
@Slf4j
public class SolaceSailingHotkeys extends Plugin implements KeyListener {

    @Inject
    private SolaceSailingHotkeysConfig config;

    @Inject
    private KeyManager keyManager;

    @Getter
    @Setter
    private int currentDirection = -1;

    @Override
    public void startUp() {
        keyManager.registerKeyListener(this);
    }

    @Override
    public void shutDown() throws Exception {
        keyManager.unregisterKeyListener(this);
    }

    @Provides
    SolaceSailingHotkeysConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(SolaceSailingHotkeysConfig.class);
    }

    public void steerLeft() {
        if (Sailing.isOnBoat() && Sailing.isNavigating()) {
            int currentDirectionCode = getCurrentDirection();
            int nextCode = (currentDirectionCode - 1 + 16) % 16;
            SailingDirection direction = SailingDirection.fromCode(nextCode);
            Sailing.setDirection(direction);
            setCurrentDirection(nextCode);
        }
    }

    public void steerRight() {
        if (Sailing.isOnBoat() && Sailing.isNavigating()) {
            int currentDirectionCode = getCurrentDirection();
            int nextCode = (currentDirectionCode + 1) % 16;
            SailingDirection direction = SailingDirection.fromCode(nextCode);
            Sailing.setDirection(direction);
            setCurrentDirection(nextCode);
        }
    }

    public void increaseSpeed() {
        if (Sailing.isOnBoat()) {
            if (Sailing.isNavigating()) {
                Sailing.increaseSpeed();
            } else {
                Sailing.navigate();
            }
        }
    }

    public void decreaseSpeed() {
        if (Sailing.isOnBoat() && Sailing.isNavigating()) {
            Sailing.decreaseSpeed();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        Static.getClientThread().invokeLater(() -> {
            if (!Sailing.isOnBoat()) {
                return;
            }

            if (getCurrentDirection() == -1) {
                setCurrentDirection(Sailing.getDirection().getCode());
            }

            var code = e.getKeyCode();

            switch (code) {
                case KeyEvent.VK_A:
                    steerLeft();
                    break;
                case KeyEvent.VK_D:
                    steerRight();
                    break;
                case KeyEvent.VK_W:
                    increaseSpeed();
                    break;
                case KeyEvent.VK_S:
                    decreaseSpeed();
                    break;
            }
        });

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
