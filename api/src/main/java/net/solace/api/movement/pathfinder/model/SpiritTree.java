package net.solace.api.movement.pathfinder.model;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import net.runelite.api.Quest;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.solace.api.Static;
import net.solace.api.movement.pathfinder.model.Transport;
import net.solace.api.movement.pathfinder.model.requirement.QuestRequirement;
import net.solace.api.movement.pathfinder.model.requirement.Requirements;
import net.solace.api.movement.pathfinder.model.requirement.SkillRequirement;

public enum SpiritTree {
    TREE_GNOME_VILLAGE(new WorldPoint(2542, 3170, 0), "Tree Gnome Village"),
    GNOME_STRONGHOLD(new WorldPoint(2461, 3444, 0), "Gnome Stronghold"),
    BATTLEFIELD_OF_KHAZARD(new WorldPoint(2555, 3259, 0), "Battlefield of Khazard"),
    GRAND_EXCHANGE(new WorldPoint(3185, 3508, 0), "Grand Exchange"),
    FELDIP_HILLS(new WorldPoint(2488, 2850, 0), "Feldip Hills"),
    PRIFDDINAS(new WorldPoint(3274, 6123, 0), "Prifddinas", Requirements.of(new QuestRequirement(Quest.SONG_OF_THE_ELVES, Set.of(QuestState.FINISHED)))),
    POISON_WASTE(new WorldPoint(2336, 3111, 0), "Poison Waste", Requirements.of(new QuestRequirement(Quest.THE_PATH_OF_GLOUPHRIE, Set.of(QuestState.FINISHED)))),
    PORT_SARIM(new WorldPoint(3058, 3257, 0), "Port Sarim"),
    ETCETERIA(new WorldPoint(2613, 3855, 0), "Etceteria"),
    BRIMHAVEN(new WorldPoint(2800, 3203, 0), "Brimhaven"),
    HOSIDIUS(new WorldPoint(1693, 3540, 0), "Hosidius"),
    FARMING_GUILD(new WorldPoint(1251, 3750, 0), "Farming Guild", Requirements.of(new SkillRequirement(Skill.FARMING, 85)));

    private final WorldPoint position;
    private final String location;
    private static final SpiritTree[] VALUES;
    private final Requirements requirements;

    private SpiritTree(WorldPoint position, String location) {
        this.position = position;
        this.location = location;
        this.requirements = new Requirements();
    }

    public static SpiritTree[] getAll() {
        return VALUES;
    }

    public static Set<SpiritTree> getAllWithNoRequirements() {
        return Set.of(TREE_GNOME_VILLAGE, BATTLEFIELD_OF_KHAZARD, GRAND_EXCHANGE, FELDIP_HILLS);
    }

    public boolean canUse() {
        return this.requirements.fulfilled();
    }

    public static List<Transport> getTransports() {
        return Static.getSolaceConfig().spiritTrees().stream().filter(source -> !(source.getLocation().equals("Gnome Stronghold") && !Static.getQuests().isFinished(Quest.THE_GRAND_TREE) || source.getLocation().equals("Prifddinas") || source.getLocation().equals("Poison Waste") && !Static.getQuests().isFinished(Quest.THE_PATH_OF_GLOUPHRIE) || !source.canUse())).flatMap(source -> Static.getSolaceConfig().spiritTrees().stream().filter(target -> source != target && target.canUse()).map(target -> Static.getTransportLoader().spritTreeTransport(source.getPosition(), target.getPosition(), target.getLocation()))).collect(Collectors.toList());
    }

    private SpiritTree(WorldPoint position, String location, Requirements requirements) {
        this.position = position;
        this.location = location;
        this.requirements = requirements;
    }

    public WorldPoint getPosition() {
        return this.position;
    }

    public String getLocation() {
        return this.location;
    }

    public Requirements getRequirements() {
        return this.requirements;
    }

    static {
        VALUES = SpiritTree.values();
    }
}

