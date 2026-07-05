package net.solace.loader;

import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.RuneLiteProperties;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.events.ExternalPluginsChanged;
import net.solace.api.containers.NpcContainer;
import net.solace.api.containers.PlayerContainer;
import net.solace.api.containers.TileContainer;
import net.solace.api.game.GameStateManager;
import net.solace.api.game.IVars;
import net.solace.api.interact.InteractManager;
import net.solace.api.items.IBank;
import net.solace.api.items.IBankInventory;
import net.solace.api.items.IEquipment;
import net.solace.api.items.IInventory;
import net.solace.api.items.ITradeInventory;
import net.solace.api.items.ITradeOther;
import net.solace.api.items.ITradeOurs;
import net.solace.api.items.loadouts.LoadoutManager;
import net.solace.api.plugins.PluginManager;
import net.solace.api.plugins.config.ConfigManager;
import net.solace.api.plugins.exception.PluginInstantiationException;
import net.solace.api.quests.IQuests;
import net.solace.impl.containers.ShipContainer;
import net.solace.impl.movement.WalkerManager;
import net.solace.impl.reflection.ReflectionManager;
import net.solace.loader.events.EventManager;
import net.solace.loader.local.LocalBootstrap;
import net.solace.loader.local.LocalVersionPackageLoader;
import net.solace.loader.thirdparty.EternalFarmCompat;
import net.solace.loader.thirdparty.IncompatiblePluginChecker;
import net.solace.loader.ui.SolaceUI;
import net.solace.sdn.plugins.version.VersionPackage;
import net.solace.ui.plugins.ProfilePanel;

import javax.annotation.Nullable;
import javax.inject.Named;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.Objects;

@Slf4j
@Singleton
public class SolaceInitializer implements SolaceManager {
    @Inject
    private Client client;

    @Inject
    private EventBus eventBus;

    @Inject
    private ConfigManager configManager;

    @Inject
    private PluginManager pluginManager;

    @Inject
    private net.runelite.client.plugins.PluginManager rlPluginManager;

    @Inject
    private SolaceUI solaceUI;

    @Inject
    private InteractManager interactManager;

    @Inject
    private EventManager eventManager;

    @Inject
    private ReflectionManager reflectionManager;

    @Inject
    private WalkerManager walkerManager;

    @Inject
    private IVars vars;

    @Inject
    private LocalBootstrap localBootstrap;

    @Inject
    private NpcContainer npcContainer;

    @Inject
    private TileContainer tileContainer;

    @Inject
    private PlayerContainer playerContainer;

    @Inject
    private IInventory inventory;

    @Inject
    private IEquipment equipment;

    @Inject
    private IBank bank;

    @Inject
    private IBankInventory bankInventory;

    @Inject
    private ITradeInventory tradeInventory;

    @Inject
    private ITradeOther tradeOther;

    @Inject
    private ITradeOurs tradeOurs;

    @Inject
    private IQuests quests;

    @Inject
    private GameStateManager gameStateManager;

    @Inject
    private ProfilePanel profilePanel;

    @Inject
    private LoadoutManager loadoutManager;

    @Inject
    private IncompatiblePluginChecker incompatiblePluginChecker;

    @Inject
    private ShipContainer shipContainer;

    @Inject
    @Named("ef")
    @Nullable
    private String eternalFarmArg;

    @Override
    public void start() throws Exception {
        incompatiblePluginChecker.checkAndDisable();
        onVersionPackageReady(LocalVersionPackageLoader.load());
    }

    private void onVersionPackageReady(VersionPackage versionPackage) {
        var startTime = System.currentTimeMillis();

        if (!Objects.equals(versionPackage.getRuneLiteCommit(), RuneLiteProperties.getCommit())
                || !Objects.equals(versionPackage.getRuneLiteVersion(), RuneLiteProperties.getVersion())) {
            throw new IllegalStateException("Solace has not yet been updated for this version of RuneLite, please wait for an update." +
                    " If you'd like to use RuneLite normally, please restart the client and select the 'Normal RuneLite' option.");
        }

        try {
            reflectionManager.load(client.getClass().getClassLoader(), versionPackage);
        } catch (Exception e) {
            throw new RuntimeException("Solace failed to initialize.", e);
        }

        configManager.load();

        registerEventManagers();

        solaceUI.init();

        profilePanel.init();

        try {
            rlPluginManager.loadSideLoadPlugins();
            rlPluginManager.loadDefaultPluginConfiguration(null);
            rlPluginManager.startPlugins();
            eventBus.post(new ExternalPluginsChanged());
        } catch (Exception e) {
            log.error("Error loading side-load plugins", e);
        }

        try {
            pluginManager.loadCorePlugins();
            pluginManager.startCorePlugins();
        } catch (IOException | PluginInstantiationException e) {
            log.error("Error loading core plugins", e);
        }

        localBootstrap.loadPlugins();

        if (eternalFarmArg != null) {
            EternalFarmCompat.init(eternalFarmArg, pluginManager.getClass().getClassLoader());
        }

        log.info("Solace initialized in {} ms", System.currentTimeMillis() - startTime);
    }

    @Override
    public void unload() {
        pluginManager.stopPlugins();

        eventBus.unregister(configManager);
        eventBus.unregister(interactManager);
        eventBus.unregister(eventManager);
        eventBus.unregister(walkerManager);
        eventBus.unregister(vars);
        eventBus.unregister(gameStateManager);
        eventBus.unregister(loadoutManager);
        eventBus.unregister(npcContainer);
        eventBus.unregister(tileContainer);
        eventBus.unregister(playerContainer);
        eventBus.unregister(inventory);
        eventBus.unregister(equipment);
        eventBus.unregister(bank);
        eventBus.unregister(bankInventory);
        eventBus.unregister(tradeInventory);
        eventBus.unregister(tradeOther);
        eventBus.unregister(tradeOurs);
        eventBus.unregister(quests);
        eventBus.unregister(shipContainer);

        solaceUI.clear();
    }

    private void registerEventManagers() {
        eventManager.init();
        walkerManager.init();

        eventBus.register(configManager);
        eventBus.register(interactManager);
        eventBus.register(eventManager);
        eventBus.register(walkerManager);
        eventBus.register(vars);
        eventBus.register(gameStateManager);
        eventBus.register(loadoutManager);
        eventBus.register(npcContainer);
        eventBus.register(tileContainer);
        eventBus.register(playerContainer);
        eventBus.register(inventory);
        eventBus.register(equipment);
        eventBus.register(bank);
        eventBus.register(bankInventory);
        eventBus.register(tradeInventory);
        eventBus.register(tradeOther);
        eventBus.register(tradeOurs);
        eventBus.register(quests);
        eventBus.register(shipContainer);
    }
}
