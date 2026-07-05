package net.solace.loader.plugins.profiles;

import com.google.inject.Inject;
import com.google.inject.Provides;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;
import net.solace.api.plugins.Plugin;
import net.solace.api.plugins.PluginDescriptor;
import net.solace.api.plugins.config.ConfigManager;
import net.solace.loader.plugins.profiles.data.AccountManager;
import net.solace.loader.plugins.profiles.panel.AccountSwitcherPanel;

@PluginDescriptor(
        name = "Solace Profiles",
        description = "Solace Profiles",
        tags = {"solace", "profiles"},
        enabledByDefault = true
)
public class SolaceProfiles extends Plugin {
    private AccountSwitcherPanel panel;
    private AccountManager accountManager;
    private NavigationButton navButton;

    @Inject
    private ClientToolbar clientToolbar;

    @Override
    public void startUp() {
        accountManager = new AccountManager();
        panel = new AccountSwitcherPanel(accountManager);

        var icon = ImageUtil.loadImageResource(SolaceProfiles.class, "profiles_icon.png");
        navButton = NavigationButton.builder().tooltip("Solace Profiles").icon(icon).priority(1).panel(panel).build();

        clientToolbar.addNavigation(navButton);
    }

    @Override
    public void shutDown() throws Exception {
        clientToolbar.removeNavigation(navButton);
    }

    @Provides
    SolaceProfilesConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(SolaceProfilesConfig.class);
    }
}
