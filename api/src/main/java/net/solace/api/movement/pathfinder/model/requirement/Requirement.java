package net.solace.api.movement.pathfinder.model.requirement;

import java.util.Set;
import java.util.function.Supplier;
import net.runelite.api.Quest;
import net.runelite.api.QuestState;
import net.solace.api.movement.pathfinder.model.MovementConstants;
import net.solace.api.movement.pathfinder.model.requirement.Comparison;
import net.solace.api.movement.pathfinder.model.requirement.QuestRequirement;
import net.solace.api.movement.pathfinder.model.requirement.VarRequirement;
import net.solace.api.movement.pathfinder.model.requirement.VarType;

public interface Requirement
extends Supplier<Boolean> {
    public static final QuestRequirement CABIN_FEVER_QUEST = new QuestRequirement(Quest.CABIN_FEVER, Set.of(QuestState.FINISHED));
    public static final QuestRequirement GRAND_TREE_QUEST = new QuestRequirement(Quest.THE_GRAND_TREE, Set.of(QuestState.FINISHED));
    public static final QuestRequirement MONKEY_MADNESS_I_FINISHED = new QuestRequirement(Quest.MONKEY_MADNESS_I, Set.of(QuestState.FINISHED));
    public static final QuestRequirement REGICIDE_QUEST = new QuestRequirement(Quest.REGICIDE, Set.of(QuestState.FINISHED));
    public static final QuestRequirement PRIEST_IN_PERIL_QUEST = new QuestRequirement(Quest.PRIEST_IN_PERIL, Set.of(QuestState.FINISHED));
    public static final QuestRequirement SONG_OF_THE_ELVES_QUEST = new QuestRequirement(Quest.SONG_OF_THE_ELVES, Set.of(QuestState.FINISHED));
    public static final QuestRequirement MONKEY_MADNESS_I = new QuestRequirement(Quest.MONKEY_MADNESS_I, Set.of(QuestState.IN_PROGRESS, QuestState.FINISHED));
    public static final QuestRequirement MONKEY_MADNESS_II = new QuestRequirement(Quest.MONKEY_MADNESS_II, Set.of(QuestState.FINISHED));
    public static final QuestRequirement ONE_SMALL_FAVOUR = new QuestRequirement(Quest.ONE_SMALL_FAVOUR, Set.of(QuestState.FINISHED));
    public static final QuestRequirement THE_GOLEM = new QuestRequirement(Quest.THE_GOLEM, Set.of(QuestState.FINISHED));
    public static final QuestRequirement ICTHLARINS_LITTLE_HELPER = new QuestRequirement(Quest.ICTHLARINS_LITTLE_HELPER, Set.of(QuestState.FINISHED));
    public static final QuestRequirement PANDEMONIUM_COMPLETED = new QuestRequirement(Quest.PANDEMONIUM, Set.of(QuestState.FINISHED));
    public static final QuestRequirement SUMMER_SHORE_COMPLETED = new QuestRequirement(Quest.TROUBLED_TORTUGANS, Set.of(QuestState.FINISHED));
    public static final VarRequirement KUDOS_153 = new VarRequirement(Comparison.GREATER_THAN_EQUAL, VarType.VARBIT, 3637, 153);
    public static final VarRequirement KHAREDST_PAGE_1 = new VarRequirement(Comparison.EQUAL, VarType.VARBIT, 6030, 1);
    public static final VarRequirement KHAREDST_PAGE_2 = new VarRequirement(Comparison.EQUAL, VarType.VARBIT, 6031, 1);
    public static final VarRequirement KHAREDST_PAGE_3 = new VarRequirement(Comparison.EQUAL, VarType.VARBIT, 6032, 1);
    public static final VarRequirement KHAREDST_PAGE_4 = new VarRequirement(Comparison.EQUAL, VarType.VARBIT, 6033, 1);
    public static final VarRequirement KHAREDST_PAGE_5 = new VarRequirement(Comparison.EQUAL, VarType.VARBIT, 6034, 1);
    public static final VarRequirement ARCEUUS_FAIRY_RING = new VarRequirement(Comparison.GREATER_THAN_EQUAL, VarType.VARBIT, 4885, 1);
    public static final VarRequirement VISITED_VARLAMORE = new VarRequirement(Comparison.GREATER_THAN_EQUAL, VarType.VARBIT, MovementConstants.VISISTED_VARLAMORE, 2);
    public static final VarRequirement VISITED_KOUREND = new VarRequirement(Comparison.GREATER_THAN_EQUAL, VarType.VARBIT, 4897, 1);
    public static final VarRequirement VISITED_DEEPFIN_POINT = new VarRequirement(Comparison.GREATER_THAN_EQUAL, VarType.VARBIT, 19566, 1);
    public static final VarRequirement VISITED_PORT_ROBERTS = new VarRequirement(Comparison.GREATER_THAN_EQUAL, VarType.VARBIT, 18152, 1);
    public static final VarRequirement KARAMJA_DIARY_HARD = new VarRequirement(Comparison.EQUAL, VarType.VARBIT, 3611, 2);
    public static final VarRequirement DESERT_DIARY_ELITE = new VarRequirement(Comparison.EQUAL, VarType.VARBIT, 4486, 1);
    public static final Requirement VISITED_MORYTANIA = new VarRequirement(Comparison.EQUAL, VarType.VARP, 302, 61);
}

