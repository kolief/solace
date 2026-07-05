package net.solace.api.movement.pathfinder.model;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import net.runelite.api.Quest;
import net.runelite.api.QuestState;
import net.runelite.api.coords.WorldPoint;
import net.solace.api.Static;
import net.solace.api.domain.widgets.IWidget;
import net.solace.api.movement.pathfinder.model.Transport;
import net.solace.api.movement.pathfinder.model.requirement.ItemRequirement;
import net.solace.api.movement.pathfinder.model.requirement.QuestRequirement;
import net.solace.api.movement.pathfinder.model.requirement.Requirement;
import net.solace.api.movement.pathfinder.model.requirement.Requirements;
import net.solace.api.movement.pathfinder.model.requirement.SkillRequirement;
import net.solace.api.movement.pathfinder.model.requirement.VarRequirement;

public enum GnomeGlider {
    GRAND_TREE_TO_WHITE_WOLF_MOUNTAIN(GliderLocation.GRAND_TREE, GliderLocation.WHITE_WOLF_MOUNTAIN, new Requirement[0]),
    GRAND_TREE_TO_DIGSITE(GliderLocation.GRAND_TREE, GliderLocation.DIGSITE, new Requirement[0]),
    GRAND_TREE_TO_AL_KHARID(GliderLocation.GRAND_TREE, GliderLocation.AL_KHARID, new Requirement[0]),
    GRAND_TREE_TO_FELDIP_HILLS(GliderLocation.GRAND_TREE, GliderLocation.FELDIP_HILLS, Requirement.ONE_SMALL_FAVOUR),
    GRAND_TREE_TO_APE_ATOLL(GliderLocation.GRAND_TREE, GliderLocation.APE_ATOLL, Requirement.MONKEY_MADNESS_II),
    GRAND_TREE_TO_KARAMJA(GliderLocation.GRAND_TREE, GliderLocation.KARAMJA, new Requirement[0]),
    WHITE_WOLF_MOUNTAIN_TO_GRAND_TREE(GliderLocation.WHITE_WOLF_MOUNTAIN, GliderLocation.GRAND_TREE, new Requirement[0]),
    WHITE_WOLF_MOUNTAIN_TO_DIGSITE(GliderLocation.WHITE_WOLF_MOUNTAIN, GliderLocation.DIGSITE, new Requirement[0]),
    WHITE_WOLF_MOUNTAIN_TO_AL_KHARID(GliderLocation.WHITE_WOLF_MOUNTAIN, GliderLocation.AL_KHARID, new Requirement[0]),
    WHITE_WOLF_MOUNTAIN_TO_FELDIP_HILLS(GliderLocation.WHITE_WOLF_MOUNTAIN, GliderLocation.FELDIP_HILLS, Requirement.ONE_SMALL_FAVOUR),
    WHITE_WOLF_MOUNTAIN_TO_APE_ATOLL(GliderLocation.WHITE_WOLF_MOUNTAIN, GliderLocation.APE_ATOLL, Requirement.MONKEY_MADNESS_II),
    WHITE_WOLF_MOUNTAIN_TO_KARAMJA(GliderLocation.WHITE_WOLF_MOUNTAIN, GliderLocation.KARAMJA, new Requirement[0]),
    AL_KHARID_TO_GRAND_TREE(GliderLocation.AL_KHARID, GliderLocation.GRAND_TREE, new Requirement[0]),
    AL_KHARID_TO_WHITE_WOLF_MOUNTAIN(GliderLocation.AL_KHARID, GliderLocation.WHITE_WOLF_MOUNTAIN, new Requirement[0]),
    AL_KHARID_TO_DIGSITE(GliderLocation.AL_KHARID, GliderLocation.DIGSITE, new Requirement[0]),
    AL_KHARID_TO_FELDIP_HILLS(GliderLocation.AL_KHARID, GliderLocation.FELDIP_HILLS, Requirement.ONE_SMALL_FAVOUR),
    AL_KHARID_TO_APE_ATOLL(GliderLocation.AL_KHARID, GliderLocation.APE_ATOLL, Requirement.MONKEY_MADNESS_II),
    AL_KHARID_TO_KARAMJA(GliderLocation.AL_KHARID, GliderLocation.KARAMJA, new Requirement[0]),
    FELDIP_HILLS_TO_GRAND_TREE(GliderLocation.FELDIP_HILLS, GliderLocation.GRAND_TREE, Requirement.ONE_SMALL_FAVOUR),
    FELDIP_HILLS_TO_WHITE_WOLF_MOUNTAIN(GliderLocation.FELDIP_HILLS, GliderLocation.WHITE_WOLF_MOUNTAIN, Requirement.ONE_SMALL_FAVOUR),
    FELDIP_HILLS_TO_DIGSITE(GliderLocation.FELDIP_HILLS, GliderLocation.DIGSITE, Requirement.ONE_SMALL_FAVOUR),
    FELDIP_HILLS_TO_AL_KHARID(GliderLocation.FELDIP_HILLS, GliderLocation.AL_KHARID, Requirement.ONE_SMALL_FAVOUR),
    FELDIP_HILLS_TO_APE_ATOLL(GliderLocation.FELDIP_HILLS, GliderLocation.APE_ATOLL, Requirement.MONKEY_MADNESS_II, Requirement.ONE_SMALL_FAVOUR),
    FELDIP_HILLS_TO_KARAMJA(GliderLocation.FELDIP_HILLS, GliderLocation.KARAMJA, Requirement.ONE_SMALL_FAVOUR),
    APE_ATOLL_TO_GRAND_TREE(GliderLocation.APE_ATOLL, GliderLocation.GRAND_TREE, Requirement.MONKEY_MADNESS_II),
    APE_ATOLL_TO_WHITE_WOLF_MOUNTAIN(GliderLocation.APE_ATOLL, GliderLocation.WHITE_WOLF_MOUNTAIN, Requirement.MONKEY_MADNESS_II),
    APE_ATOLL_TO_DIGSITE(GliderLocation.APE_ATOLL, GliderLocation.DIGSITE, Requirement.MONKEY_MADNESS_II),
    APE_ATOLL_TO_AL_KHARID(GliderLocation.APE_ATOLL, GliderLocation.AL_KHARID, Requirement.MONKEY_MADNESS_II),
    APE_ATOLL_TO_FELDIP_HILLS(GliderLocation.APE_ATOLL, GliderLocation.FELDIP_HILLS, Requirement.MONKEY_MADNESS_II, Requirement.ONE_SMALL_FAVOUR),
    APE_ATOLL_TO_KARAMJA(GliderLocation.APE_ATOLL, GliderLocation.KARAMJA, Requirement.MONKEY_MADNESS_II),
    KARAMJA_TO_GRAND_TREE(GliderLocation.KARAMJA, GliderLocation.GRAND_TREE, new Requirement[0]),
    KARAMJA_TO_WHITE_WOLF_MOUNTAIN(GliderLocation.KARAMJA, GliderLocation.WHITE_WOLF_MOUNTAIN, new Requirement[0]),
    KARAMJA_TO_DIGSITE(GliderLocation.KARAMJA, GliderLocation.DIGSITE, new Requirement[0]),
    KARAMJA_TO_AL_KHARID(GliderLocation.KARAMJA, GliderLocation.AL_KHARID, new Requirement[0]),
    KARAMJA_TO_FELDIP_HILLS(GliderLocation.KARAMJA, GliderLocation.FELDIP_HILLS, Requirement.ONE_SMALL_FAVOUR),
    KARAMJA_TO_APE_ATOLL(GliderLocation.KARAMJA, GliderLocation.APE_ATOLL, Requirement.MONKEY_MADNESS_II);

    private final GliderLocation origin;
    private final GliderLocation destination;
    private final Requirement[] requirements;

    private GnomeGlider(GliderLocation origin, GliderLocation destination, Requirement ... requirements) {
        this.origin = origin;
        this.destination = destination;
        this.requirements = requirements;
    }

    public static List<Transport> getTransports() {
        return Arrays.stream(GnomeGlider.values()).filter(x -> x.getRequirements().fulfilled()).map(Static.getTransportLoader()::gnomeGlider).collect(Collectors.toList());
    }

    public Requirements getRequirements() {
        Requirements req = new Requirements();
        if (this.requirements != null) {
            for (Requirement requirement : this.requirements) {
                if (requirement instanceof QuestRequirement) {
                    req.getQuestRequirements().add((QuestRequirement)requirement);
                    continue;
                }
                if (requirement instanceof ItemRequirement) {
                    req.getItemRequirements().add((ItemRequirement)requirement);
                    continue;
                }
                if (requirement instanceof SkillRequirement) {
                    req.getSkillRequirements().add((SkillRequirement)requirement);
                    continue;
                }
                if (!(requirement instanceof VarRequirement)) continue;
                req.getVarRequirements().add((VarRequirement)requirement);
            }
        }
        req.getQuestRequirements().add(new QuestRequirement(Quest.THE_GRAND_TREE, Set.of(QuestState.FINISHED)));
        return req;
    }

    public GliderLocation getOrigin() {
        return this.origin;
    }

    public GliderLocation getDestination() {
        return this.destination;
    }

    public static enum GliderLocation {
        GRAND_TREE(new WorldPoint(2465, 3501, 3), 4, "Ta Quir Priw"),
        WHITE_WOLF_MOUNTAIN(new WorldPoint(2850, 3498, 0), 7, "Sindarpos"),
        DIGSITE(new WorldPoint(3323, 3428, 0), 10, "Lemanto Andra"),
        AL_KHARID(new WorldPoint(3279, 3212, 0), 13, "Kar-Hewo"),
        FELDIP_HILLS(new WorldPoint(2543, 2970, 0), 21, "Lemantolly Undri"),
        APE_ATOLL(new WorldPoint(2716, 2801, 0), 25, "Ookookolly Undri"),
        KARAMJA(new WorldPoint(2967, 2967, 0), 16, "Gandius");

        private final WorldPoint location;
        private final int widgetId;
        private final String name;

        public IWidget getWidget() {
            return Static.getWidgets().get(138, this.widgetId);
        }

        public WorldPoint getLocation() {
            return this.location;
        }

        public int getWidgetId() {
            return this.widgetId;
        }

        public String getName() {
            return this.name;
        }

        private GliderLocation(WorldPoint location, int widgetId, String name) {
            this.location = location;
            this.widgetId = widgetId;
            this.name = name;
        }
    }
}

