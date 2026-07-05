package net.solace.api.magic;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import net.runelite.api.Quest;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.solace.api.Static;
import net.solace.api.items.IInventory;
import net.solace.api.magic.Rune;
import net.solace.api.magic.RuneRequirement;
import net.solace.api.magic.Spell;
import net.solace.api.movement.pathfinder.model.requirement.Comparison;
import net.solace.api.movement.pathfinder.model.requirement.GenericRequirement;
import net.solace.api.movement.pathfinder.model.requirement.ItemRequirement;
import net.solace.api.movement.pathfinder.model.requirement.QuestRequirement;
import net.solace.api.movement.pathfinder.model.requirement.Reduction;
import net.solace.api.movement.pathfinder.model.requirement.Requirements;
import net.solace.api.movement.pathfinder.model.requirement.SkillRequirement;
import net.solace.api.movement.pathfinder.model.requirement.VarRequirement;
import net.solace.api.movement.pathfinder.model.requirement.VarType;

public enum SpellBook {
    STANDARD(0),
    ANCIENT(1),
    LUNAR(2),
    NECROMANCY(3);

    private final int varbitValue;

    private SpellBook(int varbitValue) {
        this.varbitValue = varbitValue;
    }

    public static SpellBook getCurrent() {
        return Arrays.stream(SpellBook.values()).filter(x -> Static.getVars().getBit(4070) == x.varbitValue).findFirst().orElse(null);
    }

    public static enum Necromancy implements Spell
    {
        ARCEUUS_HOME_TELEPORT(1, 14287001, new RuneRequirement[0]),
        ARCEUUS_LIBRARY_TELEPORT(6, 14287003, new RuneRequirement(2, Rune.EARTH), new RuneRequirement(1, Rune.LAW)),
        DRAYNOR_MANOR_TELEPORT(17, 14287007, new RuneRequirement(1, Rune.EARTH), new RuneRequirement(1, Rune.WATER), new RuneRequirement(1, Rune.LAW)),
        BATTLEFRONT_TELEPORT(23, 14287019, new RuneRequirement(1, Rune.EARTH), new RuneRequirement(1, Rune.FIRE), new RuneRequirement(1, Rune.LAW)),
        MIND_ALTAR_TELEPORT(28, 14287009, new RuneRequirement(2, Rune.MIND), new RuneRequirement(1, Rune.LAW)),
        RESPAWN_TELEPORT(34, 14287010, new RuneRequirement(1, Rune.SOUL), new RuneRequirement(1, Rune.LAW)),
        SALVE_GRAVEYARD_TELEPORT(40, 14287011, new RuneRequirement(2, Rune.SOUL), new RuneRequirement(1, Rune.LAW)),
        FENKENSTRAINS_CASTLE_TELEPORT(48, 14287012, new RuneRequirement(1, Rune.EARTH), new RuneRequirement(1, Rune.SOUL), new RuneRequirement(1, Rune.LAW)),
        WEST_ARDOUGNE_TELEPORT(61, 14287013, new RuneRequirement(2, Rune.SOUL), new RuneRequirement(2, Rune.LAW)),
        HARMONY_ISLAND_TELEPORT(65, 14287014, new RuneRequirement(1, Rune.NATURE), new RuneRequirement(1, Rune.SOUL), new RuneRequirement(1, Rune.LAW)),
        CEMETERY_TELEPORT(71, 14287015, new RuneRequirement(1, Rune.BLOOD), new RuneRequirement(1, Rune.SOUL), new RuneRequirement(1, Rune.LAW)),
        BARROWS_TELEPORT(83, 14287017, new RuneRequirement(1, Rune.BLOOD), new RuneRequirement(2, Rune.SOUL), new RuneRequirement(2, Rune.LAW)),
        APE_ATOLL_TELEPORT(90, 0xDA00AA, new RuneRequirement(2, Rune.BLOOD), new RuneRequirement(2, Rune.SOUL), new RuneRequirement(2, Rune.LAW)),
        GHOSTLY_GRASP(35, 14287024, -1, 56, new RuneRequirement(4, Rune.AIR), new RuneRequirement(1, Rune.CHAOS)),
        SKELETAL_GRASP(56, 14287025, -1, 57, Requirements.of(new QuestRequirement(Quest.A_KINGDOM_DIVIDED, Set.of(QuestState.FINISHED))), new RuneRequirement(8, Rune.EARTH), new RuneRequirement(1, Rune.DEATH)),
        UNDEAD_GRASP(79, 14287026, -1, 58, Requirements.of(new QuestRequirement(Quest.A_KINGDOM_DIVIDED, Set.of(QuestState.FINISHED))), new RuneRequirement(12, Rune.FIRE), new RuneRequirement(1, Rune.BLOOD)),
        INFERIOR_DEMONBANE(44, 14287020, -1, 53, new RuneRequirement(3, Rune.FIRE), new RuneRequirement(1, Rune.CHAOS)),
        SUPERIOR_DEMONBANE(62, 0xDA00AD, -1, 54, Requirements.of(new QuestRequirement(Quest.A_KINGDOM_DIVIDED, Set.of(QuestState.FINISHED))), new RuneRequirement(5, Rune.FIRE), new RuneRequirement(1, Rune.SOUL)),
        DARK_DEMONBANE(82, 14287022, -1, 55, Requirements.of(new QuestRequirement(Quest.A_KINGDOM_DIVIDED, Set.of(QuestState.FINISHED))), new RuneRequirement(7, Rune.FIRE), new RuneRequirement(2, Rune.SOUL)),
        LESSER_CORRUPTION(64, 14287028, Requirements.of(new QuestRequirement(Quest.A_KINGDOM_DIVIDED, Set.of(QuestState.FINISHED))), new RuneRequirement(1, Rune.DEATH), new RuneRequirement(2, Rune.SOUL)),
        GREATER_CORRUPTION(85, 14287029, Requirements.of(new QuestRequirement(Quest.A_KINGDOM_DIVIDED, Set.of(QuestState.FINISHED))), new RuneRequirement(1, Rune.BLOOD), new RuneRequirement(3, Rune.SOUL)),
        RESURRECT_LESSER_GHOST(38, 14287037, 1, Requirements.of(new QuestRequirement(Quest.A_KINGDOM_DIVIDED, Set.of(QuestState.FINISHED)), new ItemRequirement(Reduction.AND, List.of(Integer.valueOf(25818)), ItemRequirement.Location.EITHER, 1), new SkillRequirement(Skill.PRAYER, 2), new VarRequirement(Comparison.EQUAL, VarType.VARBIT, 12290, 0)), new RuneRequirement(10, Rune.AIR), new RuneRequirement(1, Rune.COSMIC), new RuneRequirement(5, Rune.MIND)),
        RESURRECT_LESSER_SKELETON(38, 14287038, 1, Requirements.of(new QuestRequirement(Quest.A_KINGDOM_DIVIDED, Set.of(QuestState.FINISHED)), new ItemRequirement(Reduction.AND, List.of(Integer.valueOf(25818)), ItemRequirement.Location.EITHER, 1), new SkillRequirement(Skill.PRAYER, 2), new VarRequirement(Comparison.EQUAL, VarType.VARBIT, 12290, 0)), new RuneRequirement(10, Rune.AIR), new RuneRequirement(1, Rune.COSMIC), new RuneRequirement(5, Rune.MIND)),
        RESURRECT_LESSER_ZOMBIE(38, 14287039, 1, Requirements.of(new QuestRequirement(Quest.A_KINGDOM_DIVIDED, Set.of(QuestState.FINISHED)), new ItemRequirement(Reduction.AND, List.of(Integer.valueOf(25818)), ItemRequirement.Location.EITHER, 1), new SkillRequirement(Skill.PRAYER, 2), new VarRequirement(Comparison.EQUAL, VarType.VARBIT, 12290, 0)), new RuneRequirement(10, Rune.AIR), new RuneRequirement(1, Rune.COSMIC), new RuneRequirement(5, Rune.MIND)),
        RESURRECT_SUPERIOR_GHOST(57, 14287040, 1, Requirements.of(new QuestRequirement(Quest.A_KINGDOM_DIVIDED, Set.of(QuestState.FINISHED)), new ItemRequirement(Reduction.AND, List.of(Integer.valueOf(25818)), ItemRequirement.Location.EITHER, 1), new SkillRequirement(Skill.PRAYER, 4), new VarRequirement(Comparison.EQUAL, VarType.VARBIT, 12290, 0)), new RuneRequirement(10, Rune.EARTH), new RuneRequirement(1, Rune.COSMIC), new RuneRequirement(5, Rune.DEATH)),
        RESURRECT_SUPERIOR_SKELETON(57, 14287041, Requirements.of(new QuestRequirement(Quest.A_KINGDOM_DIVIDED, Set.of(QuestState.FINISHED)), new ItemRequirement(Reduction.AND, List.of(Integer.valueOf(25818)), ItemRequirement.Location.EITHER, 1), new SkillRequirement(Skill.PRAYER, 4), new VarRequirement(Comparison.EQUAL, VarType.VARBIT, 12290, 0)), new RuneRequirement(10, Rune.EARTH), new RuneRequirement(1, Rune.COSMIC), new RuneRequirement(5, Rune.DEATH)),
        RESURRECT_SUPERIOR_ZOMBIE(57, 14287042, 1, Requirements.of(new QuestRequirement(Quest.A_KINGDOM_DIVIDED, Set.of(QuestState.FINISHED)), new ItemRequirement(Reduction.AND, List.of(Integer.valueOf(25818)), ItemRequirement.Location.EITHER, 1), new SkillRequirement(Skill.PRAYER, 4), new VarRequirement(Comparison.EQUAL, VarType.VARBIT, 12290, 0)), new RuneRequirement(10, Rune.EARTH), new RuneRequirement(1, Rune.COSMIC), new RuneRequirement(5, Rune.DEATH)),
        RESURRECT_GREATER_GHOST(76, 14287043, 1, Requirements.of(new QuestRequirement(Quest.A_KINGDOM_DIVIDED, Set.of(QuestState.FINISHED)), new ItemRequirement(Reduction.AND, List.of(Integer.valueOf(25818)), ItemRequirement.Location.EITHER, 1), new SkillRequirement(Skill.PRAYER, 6), new VarRequirement(Comparison.EQUAL, VarType.VARBIT, 12290, 0)), new RuneRequirement(10, Rune.FIRE), new RuneRequirement(1, Rune.COSMIC), new RuneRequirement(5, Rune.BLOOD)),
        RESURRECT_GREATER_SKELETON(76, 14287044, 1, Requirements.of(new QuestRequirement(Quest.A_KINGDOM_DIVIDED, Set.of(QuestState.FINISHED)), new ItemRequirement(Reduction.AND, List.of(Integer.valueOf(25818)), ItemRequirement.Location.EITHER, 1), new SkillRequirement(Skill.PRAYER, 6), new VarRequirement(Comparison.EQUAL, VarType.VARBIT, 12290, 0)), new RuneRequirement(10, Rune.FIRE), new RuneRequirement(1, Rune.COSMIC), new RuneRequirement(5, Rune.BLOOD)),
        RESURRECT_GREATER_ZOMBIE(76, 14287045, 1, Requirements.of(new QuestRequirement(Quest.A_KINGDOM_DIVIDED, Set.of(QuestState.FINISHED)), new ItemRequirement(Reduction.AND, List.of(Integer.valueOf(25818)), ItemRequirement.Location.EITHER, 1), new SkillRequirement(Skill.PRAYER, 6), new VarRequirement(Comparison.EQUAL, VarType.VARBIT, 12290, 0)), new RuneRequirement(10, Rune.FIRE), new RuneRequirement(1, Rune.COSMIC), new RuneRequirement(5, Rune.BLOOD)),
        DARK_LURE(50, 14287035, Requirements.of(new QuestRequirement(Quest.A_KINGDOM_DIVIDED, Set.of(QuestState.FINISHED))), new RuneRequirement(1, Rune.DEATH), new RuneRequirement(1, Rune.NATURE)),
        MARK_OF_DARKNESS(59, 14287023, 1, Requirements.of(new QuestRequirement(Quest.A_KINGDOM_DIVIDED, Set.of(QuestState.FINISHED))), new RuneRequirement(1, Rune.COSMIC), new RuneRequirement(1, Rune.SOUL)),
        WARD_OF_ARCEUUS(73, 14287027, 1, Requirements.of(new QuestRequirement(Quest.A_KINGDOM_DIVIDED, Set.of(QuestState.FINISHED))), new RuneRequirement(1, Rune.COSMIC), new RuneRequirement(2, Rune.NATURE), new RuneRequirement(4, Rune.SOUL)),
        BASIC_REANIMATION(16, 14287002, new RuneRequirement(4, Rune.BODY), new RuneRequirement(2, Rune.NATURE)),
        ADEPT_REANIMATION(41, 14287004, new RuneRequirement(4, Rune.BODY), new RuneRequirement(3, Rune.NATURE), new RuneRequirement(1, Rune.SOUL)),
        EXPERT_REANIMATION(72, 14287005, new RuneRequirement(1, Rune.BLOOD), new RuneRequirement(3, Rune.NATURE), new RuneRequirement(2, Rune.SOUL)),
        MASTER_REANIMATION(90, 14287006, new RuneRequirement(2, Rune.BLOOD), new RuneRequirement(4, Rune.NATURE), new RuneRequirement(4, Rune.SOUL)),
        DEMONIC_OFFERING(84, 14287030, 1, Requirements.of(new QuestRequirement(Quest.A_KINGDOM_DIVIDED, Set.of(QuestState.FINISHED))), new RuneRequirement(1, Rune.SOUL), new RuneRequirement(1, Rune.WRATH)),
        SINISTER_OFFERING(92, 14287031, 1, Requirements.of(new QuestRequirement(Quest.A_KINGDOM_DIVIDED, Set.of(QuestState.FINISHED))), new RuneRequirement(1, Rune.BLOOD), new RuneRequirement(1, Rune.WRATH)),
        SHADOW_VEIL(47, 14287033, 1, Requirements.of(new QuestRequirement(Quest.A_KINGDOM_DIVIDED, Set.of(QuestState.FINISHED))), new RuneRequirement(5, Rune.EARTH), new RuneRequirement(5, Rune.FIRE), new RuneRequirement(5, Rune.COSMIC)),
        VILE_VIGOUR(66, 14287034, 1, Requirements.of(new VarRequirement(Comparison.EQUAL, VarType.VARBIT, 12292, 0), new SkillRequirement(Skill.PRAYER, 1), new QuestRequirement(Quest.A_KINGDOM_DIVIDED, Set.of(QuestState.FINISHED)), new GenericRequirement(() -> Static.getMovement().getRunEnergy(), 100, Comparison.LESS_THAN)), new RuneRequirement(3, Rune.AIR), new RuneRequirement(1, Rune.SOUL)),
        DEGRIME(70, 14287032, 1, Requirements.of(new QuestRequirement(Quest.A_KINGDOM_DIVIDED, Set.of(QuestState.FINISHED))), new RuneRequirement(4, Rune.EARTH), new RuneRequirement(2, Rune.NATURE)),
        RESURRECT_CROPS(78, 14287016, new RuneRequirement(25, Rune.EARTH), new RuneRequirement(8, Rune.BLOOD), new RuneRequirement(12, Rune.NATURE), new RuneRequirement(8, Rune.SOUL)),
        DEATH_CHARGE(80, 14287036, 1, Requirements.of(new QuestRequirement(Quest.A_KINGDOM_DIVIDED, Set.of(QuestState.FINISHED)), new VarRequirement(Comparison.EQUAL, VarType.VARBIT, 12138, 0), new VarRequirement(Comparison.EQUAL, VarType.VARBIT, 12411, 0)), new RuneRequirement(1, Rune.BLOOD), new RuneRequirement(1, Rune.DEATH), new RuneRequirement(1, Rune.SOUL));

        private final int level;
        private final int component;
        private final int menuIdentifier;
        private final int autocastIndex;
        private final Requirements otherRequirements;
        private final RuneRequirement[] requirements;

        private Necromancy(int level, int component, int menuIdentifier, int autocastIndex, Requirements otherRequirements, RuneRequirement ... requirements) {
            this.level = level;
            this.component = component;
            this.menuIdentifier = menuIdentifier;
            this.autocastIndex = autocastIndex;
            this.otherRequirements = otherRequirements;
            this.requirements = requirements;
        }

        private Necromancy(int level, int component, int menuIdentifier, Requirements otherRequirements, RuneRequirement ... requirements) {
            this(level, component, menuIdentifier, -1, otherRequirements, requirements);
        }

        private Necromancy(int level, int component, Requirements otherRequirements, RuneRequirement ... requirements) {
            this(level, component, -1, otherRequirements, requirements);
        }

        private Necromancy(int level, int component, int menuIdentifier, int autocastIndex, RuneRequirement ... requirements) {
            this(level, component, menuIdentifier, autocastIndex, new Requirements(), requirements);
        }

        private Necromancy(int level, int component, int menuIdentifier, RuneRequirement ... requirements) {
            this(level, component, menuIdentifier, -1, requirements);
        }

        private Necromancy(int level, int component, RuneRequirement ... requirements) {
            this(level, component, -1, requirements);
        }

        @Override
        public SpellBook getSpellBook() {
            return NECROMANCY;
        }

        @Override
        public boolean canCast() {
            if (SpellBook.getCurrent() != NECROMANCY) {
                return false;
            }
            if (!Static.getWorlds().inMembersWorld()) {
                return false;
            }
            if (this == ARCEUUS_HOME_TELEPORT) {
                return !Static.getMagic().isHomeTeleportOnCooldown();
            }
            if (this.level > Static.getSkills().getBoostedLevel(Skill.MAGIC) || this.level > Static.getSkills().getBoostedLevel(Skill.MAGIC)) {
                return false;
            }
            return this.haveRunesAvailable() && this.otherRequirements.fulfilled();
        }

        public boolean haveRunesAvailable() {
            for (RuneRequirement req : this.requirements) {
                if (req.meetsRequirements()) continue;
                return false;
            }
            return true;
        }

        @Override
        public int getLevel() {
            return this.level;
        }

        @Override
        public int getComponent() {
            return this.component;
        }

        @Override
        public int getMenuIdentifier() {
            return this.menuIdentifier;
        }

        @Override
        public int getAutocastIndex() {
            return this.autocastIndex;
        }

        public Requirements getOtherRequirements() {
            return this.otherRequirements;
        }

        @Override
        public RuneRequirement[] getRequirements() {
            return this.requirements;
        }
    }

    public static enum Lunar implements Spell
    {
        LUNAR_HOME_TELEPORT(0, 14286957, new RuneRequirement[0]),
        MOONCLAN_TELEPORT(69, 14286964, new RuneRequirement(2, Rune.EARTH), new RuneRequirement(2, Rune.ASTRAL), new RuneRequirement(1, Rune.LAW)),
        TELE_GROUP_MOONCLAN(70, 14286965, new RuneRequirement(4, Rune.EARTH), new RuneRequirement(2, Rune.ASTRAL), new RuneRequirement(1, Rune.LAW)),
        OURANIA_TELEPORT(71, 14287000, Requirements.of(new VarRequirement(Comparison.GREATER_THAN_EQUAL, VarType.VARBIT, 5376, 1)), new RuneRequirement(6, Rune.EARTH), new RuneRequirement(2, Rune.ASTRAL), new RuneRequirement(1, Rune.LAW)),
        WATERBIRTH_TELEPORT(72, 14286968, new RuneRequirement(1, Rune.WATER), new RuneRequirement(2, Rune.ASTRAL), new RuneRequirement(1, Rune.LAW)),
        TELE_GROUP_WATERBIRTH(73, 14286969, new RuneRequirement(5, Rune.WATER), new RuneRequirement(2, Rune.ASTRAL), new RuneRequirement(1, Rune.LAW)),
        BARBARIAN_TELEPORT(75, 14286972, new RuneRequirement(3, Rune.FIRE), new RuneRequirement(2, Rune.ASTRAL), new RuneRequirement(2, Rune.LAW)),
        TELE_GROUP_BARBARIAN(76, 14286973, new RuneRequirement(6, Rune.FIRE), new RuneRequirement(2, Rune.ASTRAL), new RuneRequirement(2, Rune.LAW)),
        KHAZARD_TELEPORT(78, 14286976, new RuneRequirement(4, Rune.WATER), new RuneRequirement(2, Rune.ASTRAL), new RuneRequirement(2, Rune.LAW)),
        TELE_GROUP_KHAZARD(79, 14286977, new RuneRequirement(8, Rune.WATER), new RuneRequirement(2, Rune.ASTRAL), new RuneRequirement(2, Rune.LAW)),
        FISHING_GUILD_TELEPORT(85, 14286984, new RuneRequirement(10, Rune.WATER), new RuneRequirement(3, Rune.ASTRAL), new RuneRequirement(3, Rune.LAW)),
        TELE_GROUP_FISHING_GUILD(86, 14286985, new RuneRequirement(14, Rune.WATER), new RuneRequirement(3, Rune.ASTRAL), new RuneRequirement(3, Rune.LAW)),
        CATHERBY_TELEPORT(87, 14286987, new RuneRequirement(10, Rune.WATER), new RuneRequirement(3, Rune.ASTRAL), new RuneRequirement(3, Rune.LAW)),
        TELE_GROUP_CATHERBY(88, 14286988, new RuneRequirement(15, Rune.WATER), new RuneRequirement(3, Rune.ASTRAL), new RuneRequirement(3, Rune.LAW)),
        ICE_PLATEAU_TELEPORT(89, 14286990, new RuneRequirement(8, Rune.WATER), new RuneRequirement(3, Rune.ASTRAL), new RuneRequirement(3, Rune.LAW)),
        TELE_GROUP_ICE_PLATEAU(90, 14286991, new RuneRequirement(16, Rune.WATER), new RuneRequirement(3, Rune.ASTRAL), new RuneRequirement(3, Rune.LAW)),
        MONSTER_EXAMINE(66, 14286960, new RuneRequirement(1, Rune.MIND), new RuneRequirement(1, Rune.COSMIC), new RuneRequirement(1, Rune.ASTRAL)),
        CURE_OTHER(68, 14286962, new RuneRequirement(10, Rune.EARTH), new RuneRequirement(1, Rune.ASTRAL), new RuneRequirement(1, Rune.LAW)),
        CURE_ME(71, 14286966, new RuneRequirement(2, Rune.COSMIC), new RuneRequirement(2, Rune.ASTRAL), new RuneRequirement(1, Rune.LAW)),
        CURE_GROUP(74, 14286970, new RuneRequirement(2, Rune.COSMIC), new RuneRequirement(2, Rune.ASTRAL), new RuneRequirement(2, Rune.LAW)),
        STAT_SPY(75, 14286971, new RuneRequirement(5, Rune.BODY), new RuneRequirement(1, Rune.COSMIC), new RuneRequirement(2, Rune.ASTRAL)),
        DREAM(79, 14286978, new RuneRequirement(5, Rune.BODY), new RuneRequirement(1, Rune.COSMIC), new RuneRequirement(2, Rune.ASTRAL)),
        STAT_RESTORE_POT_SHARE(81, 14286980, new RuneRequirement(10, Rune.WATER), new RuneRequirement(10, Rune.EARTH), new RuneRequirement(2, Rune.ASTRAL)),
        BOOST_POTION_SHARE(84, 14286983, new RuneRequirement(10, Rune.WATER), new RuneRequirement(12, Rune.EARTH), new RuneRequirement(3, Rune.ASTRAL)),
        ENERGY_TRANSFER(91, 14286992, new RuneRequirement(3, Rune.ASTRAL), new RuneRequirement(1, Rune.NATURE), new RuneRequirement(2, Rune.LAW)),
        HEAL_OTHER(92, 14286993, new RuneRequirement(3, Rune.ASTRAL), new RuneRequirement(3, Rune.LAW), new RuneRequirement(1, Rune.BLOOD)),
        VENGEANCE_OTHER(93, 14286994, new RuneRequirement(10, Rune.EARTH), new RuneRequirement(3, Rune.ASTRAL), new RuneRequirement(2, Rune.DEATH)),
        VENGEANCE(94, 14286995, new RuneRequirement(10, Rune.EARTH), new RuneRequirement(4, Rune.ASTRAL), new RuneRequirement(2, Rune.DEATH)),
        HEAL_GROUP(95, 14286996, new RuneRequirement(4, Rune.ASTRAL), new RuneRequirement(6, Rune.LAW), new RuneRequirement(3, Rune.BLOOD)),
        BAKE_PIE(65, 14286958, new RuneRequirement(4, Rune.WATER), new RuneRequirement(5, Rune.FIRE), new RuneRequirement(1, Rune.ASTRAL)),
        GEOMANCY(65, 14286998, new RuneRequirement(8, Rune.EARTH), new RuneRequirement(3, Rune.ASTRAL), new RuneRequirement(3, Rune.NATURE)),
        CURE_PLANT(66, 14286959, new RuneRequirement(8, Rune.EARTH), new RuneRequirement(1, Rune.ASTRAL)),
        NPC_CONTACT(67, 14286961, new RuneRequirement(2, Rune.AIR), new RuneRequirement(1, Rune.COSMIC), new RuneRequirement(1, Rune.ASTRAL)),
        HUMIDIFY(68, 14286963, 1, new RuneRequirement(3, Rune.WATER), new RuneRequirement(1, Rune.FIRE), new RuneRequirement(1, Rune.ASTRAL)),
        HUNTER_KIT(71, 14286967, new RuneRequirement(2, Rune.EARTH), new RuneRequirement(2, Rune.ASTRAL)),
        SPIN_FLAX(76, 14286999, new RuneRequirement(5, Rune.AIR), new RuneRequirement(1, Rune.ASTRAL), new RuneRequirement(2, Rune.NATURE)),
        SUPERGLASS_MAKE(77, 14286974, new RuneRequirement(10, Rune.AIR), new RuneRequirement(6, Rune.FIRE), new RuneRequirement(2, Rune.ASTRAL)),
        TAN_LEATHER(78, 14286975, new RuneRequirement(5, Rune.FIRE), new RuneRequirement(2, Rune.ASTRAL), new RuneRequirement(1, Rune.NATURE)),
        STRING_JEWELLERY(80, 14286979, new RuneRequirement(10, Rune.EARTH), new RuneRequirement(5, Rune.WATER), new RuneRequirement(2, Rune.ASTRAL)),
        MAGIC_IMBUE(82, 14286981, new RuneRequirement(7, Rune.WATER), new RuneRequirement(7, Rune.FIRE), new RuneRequirement(2, Rune.ASTRAL)),
        FERTILE_SOIL(83, 14286982, new RuneRequirement(15, Rune.EARTH), new RuneRequirement(3, Rune.ASTRAL), new RuneRequirement(2, Rune.NATURE)),
        PLANK_MAKE(86, 14286986, new RuneRequirement(15, Rune.EARTH), new RuneRequirement(2, Rune.ASTRAL), new RuneRequirement(1, Rune.NATURE)),
        RECHARGE_DRAGONSTONE(89, 14286989, new RuneRequirement(4, Rune.WATER), new RuneRequirement(1, Rune.ASTRAL), new RuneRequirement(1, Rune.SOUL)),
        SPELLBOOK_SWAP(96, 14286997, new RuneRequirement(2, Rune.COSMIC), new RuneRequirement(3, Rune.ASTRAL), new RuneRequirement(1, Rune.LAW));

        private final int level;
        private final int component;
        private final int menuIdentifier;
        private final int autocastIndex;
        private final Requirements otherRequirements;
        private final RuneRequirement[] requirements;

        private Lunar(int level, int component, int menuIdentifier, int autocastIndex, Requirements otherRequirements, RuneRequirement ... requirements) {
            this.level = level;
            this.component = component;
            this.menuIdentifier = menuIdentifier;
            this.autocastIndex = autocastIndex;
            this.otherRequirements = otherRequirements;
            this.requirements = requirements;
        }

        private Lunar(int level, int component, int menuIdentifier, Requirements otherRequirements, RuneRequirement ... requirements) {
            this(level, component, menuIdentifier, -1, otherRequirements, requirements);
        }

        private Lunar(int level, int component, Requirements otherRequirements, RuneRequirement ... requirements) {
            this(level, component, -1, otherRequirements, requirements);
        }

        private Lunar(int level, int component, int menuIdentifier, int autocastIndex, RuneRequirement ... requirements) {
            this(level, component, menuIdentifier, autocastIndex, new Requirements(), requirements);
        }

        private Lunar(int level, int component, int menuIdentifier, RuneRequirement ... requirements) {
            this(level, component, menuIdentifier, -1, requirements);
        }

        private Lunar(int level, int component, RuneRequirement ... requirements) {
            this(level, component, -1, requirements);
        }

        @Override
        public SpellBook getSpellBook() {
            return LUNAR;
        }

        @Override
        public boolean canCast() {
            if (SpellBook.getCurrent() != LUNAR) {
                return false;
            }
            if (!Static.getWorlds().inMembersWorld()) {
                return false;
            }
            if (this == LUNAR_HOME_TELEPORT) {
                return !Static.getMagic().isHomeTeleportOnCooldown();
            }
            if (this.level > Static.getSkills().getBoostedLevel(Skill.MAGIC) || this.level > Static.getSkills().getBoostedLevel(Skill.MAGIC)) {
                return false;
            }
            return this.haveRunesAvailable() && this.otherRequirements.fulfilled();
        }

        public boolean haveRunesAvailable() {
            for (RuneRequirement req : this.requirements) {
                if (req.meetsRequirements()) continue;
                return false;
            }
            return true;
        }

        @Override
        public int getLevel() {
            return this.level;
        }

        @Override
        public int getComponent() {
            return this.component;
        }

        @Override
        public int getMenuIdentifier() {
            return this.menuIdentifier;
        }

        @Override
        public int getAutocastIndex() {
            return this.autocastIndex;
        }

        public Requirements getOtherRequirements() {
            return this.otherRequirements;
        }

        @Override
        public RuneRequirement[] getRequirements() {
            return this.requirements;
        }
    }

    public static enum Ancient implements Spell
    {
        EDGEVILLE_HOME_TELEPORT(0, 14286956, new RuneRequirement[0]),
        PADDEWWA_TELEPORT(54, 14286948, new RuneRequirement(1, Rune.AIR), new RuneRequirement(1, Rune.FIRE), new RuneRequirement(2, Rune.LAW)),
        SENNTISTEN_TELEPORT(60, 14286949, new RuneRequirement(2, Rune.LAW), new RuneRequirement(1, Rune.SOUL)),
        KHARYRLL_TELEPORT(66, 14286950, new RuneRequirement(2, Rune.LAW), new RuneRequirement(1, Rune.BLOOD)),
        LASSAR_TELEPORT(72, 14286951, new RuneRequirement(4, Rune.WATER), new RuneRequirement(2, Rune.LAW)),
        DAREEYAK_TELEPORT(78, 14286952, new RuneRequirement(2, Rune.AIR), new RuneRequirement(3, Rune.FIRE), new RuneRequirement(2, Rune.LAW)),
        CARRALLANGER_TELEPORT(84, 14286953, new RuneRequirement(2, Rune.LAW), new RuneRequirement(2, Rune.SOUL)),
        BOUNTY_TARGET_TELEPORT(85, 14286924, new RuneRequirement(1, Rune.CHAOS), new RuneRequirement(1, Rune.DEATH), new RuneRequirement(1, Rune.LAW)),
        ANNAKARL_TELEPORT(90, 14286954, new RuneRequirement(2, Rune.LAW), new RuneRequirement(2, Rune.BLOOD)),
        GHORROCK_TELEPORT(96, 14286955, new RuneRequirement(8, Rune.WATER), new RuneRequirement(2, Rune.LAW)),
        SMOKE_RUSH(50, 14286940, -1, 31, new RuneRequirement(1, Rune.AIR), new RuneRequirement(1, Rune.FIRE), new RuneRequirement(2, Rune.CHAOS), new RuneRequirement(2, Rune.DEATH)),
        SHADOW_RUSH(52, 14286944, -1, 32, new RuneRequirement(1, Rune.AIR), new RuneRequirement(2, Rune.CHAOS), new RuneRequirement(2, Rune.DEATH), new RuneRequirement(1, Rune.SOUL)),
        BLOOD_RUSH(56, 14286936, -1, 33, new RuneRequirement(2, Rune.CHAOS), new RuneRequirement(2, Rune.DEATH), new RuneRequirement(1, Rune.BLOOD)),
        ICE_RUSH(58, 14286932, -1, 34, new RuneRequirement(2, Rune.WATER), new RuneRequirement(2, Rune.CHAOS), new RuneRequirement(2, Rune.DEATH)),
        SMOKE_BURST(62, 14286942, -1, 35, new RuneRequirement(2, Rune.AIR), new RuneRequirement(2, Rune.FIRE), new RuneRequirement(4, Rune.CHAOS), new RuneRequirement(2, Rune.DEATH)),
        SHADOW_BURST(64, 14286946, -1, 36, new RuneRequirement(1, Rune.AIR), new RuneRequirement(4, Rune.CHAOS), new RuneRequirement(2, Rune.DEATH), new RuneRequirement(2, Rune.SOUL)),
        BLOOD_BURST(68, 14286938, -1, 37, new RuneRequirement(2, Rune.CHAOS), new RuneRequirement(4, Rune.DEATH), new RuneRequirement(2, Rune.BLOOD)),
        ICE_BURST(70, 14286934, -1, 38, new RuneRequirement(4, Rune.WATER), new RuneRequirement(4, Rune.CHAOS), new RuneRequirement(2, Rune.DEATH)),
        SMOKE_BLITZ(74, 14286941, -1, 39, new RuneRequirement(2, Rune.AIR), new RuneRequirement(2, Rune.FIRE), new RuneRequirement(2, Rune.DEATH), new RuneRequirement(2, Rune.BLOOD)),
        SHADOW_BLITZ(76, 14286945, -1, 40, new RuneRequirement(2, Rune.AIR), new RuneRequirement(2, Rune.DEATH), new RuneRequirement(2, Rune.BLOOD), new RuneRequirement(2, Rune.SOUL)),
        BLOOD_BLITZ(80, 14286937, -1, 41, new RuneRequirement(2, Rune.DEATH), new RuneRequirement(4, Rune.BLOOD)),
        ICE_BLITZ(82, 14286933, -1, 42, new RuneRequirement(3, Rune.WATER), new RuneRequirement(2, Rune.DEATH), new RuneRequirement(2, Rune.BLOOD)),
        SMOKE_BARRAGE(86, 14286943, -1, 43, new RuneRequirement(4, Rune.AIR), new RuneRequirement(4, Rune.FIRE), new RuneRequirement(4, Rune.DEATH), new RuneRequirement(2, Rune.BLOOD)),
        SHADOW_BARRAGE(88, 14286947, -1, 44, new RuneRequirement(4, Rune.AIR), new RuneRequirement(4, Rune.DEATH), new RuneRequirement(2, Rune.BLOOD), new RuneRequirement(3, Rune.SOUL)),
        BLOOD_BARRAGE(92, 14286939, -1, 45, new RuneRequirement(4, Rune.DEATH), new RuneRequirement(4, Rune.BLOOD), new RuneRequirement(1, Rune.SOUL)),
        ICE_BARRAGE(94, 14286935, -1, 46, new RuneRequirement(6, Rune.WATER), new RuneRequirement(4, Rune.DEATH), new RuneRequirement(2, Rune.BLOOD));

        private final int level;
        private final int component;
        private final int menuIdentifier;
        private final int autocastIndex;
        private final Requirements otherRequirements;
        private final RuneRequirement[] requirements;

        private Ancient(int level, int component, int menuIdentifier, int autocastIndex, Requirements otherRequirements, RuneRequirement ... requirements) {
            this.level = level;
            this.component = component;
            this.menuIdentifier = menuIdentifier;
            this.autocastIndex = autocastIndex;
            this.otherRequirements = otherRequirements;
            this.requirements = requirements;
        }

        private Ancient(int level, int component, int menuIdentifier, Requirements otherRequirements, RuneRequirement ... requirements) {
            this(level, component, menuIdentifier, -1, otherRequirements, requirements);
        }

        private Ancient(int level, int component, Requirements otherRequirements, RuneRequirement ... requirements) {
            this(level, component, -1, otherRequirements, requirements);
        }

        private Ancient(int level, int component, int menuIdentifier, int autocastIndex, RuneRequirement ... requirements) {
            this(level, component, menuIdentifier, autocastIndex, new Requirements(), requirements);
        }

        private Ancient(int level, int component, int menuIdentifier, RuneRequirement ... requirements) {
            this(level, component, menuIdentifier, -1, requirements);
        }

        private Ancient(int level, int component, RuneRequirement ... requirements) {
            this(level, component, -1, requirements);
        }

        @Override
        public SpellBook getSpellBook() {
            return ANCIENT;
        }

        @Override
        public boolean canCast() {
            IInventory inv;
            if (SpellBook.getCurrent() != ANCIENT) {
                return false;
            }
            if (!Static.getWorlds().inMembersWorld()) {
                return false;
            }
            if (this == EDGEVILLE_HOME_TELEPORT) {
                return !Static.getMagic().isHomeTeleportOnCooldown();
            }
            if (this.level > Static.getSkills().getBoostedLevel(Skill.MAGIC) || this.level > Static.getSkills().getBoostedLevel(Skill.MAGIC)) {
                return false;
            }
            if ((this == ICE_BARRAGE || this == ICE_BLITZ || this == ICE_BURST || this == ICE_RUSH) && (inv = Static.getInventory()).contains(24607)) {
                return true;
            }
            return this.haveRunesAvailable() && this.otherRequirements.fulfilled();
        }

        public boolean haveRunesAvailable() {
            for (RuneRequirement req : this.requirements) {
                if (req.meetsRequirements()) continue;
                return false;
            }
            return true;
        }

        @Override
        public int getLevel() {
            return this.level;
        }

        @Override
        public int getComponent() {
            return this.component;
        }

        @Override
        public int getMenuIdentifier() {
            return this.menuIdentifier;
        }

        @Override
        public int getAutocastIndex() {
            return this.autocastIndex;
        }

        public Requirements getOtherRequirements() {
            return this.otherRequirements;
        }

        @Override
        public RuneRequirement[] getRequirements() {
            return this.requirements;
        }
    }

    public static enum Standard implements Spell
    {
        HOME_TELEPORT(0, 14286854, false, new RuneRequirement[0]),
        VARROCK_TELEPORT(25, 14286874, false, new RuneRequirement(3, Rune.AIR), new RuneRequirement(1, Rune.FIRE), new RuneRequirement(1, Rune.LAW)),
        LUMBRIDGE_TELEPORT(31, 14286877, false, new RuneRequirement(3, Rune.AIR), new RuneRequirement(1, Rune.EARTH), new RuneRequirement(1, Rune.LAW)),
        FALADOR_TELEPORT(37, 14286880, false, new RuneRequirement(3, Rune.AIR), new RuneRequirement(1, Rune.WATER), new RuneRequirement(1, Rune.LAW)),
        TELEPORT_TO_HOUSE(40, 14286882, true, new RuneRequirement(1, Rune.AIR), new RuneRequirement(1, Rune.EARTH), new RuneRequirement(1, Rune.LAW)),
        CAMELOT_TELEPORT(45, 14286885, true, new RuneRequirement(5, Rune.AIR), new RuneRequirement(1, Rune.LAW)),
        TELEPORT_TO_KOUREND(48, 14286887, true, new RuneRequirement(1, Rune.FIRE), new RuneRequirement(1, Rune.WATER), new RuneRequirement(2, Rune.LAW)),
        ARDOUGNE_TELEPORT(51, 14286892, true, new RuneRequirement(2, Rune.WATER), new RuneRequirement(2, Rune.LAW)),
        TELEPORT_TO_CIVITAS_ILLA_FORTIS(58, 14286894, true, new RuneRequirement(2, Rune.LAW), new RuneRequirement(1, Rune.EARTH), new RuneRequirement(1, Rune.FIRE)),
        WATCHTOWER_TELEPORT(58, 14286898, true, new RuneRequirement(2, Rune.EARTH), new RuneRequirement(2, Rune.LAW)),
        TROLLHEIM_TELEPORT(61, 14286905, true, new RuneRequirement(2, Rune.FIRE), new RuneRequirement(2, Rune.LAW)),
        TELEPORT_TO_APE_ATOLL(64, 14286908, true, new RuneRequirement(2, Rune.FIRE), new RuneRequirement(2, Rune.WATER), new RuneRequirement(2, Rune.LAW)),
        TELEOTHER_LUMBRIDGE(74, 14286915, true, new RuneRequirement(1, Rune.EARTH), new RuneRequirement(1, Rune.LAW), new RuneRequirement(1, Rune.SOUL)),
        TELEOTHER_FALADOR(82, 14286921, true, new RuneRequirement(1, Rune.WATER), new RuneRequirement(1, Rune.LAW), new RuneRequirement(1, Rune.SOUL)),
        TELEPORT_TO_BOUNTY_TARGET(85, 14286924, true, new RuneRequirement(1, Rune.CHAOS), new RuneRequirement(1, Rune.DEATH), new RuneRequirement(1, Rune.LAW)),
        TELEOTHER_CAMELOT(90, 14286885, true, new RuneRequirement(1, Rune.LAW), new RuneRequirement(2, Rune.SOUL)),
        WIND_STRIKE(1, 14286859, false, -1, 1, new RuneRequirement(1, Rune.AIR), new RuneRequirement(1, Rune.MIND)),
        WATER_STRIKE(5, 14286862, false, -1, 2, new RuneRequirement(1, Rune.AIR), new RuneRequirement(1, Rune.WATER), new RuneRequirement(1, Rune.MIND)),
        EARTH_STRIKE(9, 14286865, false, -1, 3, new RuneRequirement(1, Rune.AIR), new RuneRequirement(2, Rune.EARTH), new RuneRequirement(1, Rune.MIND)),
        FIRE_STRIKE(13, 14286867, false, -1, 4, new RuneRequirement(2, Rune.AIR), new RuneRequirement(3, Rune.FIRE), new RuneRequirement(1, Rune.MIND)),
        WIND_BOLT(17, 14286869, false, -1, 5, new RuneRequirement(2, Rune.AIR), new RuneRequirement(1, Rune.CHAOS)),
        WATER_BOLT(23, 14286873, false, -1, 6, new RuneRequirement(2, Rune.AIR), new RuneRequirement(2, Rune.WATER), new RuneRequirement(1, Rune.CHAOS)),
        EARTH_BOLT(29, 14286876, false, -1, 7, new RuneRequirement(2, Rune.AIR), new RuneRequirement(3, Rune.EARTH), new RuneRequirement(1, Rune.CHAOS)),
        FIRE_BOLT(35, 14286879, false, -1, 8, new RuneRequirement(3, Rune.AIR), new RuneRequirement(4, Rune.FIRE), new RuneRequirement(1, Rune.CHAOS)),
        WIND_BLAST(41, 14286883, false, -1, 9, new RuneRequirement(3, Rune.AIR), new RuneRequirement(1, Rune.DEATH)),
        WATER_BLAST(47, 14286886, false, -1, 10, new RuneRequirement(3, Rune.AIR), new RuneRequirement(3, Rune.WATER), new RuneRequirement(1, Rune.DEATH)),
        EARTH_BLAST(53, 14286893, false, -1, 11, new RuneRequirement(3, Rune.AIR), new RuneRequirement(4, Rune.EARTH), new RuneRequirement(1, Rune.DEATH)),
        FIRE_BLAST(59, 14286899, false, -1, 12, new RuneRequirement(4, Rune.AIR), new RuneRequirement(5, Rune.FIRE), new RuneRequirement(1, Rune.DEATH)),
        WIND_WAVE(62, 14286906, true, -1, 13, new RuneRequirement(5, Rune.AIR), new RuneRequirement(1, Rune.BLOOD)),
        WATER_WAVE(65, 14286909, true, -1, 14, new RuneRequirement(5, Rune.AIR), new RuneRequirement(7, Rune.WATER), new RuneRequirement(1, Rune.BLOOD)),
        EARTH_WAVE(70, 14286913, true, -1, 15, new RuneRequirement(5, Rune.AIR), new RuneRequirement(7, Rune.EARTH), new RuneRequirement(1, Rune.BLOOD)),
        FIRE_WAVE(75, 14286916, true, -1, 16, new RuneRequirement(5, Rune.AIR), new RuneRequirement(7, Rune.FIRE), new RuneRequirement(1, Rune.BLOOD)),
        WIND_SURGE(81, 14286920, true, -1, 48, new RuneRequirement(7, Rune.AIR), new RuneRequirement(1, Rune.WRATH)),
        WATER_SURGE(85, 14286922, true, -1, 49, new RuneRequirement(7, Rune.AIR), new RuneRequirement(10, Rune.WATER), new RuneRequirement(1, Rune.WRATH)),
        EARTH_SURGE(90, 14286927, true, -1, 50, new RuneRequirement(7, Rune.AIR), new RuneRequirement(10, Rune.EARTH), new RuneRequirement(1, Rune.WRATH)),
        FIRE_SURGE(95, 14286929, true, -1, 51, new RuneRequirement(7, Rune.AIR), new RuneRequirement(10, Rune.FIRE), new RuneRequirement(1, Rune.WRATH)),
        SARADOMIN_STRIKE(60, 14286902, true, -1, 52, new RuneRequirement(4, Rune.AIR), new RuneRequirement(2, Rune.FIRE), new RuneRequirement(2, Rune.BLOOD)),
        CLAWS_OF_GUTHIX(60, 14286903, true, -1, 19, new RuneRequirement(4, Rune.AIR), new RuneRequirement(1, Rune.FIRE), new RuneRequirement(2, Rune.BLOOD)),
        FLAMES_OF_ZAMORAK(60, 14286904, true, new RuneRequirement(1, Rune.AIR), new RuneRequirement(4, Rune.FIRE), new RuneRequirement(2, Rune.BLOOD)),
        CRUMBLE_UNDEAD(39, 14286881, false, -1, 17, new RuneRequirement(2, Rune.AIR), new RuneRequirement(2, Rune.EARTH), new RuneRequirement(1, Rune.CHAOS)),
        IBAN_BLAST(50, 14286889, true, -1, 47, new RuneRequirement(5, Rune.FIRE), new RuneRequirement(1, Rune.DEATH)),
        MAGIC_DART(50, 14286891, true, -1, 18, new RuneRequirement(1, Rune.DEATH), new RuneRequirement(4, Rune.MIND)),
        CONFUSE(3, 14286860, false, new RuneRequirement(2, Rune.EARTH), new RuneRequirement(3, Rune.WATER), new RuneRequirement(1, Rune.BODY)),
        WEAKEN(11, 14286866, false, new RuneRequirement(2, Rune.EARTH), new RuneRequirement(3, Rune.WATER), new RuneRequirement(1, Rune.BODY)),
        CURSE(19, 14286870, false, new RuneRequirement(3, Rune.EARTH), new RuneRequirement(2, Rune.WATER), new RuneRequirement(1, Rune.BODY)),
        BIND(20, 14286871, false, new RuneRequirement(3, Rune.EARTH), new RuneRequirement(3, Rune.WATER), new RuneRequirement(2, Rune.NATURE)),
        SNARE(50, 14286890, false, new RuneRequirement(4, Rune.EARTH), new RuneRequirement(4, Rune.WATER), new RuneRequirement(3, Rune.NATURE)),
        VULNERABILITY(66, 14286911, true, new RuneRequirement(5, Rune.EARTH), new RuneRequirement(5, Rune.WATER), new RuneRequirement(1, Rune.SOUL)),
        ENFEEBLE(73, 14286914, true, new RuneRequirement(8, Rune.EARTH), new RuneRequirement(8, Rune.WATER), new RuneRequirement(1, Rune.SOUL)),
        ENTANGLE(79, 14286917, true, new RuneRequirement(5, Rune.EARTH), new RuneRequirement(5, Rune.WATER), new RuneRequirement(4, Rune.NATURE)),
        STUN(80, 14286918, true, new RuneRequirement(12, Rune.EARTH), new RuneRequirement(12, Rune.WATER), new RuneRequirement(1, Rune.SOUL)),
        TELE_BLOCK(85, 14286923, false, new RuneRequirement(1, Rune.CHAOS), new RuneRequirement(1, Rune.DEATH), new RuneRequirement(1, Rune.LAW)),
        CHARGE(80, 14286919, true, new RuneRequirement(3, Rune.AIR), new RuneRequirement(3, Rune.FIRE), new RuneRequirement(3, Rune.BLOOD)),
        BONES_TO_BANANAS(15, 14286868, false, new RuneRequirement(2, Rune.EARTH), new RuneRequirement(2, Rune.WATER), new RuneRequirement(1, Rune.NATURE)),
        LOW_LEVEL_ALCHEMY(21, 14286872, false, new RuneRequirement(3, Rune.FIRE), new RuneRequirement(1, Rune.NATURE)),
        SUPERHEAT_ITEM(43, 14286884, false, new RuneRequirement(4, Rune.FIRE), new RuneRequirement(1, Rune.NATURE)),
        HIGH_LEVEL_ALCHEMY(55, 14286895, false, new RuneRequirement(5, Rune.FIRE), new RuneRequirement(1, Rune.NATURE)),
        BONES_TO_PEACHES(60, 14286901, true, new RuneRequirement(2, Rune.EARTH), new RuneRequirement(4, Rune.WATER), new RuneRequirement(2, Rune.NATURE)),
        BOLT_ENCHANT(4, 0xDA000D, true, new RuneRequirement[0]),
        LVL_1_ENCHANT(7, 14286864, false, new RuneRequirement(1, Rune.WATER), new RuneRequirement(1, Rune.COSMIC)),
        LVL_2_ENCHANT(27, 14286875, false, new RuneRequirement(3, Rune.AIR), new RuneRequirement(1, Rune.COSMIC)),
        LVL_3_ENCHANT(49, 14286888, false, new RuneRequirement(5, Rune.FIRE), new RuneRequirement(1, Rune.COSMIC)),
        CHARGE_WATER_ORB(56, 14286896, true, new RuneRequirement(30, Rune.WATER), new RuneRequirement(3, Rune.COSMIC)),
        LVL_4_ENCHANT(57, 14286897, false, new RuneRequirement(10, Rune.EARTH), new RuneRequirement(1, Rune.COSMIC)),
        CHARGE_EARTH_ORB(60, 14286900, true, new RuneRequirement(30, Rune.EARTH), new RuneRequirement(3, Rune.COSMIC)),
        CHARGE_FIRE_ORB(63, 14286907, true, new RuneRequirement(30, Rune.FIRE), new RuneRequirement(3, Rune.COSMIC)),
        CHARGE_AIR_ORB(66, 14286910, true, new RuneRequirement(30, Rune.AIR), new RuneRequirement(3, Rune.COSMIC)),
        LVL_5_ENCHANT(68, 14286912, true, new RuneRequirement(15, Rune.EARTH), new RuneRequirement(15, Rune.WATER), new RuneRequirement(1, Rune.COSMIC)),
        LVL_6_ENCHANT(87, 14286925, true, new RuneRequirement(20, Rune.EARTH), new RuneRequirement(20, Rune.FIRE), new RuneRequirement(1, Rune.COSMIC)),
        LVL_7_ENCHANT(93, 14286928, true, new RuneRequirement(20, Rune.BLOOD), new RuneRequirement(20, Rune.SOUL), new RuneRequirement(1, Rune.COSMIC)),
        TELEKINETIC_GRAB(31, 14286878, false, new RuneRequirement(1, Rune.AIR), new RuneRequirement(1, Rune.LAW)),
        TELE_BOAT_TO_ME(56, 14286930, true, new RuneRequirement(1, Rune.EARTH), new RuneRequirement(1, Rune.WATER), new RuneRequirement(2, Rune.LAW)),
        TELE_ME_TO_BOAT(67, 14286931, true, new RuneRequirement(2, Rune.EARTH), new RuneRequirement(2, Rune.WATER), new RuneRequirement(2, Rune.LAW)),
        MONSTER_INSPECT(42, 14287046, true, new RuneRequirement(2, Rune.MIND), new RuneRequirement(2, Rune.BODY));

        private final int level;
        private final int component;
        private final boolean members;
        private final int menuIdentifier;
        private final int autocastIndex;
        private final Requirements otherRequirements;
        private final RuneRequirement[] requirements;

        private Standard(int level, int component, boolean members, int menuIdentifier, int autocastIndex, Requirements otherRequirements, RuneRequirement ... requirements) {
            this.level = level;
            this.component = component;
            this.members = members;
            this.menuIdentifier = menuIdentifier;
            this.autocastIndex = autocastIndex;
            this.otherRequirements = otherRequirements;
            this.requirements = requirements;
        }

        private Standard(int level, int component, boolean members, int menuIdentifier, Requirements otherRequirements, RuneRequirement ... requirements) {
            this(level, component, members, menuIdentifier, -1, otherRequirements, requirements);
        }

        private Standard(int level, int component, boolean members, Requirements otherRequirements, RuneRequirement ... requirements) {
            this(level, component, members, -1, otherRequirements, requirements);
        }

        private Standard(int level, int component, boolean members, int menuIdentifier, int autocastIndex, RuneRequirement ... requirements) {
            this(level, component, members, menuIdentifier, autocastIndex, new Requirements(), requirements);
        }

        private Standard(int level, int component, boolean members, int menuIdentifier, RuneRequirement ... requirements) {
            this(level, component, members, menuIdentifier, -1, requirements);
        }

        private Standard(int level, int component, boolean members, RuneRequirement ... requirements) {
            this(level, component, members, -1, requirements);
        }

        @Override
        public SpellBook getSpellBook() {
            return STANDARD;
        }

        @Override
        public boolean canCast() {
            IInventory inv;
            if (SpellBook.getCurrent() != STANDARD) {
                return false;
            }
            if (this.members && !Static.getWorlds().inMembersWorld()) {
                return false;
            }
            if (this == HOME_TELEPORT) {
                return !Static.getMagic().isHomeTeleportOnCooldown();
            }
            if (this.level > Static.getSkills().getBoostedLevel(Skill.MAGIC) || this.level > Static.getSkills().getBoostedLevel(Skill.MAGIC)) {
                return false;
            }
            if (!(this != WATCHTOWER_TELEPORT || Static.getQuests().isFinished(Quest.WATCHTOWER) && Static.getVars().getVarp(212) >= 14)) {
                return false;
            }
            if (this == ARDOUGNE_TELEPORT && Static.getVars().getVarp(165) < 30) {
                return false;
            }
            if (this == TROLLHEIM_TELEPORT && !Static.getQuests().isFinished(Quest.EADGARS_RUSE)) {
                return false;
            }
            if (this == TELEPORT_TO_CIVITAS_ILLA_FORTIS && !Static.getQuests().isFinished(Quest.TWILIGHTS_PROMISE)) {
                return false;
            }
            if (this == TELEPORT_TO_KOUREND && !Static.getQuests().isFinished(Quest.CLIENT_OF_KOUREND)) {
                return false;
            }
            if ((this == EARTH_SURGE || this == FIRE_SURGE || this == WATER_SURGE || this == WIND_SURGE || this == EARTH_WAVE || this == FIRE_WAVE || this == WATER_WAVE || this == WIND_WAVE) && (inv = Static.getInventory()).contains(26705)) {
                return true;
            }
            return this.haveEquipment() && this.haveItem() && this.haveRunesAvailable() && this.otherRequirements.fulfilled();
        }

        public boolean haveRunesAvailable() {
            for (RuneRequirement req : this.requirements) {
                if (req.meetsRequirements()) continue;
                return false;
            }
            return true;
        }

        public boolean haveEquipment() {
            switch (this) {
                case IBAN_BLAST: {
                    return Static.getEquipment().contains(1409, 1410, 12658);
                }
                case MAGIC_DART: {
                    return Static.getEquipment().contains(21255, 4170, 11791, 23613, 12904, 22296, 24144);
                }
                case SARADOMIN_STRIKE: {
                    return Static.getEquipment().contains(2415, 22296);
                }
                case FLAMES_OF_ZAMORAK: {
                    return Static.getEquipment().contains(2417, 11791, 23613, 12904);
                }
                case CLAWS_OF_GUTHIX: {
                    return Static.getEquipment().contains(2416, 8841, 24144);
                }
            }
            return true;
        }

        public boolean haveItem() {
            switch (this) {
                case TELEPORT_TO_APE_ATOLL: {
                    return Static.getInventory().contains(1963);
                }
                case CHARGE_AIR_ORB: 
                case CHARGE_WATER_ORB: 
                case CHARGE_EARTH_ORB: 
                case CHARGE_FIRE_ORB: {
                    return Static.getInventory().contains(567);
                }
            }
            return true;
        }

        @Override
        public int getLevel() {
            return this.level;
        }

        @Override
        public int getComponent() {
            return this.component;
        }

        public boolean isMembers() {
            return this.members;
        }

        @Override
        public int getMenuIdentifier() {
            return this.menuIdentifier;
        }

        @Override
        public int getAutocastIndex() {
            return this.autocastIndex;
        }

        public Requirements getOtherRequirements() {
            return this.otherRequirements;
        }

        @Override
        public RuneRequirement[] getRequirements() {
            return this.requirements;
        }
    }
}

