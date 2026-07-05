package net.solace.api;

import com.google.inject.Inject;
import com.google.inject.Injector;
import javax.annotation.Nullable;
import javax.inject.Named;
import net.runelite.api.Client;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.NPCManager;
import net.runelite.client.game.WorldService;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.overlay.OverlayManager;
import net.solace.api.commons.ITime;
import net.solace.api.domain.game.IClient;
import net.solace.api.domain.game.IClientThread;
import net.solace.api.entities.INPCs;
import net.solace.api.entities.IPlayers;
import net.solace.api.entities.ITileItems;
import net.solace.api.entities.ITileObjects;
import net.solace.api.entities.ITiles;
import net.solace.api.game.ICombat;
import net.solace.api.game.IGame;
import net.solace.api.game.IHouse;
import net.solace.api.game.ISkills;
import net.solace.api.game.IVars;
import net.solace.api.game.IWorldMap;
import net.solace.api.game.IWorlds;
import net.solace.api.input.IKeyboard;
import net.solace.api.interact.builder.IMenuFactory;
import net.solace.api.interact.mouse.MouseManager;
import net.solace.api.items.IBank;
import net.solace.api.items.IBankInventory;
import net.solace.api.items.IDepositBox;
import net.solace.api.items.IEquipment;
import net.solace.api.items.IGrandExchange;
import net.solace.api.items.IInventory;
import net.solace.api.items.ITrade;
import net.solace.api.items.ITradeInventory;
import net.solace.api.items.ITradeOther;
import net.solace.api.items.ITradeOurs;
import net.solace.api.items.loadouts.ILoadoutFactory;
import net.solace.api.magic.IMagic;
import net.solace.api.movement.IMovement;
import net.solace.api.movement.IReachable;
import net.solace.api.movement.ISailing;
import net.solace.api.movement.IWalker;
import net.solace.api.movement.pathfinder.GlobalCollisionMap;
import net.solace.api.movement.pathfinder.IChargeManager;
import net.solace.api.movement.pathfinder.ITeleportLoader;
import net.solace.api.movement.pathfinder.ITransportLoader;
import net.solace.api.plugins.IPlugins;
import net.solace.api.plugins.PluginManager;
import net.solace.api.plugins.config.ConfigManager;
import net.solace.api.plugins.config.SolaceConfig;
import net.solace.api.quests.IQuests;
import net.solace.api.sailing.IShips;
import net.solace.api.util.SolaceProperties;
import net.solace.api.widgets.IBankWornItems;
import net.solace.api.widgets.IDialog;
import net.solace.api.widgets.IFriends;
import net.solace.api.widgets.IMinigames;
import net.solace.api.widgets.IPrayers;
import net.solace.api.widgets.IProduction;
import net.solace.api.widgets.ITabs;
import net.solace.api.widgets.IWidgets;

public class Static {
    public static Injector injector;
    @Inject
    private static IClient client;
    @Inject
    private static IInventory inventory;
    @Inject
    private static IEquipment equipment;
    @Inject
    private static IMagic magic;
    @Inject
    private static IVars vars;
    @Inject
    private static ISkills skills;
    @Inject
    private static IMovement movement;
    @Inject
    private static IWorlds worlds;
    @Inject
    private static IWidgets widgets;
    @Inject
    private static IPlayers players;
    @Inject
    private static IPlugins plugins;
    @Inject
    private static ITileItems tileItems;
    @Inject
    private static IQuests quests;
    @Inject
    private static ITileObjects tileObjects;
    @Inject
    private static IBank bank;
    @Inject
    private static IBankInventory bankInventory;
    @Inject
    private static ITiles tiles;
    @Inject
    private static INPCs npcs;
    @Inject
    private static IHouse house;
    @Inject
    private static IGame game;
    @Inject
    private static ITrade trade;
    @Inject
    private static ITradeOurs tradeOurs;
    @Inject
    private static ITradeInventory tradeInventory;
    @Inject
    private static ITradeOther tradeOther;
    @Inject
    private static IWorldMap worldMap;
    @Inject
    private static SolaceConfig solaceConfig;
    @Inject
    private static ChatMessageManager chatMessageManager;
    @Inject
    private static GlobalCollisionMap globalCollisionMap;
    @Inject
    private static ItemManager itemManager;
    @Inject
    private static Client wrappedClient;
    @Inject
    private static PluginManager pluginManager;
    @Inject
    private static NPCManager NPCManager;
    @Inject
    private static OverlayManager overlayManager;
    @Inject
    private static ClientToolbar clientToolbar;
    @Inject
    private static ConfigManager configManager;
    @Inject
    private static net.runelite.client.config.ConfigManager runeliteConfigManager;
    @Inject
    private static WorldService worldService;
    @Inject
    private static ClientThread clientThread;
    @Inject
    private static ITransportLoader transportLoader;
    @Inject
    private static IWalker walker;
    @Inject
    private static IClientThread gameThread;
    @Inject
    private static IDialog dialog;
    @Inject
    private static ICombat combat;
    @Inject
    private static IPrayers prayers;
    @Inject
    private static IGrandExchange grandExchange;
    @Inject
    private static ITabs tabs;
    @Inject
    private static IMinigames minigames;
    @Inject
    private static IReachable reachable;
    @Inject
    private static IKeyboard keyboard;
    @Inject
    private static ITime time;
    @Inject
    private static ITeleportLoader teleportLoader;
    @Inject
    private static IProduction production;
    @Inject
    private static IFriends friends;
    @Inject
    @Named(value="scriptArgs")
    @Nullable
    private static String[] scriptArgs;
    @Inject
    private static ILoadoutFactory loadoutFactory;
    @Inject
    private static IMenuFactory menuFactory;
    @Inject
    private static IBankWornItems bankWornItems;
    @Inject
    private static SolaceProperties solaceProperties;
    @Inject
    private static IShips ships;
    @Inject
    private static ISailing sailing;
    @Inject
    private static IChargeManager chargeManager;
    @Inject
    private static IDepositBox depositBox;
    @Inject
    private static MouseManager mouseManager;

    public static IClient getClient() {
        return client;
    }

    public static IInventory getInventory() {
        return inventory;
    }

    public static IEquipment getEquipment() {
        return equipment;
    }

    public static IMagic getMagic() {
        return magic;
    }

    public static IVars getVars() {
        return vars;
    }

    public static ISkills getSkills() {
        return skills;
    }

    public static IMovement getMovement() {
        return movement;
    }

    public static IWorlds getWorlds() {
        return worlds;
    }

    public static IWidgets getWidgets() {
        return widgets;
    }

    public static IPlayers getPlayers() {
        return players;
    }

    public static IPlugins getPlugins() {
        return plugins;
    }

    public static ITileItems getTileItems() {
        return tileItems;
    }

    public static IQuests getQuests() {
        return quests;
    }

    public static ITileObjects getTileObjects() {
        return tileObjects;
    }

    public static IBank getBank() {
        return bank;
    }

    public static IBankInventory getBankInventory() {
        return bankInventory;
    }

    public static ITiles getTiles() {
        return tiles;
    }

    public static INPCs getNpcs() {
        return npcs;
    }

    public static IHouse getHouse() {
        return house;
    }

    public static IGame getGame() {
        return game;
    }

    public static ITrade getTrade() {
        return trade;
    }

    public static ITradeOurs getTradeOurs() {
        return tradeOurs;
    }

    public static ITradeInventory getTradeInventory() {
        return tradeInventory;
    }

    public static ITradeOther getTradeOther() {
        return tradeOther;
    }

    public static IWorldMap getWorldMap() {
        return worldMap;
    }

    public static SolaceConfig getSolaceConfig() {
        return solaceConfig;
    }

    public static ChatMessageManager getChatMessageManager() {
        return chatMessageManager;
    }

    public static GlobalCollisionMap getGlobalCollisionMap() {
        return globalCollisionMap;
    }

    public static ItemManager getItemManager() {
        return itemManager;
    }

    public static Client getWrappedClient() {
        return wrappedClient;
    }

    public static PluginManager getPluginManager() {
        return pluginManager;
    }

    public static NPCManager getNPCManager() {
        return NPCManager;
    }

    public static OverlayManager getOverlayManager() {
        return overlayManager;
    }

    public static ClientToolbar getClientToolbar() {
        return clientToolbar;
    }

    public static ConfigManager getConfigManager() {
        return configManager;
    }

    public static net.runelite.client.config.ConfigManager getRuneliteConfigManager() {
        return runeliteConfigManager;
    }

    public static WorldService getWorldService() {
        return worldService;
    }

    public static ClientThread getClientThread() {
        return clientThread;
    }

    public static ITransportLoader getTransportLoader() {
        return transportLoader;
    }

    public static IWalker getWalker() {
        return walker;
    }

    public static IClientThread getGameThread() {
        return gameThread;
    }

    public static IDialog getDialog() {
        return dialog;
    }

    public static ICombat getCombat() {
        return combat;
    }

    public static IPrayers getPrayers() {
        return prayers;
    }

    public static IGrandExchange getGrandExchange() {
        return grandExchange;
    }

    public static ITabs getTabs() {
        return tabs;
    }

    public static IMinigames getMinigames() {
        return minigames;
    }

    public static IReachable getReachable() {
        return reachable;
    }

    public static IKeyboard getKeyboard() {
        return keyboard;
    }

    public static ITime getTime() {
        return time;
    }

    public static ITeleportLoader getTeleportLoader() {
        return teleportLoader;
    }

    public static IProduction getProduction() {
        return production;
    }

    public static IFriends getFriends() {
        return friends;
    }

    @Nullable
    public static String[] getScriptArgs() {
        return scriptArgs;
    }

    public static ILoadoutFactory getLoadoutFactory() {
        return loadoutFactory;
    }

    public static IMenuFactory getMenuFactory() {
        return menuFactory;
    }

    public static IBankWornItems getBankWornItems() {
        return bankWornItems;
    }

    public static SolaceProperties getSolaceProperties() {
        return solaceProperties;
    }

    public static IShips getShips() {
        return ships;
    }

    public static ISailing getSailing() {
        return sailing;
    }

    public static IChargeManager getChargeManager() {
        return chargeManager;
    }

    public static IDepositBox getDepositBox() {
        return depositBox;
    }

    public static MouseManager getMouseManager() {
        return mouseManager;
    }
}

