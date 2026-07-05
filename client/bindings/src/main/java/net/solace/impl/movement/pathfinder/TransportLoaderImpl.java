package net.solace.impl.movement.pathfinder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.MenuAction;
import net.runelite.api.Quest;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.api.gameval.VarPlayerID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.api.widgets.WidgetID;
import net.solace.api.commons.Predicates;
import net.solace.api.domain.game.IClientThread;
import net.solace.api.domain.items.IInventoryItem;
import net.solace.api.domain.items.IItem;
import net.solace.api.domain.tiles.ITileObject;
import net.solace.api.entities.INPCs;
import net.solace.api.entities.IPlayers;
import net.solace.api.entities.ITileObjects;
import net.solace.api.game.ISkills;
import net.solace.api.game.IVars;
import net.solace.api.game.IWorlds;
import net.solace.api.items.IEquipment;
import net.solace.api.items.IInventory;
import net.solace.api.movement.pathfinder.ITransportLoader;
import net.solace.api.movement.pathfinder.model.BirdFlight;
import net.solace.api.movement.pathfinder.model.CharterShip;
import net.solace.api.movement.pathfinder.model.FairyRing;
import net.solace.api.movement.pathfinder.model.GnomeGlider;
import net.solace.api.movement.pathfinder.model.IgnoredDoor;
import net.solace.api.movement.pathfinder.model.MagicCarpet;
import net.solace.api.movement.pathfinder.model.MagicMushtree;
import net.solace.api.movement.pathfinder.model.Minecart;
import net.solace.api.movement.pathfinder.model.SpiritTree;
import net.solace.api.movement.pathfinder.model.Transport;
import net.solace.api.movement.pathfinder.model.requirement.ItemRequirement;
import net.solace.api.movement.pathfinder.model.requirement.Reduction;
import net.solace.api.movement.pathfinder.model.requirement.Requirements;
import net.solace.api.quests.IQuests;
import net.solace.api.widgets.IDialog;
import net.solace.api.widgets.IWidgets;
import net.solace.api.widgets.InterfaceAddress;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.solace.api.movement.pathfinder.model.MovementConstants.CHARTERSHIP_BLACKLITSED;
import static net.solace.api.movement.pathfinder.model.MovementConstants.FAIRY_ITEMS;
import static net.solace.api.movement.pathfinder.model.MovementConstants.SLASH_WEB_POINTS;
import static net.solace.api.movement.pathfinder.model.MovementConstants.SPIRIT_TREE_BLACKLISTED;
import static net.solace.api.util.EtcUtils.containsItem;

@RequiredArgsConstructor
@Slf4j
public class TransportLoaderImpl implements ITransportLoader {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    @Getter
    private final List<Transport> customTransports = new ArrayList<>();
    private final List<Transport> staticTransports = new ArrayList<>();
    @Getter
    private final List<Transport> lastTransports = new ArrayList<>();
    private final List<Transport> cachedTransports = new ArrayList<>();
    private final List<IgnoredDoor> staticIgnoredDoors = new ArrayList<>();
    @Getter
    private final List<IgnoredDoor> lastIgnoredDoors = new ArrayList<>();

    private final List<Integer> idleTransports = List.of(ObjectID.FOSSIL_SHORTCUT_BASECAMP_A, ObjectID.FOSSIL_SHORTCUT_BASECAMP_B, ObjectID.MDAUGHTER_ROCKSLIDE);

    private final IClientThread clientThread;
    private final IInventory inventory;
    private final IWorlds worlds;
    private final IVars vars;
    private final IQuests quests;
    private final ITileObjects tileObjects;
    private final IEquipment equipment;
    private final IWidgets widgets;
    private final INPCs npcs;
    private final IPlayers players;
    private final IDialog dialog;
    private final ISkills skills;

    @Override
    public void init() {
        loadTransports();
        loadIgnoredDoors();
    }

    private void loadTransports() {
        log.info("Loading transports");
        try (InputStream stream = ITransportLoader.class.getResourceAsStream("/transports.json")) {
            if (stream == null) {
                log.error("Failed to load transports.");
                return;
            }

            var json = GSON.fromJson(new String(stream.readAllBytes()), TransportDto[].class);

            var list = Arrays.stream(json)
                    .map(dto -> dto.toTransport(this))
                    .collect(Collectors.toList());
            staticTransports.addAll(list);
        } catch (Exception e) {
            log.error("Failed to load transports - Please report.", e);
        }

        log.info("Loaded {} transports", staticTransports.size());
    }

    private void loadIgnoredDoors() {
        log.info("Loading ignored doors");

        try (InputStream stream = ITransportLoader.class.getResourceAsStream("/ignoredDoors.json")) {
            if (stream == null) {
                log.error("Failed to load ignored doors.");
                return;
            }

            var json = GSON.fromJson(new String(stream.readAllBytes()), IgnoredDoorDto[].class);

            var list = Arrays.stream(json)
                    .map(IgnoredDoorDto::toIgnoredDoor)
                    .collect(Collectors.toList());
            staticIgnoredDoors.addAll(list);
        } catch (IOException e) {
            log.error("Failed to load ignored doors.", e);
        }

        log.info("Loaded {} ignored doors", staticIgnoredDoors.size());
    }

    @Override
    public List<Transport> buildTransports(boolean useCached) {
        List<Transport> lastTransportsCopy = useCached ? new ArrayList<>(cachedTransports) : new ArrayList<>(lastTransports);
        List<Transport> customTransportsCopy = new ArrayList<>(customTransports);
        
        return Stream.concat(lastTransportsCopy.stream(), customTransportsCopy.stream())
                .filter(Objects::nonNull)
                .filter(it -> it.getRequirements().fulfilled())
                .collect(Collectors.toList());
    }

    @Override
    public void refreshTransports(List<Integer> items) {
        clientThread.invoke(() -> {
            List<Transport> filteredStatic = staticTransports.stream()
                    .filter(it -> it.getRequirements().fulfilled())
                    .collect(Collectors.toList());

            List<Transport> transports = new ArrayList<>();

            int gold = inventory.getFirst(995) != null ? inventory.getFirst(995).getQuantity() : 0;

            var isMembers = worlds.inMembersWorld();

            if (isMembers) {
                transports.addAll(edgeLever());
                transports.addAll(warriorsGuild());
                transports.addAll(shamans());
                transports.addAll(crabClawIsland(gold));
                transports.addAll(zeahBoat());
                transports.addAll(lunarIsleBoat());
                transports.addAll(spiritTrees());
                transports.addAll(lumbridgeCellarMine());
                transports.addAll(lumbridgeShack());
                transports.addAll(treeGnomeVillageElkoy());
                transports.addAll(eaglesPeakCave());
                transports.addAll(rellekaBoats(gold));
                transports.addAll(corsairCove());
                transports.addAll(lumbridgeCastleDiningRoom());
                transports.addAll(digsiteGate());
                transports.addAll(fairyRings(items));
                transports.addAll(charterShips());
                transports.addAll(magicCarpets());
                transports.addAll(fossilIsland());
                transports.addAll(MagicMushtree.getMushtreeTransports());
                transports.addAll(gnomeStronghold());
                transports.addAll(paterdomus());
                transports.addAll(glarialsTomb());
                transports.addAll(waterfallIsland());
                transports.addAll(undergroundPass());
                transports.addAll(piscarillius());
                transports.addAll(canifis());
                transports.addAll(apeAtoll());
                transports.addAll(ectofuntus());
                transports.addAll(mountainCamp());
                transports.addAll(GnomeGlider.getTransports());
                transports.addAll(whirlPool());
                transports.addAll(kalphiteTransports());
                transports.addAll(shantayPass(items));
                transports.addAll(boats());
            }

            transports.addAll(entrana());
            transports.addAll(edgeville());
            transports.addAll(varrock());
            transports.addAll(quests(gold));
            transports.addAll(slashableWebs());

            if (gold >= 20 || quests.isFinished(Quest.THE_FORSAKEN_TOWER)) {
                transports.addAll(Minecart.getMinecartTransports());
            }

            if (vars.getBit(VarbitID.VMQ2) >= 38) {
                transports.addAll(BirdFlight.getBirdFlightTransports());
            }

            if (!items.isEmpty()) {
                cachedTransports.clear();
                cachedTransports.addAll(transports);
                cachedTransports.addAll(filteredStatic);
            } else {
                lastTransports.clear();
                lastTransports.addAll(filteredStatic);
                lastTransports.addAll(transports);
            }

            log.debug("Loaded {} transports", lastTransports.size());

            List<IgnoredDoor> filteredStaticIgnoredDoors = staticIgnoredDoors.stream()
                    .filter(it -> it.shouldIgnore(isMembers))
                    .collect(Collectors.toList());

            lastIgnoredDoors.clear();
            lastIgnoredDoors.addAll(filteredStaticIgnoredDoors);

            log.debug("Loaded {} ignored doors", lastIgnoredDoors.size());
        });
    }

    @Override
    public Transport trapDoorTransport(
            WorldPoint source,
            WorldPoint destination,
            int closedId,
            int openedId
    ) {
        return Transport.builder()
                .source(source)
                .destination(destination)
                .handler(() ->
                {
                    if (dialog.canContinue()) {
                        dialog.continueSpace();
                        return true;
                    }

                    var openedTrapdoor = tileObjects.getFirstSurrounding(source, 5, openedId);
                    if (openedTrapdoor != null) {
                        openedTrapdoor.interact(0);
                        return true;
                    }

                    var closedTrapDoor = tileObjects.getFirstSurrounding(source, 5, closedId);
                    if (closedTrapDoor != null) {
                        closedTrapDoor.interact(0);
                        return true;
                    }

                    return false;
                })
                .build();
    }

    @Override
    public Transport itemUseAndObjectTransport(
            WorldPoint source,
            WorldPoint destination,
            int beforeItem,
            int afterItem,
            int itemId
    ) {
        return Transport.builder()
                .source(source)
                .destination(destination)
                .handler(() ->
                {
                    var ropeTransport = tileObjects.getFirstSurrounding(source, 5, beforeItem);
                    if (ropeTransport != null) {
                        ropeTransport.interact(0);
                        return true;
                    }

                    IInventoryItem item = inventory.getFirst(itemId);

                    if (item == null) {
                        log.debug("Can't find item {} to use on transport", itemId);
                        return false;
                    }

                    var transport = tileObjects.getFirstSurrounding(source, 5, afterItem);
                    if (transport != null) {
                        item.useOn(transport);
                        return true;
                    }

                    return false;
                })
                .build();
    }


    public Transport itemUseAndObjectNameTransport(
            WorldPoint source,
            WorldPoint destination,
            int itemId,
            String objectName,
            String action
    ) {
        return Transport.builder()
                .source(source)
                .destination(destination)
                .handler(() ->
                {
                    var object = tileObjects.getFirstSurrounding(source, 5, objectName);

                    if (object == null) {
                        log.debug("Can't find object {} to use item on transport", objectName);
                        return false;
                    }

                    if (object.hasAction(action)) {
                        object.interact(action);
                        return true;
                    }

                    var item = inventory.getFirst(itemId);

                    if (item == null) {
                        log.debug("Can't find item {} to use on transport", itemId);
                        return false;
                    }

                    item.useOn(object);
                    return true;
                })
                .build();
    }

    @Override
    public Transport fairyRingTransport(
            FairyRing source,
            FairyRing destination
    ) {
        return Transport.builder()
                .source(source.getLocation())
                .destination(destination.getLocation())
                .itemRequirements(vars.getBit(VarbitID.LUMBRIDGE_DIARY_ELITE_COMPLETE) == 0 ? FAIRY_ITEMS : null)
                .handler(() ->
                {
                    log.debug("Looking for fairy ring at {} to {}", source.getLocation(), destination.getLocation());
                    var ring = tileObjects.getFirstSurrounding(source.getLocation(), 5, "Fairy ring");

                    if (ring == null) {
                        log.debug("Fairy ring at {} is null", source.getLocation());
                        return false;
                    }

                    IItem staff = inventory.getFirst(FAIRY_ITEMS);

                    if (vars.getBit(VarbitID.LUMBRIDGE_DIARY_ELITE_COMPLETE) != 1) {
                        if (!equipment.contains(FAIRY_ITEMS)) {
                            if (staff != null) {
                                staff.interact("Wield");
                                return true;
                            }
                            return false;
                        }
                    }

                    if (destination == FairyRing.ZANARIS) {
                        ring.interact("Zanaris");
                        return true;
                    }

                    if (ring.hasAction(a -> a != null && a.contains(destination.getCode()))) {
                        ring.interact(a -> a != null && a.contains(destination.getCode()));
                        return true;
                    }

                    if (widgets.isVisible(WidgetID.FAIRY_RING_GROUP_ID, 0)) {
                        destination.travel();
                        return true;
                    }

                    ring.interact("Configure");
                    return true;
                })
                .build();
    }

    @Override
    public Transport itemUseTransport(
            WorldPoint source,
            WorldPoint destination,
            int itemId,
            int objId,
            int radius
    ) {
        return Transport.builder()
                .source(source)
                .destination(destination)
                .handler(() ->
                {
                    var item = inventory.getFirst(itemId);
                    if (item == null) {
                        return false;
                    }

                    var transport = tileObjects.getFirstSurrounding(source, radius, objId);
                    if (transport != null) {
                        item.useOn(transport);
                        return true;
                    }

                    return false;
                })
                .delay(2)
                .build();
    }

    @Override
    public Transport itemWearTransport(
            int radius,
            WorldPoint source,
            WorldPoint destination,
            int objId,
            String actions,
            int... itemIds
    ) {
        return Transport.builder()
                .source(source)
                .itemRequirements(itemIds)
                .destination(destination)
                .handler(() ->
                {
                    var items = inventory.getFirst(itemIds);

                    if (!equipment.contains(itemIds)) {
                        if (items != null) {
                            items.interact("Wield", "Wear", "Equip");
                            return true;
                        }
                        return false;
                    }

                    var first = tileObjects.getFirstAt(source, objId);
                    if (first != null && players.getLocal().distanceTo(first) <= radius) {
                        log.debug("Transport found {}", first.getWorldLocation());
                        first.interact(actions);
                        return true;
                    }

                    var object = tileObjects.getSurrounding(source, MAX_RADIUS, x -> x.getId() == objId).stream()
                            .min(Comparator.<ITileObject>comparingInt(o -> o.distanceTo(source))
                                    .thenComparingInt(o -> o.distanceTo(destination)))
                            .orElse(null);

                    if (object != null && players.getLocal().distanceTo(object) <= radius) {
                        object.interact(actions);
                        return true;
                    }

                    log.debug("Transport not found {}, {}", source, objId);
                    return false;
                })
                .build();
    }

    @Override
    public Transport npcTransport(
            int radius,
            WorldPoint source,
            WorldPoint destination,
            int npcId,
            Requirements requirements,
            String... actions
    ) {
        return Transport.builder()
                .source(source)
                .destination(destination)
                .handler(() ->
                {
                    var npc = npcs.getNearest(x -> x.getWorldLocation().distanceTo(source) <= radius && x.getId() == npcId);
                    if (npc != null && players.getLocal().distanceTo(npc) < radius) {
                        npc.interact(actions);
                        return true;
                    }

                    return false;
                })
                .itemRequirements(requirements.getItemRequirements().isEmpty() ? null :
                        requirements.getItemRequirements().stream()
                                .flatMap(req -> req.getIds().stream())
                                .mapToInt(Integer::intValue)
                                .toArray())
                .requirements(requirements)
                .build();
    }

    @Override
    public Transport npcTransport(
            int radius,
            WorldPoint source,
            WorldPoint destination,
            String npcName,
            Requirements requirements,
            String... actions
    ) {
        return Transport.builder()
                .source(source)
                .destination(destination)
                .handler(() ->
                {
                    var npc = npcs.getNearest(x -> x.getWorldLocation().distanceTo(source) <= radius && x.getName().equalsIgnoreCase(npcName));
                    if (npc != null && players.getLocal().distanceTo(npc) < radius) {
                        npc.interact(actions);
                        return true;
                    }

                    return false;
                })
                .requirements(requirements)
                .build();
    }

    @Override
    public Transport npcDialogTransport(
            int radius,
            WorldPoint source,
            WorldPoint destination,
            int npcId,
            Requirements requirements,
            String... chatOptions
    ) {
        return Transport.builder()
                .source(source)
                .destination(destination)
                .handler(() ->
                {
                    if (dialog.canContinue()) {
                        dialog.continueSpace();
                        return true;
                    }

                    if (dialog.isViewingOptions()) {
                        if (dialog.chooseOption(chatOptions)) {
                            return true;
                        }

                        return true;
                    }

                    var npc = npcs.getNearest(x -> x.getWorldLocation().distanceTo(source) <= radius && x.getId() == npcId);
                    if (npc != null && players.getLocal().distanceTo(npc) < radius) {
                        npc.interact(0);
                        return true;
                    }

                    return false;
                })
                .requirements(requirements)
                .build();
    }

    @Override
    public Transport objectTransport(
            WorldPoint source,
            WorldPoint destination,
            int objId,
            String actions
    ) {
        return Transport.builder()
                .source(source)
                .destination(destination)
                .handler(() ->
                {
                    var first = tileObjects.getFirstAt(source, objId);
                    if (first != null) {
                        first.interact(actions);
                        return true;
                    }

                    var object = tileObjects.getSurrounding(source, 5, x -> x.getId() == objId).stream()
                            .min(Comparator.<ITileObject>comparingDouble(o -> o.distanceTo2DHypotenuse(source))
                                    .thenComparingDouble(o -> o.distanceTo2DHypotenuse(destination)))
                            .orElse(null);

                    if (object != null && players.getLocal().distanceTo(object) < MAX_INTERACT_DISTANCE) {
                        object.interact(actions);
                        return true;
                    }

                    return false;
                })
                .build();
    }

    @Override
    public Transport objectTransport(
            int radius,
            WorldPoint source,
            WorldPoint destination,
            int objId,
            String actions,
            Requirements requirements
    ) {
        return Transport.builder()
                .source(source)
                .destination(destination)
                .handler(() ->
                {
                    var player = players.getLocal();

                    // Prevent same tick click on instant transport for now. Should be moved to individual transports later.
                    if (idleTransports.contains(objId) && !player.isIdle()) {
                        return true;
                    }

                    var first = tileObjects.getFirstAt(source, objId);
                    if (first != null && player.distanceTo(first) <= radius) {
                        log.debug("Transport found {}", first.getWorldLocation());
                        first.interact(actions);
                        return true;
                    }

                    var object = tileObjects.getSurrounding(source, MAX_RADIUS, x -> x.getId() == objId).stream()
                            .min(Comparator.<ITileObject>comparingDouble(o -> o.distanceTo2DHypotenuse(source))
                                    .thenComparingDouble(o -> o.distanceTo2DHypotenuse(destination)))
                            .orElse(null);

                    if (object != null && player.distanceTo(object) <= radius) {
                        object.interact(actions);
                        return true;
                    }

                    log.debug("Transport not found {}, {}", source, objId);
                    return false;
                })
                .itemRequirements(requirements.getItemRequirements().isEmpty() ? null :
                        requirements.getItemRequirements().stream()
                                .flatMap(req -> req.getIds().stream())
                                .mapToInt(Integer::intValue)
                                .toArray())
                .requirements(requirements)
                .build();
    }

    @Override
    public Transport objectDialogTransport(
            WorldPoint source,
            WorldPoint destination,
            int objId,
            String action,
            Requirements requirements,
            String... chatOptions
    ) {
        return Transport.builder()
                .source(source)
                .destination(destination)
                .handler(() ->
                {
                    if (dialog.isOpen()) {
                        if (dialog.canContinue()) {
                            dialog.continueSpace();
                            return true;
                        }

                        if (dialog.chooseOption(chatOptions)) {
                            return true;
                        }

                        return true;
                    }

                    var transport = tileObjects.getFirstSurrounding(source, 5, objId);
                    if (transport != null && transport.distanceTo(players.getLocal()) < MAX_INTERACT_DISTANCE) {
                        transport.interact(action);
                        return true;
                    }

                    return false;
                })
                .requirements(requirements)
                .build();
    }

    @Override
    public Transport slashWebTransport(
            WorldPoint source,
            WorldPoint destination
    ) {
        return Transport.builder()
                .source(source)
                .destination(destination)
                .handler(() ->
                {
                    var transport = tileObjects.getNearest(source, it -> it.distanceTo2DHypotenuse(source) <= 1 && it.getName() != null && it.getName().contains("Web") && it.hasAction("Slash"));
                    if (transport != null) {
                        transport.interact("Slash");
                        return true;
                    }

                    log.warn("Slash web transport not found");
                    return false;
                })
                .build();
    }

    @Override
    public Transport spritTreeTransport(WorldPoint source, WorldPoint destination, String location) {
        return Transport.builder()
                .source(source)
                .destination(destination)
                .handler(() ->
                {
                    var treeWidget = widgets.get(InterfaceID.Menu.LJ_LAYER1);

                    if (treeWidget == null) {
                        treeWidget = widgets.get(947, 9);
                    }

                    if (widgets.isVisible(treeWidget)) {
                        Arrays.stream(treeWidget.getDynamicChildren())
                                .filter(child -> child.getText().toLowerCase().contains(location.toLowerCase()))
                                .findFirst()
                                .ifPresent(child -> child.interact(MenuAction.WIDGET_CONTINUE));
                        return true;
                    }

                    var tree = tileObjects.getFirstSurrounding(source, 5, 1293, 1294, 1295, 27116, 37329, 33733);

                    if (tree == null) {
                        tree = tileObjects.getFirstSurrounding(source, 5, "Spirit Tree", "Spirit tree");
                    }

                    if (tree != null) {
                        tree.interact(MenuAction.GAME_OBJECT_FIRST_OPTION);
                        return true;
                    }

                    log.warn("Failed to find spirit tree");
                    return false;
                })
                .build();
    }

    @Override
    public Transport mushtreeTransport(WorldPoint source, WorldPoint destination, int component) {
        return mushtreeTransport(source, destination, new InterfaceAddress(component));
    }

    public Transport mushtreeTransport(WorldPoint source, WorldPoint destination, InterfaceAddress widget) {
        return Transport.builder()
                .source(source)
                .destination(destination)
                .handler(() ->
                {
                    var treeWidget = widgets.get(widget);
                    if (widgets.isVisible(treeWidget)) {
                        treeWidget.interact(MenuAction.WIDGET_CONTINUE);
                        return true;
                    }

                    var tree = tileObjects.getFirstSurrounding(source, 15, "Magic Mushtree");
                    if (tree != null) {
                        tree.interact("Use");
                        return true;
                    }

                    log.warn("Failed to find mushtree");
                    return false;
                })
                .build();
    }

    @Override
    public Transport charterShip(CharterShip charterShip) {
        CharterShip.ShipLocation destination = charterShip.getDestination();
        return Transport.builder()
                .source(charterShip.getOrigin().getLocation())
                .destination(destination.getLocation())
                .handler(() ->
                {
                    log.debug("Trying to use charter from {} to {}", charterShip.getOrigin().getLocation(), destination.getLocation());

                    var title = dialog.getOptionTitle();
                    if (widgets.isVisible(title) && title.getText().contains(destination.getName())) {
                        dialog.chooseOption("Yes, and don't ask again.");
                        return true;
                    }

                    var charterWidget = destination.getWidget();
                    if (widgets.isVisible(charterWidget)) {
                        charterWidget.interact(destination.getName());
                        return true;
                    }

                    var npc = npcs.getNearest("Trader Crewmember");
                    if (npc != null && players.getLocal().distanceTo(npc) < MAX_INTERACT_DISTANCE) {
                        String prevAction = "Charter-to " + destination.getName();
                        if (npc.hasAction(prevAction)) {
                            npc.interact(prevAction);
                            return true;
                        }

                        npc.interact("Charter");
                        return true;
                    }

                    log.warn("Failed to find charter ship");
                    return false;
                })
                .requirements(charterShip.getRequirements())
                .weight(charterShip.getCost())
                .build();
    }

    @Override
    public Transport magicCarpet(MagicCarpet magicCarpet) {
        MagicCarpet.CarpetLocation destination = magicCarpet.getDestination();

        return Transport.builder()
                .source(magicCarpet.getOrigin().getLocation())
                .destination(destination.getLocation())
                .handler(() ->
                {
                    log.info("Trying to use magic carpet from {} to {}", magicCarpet.getOrigin().getLocation(), destination.getLocation());

                    var carpetOption = destination.getName();
                    if (dialog.isOpen() && dialog.hasOption(x -> x.contains(carpetOption))) {
                        log.info("Choosing magic carpet option {}", carpetOption);
                        dialog.chooseOption(x -> x.contains(carpetOption));
                        return true;
                    }

                    var npc = npcs.getNearest("Rug Merchant");
                    if (npc != null && players.getLocal().distanceTo(npc) < MAX_INTERACT_DISTANCE) {
                        log.info("Found rug merchant at {}", npc.getWorldLocation());
                        npc.interact("Travel");
                        return true;
                    }

                    log.warn("Failed to find magic carpet at source {} from position {}", magicCarpet.getOrigin().getLocation(), players.getLocal().getWorldLocation());
                    return false;
                })
                .requirements(magicCarpet.getRequirements())
                .weight(magicCarpet.getCost())
                .build();
    }

    @Override
    public Transport birdFlight(BirdFlight source, BirdFlight destination) {
        return Transport.builder()
                .source(source.getLocation())
                .destination(destination.getLocation())
                .handler(() ->
                {
                    var destinationWidget = destination.getWidget();

                    if (widgets.isVisible(destinationWidget)) {
                        destinationWidget.interact(destination.getName());
                        return true;
                    }

                    BirdFlight lastDestination = BirdFlight.getLastDestination();

                    var bird = npcs.getNearest(npc -> source.getLocation().distanceTo(npc.getWorldLocation()) <= MAX_RADIUS && npc.hasAction("Travel") && npc.getName().equalsIgnoreCase("Renu"));
                    if (bird != null) {
                        if (lastDestination != null && lastDestination.getLastIndex() == destination.getLastIndex()) {
                            bird.interact("Last-destination");
                            return true;
                        }

                        bird.interact("Travel");
                        return true;
                    }

                    log.warn("Failed to find Bird at source {}", source);
                    return false;
                })
                .build();
    }

    @Override
    public Transport gnomeGlider(GnomeGlider gnomeGlider) {
        WorldPoint origin = gnomeGlider.getOrigin().getLocation();
        return Transport.builder()
                .source(origin)
                .destination(gnomeGlider.getDestination().getLocation())
                .handler(() ->
                {
                    var widget = gnomeGlider.getDestination().getWidget();
                    if (widgets.isVisible(widget)) {
                        widget.interact(gnomeGlider.getDestination().getName());
                        return true;
                    }

                    if (origin.distanceTo(players.getLocal().getWorldLocation()) > MAX_INTERACT_DISTANCE) {
                        return false;
                    }

                    var gnome = npcs.getNearest(npc -> npc.hasAction("Glider"));
                    if (gnome != null && players.getLocal().distanceTo(gnome) < MAX_INTERACT_DISTANCE) {
                        String action = "Glider-to " + gnomeGlider.getDestination().getName();
                        if (gnome.hasAction(action)) {
                            gnome.interact(action);
                            return true;
                        }

                        gnome.interact("Glider");
                        return true;
                    }

                    log.warn("Failed to find gnome glider");
                    return false;
                })
                .requirements(gnomeGlider.getRequirements())
                .build();
    }

    @Override
    public Transport minecartTransport(WorldPoint source, WorldPoint destination, String target) {
        return Transport.builder()
                .source(source)
                .destination(destination)
                .handler(() ->
                {
                    var minecartWidget = widgets.get(InterfaceID.Menu.LJ_LAYER1);

                    if (minecartWidget == null) {
                        minecartWidget = widgets.get(947, 9);
                    }

                    if (widgets.isVisible(minecartWidget)) {
                        Arrays.stream(minecartWidget.getDynamicChildren())
                                .filter(child -> child.getText().toLowerCase().contains(target.toLowerCase()))
                                .findFirst()
                                .ifPresent(child -> child.interact(MenuAction.WIDGET_CONTINUE));
                        return true;
                    }

                    var minecart = tileObjects.getFirstSurrounding(source, 15, "Minecart");
                    if (minecart != null) {
                        minecart.interact("Travel");
                        return true;
                    }

                    log.warn("Failed to find Minecart at source {}", source);
                    return false;
                })
                .build();
    }

    public Transport npcItemDialogTransport(WorldPoint source, WorldPoint destination, int radius, int itemId, int npcId, String... chatOptions) {
        return Transport.builder()
                .source(source)
                .destination(destination)
                .handler(() ->
                {
                    if (dialog.isOpen()) {
                        if (dialog.canContinue()) {
                            dialog.continueSpace();
                            return true;
                        }

                        if (dialog.chooseOption(chatOptions)) {
                            return true;
                        }

                        return true;
                    }

                    var item = inventory.getFirst(itemId);
                    if (item == null) {
                        return false;
                    }

                    var npc = npcs.getNearest(x -> x.getWorldLocation().distanceTo(source) <= radius && x.getId() == npcId);
                    if (npc != null && players.getLocal().distanceTo(npc) < radius) {
                        item.useOn(npc);
                        return true;
                    }

                    return false;
                })
                .build();
    }

    private List<Transport> zeahBoat() {
        List<Transport> transports = new ArrayList<>();
        // Port sarim
        if (vars.getBit(VarbitID.ZEAH_PLAYERHASVISITED) == 0) {
            if (vars.getBit(VarbitID.CLUEQUEST) >= 7) {
                transports.add(npcDialogTransport(new WorldPoint(3054, 3245, 0),
                        new WorldPoint(1824, 3691, 0),
                        8484,
                        "Can you take me to Great Kourend?"));
            } else {
                transports.add(npcDialogTransport(new WorldPoint(3054, 3245, 0),
                        new WorldPoint(1824, 3691, 0),
                        8484,
                        "That's great, can you take me there please?"));
            }
        } else {
            if (vars.getBit(VarbitID.AKD) >= 14 || quests.isFinished(Quest.A_KINGDOM_DIVIDED)) {
                // Port sarim
                transports.add(npcTransport(
                        new WorldPoint(3054, 3245, 0),
                        new WorldPoint(1824, 3695, 1),
                        "Cabin Boy Herbert",
                        "Port Piscarilius"));
                transports.add(npcTransport(
                        new WorldPoint(3054, 3245, 0),
                        new WorldPoint(1504, 3395, 1),
                        "Cabin Boy Herbert",
                        "Land's End"));

                // Piscarilius
                transports.add(npcTransport(
                        new WorldPoint(1824, 3691, 0),
                        new WorldPoint(3055, 3242, 1),
                        "Cabin Boy Herbert",
                        "Port Sarim"));
                transports.add(npcTransport(
                        new WorldPoint(1824, 3691, 0),
                        new WorldPoint(1504, 3395, 1),
                        "Cabin Boy Herbert",
                        "Land's End"));

                // Land's End
                transports.add(npcTransport(
                        new WorldPoint(1504, 3395, 0),
                        new WorldPoint(3054, 3245, 1),
                        "Captain Magoro",
                        "Port Sarim"));
                transports.add(npcTransport(
                        new WorldPoint(1504, 3395, 0),
                        new WorldPoint(1824, 3695, 1),
                        "Captain Magoro",
                        "Port Piscarilius"));
            } else {
                // Port sarim
                transports.add(npcTransport(
                        new WorldPoint(3054, 3245, 0),
                        new WorldPoint(1824, 3695, 1),
                        "Veos",
                        "Port Piscarilius"));
                transports.add(npcTransport(
                        new WorldPoint(3054, 3245, 0),
                        new WorldPoint(1504, 3395, 1),
                        "Veos",
                        "Land's End"));

                // Piscarilius
                transports.add(npcTransport(
                        new WorldPoint(1824, 3691, 0),
                        new WorldPoint(3055, 3242, 1),
                        "Veos",
                        "Port Sarim"));
                transports.add(npcTransport(
                        new WorldPoint(1824, 3691, 0),
                        new WorldPoint(1504, 3395, 1),
                        "Veos",
                        "Land's End"));

                // Land's End
                transports.add(npcTransport(
                        new WorldPoint(1504, 3395, 0),
                        new WorldPoint(3054, 3245, 1),
                        "Captain Magoro",
                        "Port Sarim"));
                transports.add(npcTransport(
                        new WorldPoint(1504, 3395, 0),
                        new WorldPoint(1824, 3695, 1),
                        "Captain Magoro",
                        "Port Piscarilius"));
            }
        }

        return transports;
    }

    private List<Transport> edgeLever() {
        List<Transport> transports = new ArrayList<>();
        //Edge > Wilderness
        transports.add(objectTransport(new WorldPoint(3090, 3475, 0), new WorldPoint(3154, 3924, 0), 26761, "Pull"));

        return transports;
    }

    private List<Transport> warriorsGuild() {
        List<Transport> transports = new ArrayList<>();
        //Warrior's Guild
        int attackLevel = skills.getLevel(Skill.ATTACK);
        int strengthLevel = skills.getLevel(Skill.STRENGTH);

        if (attackLevel + strengthLevel >= 130 || attackLevel == 99 || strengthLevel == 99) {
            transports.add(objectTransport(new WorldPoint(2877, 3546, 0), new WorldPoint(2876, 3546, 0), 24318, "Open"));
        }

        return transports;
    }

    private List<Transport> shamans() {
        List<Transport> transports = new ArrayList<>();
        //Shamans
        transports.add(objectTransport(new WorldPoint(1312, 3685, 0), new WorldPoint(1312, 10086, 0), 34405, "Enter"));
        /**
         * Doors for shamans
         */
        transports.add(objectTransport(new WorldPoint(1293, 10090, 0), new WorldPoint(1293, 10093, 0), 34642, "Pass"));
        transports.add(objectTransport(new WorldPoint(1293, 10093, 0), new WorldPoint(1293, 10091, 0), 34642, "Pass"));
        transports.add(objectTransport(new WorldPoint(1296, 10096, 0), new WorldPoint(1298, 10096, 0), 34642, "Pass"));
        transports.add(objectTransport(new WorldPoint(1298, 10096, 0), new WorldPoint(1296, 10096, 0), 34642, "Pass"));
        transports.add(objectTransport(new WorldPoint(1307, 10096, 0), new WorldPoint(1309, 10096, 0), 34642, "Pass"));
        transports.add(objectTransport(new WorldPoint(1309, 10096, 0), new WorldPoint(1307, 10096, 0), 34642, "Pass"));
        transports.add(objectTransport(new WorldPoint(1316, 10096, 0), new WorldPoint(1318, 10096, 0), 34642, "Pass"));
        transports.add(objectTransport(new WorldPoint(1318, 10096, 0), new WorldPoint(1316, 10096, 0), 34642, "Pass"));
        transports.add(objectTransport(new WorldPoint(1324, 10096, 0), new WorldPoint(1326, 10096, 0), 34642, "Pass"));
        transports.add(objectTransport(new WorldPoint(1326, 10096, 0), new WorldPoint(1324, 10096, 0), 34642, "Pass"));

        return transports;
    }

    private List<Transport> crabClawIsland(int goldCount) {
        List<Transport> transports = new ArrayList<>();
        // Crabclaw island
        if (goldCount >= 10_000) {
            transports.add(npcTransport(20, new WorldPoint(1782, 3458, 0), new WorldPoint(1778, 3417, 0), 7483, "Travel"));
        }

        transports.add(npcTransport(20, new WorldPoint(1779, 3418, 0), new WorldPoint(1784, 3458, 0), 7484, "Travel"));
        return transports;
    }

    private List<Transport> lunarIsleBoat() {
        List<Transport> transports = new ArrayList<>();
        if (quests.getState(Quest.LUNAR_DIPLOMACY) != QuestState.NOT_STARTED) {
            transports.add(npcTransport(new WorldPoint(2222, 3796, 2), new WorldPoint(2130, 3899, 2), NpcID.LUNAR_PIRATE_CAPTAIN_2OPS, "Travel"));
            transports.add(npcTransport(new WorldPoint(2130, 3899, 2), new WorldPoint(2222, 3796, 2), NpcID.LUNAR_PIRATE_CAPTAIN_2OPS, "Travel"));
        }

        return transports;
    }

    private List<Transport> spiritTrees() {
        List<Transport> transports = new ArrayList<>();
        // Spirit Trees
        if (quests.isFinished(Quest.TREE_GNOME_VILLAGE) && !isCarrying(SPIRIT_TREE_BLACKLISTED)) {
            transports.addAll(SpiritTree.getTransports());
        }

        return transports;
    }

    private List<Transport> lumbridgeCellarMine() {
        List<Transport> transports = new ArrayList<>();
        // Lumbridge cellar mine
        if (quests.isFinished(Quest.THE_LOST_TRIBE)) {
            transports.add(npcTransport(new WorldPoint(3229, 9610, 0), new WorldPoint(3316, 9613, 0), "Kazgar", "Mines", "Follow"));
            transports.add(npcTransport(new WorldPoint(3316, 9613, 0), new WorldPoint(3229, 9610, 0), "Mistag", "Cellar", "Follow"));
        }

        return transports;
    }

    private List<Transport> lumbridgeShack() {
        List<Transport> transports = new ArrayList<>();
        // Lumbridge shack
        var completedDiary = vars.getBit(VarbitID.LUMBRIDGE_DIARY_ELITE_COMPLETE) == 1;
        if (completedDiary) {
            transports.add(objectTransport(new WorldPoint(3201, 3169, 0), new WorldPoint(2452, 4473, 0), 2406, "Open"));
        } else if (isCarrying(ItemID.DRAMEN_STAFF) || vars.getBit(VarbitID.LUNAR_QUEST_MAIN) > 155 && isCarrying(FAIRY_ITEMS)) {
            transports.add(itemWearTransport(new WorldPoint(3201, 3169, 0), new WorldPoint(2452, 4473, 0), 2406, "Open", FAIRY_ITEMS));
        }

        return transports;
    }


    private List<Transport> treeGnomeVillageElkoy() {
        List<Transport> transports = new ArrayList<>();
        // Tree Gnome Village
        if (quests.getState(Quest.TREE_GNOME_VILLAGE) != QuestState.NOT_STARTED) {
            transports.add(npcTransport(new WorldPoint(2504, 3192, 0), new WorldPoint(2515, 3159, 0), 4968, "Follow"));
            transports.add(npcTransport(new WorldPoint(2515, 3159, 0), new WorldPoint(2504, 3192, 0), 4968, "Follow"));
        }

        return transports;
    }

    private List<Transport> eaglesPeakCave() {
        List<Transport> transports = new ArrayList<>();
        // Eagles peak cave
        if (vars.getVarp(VarPlayerID.EAGLEPEAK) >= 15) {
            // Entrance
            transports.add(objectTransport(new WorldPoint(2328, 3496, 0), new WorldPoint(1994, 4983, 3), 19790,
                    "Enter"));
            transports.add(objectTransport(new WorldPoint(1994, 4983, 3), new WorldPoint(2328, 3496, 0), 19891,
                    "Exit"));
        }

        return transports;
    }

    private List<Transport> rellekaBoats(int goldCount) {
        List<Transport> transports = new ArrayList<>();
        // Waterbirth islandhe i ashe th
        if (quests.isFinished(Quest.THE_FREMENNIK_TRIALS)) {
            transports.add(npcTransport(new WorldPoint(2544, 3760, 0), new WorldPoint(2620, 3682, 0), 10407, "Rellekka"));
            transports.add(npcTransport(new WorldPoint(2620, 3682, 0), new WorldPoint(2547, 3759, 0), 5937, "Waterbirth Island"));
        } else {
            if (goldCount >= 1000) {
                transports.add(npcDialogTransport(new WorldPoint(2620, 3682, 0), new WorldPoint(2547, 3759, 0), 5937, "What Jarvald is doing.", "Can I come?", "YES"));
            }
        }

        if (quests.isFinished(Quest.THE_FREMENNIK_TRIALS)) {
            transports.add(npcTransport(15, new WorldPoint(2581, 3845, 0), new WorldPoint(2629, 3692, 0), 3680, "Rellekka"));
            transports.add(npcTransport(15, new WorldPoint(2575, 3853, 0), new WorldPoint(2629, 3692, 0), 3680, "Rellekka"));
            transports.add(npcTransport(15, new WorldPoint(2629, 3692, 0), new WorldPoint(2581, 3845, 0), 3936, "Miscellania"));
        }

        // Pirates cove
        transports.add(npcTransport(new WorldPoint(2620, 3692, 0), new WorldPoint(2213, 3794, 0), NpcID.LUNAR_FREMENNIK_PIRATE_BY_PIRATESHIP, "Pirate's Cove"));
        transports.add(npcTransport(new WorldPoint(2213, 3794, 0), new WorldPoint(2620, 3692, 0), NpcID.LUNAR_FREMENNIK_PIRATE_PIRATECOVE, "Rellekka"));

        return transports;
    }

    private List<Transport> corsairCove() {
        List<Transport> transports = new ArrayList<>();
        // Corsair's Cove
        if (skills.getBoostedLevel(Skill.AGILITY) >= 10) {
            transports.add(objectTransport(new WorldPoint(2546, 2871, 0), new WorldPoint(2546, 2873, 0), 31757,
                    "Climb"));
            transports.add(objectTransport(new WorldPoint(2546, 2873, 0), new WorldPoint(2546, 2871, 0), 31757,
                    "Climb"));
        }

        return transports;
    }

    private List<Transport> lumbridgeCastleDiningRoom() {
        List<Transport> transports = new ArrayList<>();
        // Lumbridge castle dining room, ignore if RFD is in progress.
        if (quests.getState(Quest.RECIPE_FOR_DISASTER) != QuestState.IN_PROGRESS) {
            transports.add(objectTransport(new WorldPoint(3213, 3221, 0), new WorldPoint(3212, 3221, 0), 12349, "Open"));
            transports.add(objectTransport(new WorldPoint(3212, 3221, 0), new WorldPoint(3213, 3221, 0), 12349, "Open"));
            transports.add(objectTransport(new WorldPoint(3213, 3222, 0), new WorldPoint(3212, 3222, 0), 12350, "Open"));
            transports.add(objectTransport(new WorldPoint(3212, 3222, 0), new WorldPoint(3213, 3222, 0), 12350, "Open"));
            transports.add(objectTransport(new WorldPoint(3207, 3218, 0), new WorldPoint(3207, 3217, 0), 12348, "Open"));
            transports.add(objectTransport(new WorldPoint(3207, 3217, 0), new WorldPoint(3207, 3218, 0), 12348, "Open"));
        }

        return transports;
    }

    private List<Transport> digsiteGate() {
        List<Transport> transports = new ArrayList<>();
        // Digsite gate
        if (vars.getBit(VarbitID.VM_KUDOS) >= 153) {
            transports.add(objectTransport(new WorldPoint(3295, 3429, 0), new WorldPoint(3296, 3429, 0), 24561,
                    "Open"));
            transports.add(objectTransport(new WorldPoint(3296, 3429, 0), new WorldPoint(3295, 3429, 0), 24561,
                    "Open"));
            transports.add(objectTransport(new WorldPoint(3295, 3428, 0), new WorldPoint(3296, 3428, 0), 24561,
                    "Open"));
            transports.add(objectTransport(new WorldPoint(3296, 3428, 0), new WorldPoint(3295, 3428, 0), 24561,
                    "Open"));
        }

        return transports;
    }

    private List<Transport> fairyRings(List<Integer> items) {
        List<Transport> transports = new ArrayList<>();
        // Fairy Rings
        if (canUseFairyRings(items)) {
            transports.addAll(FairyRing.getTransports());
        }

        return transports;
    }

    private List<Transport> charterShips() {
        List<Transport> transports = new ArrayList<>();
        if (!isCarrying(CHARTERSHIP_BLACKLITSED)) {
            transports.addAll(CharterShip.getCharterShipTransports());
        }

        return transports;
    }

    private List<Transport> magicCarpets() {
        List<Transport> transports = new ArrayList<>();
        transports.addAll(MagicCarpet.getCarpetTransports());
        return transports;
    }

    private List<Transport> entrana() {
        List<Transport> transports = new ArrayList<>();
        // Entrana
        transports.add(npcTransport(new WorldPoint(3041, 3237, 0), new WorldPoint(2834, 3331, 1), "Monk of Entrana", "Take-boat"));
        transports.add(npcTransport(new WorldPoint(2834, 3335, 0), new WorldPoint(3048, 3231, 1), "Monk of Entrana", "Take-boat"));
        transports.add(npcDialogTransport(new WorldPoint(2821, 3374, 0),
                new WorldPoint(2822, 9774, 0),
                1164,
                "Well that is a risk I will have to take."));

        return transports;
    }

    private List<Transport> fossilIsland() {
        List<Transport> transports = new ArrayList<>();
        // Fossil Island
        transports.add(npcTransport(new WorldPoint(3362, 3445, 0),
                new WorldPoint(3724, 3808, 0),
                8012,
                "Quick-Travel")
        );

        return transports;
    }

    private List<Transport> gnomeStronghold() {
        List<Transport> transports = new ArrayList<>();
        // Gnome stronghold
        transports.add(objectDialogTransport(new WorldPoint(2461, 3382, 0),
                new WorldPoint(2461, 3385, 0),
                190,
                "Open",
                "Sorry, I'm a bit busy."));

        return transports;
    }

    private List<Transport> paterdomus() {
        List<Transport> transports = new ArrayList<>();
        // Paterdomus
        transports.add(trapDoorTransport(new WorldPoint(3405, 3506, 0), new WorldPoint(3405, 9906, 0), 1579, 1581));
        transports.add(trapDoorTransport(new WorldPoint(3423, 3485, 0), new WorldPoint(3440, 9887, 0), 3432, 3433));
        transports.add(trapDoorTransport(new WorldPoint(3422, 3484, 0), new WorldPoint(3440, 9887, 0), 3432, 3433));

        return transports;
    }

    private List<Transport> edgeville() {
        List<Transport> transports = new ArrayList<>();
        // Edgeville
        transports.add(trapDoorTransport(new WorldPoint(3096, 3468, 0), new WorldPoint(3096, 9867, 0), 1579, 1581));

        return transports;
    }

    private List<Transport> varrock() {
        List<Transport> transports = new ArrayList<>();
        //Varrock sewer
        transports.add(trapDoorTransport(new WorldPoint(3236, 3458, 0), new WorldPoint(3237, 9858, 0), 881, 882));

        return transports;
    }

    private List<Transport> glarialsTomb() {
        List<Transport> transports = new ArrayList<>();
        // Glarial's tomb
        transports.add(itemUseTransport(new WorldPoint(2557, 3444, 0), new WorldPoint(2555, 9844, 0), 294, 1992));
        transports.add(itemUseTransport(new WorldPoint(2557, 3445, 0), new WorldPoint(2555, 9844, 0), 294, 1992));
        transports.add(itemUseTransport(new WorldPoint(2558, 3443, 0), new WorldPoint(2555, 9844, 0), 294, 1992));
        transports.add(itemUseTransport(new WorldPoint(2559, 3443, 0), new WorldPoint(2555, 9844, 0), 294, 1992));
        transports.add(itemUseTransport(new WorldPoint(2560, 3444, 0), new WorldPoint(2555, 9844, 0), 294, 1992));
        transports.add(itemUseTransport(new WorldPoint(2560, 3445, 0), new WorldPoint(2555, 9844, 0), 294, 1992));
        transports.add(itemUseTransport(new WorldPoint(2558, 3446, 0), new WorldPoint(2555, 9844, 0), 294, 1992));
        transports.add(itemUseTransport(new WorldPoint(2559, 3446, 0), new WorldPoint(2555, 9844, 0), 294, 1992));

        return transports;
    }

    private List<Transport> waterfallIsland() {
        List<Transport> transports = new ArrayList<>();
        // Waterfall Island
        transports.add(itemUseTransport(new WorldPoint(2512, 3476, 0), new WorldPoint(2513, 3468, 0), 954, 1996, 10));
        transports.add(itemUseTransport(new WorldPoint(2512, 3466, 0), new WorldPoint(2511, 3463, 0), 954, 2020, 10));

        return transports;
    }

    private List<Transport> undergroundPass() {
        List<Transport> transports = new ArrayList<>();
        //Underground Pass
        transports.add(itemUseTransport(new WorldPoint(2461, 9699, 0), new WorldPoint(2466, 9699, 0), 954, 23125, 10));
        transports.add(itemUseTransport(new WorldPoint(2393, 9651, 0), new WorldPoint(2392, 9646, 0), 952, 3216, 10));

        return transports;
    }

    private List<Transport> piscarillius() {
        List<Transport> transports = new ArrayList<>();
        //Piscarilius
        transports.add(trapDoorTransport(new WorldPoint(1812, 3745, 0), new WorldPoint(1813, 10145, 0), 31706, 31707));
        transports.add(trapDoorTransport(new WorldPoint(1813, 3746, 0), new WorldPoint(1813, 10145, 0), 31706, 31707));
        transports.add(trapDoorTransport(new WorldPoint(1814, 3745, 0), new WorldPoint(1813, 10145, 0), 31706, 31707));

        return transports;
    }

    public List<Transport> canifis() {
        List<Transport> transports = new ArrayList<>();

        transports.add(trapDoorTransport(new WorldPoint(3543, 3463, 0), new WorldPoint(3549, 9865, 0), 11636, 11637));

        return transports;
    }

    private List<Transport> apeAtoll() {
        List<Transport> transports = new ArrayList<>();
        //Ape Atoll
        transports.add(trapDoorTransport(new WorldPoint(2806, 2785, 0), new WorldPoint(2807, 9201, 0), 4879, 4880));

        return transports;
    }

    private List<Transport> ectofuntus() {
        List<Transport> transports = new ArrayList<>();
        //Ecto
        transports.add(trapDoorTransport(new WorldPoint(3654, 3519, 0), new WorldPoint(3669, 9888, 3), 16113, 16114));
        transports.add(trapDoorTransport(new WorldPoint(3653, 3520, 0), new WorldPoint(3669, 9888, 3), 16113, 16114));
        transports.add(trapDoorTransport(new WorldPoint(3652, 3519, 0), new WorldPoint(3669, 9888, 3), 16113, 16114));
        transports.add(trapDoorTransport(new WorldPoint(3653, 3518, 0), new WorldPoint(3669, 9888, 3), 16113, 16114));

        return transports;
    }

    private List<Transport> mountainCamp() {
        List<Transport> transports = new ArrayList<>();
        //Mountain Camp
        transports.add(itemUseTransport(new WorldPoint(2764, 3666, 0), new WorldPoint(2766, 3663, 0), 954, 5842));

        return transports;
    }

    private List<Transport> whirlPool() {
        List<Transport> transports = new ArrayList<>();
        //Whirlpool
        transports.add(objectTransport(10, new WorldPoint(2512, 3515, 0), new WorldPoint(1763, 5365, 1), 25274, "Dive in"));

        return transports;
    }

    private List<Transport> shantayPass(List<Integer> items) {
        List<Transport> transports = new ArrayList<>();
        // Shantay pass
        if (isCarrying(ItemID.SHANTAY_PASS) || containsItem(new int[] {ItemID.SHANTAY_PASS}, items)) {
            var itemRequirement = Requirements.of(new ItemRequirement(Reduction.OR, List.of(ItemID.SHANTAY_PASS), 1));
            transports.add(objectTransport(10, new WorldPoint(3304, 3117, 0), new WorldPoint(3304, 3115, 0), 4031, "Go-through", itemRequirement));
            transports.add(objectTransport(10, new WorldPoint(3193, 2843, 0), new WorldPoint(3196, 2843, 0), 41326, "Go-through", itemRequirement));
            transports.add(objectTransport(10, new WorldPoint(3167, 2819, 0), new WorldPoint(3167, 2816, 0), 41326, "Go-through", itemRequirement));
        }

        return transports;
    }

    private List<Transport> kalphiteTransports() {
        List<Transport> transports = new ArrayList<>();

        transports.add(itemUseAndObjectNameTransport(new WorldPoint(3229, 3108, 0), new WorldPoint(3483, 9510, 2), ItemID.ROPE, "Tunnel entrance", "Climb-down"));
        transports.add(itemUseAndObjectNameTransport(new WorldPoint(3508, 9498, 2), new WorldPoint(3508, 9493, 0), ItemID.ROPE, "Tunnel entrance", "Climb-down"));

        return transports;
    }

    private List<Transport> boats() {
        List<Transport> transports = new ArrayList<>();
        if (inventory.getCount(true, ItemID.COINS) >= 30) {
            // Rimmington boat
            transports.add(npcTransport(new WorldPoint(2915, 3225, 0), new WorldPoint(2683, 3263, 1), 8763, "Ardougne"));
            transports.add(npcTransport(new WorldPoint(2915, 3225, 0), new WorldPoint(2775, 3233, 1), 8763, "Brimhaven"));

            // Ardougne boat
            transports.add(npcTransport(new WorldPoint(2680, 3276, 0), new WorldPoint(2915, 3222, 1), 9250, "Rimmington"));
            transports.add(npcTransport(new WorldPoint(2680, 3276, 0), new WorldPoint(2775, 3233, 1), 9250, "Brimhaven"));

            // Brimhaven boat
            transports.add(npcTransport(new WorldPoint(2772, 3229, 0), new WorldPoint(2915, 3222, 1), 8764, "Rimmington"));
            transports.add(npcTransport(new WorldPoint(2772, 3229, 0), new WorldPoint(2683, 3263, 1), 8764, "Ardougne"));
        }

        return transports;
    }

    private List<Transport> quests(int goldCount) {
        List<Transport> transports = new ArrayList<>();

        if (goldCount >= 30) {
            if (quests.getState(Quest.PANDEMONIUM) == QuestState.IN_PROGRESS || quests.isFinished(Quest.PANDEMONIUM)) {
                transports.add(npcTransport(new WorldPoint(3027, 3218, 0), new WorldPoint(2956, 3143, 1), 14983, "Musa Point"));
                transports.add(npcDialogTransport(new WorldPoint(2954, 3147, 0), new WorldPoint(3032, 3217, 1), 14985, "I'd like to go to Port Sarim.", "Can I journey on this ship?", "Search away. I have nothing to hide.", "Okay.", "Thank you, I'll be on my way"));
            } else {
                if (quests.isFinished(Quest.PIRATES_TREASURE)) {
                    transports.add(npcTransport(new WorldPoint(3027, 3218, 0), new WorldPoint(2956, 3143, 1), 14982, "Travel"));
                    transports.add(npcTransport(new WorldPoint(2954, 3147, 0), new WorldPoint(3032, 3217, 1), 14984, "Travel"));
                } else {
                    transports.add(npcDialogTransport(new WorldPoint(3027, 3218, 0), new WorldPoint(2956, 3143, 1), 14982, "Yes please."));
                    transports.add(npcDialogTransport(new WorldPoint(2954, 3147, 0), new WorldPoint(3032, 3217, 1), 14984, "I'd like to go to Port Sarim.", "Can I journey on this ship?", "Search away. I have nothing to hide.", "Okay.", "Thank you, I'll be on my way"));
                }
            }
        }


        if (quests.isFinished(Quest.THE_SLUG_MENACE)) {
            transports.add(npcTransport(new WorldPoint(2719, 3305, 0), new WorldPoint(2782, 3273, 0), 4803, "Travel"));
            transports.add(npcTransport(new WorldPoint(2782, 3273, 0), new WorldPoint(2719, 3305, 0), 4803, "Travel"));
        } else if (quests.getState(Quest.SEA_SLUG) == QuestState.IN_PROGRESS || quests.getState(Quest.SEA_SLUG) == QuestState.FINISHED) {
            transports.add(npcTransport(new WorldPoint(2719, 3305, 0), new WorldPoint(2782, 3273, 0), 7789, "Travel"));
            transports.add(npcTransport(new WorldPoint(2782, 3273, 0), new WorldPoint(2719, 3305, 0), 5070, "Travel"));
        }

        if (quests.getState(Quest.RECIPE_FOR_DISASTER__SIR_AMIK_VARZE) == QuestState.IN_PROGRESS || quests.getState(Quest.RECIPE_FOR_DISASTER__SIR_AMIK_VARZE) == QuestState.FINISHED) {
            transports.add(itemUseTransport(new WorldPoint(2453, 4476, 0), new WorldPoint(2461, 4356, 0), ItemID.RAW_CHICKEN, 12093));
            transports.add(itemUseAndObjectTransport(new WorldPoint(2457, 4380, 0), new WorldPoint(2441, 4381, 0), 12254, 12253, ItemID.ROPE));
        }

        if (quests.isFinished(Quest.DESERT_TREASURE_II__THE_FALLEN_EMPIRE)) {
            transports.add(npcTransport(new WorldPoint(3613, 9473, 0), new WorldPoint(2012, 6435, 0), 12384, "Travel"));
            transports.add(objectTransport(new WorldPoint(2012, 6435, 0), new WorldPoint(3613, 9473, 0), 49360, "Enter"));
        }

        if (quests.getState(Quest.TOWER_OF_LIFE) == QuestState.IN_PROGRESS || quests.getState(Quest.TOWER_OF_LIFE) == QuestState.FINISHED) {
            transports.add(trapDoorTransport(new WorldPoint(2649, 3212, 0), new WorldPoint(3038, 4376, 0), 21921, 21922));
        }

        //Watchtower rope swing
        transports.add(itemUseTransport(new WorldPoint(2500, 3087, 0), new WorldPoint(2505, 3087, 0), 954, 23638));
        return transports;
    }

    private List<Transport> slashableWebs() {
        List<Transport> transports = new ArrayList<>();
        if (isCarrying(x ->
                x.getName().equalsIgnoreCase("Knife")
                        || x.hasAction("Wield") && (x.getName().toLowerCase().contains("scimitar") || x.getName().toLowerCase().contains("sword") || x.getName().toLowerCase().contains("axe"))
        )) {
            for (Pair<WorldPoint, WorldPoint> pair : SLASH_WEB_POINTS) {
                transports.add(slashWebTransport(pair.getLeft(), pair.getRight()));
                transports.add(slashWebTransport(pair.getRight(), pair.getLeft()));
            }
        }

        return transports;
    }

    private boolean isCarrying(Predicate<? super IItem> filter) {
        return inventory.contains(filter) || equipment.contains(filter);
    }

    private boolean isCarrying(String... names) {
        return isCarrying(Predicates.names(names));
    }

    private boolean isCarrying(int... ids) {
        return isCarrying(Predicates.ids(ids));
    }

    private boolean canUseFairyRings(List<Integer> items) {
        if (quests.getState(Quest.FAIRYTALE_II__CURE_A_QUEEN) == QuestState.NOT_STARTED) {
            return false;
        }

        if (vars.getBit(VarbitID.LUMBRIDGE_DIARY_ELITE_COMPLETE) == 1) {
            return true;
        }

        return isCarrying(ItemID.DRAMEN_STAFF)
                || (isCarrying(FAIRY_ITEMS) && vars.getBit(VarbitID.LUNAR_QUEST_MAIN) > 155)
                || containsItem(FAIRY_ITEMS, items);
    }
}
