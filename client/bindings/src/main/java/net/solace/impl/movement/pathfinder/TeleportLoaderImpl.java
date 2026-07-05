package net.solace.impl.movement.pathfinder;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.GameState;
import net.runelite.api.MenuAction;
import net.runelite.api.Quest;
import net.runelite.api.QuestState;
import net.runelite.api.WorldType;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.api.gameval.VarPlayerID;
import net.runelite.api.gameval.VarbitID;
import net.solace.api.commons.ITime;
import net.solace.api.commons.Predicates;
import net.solace.api.domain.game.IClient;
import net.solace.api.domain.game.IClientThread;
import net.solace.api.domain.items.IItem;
import net.solace.api.entities.IPlayers;
import net.solace.api.entities.ITileObjects;
import net.solace.api.game.IGame;
import net.solace.api.game.IHouse;
import net.solace.api.game.IVars;
import net.solace.api.game.IWorlds;
import net.solace.api.input.IKeyboard;
import net.solace.api.items.IEquipment;
import net.solace.api.items.IInventory;
import net.solace.api.movement.pathfinder.ITeleportLoader;
import net.solace.api.movement.pathfinder.model.FairyRing;
import net.solace.api.movement.pathfinder.model.MovementConstants;
import net.solace.api.movement.pathfinder.model.SpiritTree;
import net.solace.api.movement.pathfinder.model.Teleport;
import net.solace.api.movement.pathfinder.model.TeleportItem;
import net.solace.api.movement.pathfinder.model.TeleportScroll;
import net.solace.api.movement.pathfinder.model.TeleportSpell;
import net.solace.api.movement.pathfinder.model.poh.HousePortal;
import net.solace.api.movement.pathfinder.model.poh.PortalNexus;
import net.solace.api.movement.pathfinder.model.poh.SpiritFairyTree;
import net.solace.api.movement.pathfinder.model.requirement.Requirement;
import net.solace.api.plugins.config.SolaceConfig;
import net.solace.api.quests.IQuests;
import net.solace.api.widgets.IDialog;
import net.solace.api.widgets.IMinigames;
import net.solace.api.widgets.IWidgets;
import net.solace.api.widgets.MinigameTeleport;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static net.solace.api.movement.pathfinder.model.MovementConstants.AMULET_OF_GLORY;
import static net.solace.api.movement.pathfinder.model.MovementConstants.AMULET_OF_THE_EYE;
import static net.solace.api.movement.pathfinder.model.MovementConstants.BURNING_AMULET;
import static net.solace.api.movement.pathfinder.model.MovementConstants.COMBAT_BRACELET;
import static net.solace.api.movement.pathfinder.model.MovementConstants.CONSTRUCTION_CAPE;
import static net.solace.api.movement.pathfinder.model.MovementConstants.CRAFTING_CAPE;
import static net.solace.api.movement.pathfinder.model.MovementConstants.DIGSITE_PENDANT;
import static net.solace.api.movement.pathfinder.model.MovementConstants.DRAKANS_MEDALLION;
import static net.solace.api.movement.pathfinder.model.MovementConstants.FAIRY_ITEMS;
import static net.solace.api.movement.pathfinder.model.MovementConstants.FARMING_CAPE;
import static net.solace.api.movement.pathfinder.model.MovementConstants.FISHING_CAPE;
import static net.solace.api.movement.pathfinder.model.MovementConstants.GAMES_NECKLACE;
import static net.solace.api.movement.pathfinder.model.MovementConstants.GHOMMALS_HILT;
import static net.solace.api.movement.pathfinder.model.MovementConstants.HOUSE_ITEMS;
import static net.solace.api.movement.pathfinder.model.MovementConstants.MAX_CAPE;
import static net.solace.api.movement.pathfinder.model.MovementConstants.NECKLACE_OF_PASSAGE;
import static net.solace.api.movement.pathfinder.model.MovementConstants.RADAS_BLESSING;
import static net.solace.api.movement.pathfinder.model.MovementConstants.RING_OF_DUELING;
import static net.solace.api.movement.pathfinder.model.MovementConstants.RING_OF_THE_ELEMENTS;
import static net.solace.api.movement.pathfinder.model.MovementConstants.RING_OF_WEALTH;
import static net.solace.api.movement.pathfinder.model.MovementConstants.SKILLS_NECKLACE;
import static net.solace.api.movement.pathfinder.model.MovementConstants.SLAYER_RING;
import static net.solace.api.movement.pathfinder.model.MovementConstants.SPIRIT_TREE_BLACKLISTED;
import static net.solace.api.movement.pathfinder.model.MovementConstants.XERICS_TALISMAN;
import static net.solace.api.util.EtcUtils.containsItem;

@RequiredArgsConstructor
@Slf4j
public class TeleportLoaderImpl implements ITeleportLoader {
    private static final Pattern WILDY_PATTERN = Pattern.compile("Okay, teleport to level [\\d,]* Wilderness\\.");

    @Getter
    private final List<Teleport> customTeleports = new ArrayList<>();
    private final List<Teleport> lastTeleports = new ArrayList<>();
    private final List<Teleport> cachedTeleports = new ArrayList<>();

    private final SolaceConfig solaceConfig;
    private final IClientThread clientThread;
    private final IWorlds worlds;
    private final IGame game;
    private final IHouse house;
    private final ITileObjects tileObjects;
    private final IClient client;
    private final IPlayers players;
    private final IInventory inventory;
    private final IEquipment equipment;
    private final IWidgets widgets;
    private final IVars vars;
    private final IDialog dialog;
    private final IQuests quests;
    private final IMinigames minigames;
    private final IKeyboard keyboard;
    private final ITime time;

    @Override
    public void refreshTeleports(List<Integer> items) {
        clientThread.invoke(() ->
        {
            List<Teleport> teleports = new ArrayList<>();
            if (worlds.inMembersWorld()) {
                if (solaceConfig.usePoh() && ((house.canEnter() || containsItem(HOUSE_ITEMS, items)) || house.isInside())) {
                    teleports.addAll(pohMountedTeleports());
                    teleports.addAll(pohJewelryBox());
                    teleports.addAll(pohPortals());
                    teleports.addAll(pohSpiritFairyTree(items));
                }

                if (house.isInside()) {
                    teleports.add(Teleport.builder()
                            .destination(house.getOutsideLocation())
                            .handler(() -> {
                                tileObjects.getNearest(x -> x.hasAction("Enter") && x.hasAction("Lock")).interact("Enter");
                                return true;
                            })
                            .poh(true)
                            .priority(1)
                            .build());
                }

                teleports.addAll(maxCapeTeleports(items));

                // One click teleport items
                teleports.addAll(teleportItems(items));

                teleports.addAll(duelingRing(items));
                teleports.addAll(gamesNecklace(items));
                teleports.addAll(necklaceOfPassage(items));
                teleports.addAll(xericsTalisman(items));
                teleports.addAll(digsitePendant(items));
                teleports.addAll(skillCapes(items));
                teleports.addAll(kharedstMemoirs(items));
                teleports.addAll(diaryItems(items));
                teleports.addAll(ringOfTheElements(items));
                teleports.addAll(ringOfShadows(items));
                teleports.addAll(questItems(items));

                teleports.addAll(combatBracelet(items));
                teleports.addAll(skillsNecklace(items));
                teleports.addAll(ringOfWealth(items));
                teleports.addAll(amuletOfGlory(items));
                teleports.addAll(burningAmulet(items));
                teleports.addAll(slayerRing(items));
                teleports.addAll(giantsoulAmulet(items));
                teleports.addAll(pendentOfAtes(items));

                teleports.addAll(getFairyMushroomLeprechauns());
                teleports.addAll(getFairyMushroomRings());
                teleports.addAll(getFairyMushroomSpiritTrees());
                teleports.addAll(getBankersBriefcaseTeleports());

                if (MovementConstants.inDeathDomain()) {
                    teleports.add(Teleport.builder()
                            .destination(new WorldPoint(3095, 3476, 0))
                            .handler(() ->
                            {
                                tileObjects.getNearest(x -> x.hasAction("Use") && x.getId() == ObjectID.DEATH_OFFICE_EXITPORTAL).interact("Use");
                                return true;
                            })
                            .priority(1)
                            .build());
                }
            }

            if (!items.isEmpty()) {
                cachedTeleports.clear();
                cachedTeleports.addAll(teleports);
            } else {
                lastTeleports.clear();
                lastTeleports.addAll(teleports);
            }

            log.debug("Loaded {} teleports", lastTeleports.size());
        });
    }

    @Override
    public List<Teleport> buildTeleports(Boolean useCached) {
        List<Teleport> teleports = new ArrayList<>();
        teleports.addAll(customTeleports);
        teleports.addAll(buildTimedTeleports());
        teleports.addAll(useCached ? cachedTeleports : lastTeleports);
        return teleports;
    }

    @Override
    public List<Teleport> buildTimedTeleports() {
        List<Teleport> teleports = new ArrayList<>();

        if (worlds.inMembersWorld()) {
            for (var teleportScroll : TeleportScroll.values()) {
                if (teleportScroll.canUse()) {
                    teleports.add(Teleport.builder()
                            .destination(teleportScroll.getDestination())
                            .handler(teleportScroll::use)
                            .itemRequirements(new int[]{teleportScroll.getItemId()})
                            .build());
                }
            }

            if (solaceConfig.useMinigameTeleports()
                    && !MovementConstants.inDeathDomain()
                    && !players.getLocal().isHealthBarVisible()
                    && !client.isInInstancedRegion()
                    && !house.isInside()
                    && vars.getBit(14002) == 0
                    && client.getWorldType().stream().noneMatch(x -> x == WorldType.DEADMAN || x == WorldType.SEASONAL)) {
                // Minigames
                if (minigames.canTeleport()) {
                    for (var tp : MinigameTeleport.values()) {
                        if (tp.canUse()) {
                            teleports.add(Teleport.builder()
                                    .destination(tp.getLocation())
                                    .handler(() -> minigames.teleport(tp))
                                    .maximumWildernessLevel(0)
                                    .teleportDelay(3)
                                    .isMinigameTeleport(true)
                                    .isTimedTeleport(true)
                                    .build()
                            );
                        }
                    }
                }
            }
        }

        for (var teleportSpell : TeleportSpell.values()) {
            if (!teleportSpell.canCast() || teleportSpell.getPoint() == null || teleportSpell == TeleportSpell.TELEPORT_TO_HOUSE) {
                log.debug("Cannot cast {}", teleportSpell);
                continue;
            }

            var isHomeTele = teleportSpell == TeleportSpell.LUNAR_HOME_TELEPORT
                    || teleportSpell == TeleportSpell.ARCEUUS_HOME_TELEPORT
                    || teleportSpell == TeleportSpell.LUMBRIDGE_HOME_TELEPORT
                    || teleportSpell == TeleportSpell.EDGEVILLE_HOME_TELEPORT;

            if (isHomeTele && (!solaceConfig.useHomeTeleports() || players.getLocal().isHealthBarVisible())) {
                continue;
            }

            if (teleportSpell.getPoint().distanceTo(players.getLocal().getWorldLocation()) > 50) {
                teleports.add(Teleport.builder()
                        .destination(teleportSpell.getPoint())
                        .priority(isHomeTele ? Integer.MAX_VALUE : 0)
                        .handler(() ->
                        {
                            if (isHomeTele) {
                                if (players.getLocal().getSpotAnimationCount() <= 0 && players.getLocal().isIdle()) {
                                    teleportSpell.cast();
                                }
                            } else {
                                teleportSpell.cast();
                            }

                            return true;
                        })
                        .isHomeTeleport(isHomeTele)
                        .isTimedTeleport(isHomeTele)
                        .teleportDelay(isHomeTele ? 3 : 0)
                        .itemRequirements(isHomeTele ? null : Arrays.stream(teleportSpell.getSpell().getRequirements()).mapToInt(req -> req.getRune().getRuneId()).toArray())
                        .build());
            }
        }

        return teleports;
    }

    @Override
    public List<Teleport> teleportItems(List<Integer> items) {
        List<Teleport> teleports = new ArrayList<>();
        for (var tele : TeleportItem.values()) {
            if (tele.canUse(items) && tele.getDestination().distanceTo(players.getLocal().getWorldLocation()) > 20) {
                if (tele == TeleportItem.ROYAL_SEED_POD) {
                    if (game.getWildyLevel() <= 30) {
                        final var teleport = itemTeleport(tele);
                        teleport.setMaximumWildernessLevel(30);
                        teleports.add(teleport);
                    }
                } else if (game.getWildyLevel() <= 20) {
                    final var teleport = itemTeleport(tele);
                    teleport.setMaximumWildernessLevel(30);

                    if (tele == TeleportItem.ECTOPHIAL) {
                        teleport.setWalkerDelay(8);
                    }
                    teleports.add(teleport);
                }
            }
        }

        return teleports;
    }

    @Override
    public List<Teleport> duelingRing(List<Integer> items) {
        List<Teleport> teleports = new ArrayList<>();
        if (isCarrying(RING_OF_DUELING) || containsItem(RING_OF_DUELING, items)) {
            teleports.add(Teleport.builder()
                    .destination(new WorldPoint(3315, 3235, 0))
                    .handler(() -> jewelryTeleport("Emir's Arena", RING_OF_DUELING))
                    .priority(1)
                    .itemRequirements(RING_OF_DUELING)
                    .build());
            teleports.add(Teleport.builder()
                    .destination(new WorldPoint(2440, 3090, 0))
                    .handler(() -> jewelryTeleport("Castle Wars", RING_OF_DUELING))
                    .priority(1)
                    .itemRequirements(RING_OF_DUELING)
                    .build());
            teleports.add(Teleport.builder()
                    .destination(new WorldPoint(3151, 3635, 0))
                    .handler(() -> jewelryTeleport("Ferox Enclave", RING_OF_DUELING))
                    .priority(1)
                    .itemRequirements(RING_OF_DUELING)
                    .build());

            if (vars.getVarp(4130) >= 12000) {
                teleports.add(Teleport.builder()
                        .destination(new WorldPoint(1793, 3109, 0))
                        .handler(() -> jewelryTeleport("Fortis Colosseum", RING_OF_DUELING))
                        .priority(1)
                        .itemRequirements(RING_OF_DUELING)
                        .build());
            }
        }

        return teleports;
    }

    @Override
    public List<Teleport> gamesNecklace(List<Integer> items) {
        List<Teleport> teleports = new ArrayList<>();
        if (isCarrying(GAMES_NECKLACE) || containsItem(GAMES_NECKLACE, items)) {
            teleports.add(Teleport.builder()
                    .destination(new WorldPoint(2898, 3553, 0))
                    .handler(() -> jewelryTeleport("Burthorpe", GAMES_NECKLACE))
                    .priority(1)
                    .itemRequirements(GAMES_NECKLACE)
                    .build());
            teleports.add(Teleport.builder()
                    .destination(new WorldPoint(2520, 3571, 0))
                    .handler(() -> jewelryTeleport("Barbarian Outpost", GAMES_NECKLACE))
                    .priority(1)
                    .itemRequirements(GAMES_NECKLACE)
                    .build());
            teleports.add(Teleport.builder()
                    .destination(new WorldPoint(2964, 4382, 2))
                    .handler(() -> jewelryTeleport("Corporeal Beast", GAMES_NECKLACE))
                    .priority(1)
                    .itemRequirements(GAMES_NECKLACE)
                    .build());

            if (quests.isFinished(Quest.TEARS_OF_GUTHIX)) {
                teleports.add(Teleport.builder()
                        .destination(new WorldPoint(3244, 9501, 2))
                        .handler(() -> jewelryTeleport("Tears of Guthix", GAMES_NECKLACE))
                        .priority(1)
                        .itemRequirements(GAMES_NECKLACE)
                        .build());
            }

            if (vars.getBit(VarbitID.ZEAH_PLAYERHASVISITED) == 1) {
                teleports.add(Teleport.builder()
                        .destination(new WorldPoint(1624, 3938, 0))
                        .handler(() -> jewelryTeleport("Wintertodt Camp", GAMES_NECKLACE))
                        .priority(1)
                        .itemRequirements(GAMES_NECKLACE)
                        .build());
            }
        }

        return teleports;
    }

    @Override
    public List<Teleport> necklaceOfPassage(List<Integer> items) {
        List<Teleport> teleports = new ArrayList<>();
        if (isCarrying(NECKLACE_OF_PASSAGE) || containsItem(NECKLACE_OF_PASSAGE, items)) {
            teleports.add(Teleport.builder()
                    .destination(new WorldPoint(3114, 3179, 0))
                    .handler(() -> jewelryTeleport("Wizards' Tower", NECKLACE_OF_PASSAGE))
                    .priority(1)
                    .itemRequirements(NECKLACE_OF_PASSAGE)
                    .build());
            teleports.add(Teleport.builder()
                    .destination(new WorldPoint(2430, 3348, 0))
                    .handler(() -> jewelryTeleport("The Outpost", NECKLACE_OF_PASSAGE))
                    .priority(1)
                    .itemRequirements(NECKLACE_OF_PASSAGE)
                    .build());
            teleports.add(Teleport.builder()
                    .destination(new WorldPoint(3405, 3157, 0))
                    .handler(() -> jewelryTeleport("Eagles' Eyrie", NECKLACE_OF_PASSAGE))
                    .priority(1)
                    .itemRequirements(NECKLACE_OF_PASSAGE)
                    .build());
        }

        return teleports;
    }

    @Override
    public List<Teleport> xericsTalisman(List<Integer> items) {
        List<Teleport> teleports = new ArrayList<>();
        if (isCarrying(XERICS_TALISMAN) || containsItem(XERICS_TALISMAN, items)) {
            teleports.add(Teleport.builder()
                    .destination(new WorldPoint(1576, 3530, 0))
                    .handler(() -> jewelryPopupTeleport("Xeric's Lookout", XERICS_TALISMAN))
                    .priority(1)
                    .itemRequirements(XERICS_TALISMAN)
                    .build());
            teleports.add(Teleport.builder()
                    .destination(new WorldPoint(1752, 3566, 0))
                    .handler(() -> jewelryPopupTeleport("Xeric's Glade", XERICS_TALISMAN))
                    .priority(1)
                    .itemRequirements(XERICS_TALISMAN)
                    .build());
            teleports.add(Teleport.builder()
                    .destination(new WorldPoint(1504, 3817, 0))
                    .handler(() -> jewelryPopupTeleport("Xeric's Inferno", XERICS_TALISMAN))
                    .priority(1)
                    .itemRequirements(XERICS_TALISMAN)
                    .build());
            teleports.add(Teleport.builder()
                    .destination(new WorldPoint(1640, 3674, 0))
                    .handler(() -> jewelryPopupTeleport("Xeric's Heart", XERICS_TALISMAN))
                    .priority(1)
                    .itemRequirements(XERICS_TALISMAN)
                    .build());
        }

        return teleports;
    }

    @Override
    public List<Teleport> digsitePendant(List<Integer> items) {
        List<Teleport> teleports = new ArrayList<>();
        if (isCarrying(DIGSITE_PENDANT) || containsItem(DIGSITE_PENDANT, items)) {
            teleports.add(Teleport.builder()
                    .destination(new WorldPoint(3341, 3445, 0))
                    .handler(() -> jewelryTeleport("Digsite", DIGSITE_PENDANT))
                    .priority(1)
                    .itemRequirements(DIGSITE_PENDANT)
                    .build());
            teleports.add(Teleport.builder()
                    .destination(new WorldPoint(3764, 3869, 1))
                    .handler(() -> jewelryTeleport("Fossil Island", DIGSITE_PENDANT))
                    .priority(1)
                    .itemRequirements(DIGSITE_PENDANT)
                    .build());
            if (vars.getBit(VarbitID.LITHKREN_RUBY_NECKLACE_REDIRECT) == 1) {
                teleports.add(Teleport.builder()
                        .destination(new WorldPoint(3549, 10456, 0))
                        .handler(() -> jewelryTeleport("Lithkren", DIGSITE_PENDANT))
                        .priority(1)
                        .itemRequirements(DIGSITE_PENDANT)
                        .build());
            }
        }

        return teleports;
    }

    @Override
    public List<Teleport> skillCapes(List<Integer> items) {
        List<Teleport> teleports = new ArrayList<>();
        //Construction teleports
        if (isCarrying(CONSTRUCTION_CAPE)
            || isCarrying(MAX_CAPE)
            || containsItem(CONSTRUCTION_CAPE, items)) {
            teleports.add(constructionCapeTeleport(new WorldPoint(2954, 3224, 0), "Rimmington"));
            teleports.add(constructionCapeTeleport(new WorldPoint(2894, 3465, 0), "Taverley"));
            teleports.add(constructionCapeTeleport(new WorldPoint(3340, 3004, 0), "Pollnivneach"));
            teleports.add(constructionCapeTeleport(new WorldPoint(1744, 3517, 0), "Hosidius"));
            teleports.add(constructionCapeTeleport(new WorldPoint(1422, 2965, 0), "Aldarin"));
            teleports.add(constructionCapeTeleport(new WorldPoint(2670, 3632, 0), "Rellekka"));
            teleports.add(constructionCapeTeleport(new WorldPoint(2758, 3178, 0), "Brimhaven"));
            teleports.add(constructionCapeTeleport(new WorldPoint(2544, 3095, 0), "Yanille"));
            if (quests.isFinished(Quest.SONG_OF_THE_ELVES)) {
                teleports.add(constructionCapeTeleport(new WorldPoint(3239, 6076, 0), "Prifddinas"));
            }
        }

        if (isCarrying(CRAFTING_CAPE) || containsItem(CRAFTING_CAPE, items)) {
            final var craftingTeleport = equipableTeleport(new WorldPoint(2931, 3286, 0), "Teleport", "Teleport", CRAFTING_CAPE);
            craftingTeleport.setForceLoad(true);
            teleports.add(craftingTeleport);
        }

        if (isCarrying(FISHING_CAPE) || containsItem(FISHING_CAPE, items)) {
            teleports.add(equipableTeleport(new WorldPoint(2604, 3401, 0), "Fishing Guild", "Fishing Guild", FISHING_CAPE));
            teleports.add(equipableTeleport(new WorldPoint(2504, 3484, 0), "Otto's Grotto", "Otto's Grotto", FISHING_CAPE));
        }

        if (isCarrying(FARMING_CAPE) || containsItem(FARMING_CAPE, items)) {
            teleports.add(equipableTeleport(new WorldPoint(1249, 3724, 0), "Teleport", "Teleport", FARMING_CAPE));
        }

        IItem hunterCape = inventory.getFirst(ItemID.SKILLCAPE_HUNTING, ItemID.SKILLCAPE_HUNTING_TRIMMED);

        if (hunterCape == null) {
            hunterCape = equipment.getFirst(ItemID.SKILLCAPE_HUNTING, ItemID.SKILLCAPE_HUNTING_TRIMMED);
        }

        if (hunterCape != null && (hunterCape.hasSubOption("Hunter Guild") || hunterCape.hasAction("Hunter Guild"))) {
            teleports.add(equipableTeleport(new WorldPoint(1558,3046,0), "Hunter Guild", "Hunter Guild", ItemID.SKILLCAPE_HUNTING, ItemID.SKILLCAPE_HUNTING_TRIMMED));
        }

        if (isCarrying(ItemID.SKILLCAPE_AD, ItemID.SKILLCAPE_AD_TRIMMED) || containsItem(new int[]{ItemID.SKILLCAPE_AD, ItemID.SKILLCAPE_AD_TRIMMED}, items)) {
            teleports.add(equipableTeleport(new WorldPoint(2573,3324,0), "Ardougne", "Ardougne", ItemID.SKILLCAPE_AD, ItemID.SKILLCAPE_AD_TRIMMED));
            teleports.add(equipableTeleport(new WorldPoint(3300,3121,0), "Desert", "Desert", ItemID.SKILLCAPE_AD, ItemID.SKILLCAPE_AD_TRIMMED));
            teleports.add(equipableTeleport(new WorldPoint(2978,3346,0), "Falador", "Falador", ItemID.SKILLCAPE_AD, ItemID.SKILLCAPE_AD_TRIMMED));
            teleports.add(equipableTeleport(new WorldPoint(2660,3627,0), "Fremennik", "Fremennik", ItemID.SKILLCAPE_AD, ItemID.SKILLCAPE_AD_TRIMMED));
            teleports.add(equipableTeleport(new WorldPoint(2743,3445,0), "Kandarin", "Kandarin", ItemID.SKILLCAPE_AD, ItemID.SKILLCAPE_AD_TRIMMED));
            teleports.add(equipableTeleport(new WorldPoint(2809,3191,0), "Karamja", "Karamja", ItemID.SKILLCAPE_AD, ItemID.SKILLCAPE_AD_TRIMMED));
            teleports.add(equipableTeleport(new WorldPoint(2862,2996,1), "Karamja (Shilo)", "Karamja (Shilo)", ItemID.SKILLCAPE_AD, ItemID.SKILLCAPE_AD_TRIMMED));
            teleports.add(equipableTeleport(new WorldPoint(2795,2945,0), "Karamja (Jungle)", "Karamja (Jungle)", ItemID.SKILLCAPE_AD, ItemID.SKILLCAPE_AD_TRIMMED));
            teleports.add(equipableTeleport(new WorldPoint(2454,5134,0), "Karamja (Mor Ul Rek)", "Karamja (Mor Ul Rek)", ItemID.SKILLCAPE_AD, ItemID.SKILLCAPE_AD_TRIMMED));
            teleports.add(equipableTeleport(new WorldPoint(1647,3667,0), "Kourend & Kebos", "Kourend & Kebos", ItemID.SKILLCAPE_AD, ItemID.SKILLCAPE_AD_TRIMMED));
            teleports.add(equipableTeleport(new WorldPoint(3238,3220,0), "Lumbridge & Draynor", "Lumbridge & Draynor", ItemID.SKILLCAPE_AD, ItemID.SKILLCAPE_AD_TRIMMED));
            teleports.add(equipableTeleport(new WorldPoint(3466,3478,0), "Morytania", "Morytania", ItemID.SKILLCAPE_AD, ItemID.SKILLCAPE_AD_TRIMMED));
            teleports.add(equipableTeleport(new WorldPoint(3223,3413,0), "Varrock", "Varrock", ItemID.SKILLCAPE_AD, ItemID.SKILLCAPE_AD_TRIMMED));
            teleports.add(equipableTeleport(new WorldPoint(3121,3516,0), "Wilderness", "Wilderness", ItemID.SKILLCAPE_AD, ItemID.SKILLCAPE_AD_TRIMMED));
            teleports.add(equipableTeleport(new WorldPoint(2467,3458,0), "Western Provinces", "Western Provinces", ItemID.SKILLCAPE_AD, ItemID.SKILLCAPE_AD_TRIMMED));
            teleports.add(equipableTeleport(new WorldPoint(3097,3227,0), "Twiggy O'Korn", "Twiggy O'Korn", ItemID.SKILLCAPE_AD, ItemID.SKILLCAPE_AD_TRIMMED));

        }

        return teleports;
    }

    @Override
    public List<Teleport> kharedstMemoirs(List<Integer> items) {
        List<Teleport> teleports = new ArrayList<>();
        if (isCarrying(ItemID.VEOS_KHAREDSTS_MEMOIRS, ItemID.BOOK_OF_THE_DEAD) || containsItem(new int[]{ItemID.VEOS_KHAREDSTS_MEMOIRS, ItemID.BOOK_OF_THE_DEAD}, items)) {
            if (vars.getBit(VarbitID.VEOS_MEMOIR_CHARGES) > 0) {
                if (Requirement.KHAREDST_PAGE_1.get()) {
                    teleports.add(equipableTeleport(new WorldPoint(1714, 3611, 0), "Reminisce", "Lunch by the Lancalliums", ItemID.VEOS_KHAREDSTS_MEMOIRS, ItemID.BOOK_OF_THE_DEAD));
                }

                if (Requirement.KHAREDST_PAGE_2.get()) {
                    teleports.add(equipableTeleport(new WorldPoint(1802, 3749, 0), "Reminisce", "The Fisher's Flute", ItemID.VEOS_KHAREDSTS_MEMOIRS, ItemID.BOOK_OF_THE_DEAD));
                }

                if (Requirement.KHAREDST_PAGE_3.get()) {
                    teleports.add(equipableTeleport(new WorldPoint(1479, 3576, 0), "Reminisce", "History and Hearsay", ItemID.VEOS_KHAREDSTS_MEMOIRS, ItemID.BOOK_OF_THE_DEAD));
                }

                if (Requirement.KHAREDST_PAGE_4.get()) {
                    teleports.add(equipableTeleport(new WorldPoint(1545, 3761, 0), "Reminisce", "Jewellery of Jubilation", ItemID.VEOS_KHAREDSTS_MEMOIRS, ItemID.BOOK_OF_THE_DEAD));
                }

                if (Requirement.KHAREDST_PAGE_5.get()) {
                    teleports.add(equipableTeleport(new WorldPoint(1681, 3745, 0), "Reminisce", "A Dark Disposition", ItemID.VEOS_KHAREDSTS_MEMOIRS, ItemID.BOOK_OF_THE_DEAD));
                }
            }
        }

        return teleports;
    }

    @Override
    public List<Teleport> diaryItems(List<Integer> items) {
        List<Teleport> teleports = new ArrayList<>();
        if (isCarrying(GHOMMALS_HILT) || containsItem(GHOMMALS_HILT, items)) {
            teleports.add(Teleport.builder()
                    .destination(new WorldPoint(2898, 3709, 0))
                    .itemRequirements(GHOMMALS_HILT)
                    .handler(() -> jewelryTeleport("Trollheim", GHOMMALS_HILT))
                    .build());
        }

        if (vars.getBit(VarbitID.CA_TELEPORT_COUNT_TROLLHEIM) < 5 && (isCarrying(ItemID.CA_OFFHAND_MEDIUM) || containsItem(new int[]{ItemID.CA_OFFHAND_MEDIUM}, items))) {
            teleports.add(Teleport.builder()
                    .destination(new WorldPoint(2898, 3709, 0))
                    .itemRequirements(new int[]{ItemID.CA_OFFHAND_MEDIUM})
                    .handler(() -> jewelryTeleport("Trollheim", ItemID.CA_OFFHAND_MEDIUM))
                    .build());
        }

        if (isCarrying(ItemID.FREMENNIK_BOOTS_ELITE) || containsItem(new int[]{ItemID.FREMENNIK_BOOTS_ELITE}, items)) {
            teleports.add(equipableTeleport(new WorldPoint(2644, 3677, 0), "Teleport", "Teleport", ItemID.FREMENNIK_BOOTS_ELITE));
        }

        if (isCarrying(ItemID.ATJUN_GLOVES_HARD) || containsItem(new int[]{ItemID.ATJUN_GLOVES_HARD}, items)) {
            teleports.add(equipableTeleport(new WorldPoint(2840, 9388, 0), "Gem Mine", "Gem Mine", ItemID.ATJUN_GLOVES_HARD));
        }

        if (isCarrying(ItemID.ATJUN_GLOVES_ELITE) || containsItem(new int[]{ItemID.ATJUN_GLOVES_ELITE}, items)) {
            teleports.add(equipableTeleport(new WorldPoint(2869, 2982, 1), "Slayer Master", "Slayer Master", ItemID.ATJUN_GLOVES_ELITE));
            teleports.add(equipableTeleport(new WorldPoint(2840, 9388, 0), "Gem Mine", "Gem Mine", ItemID.ATJUN_GLOVES_ELITE));
        }

        if (isCarrying(ItemID.ARDY_CAPE_ELITE, ItemID.SKILLCAPE_MAX_ARDY) || containsItem(new int[]{ItemID.ARDY_CAPE_ELITE, ItemID.SKILLCAPE_MAX_ARDY}, items)) {
            teleports.add(equipableTeleport(new WorldPoint(2662, 3375, 0), "Farm Teleport", "Ardougne Farm", ItemID.ARDY_CAPE_ELITE));
        }

        if (isCarrying(RADAS_BLESSING) || containsItem(RADAS_BLESSING, items)) {
            teleports.add(equipableTeleport(new WorldPoint(1551, 3458, 0), "Kourend Woodland", "Kourend Woodland", RADAS_BLESSING));
            //Varbit is for Rada's blessing usages, that are limited to 3 per day for radas blessing 3
            //Not sure if using gloves 4 increases it, its possible just checking the varbit would be enough
            if (vars.getBit(VarbitID.ZEAH_BLESSING_BRIMSTONE_TELEPORT) < 3 || (isCarrying(ItemID.ZEAH_BLESSING_ELITE) || containsItem(new int[]{ItemID.ZEAH_BLESSING_ELITE}, items))) {
                teleports.add(equipableTeleport(new WorldPoint(1311, 3798, 0), "Mount Karuulm", "Mount Karuulm", RADAS_BLESSING));
            }
        }

        return teleports;
    }

    @Override
    public List<Teleport> ringOfTheElements(List<Integer> items) {
        List<Teleport> teleports = new ArrayList<>();
        if (isCarrying(RING_OF_THE_ELEMENTS) || containsItem(RING_OF_THE_ELEMENTS, items)) {
            teleports.add(Teleport.builder()
                    .destination(new WorldPoint(2981, 3276, 0))
                    .itemRequirements(RING_OF_THE_ELEMENTS)
                    .handler(() -> ringOfTheElementsTeleport("Air Altar"))
                    .priority(1)
                    .build());
            teleports.add(Teleport.builder()
                    .destination(new WorldPoint(3164, 3156, 0))
                    .itemRequirements(RING_OF_THE_ELEMENTS)
                    .handler(() -> ringOfTheElementsTeleport("Water Altar"))
                    .priority(1)
                    .build());
            teleports.add(Teleport.builder()
                    .destination(new WorldPoint(3288, 3467, 0))
                    .itemRequirements(RING_OF_THE_ELEMENTS)
                    .handler(() -> ringOfTheElementsTeleport("Earth Altar"))
                    .priority(1)
                    .build());
            teleports.add(Teleport.builder()
                    .destination(new WorldPoint(3316, 3278, 0))
                    .itemRequirements(RING_OF_THE_ELEMENTS)
                    .handler(() -> ringOfTheElementsTeleport("Fire Altar"))
                    .priority(1)
                    .build());
        }

        return teleports;
    }

    @Override
    public List<Teleport> ringOfShadows(List<Integer> items) {
        List<Teleport> teleports = new ArrayList<>();
        if (isCarrying(ItemID.RING_OF_SHADOWS) || containsItem(new int[]{ItemID.RING_OF_SHADOWS}, items)) {
            //Duke
            if (vars.getBit(VarbitID.DT2_GHORROCK_TELEPORT) == 1) {
                teleports.add(Teleport.builder()
                        .destination(new WorldPoint(2910, 10336, 0))
                        .itemRequirements(new int[]{ItemID.RING_OF_SHADOWS})
                        .handler(() -> ringOfShadowsTeleport("Ghorrock Dungeon", ItemID.RING_OF_SHADOWS))
                        .priority(1)
                        .build());
            }
            //Leviathan
            if (vars.getBit(VarbitID.DT2_SCAR_TELEPORT) == 1) {
                teleports.add(Teleport.builder()
                        .destination(new WorldPoint(2018, 6433, 0))
                        .itemRequirements(new int[]{ItemID.RING_OF_SHADOWS})
                        .handler(() -> ringOfShadowsTeleport("The Scar", ItemID.RING_OF_SHADOWS))
                        .priority(1)
                        .build());
            }
            //Whisperer
            if (vars.getBit(VarbitID.DT2_LASSAR_TELEPORT) == 1) {
                teleports.add(Teleport.builder()
                        .destination(new WorldPoint(2588, 6435, 0))
                        .itemRequirements(new int[]{ItemID.RING_OF_SHADOWS})
                        .handler(() -> ringOfShadowsTeleport("Lassar Undercity", ItemID.RING_OF_SHADOWS))
                        .priority(1)
                        .build());
            }

            //Vardorvis
            if (vars.getBit(VarbitID.DT2_STRANGLEWOOD_TELEPORT) == 1) {
                teleports.add(Teleport.builder()
                        .destination(new WorldPoint(1174, 3420, 0))
                        .itemRequirements(new int[]{ItemID.RING_OF_SHADOWS})
                        .handler(() -> ringOfShadowsTeleport("The Stranglewood", ItemID.RING_OF_SHADOWS))
                        .priority(1)
                        .build());
            }
        }

        return teleports;

    }

    @Override
    public List<Teleport> questItems(List<Integer> items) {
        List<Teleport> teleports = new ArrayList<>();
        if (isCarrying(AMULET_OF_THE_EYE) || containsItem(AMULET_OF_THE_EYE, items)) {
            teleports.add(equipableTeleport(new WorldPoint(3615, 9461, 0), "Teleport", "Teleport", AMULET_OF_THE_EYE));
        }

        if (isCarrying(DRAKANS_MEDALLION) || containsItem(DRAKANS_MEDALLION, items)) {
            teleports.add(equipableTeleport(new WorldPoint(3649, 3230, 0), "Ver Sinhaza", "Ver Sinhaza", DRAKANS_MEDALLION));

            if (quests.isFinished(Quest.SINS_OF_THE_FATHER)) {
                teleports.add(equipableTeleport(new WorldPoint(3592, 3337, 0), "Darkmeyer", "Darkmeyer", DRAKANS_MEDALLION));
            }
        }

        return teleports;
    }

    @Override
    public List<Teleport> combatBracelet(List<Integer> items) {
        List<Teleport> teleports = new ArrayList<>();
        if (isCarrying(COMBAT_BRACELET) || containsItem(COMBAT_BRACELET, items)) {
            teleports.add(Teleport.builder()
                    .destination(new WorldPoint(2882, 3548, 0))
                    .itemRequirements(COMBAT_BRACELET)
                    .handler(() -> jewelryTeleport("Warriors' Guild", COMBAT_BRACELET))
                    .priority(1)
                    .maximumWildernessLevel(30)
                    .build());
            teleports.add(Teleport.builder()
                    .destination(new WorldPoint(3191, 3367, 0))
                    .itemRequirements(COMBAT_BRACELET)
                    .handler(() -> jewelryTeleport("Champions' Guild", COMBAT_BRACELET))
                    .priority(1)
                    .maximumWildernessLevel(30)
                    .build());
            teleports.add(Teleport.builder()
                    .destination(new WorldPoint(3052, 3488, 0))
                    .itemRequirements(COMBAT_BRACELET)
                    .handler(() -> jewelryTeleport("Monastery", COMBAT_BRACELET))
                    .priority(1)
                    .maximumWildernessLevel(30)
                    .build());
            teleports.add(Teleport.builder()
                    .destination(new WorldPoint(2655, 3441, 0))
                    .itemRequirements(COMBAT_BRACELET)
                    .handler(() -> jewelryTeleport("Ranging Guild", COMBAT_BRACELET))
                    .priority(1)
                    .maximumWildernessLevel(30)
                    .build());
        }

        return teleports;
    }

    @Override
    public List<Teleport> skillsNecklace(List<Integer> items) {
        List<Teleport> teleports = new ArrayList<>();
        if (isCarrying(SKILLS_NECKLACE) || containsItem(SKILLS_NECKLACE, items)) {
            teleports.add(Teleport.builder()
                    .destination(new WorldPoint(2611, 3390, 0))
                    .itemRequirements(SKILLS_NECKLACE)
                    .handler(() -> jewelryPopupTeleport("Fishing Guild", SKILLS_NECKLACE))
                    .priority(1)
                    .maximumWildernessLevel(30)
                    .build());
            teleports.add(Teleport.builder()
                    .destination(new WorldPoint(3050, 9763, 0))
                    .itemRequirements(SKILLS_NECKLACE)
                    .handler(() -> jewelryPopupTeleport("Mining Guild", SKILLS_NECKLACE))
                    .priority(1)
                    .maximumWildernessLevel(30)
                    .build());
            teleports.add(Teleport.builder()
                    .destination(new WorldPoint(2933, 3295, 0))
                    .itemRequirements(SKILLS_NECKLACE)
                    .handler(() -> jewelryPopupTeleport("Crafting Guild", SKILLS_NECKLACE))
                    .priority(1)
                    .maximumWildernessLevel(30)
                    .build());
            teleports.add(Teleport.builder()
                    .destination(new WorldPoint(3143, 3440, 0))
                    .itemRequirements(SKILLS_NECKLACE)
                    .handler(() -> jewelryPopupTeleport("Cooking Guild", SKILLS_NECKLACE))
                    .priority(1)
                    .maximumWildernessLevel(30)
                    .build());
            if (vars.getBit(VarbitID.ZEAH_PLAYERHASVISITED) >= 1) {
                teleports.add(Teleport.builder()
                        .destination(new WorldPoint(1662, 3505, 0))
                        .itemRequirements(SKILLS_NECKLACE)
                        .handler(() -> jewelryPopupTeleport("Woodcutting Guild", SKILLS_NECKLACE))
                        .priority(1)
                        .maximumWildernessLevel(30)
                        .build());
                teleports.add(Teleport.builder()
                        .destination(new WorldPoint(1249, 3718, 0))
                        .itemRequirements(SKILLS_NECKLACE)
                        .handler(() -> jewelryPopupTeleport("Farming Guild", SKILLS_NECKLACE))
                        .priority(1)
                        .maximumWildernessLevel(30)
                        .build());
            }
        }

        return teleports;
    }

    @Override
    public List<Teleport> ringOfWealth(List<Integer> items) {
        List<Teleport> teleports = new ArrayList<>();
        if (isCarrying(RING_OF_WEALTH) || containsItem(RING_OF_WEALTH, items)) {
            teleports.add(Teleport.builder()
                    .destination(new WorldPoint(3163, 3478, 0))
                    .itemRequirements(RING_OF_WEALTH)
                    .handler(() -> jewelryTeleport("Grand Exchange", RING_OF_WEALTH))
                    .priority(1)
                    .maximumWildernessLevel(30)
                    .build());
            teleports.add(Teleport.builder()
                    .destination(new WorldPoint(2996, 3375, 0))
                    .itemRequirements(RING_OF_WEALTH)
                    .handler(() -> jewelryTeleport("Falador", RING_OF_WEALTH))
                    .priority(1)
                    .maximumWildernessLevel(30)
                    .build());

            if (quests.isFinished(Quest.THRONE_OF_MISCELLANIA)) {
                teleports.add(Teleport.builder()
                        .destination(new WorldPoint(2538, 3863, 0))
                        .itemRequirements(RING_OF_WEALTH)
                        .handler(() -> jewelryTeleport("Miscellania", RING_OF_WEALTH))
                        .priority(1)
                        .maximumWildernessLevel(30)
                        .build());
            }
            if (quests.isFinished(Quest.BETWEEN_A_ROCK)) {
                teleports.add(Teleport.builder()
                        .destination(new WorldPoint(2828, 10166, 0))
                        .itemRequirements(RING_OF_WEALTH)
                        .handler(() -> jewelryTeleport("Dondakan", RING_OF_WEALTH))
                        .priority(1)
                        .maximumWildernessLevel(30)
                        .build());
            }
        }

        return teleports;
    }

    @Override
    public List<Teleport> amuletOfGlory(List<Integer> items) {
        List<Teleport> teleports = new ArrayList<>();
        if (isCarrying(AMULET_OF_GLORY) || containsItem(AMULET_OF_GLORY, items)) {
            teleports.add(Teleport.builder()
                    .destination(new WorldPoint(3087, 3496, 0))
                    .itemRequirements(AMULET_OF_GLORY)
                    .handler(() -> jewelryTeleport("Edgeville", AMULET_OF_GLORY))
                    .priority(1)
                    .maximumWildernessLevel(30)
                    .build());
            teleports.add(Teleport.builder()
                    .destination(new WorldPoint(2918, 3176, 0))
                    .itemRequirements(AMULET_OF_GLORY)
                    .handler(() -> jewelryTeleport("Karamja", AMULET_OF_GLORY))
                    .priority(1)
                    .maximumWildernessLevel(30)
                    .build());
            teleports.add(Teleport.builder()
                    .destination(new WorldPoint(3105, 3251, 0))
                    .itemRequirements(AMULET_OF_GLORY)
                    .handler(() -> jewelryTeleport("Draynor Village", AMULET_OF_GLORY))
                    .priority(1)
                    .maximumWildernessLevel(30)
                    .build());
            teleports.add(Teleport.builder()
                    .destination(new WorldPoint(3293, 3163, 0))
                    .itemRequirements(AMULET_OF_GLORY)
                    .handler(() -> jewelryTeleport("Al Kharid", AMULET_OF_GLORY))
                    .priority(1)
                    .maximumWildernessLevel(30)
                    .build());
        }

        return teleports;
    }

    @Override
    public List<Teleport> burningAmulet(List<Integer> items) {
        List<Teleport> teleports = new ArrayList<>();
        if (isCarrying(BURNING_AMULET) || containsItem(BURNING_AMULET, items)) {
            teleports.add(Teleport.builder()
                    .destination(new WorldPoint(3235, 3636, 0))
                    .itemRequirements(BURNING_AMULET)
                    .handler(() -> jewelryTeleport("Chaos Temple", true, BURNING_AMULET))
                    .priority(5)
                    .build());
            teleports.add(Teleport.builder()
                    .destination(new WorldPoint(3038, 3651, 0))
                    .itemRequirements(BURNING_AMULET)
                    .handler(() -> jewelryTeleport("Bandit Camp", true, BURNING_AMULET))
                    .priority(5)
                    .build());
            teleports.add(Teleport.builder()
                    .destination(new WorldPoint(3028, 3842, 0))
                    .itemRequirements(BURNING_AMULET)
                    .handler(() -> jewelryTeleport("Lava Maze", true, BURNING_AMULET))
                    .priority(5)
                    .build());
        }

        return teleports;
    }

    @Override
    public List<Teleport> slayerRing(List<Integer> items) {
        List<Teleport> teleports = new ArrayList<>();
        if (isCarrying(SLAYER_RING) || containsItem(SLAYER_RING, items)) {
            teleports.add(Teleport.builder()
                    .destination(new WorldPoint(2432, 3423, 0))
                    .itemRequirements(SLAYER_RING)
                    .handler(() -> slayerRingTeleport("Stronghold Slayer Cave", "Stronghold", SLAYER_RING))
                    .priority(1)
                    .maximumWildernessLevel(30)
                    .build());
            teleports.add(Teleport.builder()
                    .destination(new WorldPoint(3422, 3537, 0))
                    .itemRequirements(SLAYER_RING)
                    .handler(() -> slayerRingTeleport("Morytania Slayer Tower", "Slayer Tower", SLAYER_RING))
                    .priority(1)
                    .maximumWildernessLevel(30)
                    .build());
            teleports.add(Teleport.builder()
                    .destination(new WorldPoint(2802, 10000, 0))
                    .itemRequirements(SLAYER_RING)
                    .handler(() -> slayerRingTeleport("Fremennik Slayer Dungeon", "Fremennik Dungeon", SLAYER_RING))
                    .priority(1)
                    .maximumWildernessLevel(30)
                    .build());
            teleports.add(Teleport.builder()
                    .destination(new WorldPoint(3185, 4601, 0))
                    .itemRequirements(SLAYER_RING)
                    .handler(() -> slayerRingTeleport("Tarn's Lair", "Tarn's Lair", SLAYER_RING))
                    .priority(1)
                    .maximumWildernessLevel(30)
                    .build());
            if (quests.isFinished(Quest.MOURNINGS_END_PART_II)) {
                teleports.add(Teleport.builder()
                        .destination(new WorldPoint(2028, 4636, 0))
                        .itemRequirements(SLAYER_RING)
                        .handler(() -> slayerRingTeleport("Dark Beasts", "Dark Beasts", SLAYER_RING))
                        .priority(1)
                        .maximumWildernessLevel(30)
                        .build());
            }
        }

        return teleports;
    }

    public List<Teleport> giantsoulAmulet(List<Integer> items) {
        List<Teleport> teleports = new ArrayList<>();
        if (isCarrying(ItemID.GIANTSOUL_AMULET_CHARGED) || containsItem(new int[]{ItemID.GIANTSOUL_AMULET_CHARGED}, items)) {
            teleports.add(equipableTeleport(new WorldPoint(3174, 9898, 0), "Bryophyta", "Bryophyta", ItemID.GIANTSOUL_AMULET_CHARGED));
            teleports.add(equipableTeleport(new WorldPoint(3096, 9833, 0), "Obor", "Obor", ItemID.GIANTSOUL_AMULET_CHARGED));
            teleports.add(equipableTeleport(new WorldPoint(2952, 9574, 0), "Branda and Eldric", "Branda and Eldric", ItemID.GIANTSOUL_AMULET_CHARGED));
        }

        return teleports;

    }

    public List<Teleport> pendentOfAtes(List<Integer> items) {
        List<Teleport> teleports = new ArrayList<>();
        if (isCarrying(ItemID.PENDANT_OF_ATES) || containsItem(new int[]{ItemID.PENDANT_OF_ATES}, items)) {
            if (vars.getBit(VarbitID.PENDANT_OF_ATES_DARKFROST_FOUND) >= 1) {
                teleports.add(equipableTeleport(new WorldPoint(1489, 3284, 0), "Darkfrost", "Darkfrost", ItemID.PENDANT_OF_ATES));
            }
            if (vars.getBit(VarbitID.PENDANT_OF_ATES_TWILIGHT_FOUND) >= 1) {
                teleports.add(equipableTeleport(new WorldPoint(1666, 3224, 0), "Twilight Temple", "Twilight Temple", ItemID.PENDANT_OF_ATES));
            }
            if (vars.getBit(VarbitID.PENDANT_OF_ATES_RALOS_FOUND) >= 1) {
                teleports.add(equipableTeleport(new WorldPoint(1459, 3138, 0), "Ralos' Rise", "Ralos' Rise", ItemID.PENDANT_OF_ATES));
            }
            if (vars.getBit(VarbitID.PENDANT_OF_ATES_TLATI_FOUND) >= 1) {
                teleports.add(equipableTeleport(new WorldPoint(1368, 3086, 0), "Kastori", "Kastori", ItemID.PENDANT_OF_ATES));
            }
            if (vars.getBit(16753) >= 1) {
                teleports.add(equipableTeleport(new WorldPoint(1470, 3012, 0), "Ate's Altar", "Ate's Altar", ItemID.PENDANT_OF_ATES));
            }
            if (vars.getBit(VarbitID.PENDANT_OF_ATES_AUBURN_FOUND) >= 1) {
                teleports.add(equipableTeleport(new WorldPoint(1365, 3276, 0), "Nemus Retreat", "Nemus Retreat", ItemID.PENDANT_OF_ATES));
            }
        }

        return teleports;

    }

    public List<Teleport> maxCapeTeleports(List<Integer> items) {
        List<Teleport> teleports = new ArrayList<>();

        if (isCarrying(MAX_CAPE) || containsItem(MAX_CAPE, items)) {
            teleports.add(Teleport.builder()
                    .destination(new WorldPoint(2882, 3548, 0))
                    .handler(() -> jewelryPopupTeleport("Warrior's Guild", "Guild Teleports", MAX_CAPE))
                    .priority(1)
                    .itemRequirements(MAX_CAPE)
                    .build());

            teleports.add(Teleport.builder()
                    .destination(new WorldPoint(2611, 3390, 0))
                    .handler(() -> jewelryPopupTeleport("Fishing Guild", "Guild Teleports", MAX_CAPE))
                    .priority(1)
                    .itemRequirements(MAX_CAPE)
                    .build());

            teleports.add(Teleport.builder()
                    .destination(new WorldPoint(1249, 3725, 0))
                    .handler(() -> jewelryPopupTeleport("Farming Guild", "Guild Teleports", MAX_CAPE))
                    .priority(1)
                    .itemRequirements(MAX_CAPE)
                    .build());

            final var craftingTeleport = equipableTeleport(new WorldPoint(2931, 3286, 0), "Crafting Guild", "Crafting Guild", MAX_CAPE);
            craftingTeleport.setForceLoad(true);
            teleports.add(craftingTeleport);
        }

        return teleports;
    }

    @Override
    public List<Teleport> pohMountedTeleports() {
        List<Teleport> teleports = new ArrayList<>();
        if (solaceConfig.hasMountedGlory()) {
            teleports.add(mountedPohTeleport(new WorldPoint(3087, 3496, 0), ObjectID.POH_TROPHY_AMULETOFGLORY_4, "Edgeville"));
            teleports.add(mountedPohTeleport(new WorldPoint(2918, 3176, 0), ObjectID.POH_TROPHY_AMULETOFGLORY_4, "Karamja"));
            teleports.add(mountedPohTeleport(new WorldPoint(3105, 3251, 0), ObjectID.POH_TROPHY_AMULETOFGLORY_4, "Draynor Village"));
            teleports.add(mountedPohTeleport(new WorldPoint(3293, 3163, 0), ObjectID.POH_TROPHY_AMULETOFGLORY_4, "Al Kharid"));
        }

        if (solaceConfig.hasMountedDigsitePendant()) {
            teleports.add(mountedAdventureTeleport(new WorldPoint(3341, 3445, 0), "Digsite Pendant", 1));
            teleports.add(mountedAdventureTeleport(new WorldPoint(3766, 3870, 1), "Digsite Pendant", 2));
            if (vars.getBit(VarbitID.LITHKREN_RUBY_NECKLACE_REDIRECT) == 1) {
                teleports.add(mountedAdventureTeleport(new WorldPoint(3549, 10456, 0), "Digsite Pendant", 3));
            }
        }

        if (solaceConfig.hasMountedXericsTalisman()) {
            teleports.add(mountedAdventureTeleport(new WorldPoint(1576, 3530, 0), "Xeric's Talisman", 1));
            teleports.add(mountedAdventureTeleport(new WorldPoint(1752, 3566, 0), "Xeric's Talisman", 2));
            teleports.add(mountedAdventureTeleport(new WorldPoint(1504, 3817, 0), "Xeric's Talisman", 3));
            teleports.add(mountedAdventureTeleport(new WorldPoint(1640, 3674, 0), "Xeric's Talisman", 4));
        }

        if (solaceConfig.hasMountedMythicalCape()) {
            teleports.add(mountedPohTeleport(new WorldPoint(2457, 2849, 0), ObjectID.POH_TROPHY_MYTHICAL_CAPE, "Teleport"));
        }

        return teleports;
    }

    @Override
    public List<Teleport> pohJewelryBox() {
        List<Teleport> teleports = new ArrayList<>();
        switch (solaceConfig.hasJewelryBox()) {
            case ORNATE:
                if (quests.isFinished(Quest.THRONE_OF_MISCELLANIA)) {
                    teleports.add(pohWidgetTeleport(new WorldPoint(2538, 3863, 0), 'k', "Miscellania"));
                }
                teleports.add(pohWidgetTeleport(new WorldPoint(3163, 3478, 0), 'l', "Grand Exchange"));
                teleports.add(pohWidgetTeleport(new WorldPoint(2996, 3375, 0), 'm', "Falador Park"));
                if (quests.isFinished(Quest.BETWEEN_A_ROCK)) {
                    teleports.add(pohWidgetTeleport(new WorldPoint(2828, 10166, 0), 'n', "Dondakan's Rock"));
                }
                teleports.add(pohWidgetTeleport(new WorldPoint(3087, 3496, 0), 'o', "Edgeville"));
                teleports.add(pohWidgetTeleport(new WorldPoint(2918, 3176, 0), 'p', "Karamja"));
                teleports.add(pohWidgetTeleport(new WorldPoint(3105, 3251, 0), 'q', "Draynor Village"));
                teleports.add(pohWidgetTeleport(new WorldPoint(3293, 3163, 0), 'r', "Al Kharid"));
            case FANCY:
                teleports.add(pohWidgetTeleport(new WorldPoint(2882, 3548, 0), 'a', "Warrriors' Guild"));
                teleports.add(pohWidgetTeleport(new WorldPoint(3191, 3367, 0), 'b', "Champions' Guild"));
                teleports.add(pohWidgetTeleport(new WorldPoint(3052, 3488, 0), 'c', "Monastery"));
                teleports.add(pohWidgetTeleport(new WorldPoint(2655, 3441, 0), 'd', "Ranging Guild"));
                teleports.add(pohWidgetTeleport(new WorldPoint(2611, 3390, 0), 'e', "Fishing Guild"));
                teleports.add(pohWidgetTeleport(new WorldPoint(3050, 9763, 0), 'f', "Mining Guild"));
                teleports.add(pohWidgetTeleport(new WorldPoint(2933, 3295, 0), 'g', "Crafting Guild"));
                teleports.add(pohWidgetTeleport(new WorldPoint(3143, 3440, 0), 'h', "Cooking Guild"));
                teleports.add(pohWidgetTeleport(new WorldPoint(1662, 3505, 0), 'i', "Woodcutting Guild"));
                teleports.add(pohWidgetTeleport(new WorldPoint(1249, 3718, 0), 'j', "Farming Guild"));
            case BASIC:
                teleports.add(pohWidgetTeleport(new WorldPoint(3315, 3235, 0), '1', "PvP Arena"));
                teleports.add(pohWidgetTeleport(new WorldPoint(2440, 3090, 0), '2', "Castle Wars"));
                teleports.add(pohWidgetTeleport(new WorldPoint(3151, 3635, 0), '3', "Ferox Enclave"));
                if (vars.getVarp(VarPlayerID.COLOSSEUM_GLORY) >= 12000) {
                    teleports.add(pohWidgetTeleport(new WorldPoint(1793, 3109, 0), '4', "Fortis Colosseum"));
                }
                teleports.add(pohWidgetTeleport(new WorldPoint(2898, 3553, 0), '5', "Burthorpe"));
                teleports.add(pohWidgetTeleport(new WorldPoint(2520, 3571, 0), '6', "Barbarian Outpost"));
                teleports.add(pohWidgetTeleport(new WorldPoint(2964, 4382, 2), '7', "Corporeal Beast"));
                if (quests.isFinished(Quest.TEARS_OF_GUTHIX)) {
                    teleports.add(pohWidgetTeleport(new WorldPoint(3244, 9501, 2), '8', "Tears of Guthix"));
                }
                if (vars.getBit(VarbitID.ZEAH_PLAYERHASVISITED) == 1) {
                    teleports.add(pohWidgetTeleport(new WorldPoint(1624, 3938, 0), '9', "Wintertodt Camp"));
                }
                break;
            default:
        }

        log.debug("Loaded {} Jewelry Box teleports", teleports.size());

        return teleports;
    }

    @Override
    public List<Teleport> pohPortals() {
        //nexus portal
        var nexusTeleports = getNexusTeleports();
        List<Teleport> teleports = new ArrayList<>(nexusTeleports.values());

        log.debug("Loaded {} Nexus teleports", nexusTeleports.size());

        List<Teleport> portalTeleports = new ArrayList<>();
        //normal house portals don't add portal if nexus has it
        for (var housePortal : solaceConfig.housePortals()) {
            if (nexusTeleports.containsKey(housePortal)) {
                continue;
            }

            portalTeleports.add(pohPortalTeleport(housePortal));
        }

        log.debug("Loaded {}/{} Portal teleports", portalTeleports.size(), solaceConfig.housePortals().size());
        teleports.addAll(portalTeleports);

        return teleports;
    }

    @Override
    public List<Teleport> pohSpiritFairyTree(List<Integer> items) {
        List<Teleport> teleports = new ArrayList<>();
        SpiritFairyTree spiritFairyTree = solaceConfig.spiritFairyTree();
        boolean canUseFairyRings = canUseFairyRings(items);

        switch (spiritFairyTree) {
            case NONE:
                break;
            case FAIRY_AND_SPIRIT:
            case SPIRIT_FAIRY_TREE:
                if (!isCarrying(SPIRIT_TREE_BLACKLISTED)) {
                    teleports.addAll(getSpiritTreeTeleports());
                }
                if (canUseFairyRings) {
                    teleports.addAll(getFairyRingTeleports());
                }
                break;
            case FAIRY_RING:
                if (canUseFairyRings) {
                    teleports.addAll(getFairyRingTeleports());
                }
                break;
            case SPIRIT_TREE:
                if (!isCarrying(SPIRIT_TREE_BLACKLISTED)) {
                    teleports.addAll(getSpiritTreeTeleports());
                }
                break;
        }

        return teleports;
    }

    @Override
    public Teleport equipableTeleport(WorldPoint destination, String inventoryAction, String equippedAction, int... itemIds) {
        return Teleport.builder()
                .destination(destination)
                .itemRequirements(itemIds)
                .handler(() ->
                {
                    if (dialog.isViewingOptions() && dialog.chooseOption(inventoryAction, equippedAction)) {
                        return true;
                    }

                    var equipped = equipment.getFirst(itemIds);
                    if (equipped != null) {
                        equipped.interact(equippedAction);
                        return true;
                    }

                    var inv = inventory.getFirst(itemIds);
                    if (inv != null) {
                        inv.interact(inventoryAction);
                        return true;
                    }

                    return false;
                })
                .build();
    }

    @Override
    public Teleport pohPortalTeleport(HousePortal housePortal) {
        return Teleport.builder()
                .destination(housePortal.getDestination())
                .handler(() ->
                {
                    if (players.getLocal().isMoving() || client.getGameState() == GameState.LOADING) {
                        return true;
                    }

                    var portal = tileObjects.getNearest(housePortal.getModifiedName());
                    if (portal != null) {
                        portal.interact(housePortal.getAction());
                        return true;
                    }

                    if (house.isInside()) {
                        log.error("Couldn't find teleport portal: {} in POH.", housePortal);
                        return false;
                    }

                    enterHouse();
                    return true;
                })
                .objectNameRequirements(new String[] {housePortal.getModifiedName()})
                .poh(true)
                .build();
    }

    @Override
    public Teleport pohNexusTeleport(PortalNexus nexus) {
        var housePortal = nexus.getPortal();
        var destination = housePortal.getDestination();
        return Teleport.builder()
                .destination(destination)
                .itemRequirements(HOUSE_ITEMS)
                .handler(() ->
                {
                    if (players.getLocal().isMoving() || client.getGameState() == GameState.LOADING) {
                        return true;
                    }

                    var nexusPortal = tileObjects.getNearest("Portal Nexus");
                    if (nexusPortal == null) {
                        if (house.isInside()) {
                            log.warn("Could not find nexus portal inside POH.");
                            return false;
                        }

                        log.debug("Entering house to use nexus portal.");
                        enterHouse();
                        return true;
                    }

                    var teleportInterface = widgets.get(17, 12);
                    if (!widgets.isVisible(teleportInterface)) {
                        log.debug("Opening nexus teleport menu.");
                        nexusPortal.interact("Teleport Menu");
                        return true;
                    }

                    var teleportChildren = teleportInterface.getDynamicChildren();

                    var secondaryTeleports = widgets.get(17, 16);

                    if (widgets.isVisible(secondaryTeleports)) {
                        var secondaryTeleportChildren = secondaryTeleports.getDynamicChildren();
                        if (secondaryTeleportChildren != null && secondaryTeleportChildren.length > 0) {
                            teleportChildren = ArrayUtils.addAll(teleportChildren, secondaryTeleportChildren);
                        }
                    }

                    if (teleportChildren == null || teleportChildren.length == 0) {
                        log.warn("There are no teleports in the nexus.");
                        return false;
                    }

                    var optionalTeleportWidget = Arrays.stream(teleportChildren).
                            filter(Objects::nonNull).
                            filter(widget -> widget.getText() != null).
                            filter(widget -> widget.getText().contains(housePortal.getNexusTarget())).
                            findFirst();

                    if (optionalTeleportWidget.isEmpty()) {
                        log.warn("Could not find nexus teleport widget for {}.", housePortal.getNexusTarget());
                        return false;
                    }

                    var teleportWidget = optionalTeleportWidget.get();
                    var teleportChar = teleportWidget.getText().substring(12, 13);
                    log.debug("Pressing {} for teleport to {}.", teleportChar, housePortal.getNexusTarget());
                    keyboard.type(teleportChar);
                    return true;
                })
                .objectNameRequirements(new String[] {"Portal Nexus"})
                .poh(true)
                .build();
    }

    @Override
    public Teleport constructionCapeTeleport(WorldPoint destination, String action) {
        return Teleport.builder()
                .destination(destination)
                .itemRequirements(CONSTRUCTION_CAPE)
                .handler(() ->
                {
                    IItem cape = inventory.getFirst(ItemID.SKILLCAPE_CONSTRUCTION, ItemID.SKILLCAPE_CONSTRUCTION_TRIMMED, ItemID.SKILLCAPE_MAX, ItemID.SKILLCAPE_MAX_WORN);

                    if (cape == null) {
                        cape = equipment.getFirst(ItemID.SKILLCAPE_CONSTRUCTION, ItemID.SKILLCAPE_CONSTRUCTION_TRIMMED, ItemID.SKILLCAPE_MAX, ItemID.SKILLCAPE_MAX_WORN);
                    }

                    if (cape != null) {
                        if (cape.hasAction(action) || cape.hasSubOption(action)) {
                            cape.interact(action);
                            return true;
                        }

                        var baseWidget = widgets.get(InterfaceID.Menu.LJ_LAYER1);
                        if (baseWidget == null) {
                            baseWidget = widgets.get(947, 9);
                        }

                        if (widgets.isVisible(baseWidget)) {
                            var children = baseWidget.getChildren();
                            if (children == null) {
                                return true;
                            }

                            for (var i = 0; i < children.length; i++) {
                                var teleportItem = children[i];
                                if (teleportItem.getText().contains(action)) {
                                    keyboard.type((i + 1));
                                    return true;
                                }
                            }
                        }

                        cape.interact("Teleport", "POH Portals");
                        return true;
                    }
                    return false;
                })
                .build();
    }

    @Override
    public Teleport mountedAdventureTeleport(
            WorldPoint destination,
            String objectName,
            int action
    ) {
        return Teleport.builder()
                .destination(destination)
                .itemRequirements(HOUSE_ITEMS)
                .handler(() ->
                {
                    if (players.getLocal().isMoving() || client.getGameState() == GameState.LOADING) {
                        return true;
                    }

                    var baseWidget = widgets.get(InterfaceID.Menu.LJ_LAYER1);
                    if (baseWidget == null) {
                        baseWidget = widgets.get(947, 9);
                    }

                    if (widgets.isVisible(baseWidget)) {
                        keyboard.type(action);
                        return true;
                    }

                    var adventureTeleport = tileObjects.getNearest(objectName);
                    if (adventureTeleport != null) {
                        adventureTeleport.interact("Teleport menu");
                        return true;
                    }

                    if (house.isInside()) {
                        log.warn("Could not find object {} inside POH.", objectName);
                        return false;
                    }

                    enterHouse();
                    return true;
                })
                .objectNameRequirements(new String[] {objectName})
                .poh(true)
                .build();
    }

    @Override
    public Teleport itemTeleport(TeleportItem teleportItem) {
        return Teleport.builder()
                .destination(teleportItem.getDestination())
                .itemRequirements(teleportItem.getItemIds())
                .handler(() ->
                {
                    var optionTitle = dialog.getOptionTitle();
                    if (dialog.isOpen() && optionTitle != null) {
                        var text = optionTitle.getText();
                        if (text != null && text.contains("wilderness?") && dialog.hasOption("Yes")) {
                            dialog.chooseOption("Yes");
                            return true;
                        }
                    }

                    IItem item = inventory.getFirst(teleportItem.getItemIds());
                    if (item != null) {
                        item.interact(teleportItem.getAction());
                        return true;
                    }

                    if (teleportItem.getEquippedAction() != null) {
                        item = equipment.getFirst(teleportItem.getItemIds());
                        if (item != null) {
                            item.interact(teleportItem.getEquippedAction());
                            return true;
                        }
                    }

                    return false;
                })
                .build();
    }

    @Override
    public Teleport pohWidgetTeleport(
            WorldPoint destination,
            char action,
            String destinationName
    ) {
        return Teleport.builder()
                .destination(destination)
                .itemRequirements(HOUSE_ITEMS)
                .handler(() ->
                {
                    if (players.getLocal().isMoving() || client.getGameState() == GameState.LOADING) {
                        return true;
                    }

                    if (widgets.isVisible(590, 0)) {
                        keyboard.type(action);
                        return true;
                    }

                    var box = tileObjects.getNearest(to -> to.getName() != null && to.getName().contains("Jewellery Box"));
                    if (box != null) {
                        if (box.hasAction(destinationName)) {
                            box.interact(destinationName);
                            return true;
                        }
                        box.interact("Teleport Menu");
                        return true;
                    }

                    if (house.isInside()) {
                        log.warn("Could not find jewellery box inside POH.");
                        return false;
                    }

                    enterHouse();
                    return true;
                })
                .objectNameRequirements(new String[] {"Jewellery Box"})
                .poh(true)
                .build();
    }

    @Override
    public Teleport mountedPohTeleport(
            WorldPoint destination,
            int objId,
            String action
    ) {
        return Teleport.builder()
                .destination(destination)
                .itemRequirements(HOUSE_ITEMS)
                .handler(() ->
                {
                    if (players.getLocal().isMoving() || client.getGameState() == GameState.LOADING) {
                        return true;
                    }

                    var first = tileObjects.getNearest(objId);
                    if (first != null) {
                        first.interact(action);
                        return true;
                    }

                    if (house.isInside()) {
                        log.warn("Could not find mounted teleport inside POH.");
                        return false;
                    }

                    enterHouse();
                    return true;
                })
                .objectIdRequirements(new int[] {objId})
                .poh(true)
                .build();
    }

    @Override
    public Collection<Teleport> getSpiritTreeTeleports(Boolean inPoh) {
        return solaceConfig.spiritTrees().stream()
                .filter(Objects::nonNull)
                .map(spiritTree -> Teleport.builder()
                        .destination(spiritTree.getPosition())
                        .itemRequirements(HOUSE_ITEMS)
                        .handler(() -> pohSpiritTreeTeleport(spiritTree))
                        .objectNameRequirements(new String[] {"Spirit Tree", "Spiritual Fairy Tree"})
                        .poh(inPoh)
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public Collection<Teleport> getFairyRingTeleports(Boolean inPoh) {
        return solaceConfig.fairyRings().stream()
                .filter(Objects::nonNull)
                .map(fairyRing -> Teleport.builder()
                        .destination(fairyRing.getLocation())
                        .itemRequirements(HOUSE_ITEMS)
                        .handler(() -> pohFairyRingTeleport(fairyRing))
                        .objectNameRequirements(new String[] {"Fairy ring", "Spiritual Fairy Tree"})
                        .poh(inPoh)
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public Map<HousePortal, Teleport> getNexusTeleports() {
        return PortalNexus.getAvailable().stream()
                .collect(Collectors.toMap(PortalNexus::getPortal, this::pohNexusTeleport, (a, b) -> b));
    }

    @Override
    public void enterHouse() {
        house.enter();
    }

    private boolean jewelryPopupTeleport(String target, String rubAction, int... ids) {
        IItem inv = inventory.getFirst(ids);

        if (inv == null) {
            inv = equipment.getFirst(ids);
        }

        if (inv.hasAction(target) || inv.hasSubOption(target)) {
            inv.interact(target);
            return true;
        }

        var baseWidget = widgets.get(InterfaceID.Menu.LJ_LAYER1);
        if (baseWidget == null) {
            baseWidget = widgets.get(947, 9);
        }

        if (widgets.isVisible(baseWidget)) {
            var children = baseWidget.getChildren();
            if (children == null) {
                return true;
            }

            for (var i = 0; i < children.length; i++) {
                var teleportItem = children[i];
                if (teleportItem.getText().contains(target)) {
                    keyboard.type((i + 1));
                    return true;
                }
            }
        }

        inv.interact(rubAction);
        return true;

    }

    private boolean jewelryPopupTeleport(String target, int... ids) {
        return jewelryPopupTeleport(target, "Rub", ids);
    }

    private boolean slayerRingTeleport(String target, String equippedTarget, int... ids) {
        IItem ring = inventory.getFirst(ids);

        if (ring == null) {
            ring = equipment.getFirst(ids);
        }

        if (ring != null) {
            if (dialog.isViewingOptions()) {
                if (dialog.hasOption("Teleport")) {
                    dialog.chooseOption("Teleport");
                } else {
                    if (!dialog.chooseOption(target)) {
                        log.warn("Can't find dialog option to click, retrying teleport action");
                        ring.interact("Teleport");
                    }
                }
            } else {
                if (ring.hasSubOption(equippedTarget)) {
                    ring.interact(equippedTarget);
                    return true;
                }

                ring.interact("Teleport");
            }

            return true;
        }

        return false;
    }

    private boolean pohFairyRingTeleport(FairyRing destination) {
        if (!house.isInside()) {
            enterHouse();
            return true;
        }

        var ring = tileObjects.getNearest("Fairy ring", "Spiritual Fairy Tree");
        if (ring == null) {
            log.warn("Could not find fairy ring inside POH.");
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
            ring.interact("Zanaris", "Ring-Zanaris");
            return true;
        }

        if (ring.hasAction(a -> a != null && a.contains(destination.getCode()))) {
            ring.interact(a -> a != null && a.contains(destination.getCode()));
            return true;
        }

        if (widgets.isVisible(InterfaceID.FAIRYRINGS, 0)) {
            destination.travel();
            return true;
        }

        ring.interact("Configure", "Ring-configure");
        return true;
    }

    private boolean pohSpiritTreeTeleport(SpiritTree spiritTree) {
        if (!house.isInside()) {
            enterHouse();
            return true;
        }

        var treeWidget = widgets.get(InterfaceID.Menu.LJ_LAYER1);
        if (treeWidget == null) {
            treeWidget = widgets.get(947, 9);
        }

        if (widgets.isVisible(treeWidget)) {
            Arrays.stream(treeWidget.getDynamicChildren())
                    .filter(child -> child.getText().toLowerCase().contains(spiritTree.getLocation().toLowerCase()))
                    .findFirst()
                    .ifPresent(child -> child.interact(MenuAction.WIDGET_CONTINUE));
            return true;
        }

        var tree = tileObjects.getNearest("Spirit Tree", "Spiritual Fairy Tree");
        if (tree == null) {
            log.warn("Could not find spirit tree inside POH.");
            return false;
        }

        tree.interact(MenuAction.GAME_OBJECT_FIRST_OPTION);
        return true;
    }

    private boolean jewelryTeleport(String target, boolean wildernessTeleport, int... ids) {
        var item = equipment.getFirst(ids);

        if (item == null) {
            item = inventory.getFirst(ids);
        }

        if (item != null) {
            if (dialog.chooseOption(target)) {
                return true;
            }

            if (wildernessTeleport && dialog.isViewingOptions() && dialog.getOptions().stream()
                    .anyMatch(it -> it.getText() != null && WILDY_PATTERN.matcher(it.getText()).matches())) {
                if (dialog.chooseOption(1)) {
                    return true;
                }
            }

            if (item.hasSubOption(target)) {
                item.interact(target);
                return true;
            }

            item.interact(target, "Rub");
            return true;
        }

        return false;
    }

    private boolean jewelryTeleport(String target, int... ids) {
        return jewelryTeleport(target, false, ids);
    }

    private boolean ringOfTheElementsTeleport(String target) {
        var equipped = equipment.getFirst(RING_OF_THE_ELEMENTS);
        var lastRingOfTheElementsTeleport = lastRingOfTheElementsTeleport();

        // When equipped it has all the different options as one-click and also Last Destination for the last used teleport
        if (equipped != null) {
            if (target.equals(lastRingOfTheElementsTeleport)) {
                equipped.interact("Last Destination");
            } else {
                equipped.interact(target);
            }

            return true;
        }

        var inv = inventory.getFirst(RING_OF_THE_ELEMENTS);

        // When in inventory it has a Rub option and Last Destination for the last used teleport
        if (inv != null) {
            if (target.equals(lastRingOfTheElementsTeleport)) {
                inv.interact("Last Destination");
            } else {
                if (dialog.chooseOption(target)) {
                    return true;
                } else {
                    if (inv.hasSubOption(target)) {
                        inv.interact(target);
                        return true;
                    }

                    inv.interact("Rub");
                }
            }

            return true;
        }

        return false;
    }

    private String lastRingOfTheElementsTeleport() {
        var lastRingOfTheElementsTeleport = vars.getBit(VarbitID.RING_OF_ELEMENTS_LAST_DESTINATION);
        switch (lastRingOfTheElementsTeleport) {
            case 1:
                return "Air Altar";
            case 2:
                return "Water Altar";
            case 3:
                return "Earth Altar";
            case 4:
                return "Fire Altar";
            default:
                return null;
        }
    }

    private boolean ringOfShadowsTeleport(String target, int... ids) {
        var item = equipment.getFirst(ids);

        if (item != null) {
            item.interact(target);
            return true;
        }

        item = inventory.getFirst(ids);

        if (item != null) {
            if (dialog.chooseOption(target)) {
                return true;
            }
            if (item.hasSubOption(target)) {
                item.interact(target);
                return true;
            }

            item.interact("Teleport");
            return true;
        }

        return false;
    }

    private boolean fairyMushroomFairyRing(FairyRing fairyRing) {
        if (widgets.isVisible(InterfaceID.FAIRYRINGS, 0)) {
            fairyRing.travel();
            return true;
        }

        var item = equipment.getFirst(ItemID.LEAGUE_TRAILBLAZER_FAIRYS_FLIGHT_TELEPORT);

        if (item != null) {
            item.interact("Fairy-Ring");
            return true;
        }

        if (dialog.isOpen() && dialog.hasOption("Fairy rings.")) {
            dialog.chooseOption("Fairy rings.");
            return true;
        }

        item = inventory.getFirst(ItemID.LEAGUE_TRAILBLAZER_FAIRYS_FLIGHT_TELEPORT);

        if (item != null) {
            item.interact("Teleport");
            return true;
        }

        return false;
    }

    private boolean fairyMushroomSpiritTree(SpiritTree spiritTree) {
        var treeWidget = widgets.get(InterfaceID.Menu.LJ_LAYER1);
        if (treeWidget == null) {
            treeWidget = widgets.get(947, 9);
        }

        if (widgets.isVisible(treeWidget)) {
            Arrays.stream(treeWidget.getDynamicChildren())
                    .filter(child -> child.getText().toLowerCase().contains(spiritTree.getLocation().toLowerCase()))
                    .findFirst()
                    .ifPresent(child -> child.interact(MenuAction.WIDGET_CONTINUE));
            return true;
        }

        var item = equipment.getFirst(ItemID.LEAGUE_TRAILBLAZER_FAIRYS_FLIGHT_TELEPORT);

        if (item != null) {
            item.interact("Spirit-tree");
            return true;
        }

        if (dialog.isOpen() && dialog.hasOption("Spirit trees.")) {
            dialog.chooseOption("Spirit trees.");
            return true;
        }

        item = inventory.getFirst(ItemID.LEAGUE_TRAILBLAZER_FAIRYS_FLIGHT_TELEPORT);

        if (item != null) {
            item.interact("Teleport");
            return true;
        }

        return false;
    }

    private boolean fairyRingLeprechaun(String target) {
        var widget = widgets.get(InterfaceID.MENU, 3);

        if (widgets.isVisible(widget)) {
            Arrays.stream(widget.getDynamicChildren())
                    .filter(child -> child.getText().toLowerCase().contains(target.toLowerCase()))
                    .findFirst()
                    .ifPresent(child -> child.interact(MenuAction.WIDGET_CONTINUE));
            return true;
        }

        var item = equipment.getFirst(ItemID.LEAGUE_TRAILBLAZER_FAIRYS_FLIGHT_TELEPORT);

        if (item != null) {
            item.interact("Tool-leprechauns");
            return true;
        }

        if (dialog.isOpen() && dialog.hasOption("Tool Leprechauns.")) {
            dialog.chooseOption("Tool Leprechauns.");
            return true;
        }

        item = inventory.getFirst(ItemID.LEAGUE_TRAILBLAZER_FAIRYS_FLIGHT_TELEPORT);

        if (item != null) {
            item.interact("Teleport");
            return true;
        }

        return false;
    }

    private boolean bankersBriefcase(String target) {
        var widget = widgets.get(InterfaceID.MENU, 3);

        if (widgets.isVisible(widget)) {
            Arrays.stream(widget.getDynamicChildren())
                    .filter(child -> child.getText().toLowerCase().contains(target.toLowerCase()))
                    .findFirst()
                    .ifPresent(child -> child.interact(MenuAction.WIDGET_CONTINUE));
            return true;
        }

        var item = equipment.getFirst(ItemID.LEAGUE_BANK_HEIST_TELEPORT);

        if (item != null) {
            item.interact("Teleport");
            return true;
        }

        item = inventory.getFirst(ItemID.LEAGUE_BANK_HEIST_TELEPORT);

        if (item != null) {
            item.interact("Teleport");
            return true;
        }

        return false;
    }


    private Teleport fairyMushroomLeprechaunTeleport(String target, WorldPoint destination) {
        return Teleport.builder()
                .destination(destination)
                .handler(() -> fairyRingLeprechaun(target))
                .priority(1)
                .build();
    }

    private Teleport bankersBriefcase(String target, WorldPoint destination) {
        return Teleport.builder()
                .destination(destination)
                .handler(() -> bankersBriefcase(target))
                .priority(1)
                .build();
    }

    public Collection<Teleport> getFairyMushroomRings() {
        List<Teleport> teleports = new ArrayList<>();

        if (isCarrying(ItemID.LEAGUE_TRAILBLAZER_FAIRYS_FLIGHT_TELEPORT)) {
            var fairyRings = solaceConfig.fairyRings().stream()
                    .filter(Objects::nonNull)
                    .map(fairyRing -> Teleport.builder()
                            .destination(fairyRing.getLocation())
                            .handler(() -> fairyMushroomFairyRing(fairyRing))
                            .build())
                    .collect(Collectors.toList());

            teleports.addAll(fairyRings);
        }

        return teleports;
    }

    public Collection<Teleport> getFairyMushroomSpiritTrees() {
        List<Teleport> teleports = new ArrayList<>();

        if (isCarrying(ItemID.LEAGUE_TRAILBLAZER_FAIRYS_FLIGHT_TELEPORT)) {
            var spiritTrees = solaceConfig.spiritTrees().stream()
                    .filter(Objects::nonNull)
                    .map(spiritTree -> Teleport.builder()
                            .destination(spiritTree.getPosition())
                            .handler(() -> fairyMushroomSpiritTree(spiritTree))
                            .build())
                    .collect(Collectors.toList());;

            teleports.addAll(spiritTrees);
        }

        return teleports;
    }

    public Collection<Teleport> getFairyMushroomLeprechauns() {
        List<Teleport> teleports = new ArrayList<>();

        if (isCarrying(ItemID.LEAGUE_TRAILBLAZER_FAIRYS_FLIGHT_TELEPORT)) {
            teleports.add(fairyMushroomLeprechaunTeleport("Al Kharid", new WorldPoint(3320, 3304, 0)));
            teleports.add(fairyMushroomLeprechaunTeleport("Aldarin", new WorldPoint(1364, 2941, 0)));
            teleports.add(fairyMushroomLeprechaunTeleport("Ardougne Monastery", new WorldPoint(2613, 3224, 0)));
            teleports.add(fairyMushroomLeprechaunTeleport("Auburnvale", new WorldPoint(1447, 3354, 0)));
            teleports.add(fairyMushroomLeprechaunTeleport("Brimhaven", new WorldPoint(2768, 3214, 0)));
            teleports.add(fairyMushroomLeprechaunTeleport("Canifis", new WorldPoint(3451, 3476, 0)));
            teleports.add(fairyMushroomLeprechaunTeleport("Catherby", new WorldPoint(2816, 3466, 0)));
            teleports.add(fairyMushroomLeprechaunTeleport("Champions' Guild", new WorldPoint(3181, 3355, 0)));
            teleports.add(fairyMushroomLeprechaunTeleport("Draynor Manor", new WorldPoint(3086, 3358, 0)));
//            teleports.add(fairyMushroomLeprechaunTeleport("Entrana", new WorldPoint(2662, 3305, 0)));
            teleports.add(fairyMushroomLeprechaunTeleport("Etceteria", new WorldPoint(2593, 3860, 0)));
            teleports.add(fairyMushroomLeprechaunTeleport("Falador Farm", new WorldPoint(3053, 3306, 0)));
            teleports.add(fairyMushroomLeprechaunTeleport("Falador Park", new WorldPoint(3007, 3371, 0)));
//            teleports.add(fairyMushroomLeprechaunTeleport("Farming Guild", new WorldPoint(2662, 3305, 0)));
            teleports.add(fairyMushroomLeprechaunTeleport("Fossil Island", new WorldPoint(3712, 3838, 0)));
            teleports.add(fairyMushroomLeprechaunTeleport("Harmony Island", new WorldPoint(3798, 2836, 0)));
            teleports.add(fairyMushroomLeprechaunTeleport("Hosidius Allotment", new WorldPoint(1741, 3551, 0)));
            teleports.add(fairyMushroomLeprechaunTeleport("Hosidius Saltpetre", new WorldPoint(1697, 3542, 0)));
            teleports.add(fairyMushroomLeprechaunTeleport("Hosidius Vinery", new WorldPoint(1804, 3555, 0)));
            teleports.add(fairyMushroomLeprechaunTeleport("Kastori North", new WorldPoint(1352, 3058, 0)));
            teleports.add(fairyMushroomLeprechaunTeleport("Kastori South", new WorldPoint(1354, 3028, 0)));
            teleports.add(fairyMushroomLeprechaunTeleport("Lletya", new WorldPoint(2344, 3162, 0)));
            teleports.add(fairyMushroomLeprechaunTeleport("Locus Oasis", new WorldPoint(1682, 2971, 0)));
            teleports.add(fairyMushroomLeprechaunTeleport("Lumbridge", new WorldPoint(3190, 3232, 0)));
            teleports.add(fairyMushroomLeprechaunTeleport("Nemus Retreat", new WorldPoint(1367, 3317, 0)));
            teleports.add(fairyMushroomLeprechaunTeleport("North of Ardougne", new WorldPoint(2673, 3380, 0)));
            teleports.add(fairyMushroomLeprechaunTeleport("North of McGrubor's Wood", new WorldPoint(2671, 3521, 0)));
            teleports.add(fairyMushroomLeprechaunTeleport("North of Seth Groats' Farm", new WorldPoint(3232, 3316, 0)));
            teleports.add(fairyMushroomLeprechaunTeleport("Ortus Farm", new WorldPoint(1594, 3097, 0)));
            teleports.add(fairyMushroomLeprechaunTeleport("Port Phasmatys", new WorldPoint(3599, 3524, 0)));
            teleports.add(fairyMushroomLeprechaunTeleport("Port Sarim", new WorldPoint(3060, 3261, 0)));
//            teleports.add(fairyMushroomLeprechaunTeleport("Prifddinas", new WorldPoint(2662, 3305, 0)));
            teleports.add(fairyMushroomLeprechaunTeleport("Tai Bwo Wannai", new WorldPoint(2799, 3104, 0)));
            teleports.add(fairyMushroomLeprechaunTeleport("Taverley", new WorldPoint(2933, 3441, 0)));
            teleports.add(fairyMushroomLeprechaunTeleport("Tree Gnome Stronghold East", new WorldPoint(2473, 3449, 0)));
            teleports.add(fairyMushroomLeprechaunTeleport("Tree Gnome Stronghold West", new WorldPoint(2439, 3421, 0)));
            teleports.add(fairyMushroomLeprechaunTeleport("Tree Gnome Village", new WorldPoint(2487, 3182, 0)));
//            teleports.add(fairyMushroomLeprechaunTeleport("Troll Stronghold", new WorldPoint(2662, 3305, 0)));
//            teleports.add(fairyMushroomLeprechaunTeleport("Underwater", new WorldPoint(2662, 3305, 0)));
            teleports.add(fairyMushroomLeprechaunTeleport("Varrock", new WorldPoint(3230, 3456, 0)));
//            teleports.add(fairyMushroomLeprechaunTeleport("Weiss", new WorldPoint(2662, 3305, 0)));
            teleports.add(fairyMushroomLeprechaunTeleport("White Wolf Mountain", new WorldPoint(2856, 3433, 0)));
            teleports.add(fairyMushroomLeprechaunTeleport("Yanille", new WorldPoint(2571, 3105, 0)));
        }

        return teleports;
    }

    public Collection<Teleport> getBankersBriefcaseTeleports(){
        List<Teleport> teleports = new ArrayList<>();

        if (isCarrying(ItemID.LEAGUE_BANK_HEIST_TELEPORT)) {
            // Asgarnia
//            teleports.add(bankersBriefcase("Ancient Prison", new WorldPoint(2904, 5205, 0)));
            teleports.add(bankersBriefcase("Camdozaal", new WorldPoint(2978, 5800, 0)));
            teleports.add(bankersBriefcase("Crafting Guild", new WorldPoint(2935, 3282, 0)));
            teleports.add(bankersBriefcase("Falador (East)", new WorldPoint(3013, 3357, 0)));
            teleports.add(bankersBriefcase("Falador (West)", new WorldPoint(2947, 3370, 0)));
            teleports.add(bankersBriefcase("Mining Guild", new WorldPoint(3013, 9720, 0)));
            teleports.add(bankersBriefcase("Motherlode Mine", new WorldPoint(3757, 5668, 0)));
            teleports.add(bankersBriefcase("Port Sarim", new WorldPoint(3045, 3236, 0)));
            teleports.add(bankersBriefcase("Rogues' Den", new WorldPoint(3041, 4970, 1)));
            teleports.add(bankersBriefcase("Void Knights' Outpost", new WorldPoint(2667, 2654, 0)));
            teleports.add(bankersBriefcase("Warriors' Guild", new WorldPoint(2843, 3544, 0)));

// Desert
            teleports.add(bankersBriefcase("Al Kharid", new WorldPoint(3269, 3169, 0)));
            teleports.add(bankersBriefcase("Emir's Arena", new WorldPoint(3383, 3270, 0)));
            teleports.add(bankersBriefcase("Mage Training Arena", new WorldPoint(3365, 3320, 1)));
            teleports.add(bankersBriefcase("Nardah", new WorldPoint(3427, 2893, 0)));
            teleports.add(bankersBriefcase("Shantay Pass", new WorldPoint(3306, 3122, 0)));
            if (quests.isFinished(Quest.CONTACT)) {
                teleports.add(bankersBriefcase("Sophanem", new WorldPoint(2799, 5169, 0)));
            }
            if (quests.isFinished(Quest.BENEATH_CURSED_SANDS)) {
                teleports.add(bankersBriefcase("Tombs of Amascut", new WorldPoint(3355, 9120, 0)));
            }
            teleports.add(bankersBriefcase("Unkah", new WorldPoint(3157, 2836, 0)));

// Fremennik
            if (quests.isFinished(Quest.THE_GIANT_DWARF)) {
                teleports.add(bankersBriefcase("Blast Furnace", new WorldPoint(1948, 4959, 0)));
                teleports.add(bankersBriefcase("Keldagrim", new WorldPoint(2838, 10209, 0)));
            }
            if (quests.isFinished(Quest.THRONE_OF_MISCELLANIA)) {
                teleports.add(bankersBriefcase("Etceteria", new WorldPoint(2617, 3895, 0)));
            }
            if (quests.isFinished(Quest.THE_FREMENNIK_ISLES)) {
                teleports.add(bankersBriefcase("Jatizso", new WorldPoint(2417, 3802, 0)));
                teleports.add(bankersBriefcase("Neitiznot", new WorldPoint(2336, 3805, 0)));
            }
            teleports.add(bankersBriefcase("Lunar Isle", new WorldPoint(2098, 3918, 0)));
            teleports.add(bankersBriefcase("Peer the Seer", new WorldPoint(2635, 3670, 0)));

// Kandarin
            teleports.add(bankersBriefcase("Ape Atoll", new WorldPoint(2779, 2784, 0)));
            teleports.add(bankersBriefcase("Ardougne (North)", new WorldPoint(2615, 3333, 0)));
            teleports.add(bankersBriefcase("Ardougne (South)", new WorldPoint(2654, 3285, 0)));
            teleports.add(bankersBriefcase("Barbarian Outpost", new WorldPoint(2536, 3575, 0)));
            teleports.add(bankersBriefcase("Castle Wars", new WorldPoint(2443, 3085, 0)));
            teleports.add(bankersBriefcase("Catherby", new WorldPoint(2809, 3440, 0)));
            teleports.add(bankersBriefcase("Corsair Cove", new WorldPoint(2569, 2865, 0)));
            teleports.add(bankersBriefcase("Fishing Guild", new WorldPoint(2586, 3422, 0)));
            teleports.add(bankersBriefcase("Gnome Stronghold (North)", new WorldPoint(2449, 3484, 1)));
            teleports.add(bankersBriefcase("Gnome Stronghold (South)", new WorldPoint(2446, 3426, 1)));
            if (quests.isFinished(Quest.LEGENDS_QUEST)) {
                teleports.add(bankersBriefcase("Legends' Guild", new WorldPoint(2732, 3381, 2)));
            }
            if (quests.isFinished(Quest.DRAGON_SLAYER_II)) {
                teleports.add(bankersBriefcase("Myths' Guild", new WorldPoint(2463, 2846, 1)));
            }
            teleports.add(bankersBriefcase("Ourania", new WorldPoint(3014, 5626, 0)));
            teleports.add(bankersBriefcase("Piscatoris", new WorldPoint(2330, 3691, 0)));
            teleports.add(bankersBriefcase("Port Khazard", new WorldPoint(2661, 3159, 0)));
            teleports.add(bankersBriefcase("Seers' Village", new WorldPoint(2724, 3491, 0)));
            teleports.add(bankersBriefcase("Yanille", new WorldPoint(2613, 3094, 0)));

// Karamja
            teleports.add(bankersBriefcase("Mor Ul Rek", new WorldPoint(2541, 5143, 0)));
            teleports.add(bankersBriefcase("Rionasta", new WorldPoint(2783, 3096, 0)));
            teleports.add(bankersBriefcase("Shilo Village", new WorldPoint(2852, 2956, 0)));
            teleports.add(bankersBriefcase("Tzhaar City", new WorldPoint(2445, 5182, 0)));

// Kourend
            teleports.add(bankersBriefcase("Arceuus", new WorldPoint(1627, 3748, 0)));
            teleports.add(bankersBriefcase("Blast mine", new WorldPoint(1499, 3859, 0)));
            teleports.add(bankersBriefcase("Chambers of Xeric", new WorldPoint(1254, 3570, 0)));
            teleports.add(bankersBriefcase("Charcoal camp", new WorldPoint(1719, 3467, 0)));
            teleports.add(bankersBriefcase("Ent dungeon", new WorldPoint(1551, 9875, 0)));
            teleports.add(bankersBriefcase("Farming Guild", new WorldPoint(1253, 3741, 0)));
            teleports.add(bankersBriefcase("Hosidius", new WorldPoint(1750, 3599, 0)));
            teleports.add(bankersBriefcase("Hosidius Kitchen", new WorldPoint(1676, 3617, 0)));
            teleports.add(bankersBriefcase("Kourend Castle", new WorldPoint(1612, 3683, 2)));
            teleports.add(bankersBriefcase("Land's End", new WorldPoint(1512, 3423, 0)));
            teleports.add(bankersBriefcase("Lovakengj", new WorldPoint(1525, 3740, 0)));
            teleports.add(bankersBriefcase("Lovakengj mine", new WorldPoint(1438, 3830, 0)));
            teleports.add(bankersBriefcase("Mount Karuulm", new WorldPoint(1324, 3823, 0)));
            teleports.add(bankersBriefcase("Port Piscarilius", new WorldPoint(1804, 3789, 0)));
            teleports.add(bankersBriefcase("Saltpetre mine", new WorldPoint(1702, 3530, 0)));
            teleports.add(bankersBriefcase("Shayzien", new WorldPoint(1490, 3592, 0)));
            teleports.add(bankersBriefcase("Shayzien War Tent", new WorldPoint(1486, 3648, 0)));
            teleports.add(bankersBriefcase("Sulphur mine", new WorldPoint(1453, 3859, 0)));
            teleports.add(bankersBriefcase("Vinery", new WorldPoint(1809, 3565, 0)));
            teleports.add(bankersBriefcase("Wintertodt", new WorldPoint(1638, 3944, 0)));
            teleports.add(bankersBriefcase("Woodcutting Guild", new WorldPoint(1592, 3478, 0)));

// Misthalin
            teleports.add(bankersBriefcase("Chest-wreck", new WorldPoint(3769, 3898, 0)));
            teleports.add(bankersBriefcase("Cook's Guild", new WorldPoint(3147, 3451, 0)));
            teleports.add(bankersBriefcase("Dorgesh-Kaan", new WorldPoint(2701, 5351, 0)));
            teleports.add(bankersBriefcase("Draynor", new WorldPoint(3092, 3245, 0)));
            teleports.add(bankersBriefcase("Edgeville", new WorldPoint(3096, 3496, 0)));
            teleports.add(bankersBriefcase("Fossil Island", new WorldPoint(3741, 3807, 0)));
            teleports.add(bankersBriefcase("Grand Exchange", new WorldPoint(3165, 3484, 0)));
            if (quests.isFinished(Quest.TEMPLE_OF_THE_EYE)) {
                teleports.add(bankersBriefcase("Guardians of the Rift", new WorldPoint(3618, 9475, 0)));
            }
            teleports.add(bankersBriefcase("Isle of Souls", new WorldPoint(2212, 2860, 0)));
            teleports.add(bankersBriefcase("Lumbridge basement", new WorldPoint(3218, 9622, 0)));
            teleports.add(bankersBriefcase("Lumbridge Castle", new WorldPoint(3209, 3218, 2)));
            teleports.add(bankersBriefcase("Varrock (East)", new WorldPoint(3253, 3422, 0)));
            teleports.add(bankersBriefcase("Varrock (West)", new WorldPoint(3184, 3438, 0)));
            teleports.add(bankersBriefcase("Volcanic Mine", new WorldPoint(3818, 3810, 0)));
            teleports.add(bankersBriefcase("Zanaris", new WorldPoint(2381, 4460, 0)));

// Morytania
            teleports.add(bankersBriefcase("Burgh de Rott", new WorldPoint(3495, 3213, 0)));
            teleports.add(bankersBriefcase("Canifis", new WorldPoint(3512, 3481, 0)));
            if (quests.isFinished(Quest.SINS_OF_THE_FATHER)) {
                teleports.add(bankersBriefcase("Darkmeyer", new WorldPoint(3603, 3369, 0)));
                teleports.add(bankersBriefcase("Hallowed Sepulchre", new WorldPoint(2404, 5988, 0)));
            }
            teleports.add(bankersBriefcase("Mos Le'Harmless", new WorldPoint(3679, 2982, 0)));
            teleports.add(bankersBriefcase("Port Phasmatys", new WorldPoint(3688, 3468, 0)));
            if (quests.isFinished(Quest.HAUNTED_MINE)) {
                teleports.add(bankersBriefcase("Tarn's Lair", new WorldPoint(3195, 4569, 0)));
            }
            teleports.add(bankersBriefcase("Trouble Brewing", new WorldPoint(3810, 3023, 0)));
            teleports.add(bankersBriefcase("Ver Sinhaza", new WorldPoint(3651, 3210, 0)));

// Tirannwn
            teleports.add(bankersBriefcase("Gauntlet", new WorldPoint(3037, 6125, 0)));
            teleports.add(bankersBriefcase("Lletya", new WorldPoint(2352, 3163, 0)));
            teleports.add(bankersBriefcase("Prifddinas (North)", new WorldPoint(3258, 6107, 0)));
            teleports.add(bankersBriefcase("Prifddinas (South)", new WorldPoint(3295, 6061, 0)));
            teleports.add(bankersBriefcase("Trahaearn mine", new WorldPoint(3306, 12443, 0)));

// Varlamore
            teleports.add(bankersBriefcase("Aldarin", new WorldPoint(1398, 2928, 0)));
            teleports.add(bankersBriefcase("Cam Torum", new WorldPoint(1454, 9568, 0)));
            teleports.add(bankersBriefcase("Civitas illa Fortis (East)", new WorldPoint(1780, 3096, 0)));
            teleports.add(bankersBriefcase("Civitas illa Fortis (West)", new WorldPoint(1647, 3117, 0)));
            teleports.add(bankersBriefcase("Fortis Colosseum", new WorldPoint(1804, 9504, 0)));
            teleports.add(bankersBriefcase("Hueycoatl", new WorldPoint(1527, 3292, 0)));
            teleports.add(bankersBriefcase("Hunter Guild", new WorldPoint(1543, 3041, 0)));
            teleports.add(bankersBriefcase("Quetzacalli Gorge", new WorldPoint(1519, 3229, 0)));

// Wilderness
            teleports.add(bankersBriefcase("Ferox Enclave", new WorldPoint(3130, 3628, 0)));
            teleports.add(bankersBriefcase("Mage Arena", new WorldPoint(2534, 4714, 0)));

        }
        return teleports;
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
