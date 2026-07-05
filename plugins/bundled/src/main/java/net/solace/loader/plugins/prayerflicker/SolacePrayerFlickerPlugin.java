package net.solace.loader.plugins.prayerflicker;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.input.KeyListener;
import net.runelite.client.input.KeyManager;
import net.solace.api.interact.InteractMethod;
import net.solace.api.plugins.Plugin;
import net.solace.api.plugins.PluginDescriptor;
import net.solace.api.plugins.config.ConfigManager;
import net.solace.sdk.entities.Players;
import net.solace.sdk.widgets.Prayers;

import javax.inject.Inject;
import java.awt.event.KeyEvent;

@PluginDescriptor(
        name = "Solace Prayer Flicker"
)
@Slf4j
public class SolacePrayerFlickerPlugin extends Plugin implements KeyListener {
    private boolean toggled = false;
    private boolean prayersEnabledByPlugin = false;

    @Inject
    private SolacePrayerFlickerConfig solacePrayerFlickerConfig;

    @Inject
    private KeyManager keyManager;

    @Override
    public void startUp() {
        toggled = false;
        keyManager.registerKeyListener(this);
    }

    @Override
    public void shutDown() throws Exception {
        keyManager.unregisterKeyListener(this);
    }

    @Subscribe
    public void onGameTick(GameTick tick) {
        if (!toggled || (solacePrayerFlickerConfig.onlyInCombat() && !isInCombat())) {
            if (Prayers.isQuickPrayerEnabled() && prayersEnabledByPlugin) {
                prayersEnabledByPlugin = false;
                Prayers.disableAll(InteractMethod.INVOKE);
            }

            return;
        }

        if (Prayers.isQuickPrayerEnabled()) {
            Prayers.toggleQuickPrayer(InteractMethod.INVOKE);
        }

        Prayers.toggleQuickPrayer(InteractMethod.INVOKE);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (solacePrayerFlickerConfig.flickToggleKeybind().getKeyCode() == e.getKeyCode()) {
            log.info("Toggle PrayerFlick");
            toggled = !toggled;
            prayersEnabledByPlugin = true;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Provides
    SolacePrayerFlickerConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(SolacePrayerFlickerConfig.class);
    }

    private boolean isInCombat() {
        var local = Players.getLocal();
        if (local == null) {
            return false;
        }

        return local.getInteracting() != null || local.getHealthRatio() != -1;
    }
}
