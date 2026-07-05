package net.solace.loader.plugins.explorer.exclude;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.MenuOpened;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.widgets.WidgetID;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.input.KeyManager;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.worldmap.WorldMapPoint;
import net.runelite.client.ui.overlay.worldmap.WorldMapPointManager;
import net.runelite.client.util.HotkeyListener;
import net.solace.api.Static;
import net.solace.api.coords.Area;
import net.solace.api.events.ConfigButtonClicked;
import net.solace.api.plugins.DoNotRename;
import net.solace.api.plugins.LoopedPlugin;
import net.solace.api.plugins.PluginDescriptor;
import net.solace.api.plugins.config.ConfigManager;
import net.solace.loader.plugins.explorer.SolaceExplorerConfig;
import net.solace.loader.plugins.explorer.SolaceExplorerOverlay;
import net.solace.sdk.entities.Players;
import net.solace.sdk.game.Client;
import net.solace.sdk.game.Game;
import net.solace.sdk.game.WorldMap;
import net.solace.sdk.movement.Movement;
import net.solace.sdk.utils.MessageUtils;
import net.solace.sdk.widgets.Widgets;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

@PluginDescriptor(
        name = "Solace Explorer",
        description = "Right click anywhere within the World Map to walk there"
)
@Singleton
@Slf4j
@DoNotRename
public class SolaceExplorerPlugin extends LoopedPlugin {
    private static final Pattern WORLD_POINT_PATTERN = Pattern.compile("^\\d{4,5} \\d{4,5} \\d$");
    private static final Pattern KITTYKEYS_PATTERN = Pattern.compile(".* explorer (\\d{4,5}) (\\d{4,5}) ([0-3])");

    private final AtomicBoolean useTransports = new AtomicBoolean(true);

    private int cooldown = 0;

    @Inject
    private SolaceExplorerConfig config;

    private WorldPoint destination;

    @Inject
    private KeyManager keyManager;

    @Inject
    private WorldMapPointManager worldMapPointManager;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private SolaceExplorerOverlay overlay;

    @Override
    public void startUp() {
        keyManager.registerKeyListener(hotkeyListener);
        overlayManager.add(overlay);
    }

    @Override
    public void shutDown() {
        destination = null;
        keyManager.unregisterKeyListener(hotkeyListener);
        overlayManager.remove(overlay);
    }

    private final HotkeyListener hotkeyListener = new HotkeyListener(() -> config.toggleKeyBind()) {
        @Override
        public void hotkeyPressed() {
            // If the hotkey is pressed and there is currently a destination, stop walking
            if (destination != null) {
                destination = null;
            } else {
                WorldPoint location = null;

                switch (config.category()) {
                    case QUEST:
                        WorldPoint questLocation = getWorldPointLocation("Quest Helper");
                        if (questLocation != null) {
                            location = questLocation;
                        }
                        break;
                    case CLUE:
                        WorldPoint clueLocation = getWorldPointLocation("Clue Scroll");
                        if (clueLocation != null) {
                            location = clueLocation;
                        }
                        break;
                    case BANKS:
                        location = Area.centerOf(config.bankLocation().getArea());
                        break;
                    case CUSTOM:
                        String coords = config.coords();
                        if (!WORLD_POINT_PATTERN.matcher(coords).matches()) {
                            return;
                        }
                        String[] split = coords.split(" ");
                        location = new WorldPoint(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
                        break;
                }
                if (location != null) {
                    setDestination(Movement.getNearestWalkableTile(location));
                } else {
                    MessageUtils.addMessage("Invalid Selection");
                }
            }
        }
    };

    @Subscribe
    public void onMenuOpened(MenuOpened event) {
        if (destination != null) {
            Client.getWrapped()
                    .createMenuEntry(1)
                    .setOption("<col=00ff00>Explorer:</col>")
                    .setTarget("Cancel walking")
                    .setType(MenuAction.RUNELITE)
                    .onClick(e -> destination = null);
            return;
        }

        var worldMap = Widgets.get(InterfaceID.Worldmap.MAP_CONTAINER);
        if (worldMap == null) {
            return;
        }

        WorldPoint mouse = WorldMap.getMouseLocation();
        if (mouse == null) {
            log.info("Mouse is not on the world map");
            return;
        }

        MenuEntry parent = Client.getWrapped().createMenuEntry(-1)
                .setOption("<col=00ff00>Explorer:</col>")
                .setTarget("Walk here")
                .setType(MenuAction.RUNELITE)
                .onClick(e ->
                {
                    useTransports.set(true);
                    setDestination(mouse);

                    if (config.closeMap()) {
                        closeWorldMap();
                    }
                });

        var subMenu = parent.createSubMenu();

        subMenu.createMenuEntry(0)
                .setOption("Use transports")
                .setType(MenuAction.RUNELITE)
                .onClick(e ->
                {
                    useTransports.set(true);
                    setDestination(mouse);

                    if (config.closeMap()) {
                        closeWorldMap();
                    }
                });

        subMenu.createMenuEntry(0)
                .setOption("Just walk")
                .setType(MenuAction.RUNELITE)
                .onClick(e ->
                {
                    useTransports.set(false);
                    setDestination(mouse);

                    if (config.closeMap()) {
                        closeWorldMap();
                    }
                });
    }

    @Subscribe
    public void onConfigButtonClicked(ConfigButtonClicked e) {
        if (!"solaceexplorer".equals(e.getGroup()) || !"walk".equals(e.getKey())) {
            return;
        }

        WorldPoint location = null;

        switch (config.category()) {
            case QUEST:
                WorldPoint questLocation = getWorldPointLocation("Quest Helper");
                if (questLocation != null) {
                    location = questLocation;
                }
                break;
            case CLUE:
                WorldPoint clueLocation = getWorldPointLocation("Clue Scroll");
                if (clueLocation != null) {
                    location = clueLocation;
                }
                break;
            case BANKS:
                location = Area.centerOf(config.bankLocation().getArea());
                break;
            case CUSTOM:
                String coords = config.coords();
                if (!WORLD_POINT_PATTERN.matcher(coords).matches()) {
                    return;
                }
                String[] split = coords.split(" ");
                location = new WorldPoint(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
                break;
        }
        if (location != null) {
            setDestination(Movement.getNearestWalkableTile(location));
        } else {
            MessageUtils.addMessage("Invalid Selection");
        }
    }

    @DoNotRename
    public void setDestination(WorldPoint wp) {
        if (wp == null) {
            destination = null;
        } else {
            destination = Movement.getNearestWalkableTile(wp);
            log.debug("Walking to {}", destination);
        }
    }

    @DoNotRename
    public WorldPoint getDestination() {
        return destination;
    }

    private void closeWorldMap() {
        var closeWorldMap = Widgets.get(WidgetID.WORLD_MAP_GROUP_ID, closeButton -> closeButton.hasAction("Close"));
        if (Widgets.isVisible(closeWorldMap)) {
            closeWorldMap.interact("Close");
        }
    }

    public int loop() {
        var local = Players.getLocal();
        if (!Game.isLoggedIn() || local == null) {
            log.debug("Not logged in");
            return 600;
        }

        if (Movement.isWalking()) {
            log.debug("Already walking");
            return -2;
        }

        if (destination == null || Objects.equals(Movement.getDestination(), destination) || local.distanceTo(destination) <= 2) {
            log.debug("Destination reached");

            if (destination != null) {
                MessageUtils.addMessage("Finished exploring", ChatMessageType.TRADEREQ);
            }

            destination = null;
            return -1;
        }

        if (!useTransports.get()) {
            log.debug("Walking to {}", destination);
            Movement.getPath(List.of(local.getWorldLocation()),
                    destination.toWorldArea(),
                    Static.getGlobalCollisionMap(),
                    false,
                    false
            ).walk(false);
            return -2;
        }

        log.debug("Pathfinding to {}", destination);
        Movement.walkTo(destination);
        return -2;
    }

    private WorldPoint getWorldPointLocation(String name) {
        List<?> mapPoints = new ArrayList<>();
        try {
            Field privateField = worldMapPointManager.getClass().getDeclaredField("worldMapPoints");
            privateField.setAccessible(true);
            mapPoints = (List<?>) privateField.get(worldMapPointManager);
        } catch (Exception e) {
            log.info("Error: ", e);
        }

        for (Object mapPoint : mapPoints) {
            if (mapPoint instanceof WorldMapPoint) {
                final WorldMapPoint point = (WorldMapPoint) mapPoint;
                if (point.getName() != null && point.getName().equals(name)) {
                    return point.getWorldPoint();
                }
            }

        }
        return null;
    }

    @Subscribe
    private void onMenuOptionClicked(MenuOptionClicked e) {
        if (!e.getMenuOption().equals("Automated") && e.getMenuAction() == MenuAction.WALK) {
            destination = null;
        }
    }

    @Subscribe
    private void onChatMessage(ChatMessage e) {
        if (e.getType() == ChatMessageType.TRADEREQ) {
            var message = e.getMessage();
            var matcher = KITTYKEYS_PATTERN.matcher(message);
            if (matcher.matches()) {
                var x = Integer.parseInt(matcher.group(1));
                var y = Integer.parseInt(matcher.group(2));
                var plane = Integer.parseInt(matcher.group(3));
                setDestination(new WorldPoint(x, y, plane));
            }
        }
    }

    @Provides
    SolaceExplorerConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(SolaceExplorerConfig.class);
    }
}