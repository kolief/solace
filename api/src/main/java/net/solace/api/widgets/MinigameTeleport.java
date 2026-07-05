package net.solace.api.widgets;

import java.util.Arrays;
import java.util.Set;
import java.util.function.Supplier;
import net.runelite.api.Quest;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.solace.api.Static;
import net.solace.api.domain.actors.IPlayer;
import net.solace.api.domain.widgets.IWidget;
import net.solace.api.movement.pathfinder.UnlockRequirements;
import net.solace.api.movement.pathfinder.model.MovementConstants;

public enum MinigameTeleport {
    BARBARIAN_ASSAULT(62324761, "Barbarian Assault", new WorldPoint(2531, 3577, 0)),
    BLAST_FURNACE(62324760, "Blast Furnace", new WorldPoint(2933, 10183, 0)),
    BURTHORPE_GAMES_ROOM(62324759, "Burthorpe Games Room", new WorldPoint(2208, 4938, 0)),
    BOUNTY_HUNTER(62324758, "Bounty Hunter", new WorldPoint(3140, 3624, 0)),
    CASTLE_WARS(62324757, "Castle Wars", new WorldPoint(2439, 3092, 0)),
    CLAN_WARS(62324756, "Clan Wars", new WorldPoint(3151, 3636, 0)),
    FISHING_TRAWLER(62324755, "Fishing Trawler", new WorldPoint(2658, 3158, 0)),
    GIANTS_FOUNDRY(62324754, "Giants' Foundry", new WorldPoint(3361, 3147, 0)),
    GUARDIANS_OF_THE_RIFT(62324753, "Guardians of the Rift", new WorldPoint(3616, 9478, 0)),
    LAST_MAN_STANDING(62324752, "Last Man Standing", new WorldPoint(3149, 3635, 0)),
    MAGE_TRAINING_ARENA(62324751, "Mage Training Arena", new WorldPoint(3363, 3290, 0)),
    MASTERING_MIXOLOGY(62324750, "Mastering Mixology", new WorldPoint(1387, 2919, 0)),
    NIGHTMARE_ZONE(62324749, "Nightmare Zone", new WorldPoint(2611, 3121, 0)),
    PEST_CONTROL(62324748, "Pest Control", new WorldPoint(2653, 2655, 0)),
    RAT_PITS_ARDOUGNE(62324747, "Rat Pits", new WorldPoint(2562, 3320, 0), "Ardougne (kittens)"),
    RAT_PITS_VARROCK(62324747, "Rat Pits", new WorldPoint(3268, 3400, 0), "Varrock (grown cats)"),
    RAT_PITS_KELDAGRIM(62324747, "Rat Pits", new WorldPoint(2914, 10190, 0), "Keldagrim (overgrown cats)"),
    RAT_PITS_PORT_SARIM(62324747, "Rat Pits", new WorldPoint(3016, 3234, 0), "Port Sarim (wily cats)"),
    SHADES_OF_MORTTON(62324746, "Shades of Mort'ton", new WorldPoint(3500, 3300, 0)),
    SORCERESS_GARDEN(62324745, "Sorceress's Garden", new WorldPoint(3320, 3138, 0)),
    SOUL_WARS(62324744, "Soul Wars", new WorldPoint(2209, 2857, 0)),
    TITHE_FARM(62324743, "Tithe Farm", new WorldPoint(1793, 3501, 0)),
    TROUBLE_BREWING(62324742, "Trouble Brewing", new WorldPoint(3811, 3021, 0)),
    TZHAAR_FIGHT_PIT(62324741, "TzHaar Fight Pit", new WorldPoint(2402, 5181, 0), "No, don't ask again.", "Yes."),
    NONE(-1, "None", null);

    private static final Supplier<IWidget> MINIGAMES_DESTINATION;
    private static final Set<Quest> NMZ_QUESTS;
    private final int interfaceId;
    private final String name;
    private final WorldPoint location;
    private final String[] dialogs;

    private MinigameTeleport(int interfaceId, String name, WorldPoint location, String ... dialogs) {
        this.interfaceId = interfaceId;
        this.name = name;
        this.location = location;
        this.dialogs = dialogs;
    }

    private MinigameTeleport(int interfaceId, String name, WorldPoint location) {
        this(interfaceId, name, location, null);
    }

    public static MinigameTeleport getCurrent() {
        IWidget selectedTeleport = MINIGAMES_DESTINATION.get();
        if (Static.getWidgets().isVisible(selectedTeleport)) {
            return MinigameTeleport.byName(selectedTeleport.getText());
        }
        return NONE;
    }

    public static MinigameTeleport byName(String name) {
        return Arrays.stream(MinigameTeleport.values()).filter(x -> x.getName().equals(name)).findFirst().orElse(NONE);
    }

    public boolean canUse() {
        if (!this.hasDestination()) {
            return false;
        }
        IPlayer local = Static.getPlayers().getLocal();
        if (Arrays.stream(MovementConstants.RESTRICTED_MINIGAME_TELEPORT_AREAS).anyMatch(x -> x.contains(local.getWorldLocation()))) {
            return false;
        }
        switch (this) {
            case BURTHORPE_GAMES_ROOM: 
            case CASTLE_WARS: 
            case SOUL_WARS: 
            case TZHAAR_FIGHT_PIT: 
            case CLAN_WARS: 
            case LAST_MAN_STANDING: {
                return true;
            }
            case GIANTS_FOUNDRY: {
                return Static.getQuests().isFinished(Quest.SLEEPING_GIANTS);
            }
            case BARBARIAN_ASSAULT: {
                return Static.getVars().getBit(3251) >= 1;
            }
            case BLAST_FURNACE: {
                return Static.getVars().getBit(575) >= 1;
            }
            case FISHING_TRAWLER: {
                return Static.getSkills().getLevel(Skill.FISHING) >= 15;
            }
            case GUARDIANS_OF_THE_RIFT: {
                return Static.getQuests().isFinished(Quest.TEMPLE_OF_THE_EYE);
            }
            case NIGHTMARE_ZONE: {
                return NMZ_QUESTS.stream().filter(Static.getQuests()::isFinished).count() >= 5L;
            }
            case PEST_CONTROL: {
                return Static.getPlayers().getLocal().getCombatLevel() >= 40;
            }
            case RAT_PITS_ARDOUGNE: 
            case RAT_PITS_KELDAGRIM: 
            case RAT_PITS_PORT_SARIM: 
            case RAT_PITS_VARROCK: {
                return Static.getQuests().isFinished(Quest.RATCATCHERS);
            }
            case SHADES_OF_MORTTON: {
                return Static.getQuests().isFinished(Quest.SHADES_OF_MORTTON);
            }
            case TROUBLE_BREWING: {
                return Static.getQuests().isFinished(Quest.CABIN_FEVER) && Static.getSkills().getLevel(Skill.COOKING) >= 40;
            }
            case TITHE_FARM: {
                return Static.getSkills().getLevel(Skill.FARMING) >= 34 && Static.getChargeManager().isUnlocked(UnlockRequirements.TITHE_FARM);
            }
        }
        return false;
    }

    public boolean hasDestination() {
        return this.location != null;
    }

    public int getInterfaceId() {
        return this.interfaceId;
    }

    public String getName() {
        return this.name;
    }

    public WorldPoint getLocation() {
        return this.location;
    }

    public String[] getDialogs() {
        return this.dialogs;
    }

    static {
        MINIGAMES_DESTINATION = () -> Static.getWidgets().get(76, 11);
        NMZ_QUESTS = Set.of(Quest.THE_ASCENT_OF_ARCEUUS, Quest.CONTACT, Quest.THE_CORSAIR_CURSE, Quest.THE_DEPTHS_OF_DESPAIR, Quest.DESERT_TREASURE_I, Quest.DRAGON_SLAYER_I, Quest.DREAM_MENTOR, Quest.FAIRYTALE_I__GROWING_PAINS, Quest.FAMILY_CREST, Quest.FIGHT_ARENA, Quest.THE_FREMENNIK_ISLES, Quest.GETTING_AHEAD, Quest.THE_GRAND_TREE, Quest.THE_GREAT_BRAIN_ROBBERY, Quest.GRIM_TALES, Quest.HAUNTED_MINE, Quest.HOLY_GRAIL, Quest.HORROR_FROM_THE_DEEP, Quest.IN_SEARCH_OF_THE_MYREQUE, Quest.LEGENDS_QUEST, Quest.LOST_CITY, Quest.LUNAR_DIPLOMACY, Quest.MONKEY_MADNESS_I, Quest.MOUNTAIN_DAUGHTER, Quest.MY_ARMS_BIG_ADVENTURE, Quest.ONE_SMALL_FAVOUR, Quest.RECIPE_FOR_DISASTER, Quest.ROVING_ELVES, Quest.SHADOW_OF_THE_STORM, Quest.SHILO_VILLAGE, Quest.SONG_OF_THE_ELVES, Quest.TALE_OF_THE_RIGHTEOUS, Quest.TREE_GNOME_VILLAGE, Quest.TROLL_ROMANCE, Quest.TROLL_STRONGHOLD, Quest.VAMPYRE_SLAYER, Quest.WHAT_LIES_BELOW, Quest.WITCHS_HOUSE);
    }
}

