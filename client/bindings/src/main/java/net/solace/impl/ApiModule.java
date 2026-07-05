package net.solace.impl;

import com.google.inject.AbstractModule;
import com.google.inject.Provider;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Constants;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.game.WorldService;
import net.solace.api.Static;
import net.solace.api.breaks.BreakHandler;
import net.solace.api.movement.ISailing;
import net.solace.api.sailing.IShips;
import net.solace.impl.breaks.BreakHandlerImpl;
import net.solace.api.commons.ITime;
import net.solace.impl.commons.TimeImpl;
import net.solace.api.containers.NpcContainer;
import net.solace.impl.containers.NpcContainerImpl;
import net.solace.api.containers.PlayerContainer;
import net.solace.impl.containers.PlayerContainerImpl;
import net.solace.api.containers.TileContainer;
import net.solace.impl.containers.ShipContainer;
import net.solace.impl.containers.TileContainerImpl;
import net.solace.impl.domain.game.ClientImpl;
import net.solace.impl.domain.game.ClientThreadImpl;
import net.solace.api.domain.game.IClient;
import net.solace.api.domain.game.IClientThread;
import net.solace.impl.domain.game.VarsImpl;
import net.solace.impl.domain.game.WorldMapImpl;
import net.solace.api.entities.INPCs;
import net.solace.api.entities.IPlayers;
import net.solace.api.entities.ITileItems;
import net.solace.api.entities.ITileObjects;
import net.solace.api.entities.ITiles;
import net.solace.impl.entities.NPCsImpl;
import net.solace.impl.entities.PlayersImpl;
import net.solace.impl.entities.TileItemsImpl;
import net.solace.impl.entities.TileObjectsImpl;
import net.solace.impl.entities.TilesImpl;
import net.solace.impl.game.CombatImpl;
import net.solace.impl.game.GameImpl;
import net.solace.api.game.GameStateManager;
import net.solace.impl.game.GameStateManagerImpl;
import net.solace.impl.game.HouseImpl;
import net.solace.api.game.ICombat;
import net.solace.api.game.IGame;
import net.solace.api.game.IHouse;
import net.solace.api.game.ISkills;
import net.solace.api.game.IVars;
import net.solace.api.game.IWorldMap;
import net.solace.api.game.IWorlds;
import net.solace.impl.game.SkillsImpl;
import net.solace.impl.game.WorldsImpl;
import net.solace.api.input.IKeyboard;
import net.solace.impl.input.KeyboardImpl;
import net.solace.api.interact.InteractManager;
import net.solace.api.interact.builder.IMenuFactory;
import net.solace.impl.interact.builder.MenuFactoryImpl;
import net.solace.impl.items.DepositBoxImpl;
import net.solace.impl.items.BankImpl;
import net.solace.impl.items.BankInventoryImpl;
import net.solace.impl.items.EquipmentImpl;
import net.solace.impl.items.GrandExchangeImpl;
import net.solace.api.items.IDepositBox;
import net.solace.api.items.IBank;
import net.solace.api.items.IBankInventory;
import net.solace.api.items.IEquipment;
import net.solace.api.items.IGrandExchange;
import net.solace.api.items.IInventory;
import net.solace.api.items.ITrade;
import net.solace.api.items.ITradeInventory;
import net.solace.api.items.ITradeOther;
import net.solace.api.items.ITradeOurs;
import net.solace.impl.items.InventoryImpl;
import net.solace.impl.items.TradeImpl;
import net.solace.impl.items.TradeInventoryImpl;
import net.solace.impl.items.TradeOtherImpl;
import net.solace.impl.items.TradeOursImpl;
import net.solace.api.items.loadouts.ILoadoutFactory;
import net.solace.impl.items.loadouts.LoadoutFactoryImpl;
import net.solace.api.items.loadouts.LoadoutManager;
import net.solace.impl.items.loadouts.LoadoutManagerImpl;
import net.solace.api.magic.IMagic;
import net.solace.impl.magic.MagicImpl;
import net.solace.api.movement.IMovement;
import net.solace.api.movement.IReachable;
import net.solace.api.movement.IWalker;
import net.solace.impl.movement.MovementImpl;
import net.solace.impl.movement.pathfinder.ChargeManagerImpl;
import net.solace.impl.movement.ReachableImpl;
import net.solace.impl.movement.SailingImpl;
import net.solace.impl.movement.WalkerImpl;
import net.solace.impl.movement.WalkerManager;
import net.solace.api.movement.pathfinder.IChargeManager;
import net.solace.api.movement.pathfinder.GlobalCollisionMap;
import net.solace.api.movement.pathfinder.ITeleportLoader;
import net.solace.api.movement.pathfinder.ITransportLoader;
import net.solace.impl.movement.pathfinder.TeleportLoaderImpl;
import net.solace.impl.movement.pathfinder.TransportLoaderImpl;
import net.solace.api.plugins.IPlugins;
import net.solace.api.plugins.PluginManager;
import net.solace.impl.plugins.PluginsImpl;
import net.solace.api.plugins.config.ConfigManager;
import net.solace.api.plugins.config.SolaceConfig;
import net.solace.api.quests.IQuests;
import net.solace.impl.quests.QuestsImpl;
import net.solace.impl.sailing.ShipsImpl;
import net.solace.impl.widgets.BankWornItemsImpl;
import net.solace.impl.widgets.DialogImpl;
import net.solace.impl.widgets.FriendsImpl;
import net.solace.api.widgets.IBankWornItems;
import net.solace.api.widgets.IDialog;
import net.solace.api.widgets.IFriends;
import net.solace.api.widgets.IMinigames;
import net.solace.api.widgets.IPrayers;
import net.solace.api.widgets.IProduction;
import net.solace.api.widgets.ITabs;
import net.solace.api.widgets.IWidgets;
import net.solace.impl.widgets.MinigamesImpl;
import net.solace.impl.widgets.PrayersImpl;
import net.solace.impl.widgets.ProductionImpl;
import net.solace.impl.widgets.TabsImpl;
import net.solace.impl.widgets.WidgetsImpl;

import javax.annotation.Nullable;
import javax.inject.Singleton;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

@Slf4j
public class ApiModule extends AbstractModule {
    @Override
    protected void configure() {
        requestStaticInjection(Static.class);
    }

    @Provides
    @Singleton
    GlobalCollisionMap provideGlobalCollisionMap() throws IOException {
        try (var is = getClass().getResourceAsStream("/regions")) {
            if (is == null) {
                log.warn("Failed to load regions, falling back to empty collision map");
                return new GlobalCollisionMap();
            }

            return new GlobalCollisionMap((new GZIPInputStream(new ByteArrayInputStream(is.readAllBytes())).readAllBytes()));
        }
    }

    @Provides
    @Singleton
    SolaceConfig provideSolaceConfig(ConfigManager configManager) {
        return configManager.getConfig(SolaceConfig.class);
    }

    @Provides
    @Singleton
    IClient provideClient(@Nullable Client client, Provider<IClientThread> clientThreadProvider,
                          Provider<InteractManager> interactManager, Provider<NpcContainer> npcContainerProvider,
                          Provider<PlayerContainer> playerContainerProvider, Provider<TileContainer> tileContainerProvider,
                          Provider<ShipContainer> shipContainerProvider) {
        return new ClientImpl(client, clientThreadProvider, interactManager, npcContainerProvider,
                playerContainerProvider, tileContainerProvider, shipContainerProvider);
    }

    @Provides
    @Singleton
    IClientThread provideClientThread(ClientThread clientThread, IClient client) {
        return new ClientThreadImpl(clientThread, client);
    }

    @Provides
    @Singleton
    NpcContainer provideNpcContainer(IClient client) {
        return new NpcContainerImpl(client, -1);
    }

    @Provides
    @Singleton
    INPCs provideNpcs(NpcContainer npcContainer, PlayerContainer playerContainer) {
        return new NPCsImpl(npcContainer, playerContainer);
    }

    @Provides
    @Singleton
    PlayerContainer providePlayerContainer(IClient client) {
        return new PlayerContainerImpl(client, -1);
    }

    @Provides
    @Singleton
    IPlayers providePlayers(PlayerContainer playerContainer) {
        return new PlayersImpl(playerContainer);
    }

    @Provides
    @Singleton
    TileContainer provideTileContainer(IClient client) {
        return new TileContainerImpl(client, -1, Constants.SCENE_SIZE, Constants.SCENE_SIZE);
    }

    @Provides
    @Singleton
    ITiles provideTiles(IClient client, TileContainer tileContainer) {
        return new TilesImpl(client, tileContainer);
    }

    @Provides
    @Singleton
    ITileItems provideTileItems(IClient client, ITiles tiles) {
        return new TileItemsImpl(tiles, client);
    }

    @Provides
    @Singleton
    ITileObjects provideTileObjects(IClient client, ITiles tiles) {
        return new TileObjectsImpl(tiles, client);
    }

    @Provides
    @Singleton
    IVars provideIVars(IClient client) {
        return new VarsImpl(client);
    }

    @Provides
    @Singleton
    IGame provideIGame(IClient client, IVars vars, IWidgets widgets, ITabs tabs, GameStateManager gameStateManager) {
        return new GameImpl(client, vars, widgets, tabs, gameStateManager);
    }

    @Provides
    @Singleton
    ISkills provideISkills(IClient client) {
        return new SkillsImpl(client);
    }

    @Provides
    @Singleton
    IBankInventory provideIBankInventory(IWidgets widgets, IClient client) {
        return new BankInventoryImpl(widgets, client);
    }

    @Provides
    @Singleton
    IBank provideIBank(IClient client, IPlayers players, IWidgets widgets,
                       ITileObjects tileObjects, IMovement movement, IVars vars, IDialog dialog,
                       IBankInventory inventory, InteractManager interactManager, INPCs npcs) {
        return new BankImpl(widgets, client, players, tileObjects, movement, vars, dialog, inventory, interactManager, npcs);
    }

    @Provides
    @Singleton
    IEquipment provideIEquipment(IWidgets widgets, IClient client) {
        return new EquipmentImpl(widgets, client);
    }

    @Provides
    @Singleton
    IInventory provideIInventory(IWidgets widgets, IClient client) {
        return new InventoryImpl(widgets, client);
    }

    @Provides
    @Singleton
    IWidgets provideIWidgets(IClient client) {
        return new WidgetsImpl(client);
    }

    @Provides
    @Singleton
    IWorldMap provideIWorldMap(IClient client) {
        return new WorldMapImpl(client);
    }

    @Provides
    @Singleton
    IPlugins provideIPlugins(PluginManager pluginManager) {
        return new PluginsImpl(pluginManager);
    }

    @Provides
    @Singleton
    IQuests provideIQuests(IClient client, WalkerManager walkerManager) {
        return new QuestsImpl(client, walkerManager);
    }

    @Provides
    @Singleton
    ITradeOurs provideITradeOurs(IWidgets widgets, IClient client) {
        return new TradeOursImpl(widgets, client);
    }

    @Provides
    @Singleton
    ITrade provideITrade(ITradeOurs tradeOurs, ITradeOther tradeOther, ITradeInventory tradeInventory, IDialog dialog, IWidgets widgets, IVars vars, InteractManager interactManager) {
        return new TradeImpl(tradeOurs, tradeOther, tradeInventory, dialog, widgets, vars, interactManager);
    }

    @Provides
    @Singleton
    ITradeInventory provideITradeInventory(IWidgets widgets, IClient client) {
        return new TradeInventoryImpl(widgets, client);
    }

    @Provides
    @Singleton
    ITradeOther provideITradeOther(IWidgets widgets, IClient client) {
        return new TradeOtherImpl(widgets, client);
    }

    @Provides
    @Singleton
    IHouse provideIHouse(IClient client, IGame game, IVars vars, ITileObjects tileObjects, ITiles tiles, IInventory inventory, IEquipment equipment) {
        return new HouseImpl(client, game, vars, tileObjects, tiles, inventory, equipment);
    }

    @Provides
    @Singleton
    IMagic provideIMagic(InteractManager interactManager, IClient client, IVars vars, IWidgets widgets) {
        return new MagicImpl(interactManager, client, vars, widgets);
    }

    @Provides
    @Singleton
    ITime provideITime(IClient client, IGame game) {
        return new TimeImpl(client, game);
    }

    @Provides
    @Singleton
    IKeyboard provideIKeyboard(IClient client, ITime time) {
        return new KeyboardImpl(client, time);
    }

    @Provides
    @Singleton
    IDialog provideIDialog(IWidgets widgets, IKeyboard keyboard, IClient client,
                           IGrandExchange grandExchange, InteractManager interactManager) {
        return new DialogImpl(widgets, keyboard, client, grandExchange, interactManager);
    }

    @Provides
    @Singleton
    IWorlds provideIWorlds(WorldService worldService, IClient client, IGame game, IWidgets widgets, IKeyboard keyboard) {
        return new WorldsImpl(worldService, client, game, widgets, keyboard);
    }

    @Provides
    @Singleton
    IGrandExchange provideIGrandExchange(IWidgets widgets, IVars vars) {
        return new GrandExchangeImpl(widgets, vars);
    }

    @Provides
    @Singleton
    ITabs provideITabs(IGame game, IClient client, IVars vars) {
        return new TabsImpl(game, client, vars);
    }

    @Provides
    @Singleton
    IMinigames provideIMinigames(IDialog dialog, IWidgets widgets, IBank bank, IGrandExchange grandExchange,
                                 IClient client, IVars vars, IPlayers players, ITabs tabs) {
        return new MinigamesImpl(dialog, widgets, bank, grandExchange, client, vars, players, tabs);
    }

    @Provides
    @Singleton
    IReachable provideIReachable(IClient client, ITiles tiles) {
        return new ReachableImpl(client, tiles);
    }

    @Provides
    @Singleton
    IPrayers provideIPrayers(ISkills skills, IWidgets widgets, IVars vars, IClientThread clientThread) {
        return new PrayersImpl(skills, widgets, vars, clientThread);
    }

    @Provides
    @Singleton
    ICombat provideICombat(IVars vars, ISkills skills, IPlayers players, INPCs npcs, IWidgets widgets, IEquipment equipment) {
        return new CombatImpl(vars, skills, players, npcs, widgets, equipment);
    }

    @Provides
    @Singleton
    ITransportLoader provideITransportLoader(
            IClientThread clientThread,
            IInventory inventory,
            IWorlds worlds,
            IVars vars,
            IQuests quests,
            ITileObjects tileObjects,
            IEquipment equipment,
            IWidgets widgets,
            INPCs npcs,
            IPlayers players,
            IDialog dialog,
            ISkills skills
    ) {
        return new TransportLoaderImpl(
                clientThread,
                inventory,
                worlds,
                vars,
                quests,
                tileObjects,
                equipment,
                widgets,
                npcs,
                players,
                dialog,
                skills
        );
    }

    @Provides
    @Singleton
    ITeleportLoader provideITeleportLoader(
            SolaceConfig solaceConfig,
            IClientThread GameThread,
            IWorlds Worlds,
            IGame Game,
            IHouse House,
            ITileObjects TileObjects,
            IClient Client,
            IPlayers Players,
            IInventory Inventory,
            IEquipment Equipment,
            IWidgets Widgets,
            IVars Vars,
            IDialog Dialog,
            IQuests Quests,
            IMinigames Minigames,
            IKeyboard Keyboard,
            ITime Time
    ) {
        return new TeleportLoaderImpl(
                solaceConfig,
                GameThread,
                Worlds,
                Game,
                House,
                TileObjects,
                Client,
                Players,
                Inventory,
                Equipment,
                Widgets,
                Vars,
                Dialog,
                Quests,
                Minigames,
                Keyboard,
                Time
        );
    }

    @Provides
    @Singleton
    IWalker provideIWalker(
            SolaceConfig solaceConfig,
            IClient client,
            IGame game,
            IWidgets widgets,
            IVars vars,
            IClientThread clientThread,
            IHouse house,
            IBank bank,
            IGrandExchange grandExchange,
            IPlayers players,
            INPCs npcs,
            ITileObjects tileObjects,
            ITransportLoader transportLoader,
            ITeleportLoader teleportLoader,
            IEquipment equipment,
            IInventory inventory,
            IDialog dialog,
            ITiles tiles,
            IReachable reachable,
            WalkerManager walkerManager
    ) {
        return new WalkerImpl(
                solaceConfig,
                client,
                game,
                widgets,
                vars,
                clientThread,
                house,
                bank,
                grandExchange,
                players,
                npcs,
                tileObjects,
                transportLoader,
                teleportLoader,
                equipment,
                inventory,
                dialog,
                tiles,
                reachable,
                walkerManager
        );
    }

    @Provides
    @Singleton
    IMovement provideIMovement(
            SolaceConfig solaceConfig,
            IClient client,
            IWalker walker,
            IVars vars,
            IWidgets widgets
    ) {
        return new MovementImpl(solaceConfig, client, walker, vars, widgets);
    }

    @Provides
    @Singleton
    IProduction provideIProduction(
            IWidgets widgets,
            IKeyboard keyboard,
            IDialog dialog,
            IClient client
    ) {
        return new ProductionImpl(widgets, keyboard, dialog, client);
    }

    @Provides
    @Singleton
    IFriends provideIFriends(IClient client) {
        return new FriendsImpl(client);
    }


    @Provides
    @Singleton
    IMenuFactory provideMenuFactory() {
        return new MenuFactoryImpl();
    }

    @Provides
    @Singleton
    IDepositBox provideDepositBox() {
        return new DepositBoxImpl();
    }

    @Provides
    @Singleton
    IChargeManager provideChargeManager() {
        return new ChargeManagerImpl();
    }

    @Provides
    @Singleton
    BreakHandler provideBreakHandler(ConfigManager configManager) {
        return new BreakHandlerImpl(configManager);
    }

    @Provides
    @Singleton
    GameStateManager provideGameStateManager() {
        return new GameStateManagerImpl();
    }

    @Provides
    @Singleton
    IBankWornItems provideBankWornItems(IWidgets widgets) {
        return new BankWornItemsImpl(widgets);
    }

    @Provides
    @Singleton
    LoadoutManager loadoutManager(IBank bank, IBankInventory bankInventory, IEquipment equipment, IInventory inventory,
                                  IMovement movement, IDialog dialog, IWidgets widgets, IClient client, IBankWornItems bankWornItems,
                                  SolaceConfig solaceConfig, InteractManager interactManager) {
        return new LoadoutManagerImpl(bank, bankInventory, equipment, inventory, movement, dialog,
                widgets, client, bankWornItems, solaceConfig, interactManager);
    }

    @Provides
    @Singleton
    ILoadoutFactory provideILoadoutFactory(
            LoadoutManager loadoutManager,
            IEquipment equipment,
            IInventory inventory,
            IClient client,
            IClientThread clientThread
    ) {
        return new LoadoutFactoryImpl(equipment, inventory, client, clientThread, loadoutManager);
    }

    @Provides
    @Singleton
    ShipContainer provideShipContainer(IClient client, EventBus eventBus) {
        return new ShipContainer(client, eventBus);
    }

    @Provides
    @Singleton
    IShips provideShips(ShipContainer shipContainer) {
        return new ShipsImpl(shipContainer);
    }

    @Provides
    @Singleton
    ISailing provideSailing(
            IClient client,
            IVars vars,
            IShips ships,
            IWidgets widgets,
            IPlayers players,
            ITabs tabs
    ) {
        return new SailingImpl(client, vars, ships, widgets, players, tabs);
    }
}
