package net.solace.impl.widgets;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Prayer;
import net.runelite.api.Skill;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.VarbitID;
import net.solace.api.Static;
import net.solace.api.domain.game.IClientThread;
import net.solace.api.domain.widgets.IWidget;
import net.solace.api.game.ISkills;
import net.solace.api.game.IVars;
import net.solace.api.interact.InteractMethod;
import net.solace.api.movement.pathfinder.model.requirement.Comparison;
import net.solace.api.movement.pathfinder.model.requirement.Requirements;
import net.solace.api.movement.pathfinder.model.requirement.SkillRequirement;
import net.solace.api.movement.pathfinder.model.requirement.VarRequirement;
import net.solace.api.movement.pathfinder.model.requirement.VarType;
import net.solace.api.prayer.PrayerInfo;
import net.solace.api.widgets.IPrayers;
import net.solace.api.widgets.IWidgets;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class PrayersImpl implements IPrayers {
    private static final int RIGOUR_UNLOCKED = 5451;
    private static final int CAMELOT_TRAINING_ROOM_STATUS = 3909;
    private static final int AUGURY_UNLOCKED = 5452;
    private static final int PRESERVE_UNLOCKED = 5453;

    private static final Set<PrayerMap> MELEE_OFFENIVE_PRAYERS = Set.of(PrayerMap.BURST_OF_STRENGTH, PrayerMap.SUPERHUMAN_STRENGTH, PrayerMap.ULTIMATE_STRENGTH, PrayerMap.CHIVALRY, PrayerMap.PIETY);
    private static final Set<PrayerMap> RANGE_OFFENIVE_PRAYERS = Set.of(PrayerMap.SHARP_EYE, PrayerMap.HAWK_EYE, PrayerMap.EAGLE_EYE, PrayerMap.DEADEYE, PrayerMap.RIGOUR);
    private static final Set<PrayerMap> MAGE_OFFENIVE_PRAYERS = Set.of(PrayerMap.MYSTIC_WILL, PrayerMap.MYSTIC_LORE, PrayerMap.MYSTIC_MIGHT, PrayerMap.MYSTIC_VIGOUR, PrayerMap.AUGURY);

    private final ISkills skills;
    private final IWidgets widgets;
    private final IVars vars;
    private final IClientThread clientThread;

    @Override
    public boolean isEnabled(Prayer prayer) {
        if (!canUse(prayer)) {
            return false;
        }

        return vars.getBit(prayer.getVarbit()) == 1;
    }

    @Override
    public void toggle(InteractMethod interactMethod, Prayer prayer) {
        var prayerInfo = PrayerInfo.MAP.get(prayer);
        if (prayerInfo == null) {
            return;
        }

        var widget = widgets.get(prayerInfo.getInterfaceAddress());
        if (widget != null) {
            widget.interact(interactMethod, 0);
        }
    }

    @Override
    public int getPoints() {
        return skills.getBoostedLevel(Skill.PRAYER);
    }

    @Override
    public int getMissingPoints() {
        return Math.max(0, skills.getLevel(Skill.PRAYER) - skills.getBoostedLevel(Skill.PRAYER));
    }

    @Override
    public void toggleQuickPrayer(InteractMethod interactMethod, boolean enabled) {
        if (enabled && isQuickPrayerEnabled()) {
            return;
        }

        var widget = widgets.get(InterfaceID.Orbs.PRAYERBUTTON);
        if (widget != null) {
            widget.interact(interactMethod, enabled ? "Activate" : "Deactivate");
        }
    }

    @Override
    public void toggleQuickPrayer(InteractMethod interactMethod) {
        var widget = widgets.get(InterfaceID.Orbs.PRAYERBUTTON);
        if (widget != null) {
            widget.interact(interactMethod, 0);
        }
    }

    @Override
    public boolean isQuickPrayerEnabled() {
        return vars.getBit(VarbitID.QUICKPRAYER_ACTIVE) == 1;
    }

    @Override
    public boolean anyActive() {
        return clientThread.invokeAndWait(() -> Arrays.stream(Prayer.values()).anyMatch(this::isEnabled));
    }

    @Override
    public void disableAll(InteractMethod interactMethod) {
        clientThread.invoke(() -> Arrays.stream(Prayer.values()).filter(this::isEnabled).forEach(prayer -> toggle(interactMethod, prayer)));
    }

    @Override
    public boolean canUse(Prayer prayer) {
        return Arrays.stream(PrayerMap.values())
                .filter(prayerMap -> prayerMap.prayer == prayer)
                .findFirst()
                .map(PrayerMap::canUse)
                .orElse(false);
    }

    @Override
    public Prayer getBestRangeOffensive() {
        var bestPrayer = RANGE_OFFENIVE_PRAYERS
                .stream()
                .filter(PrayerMap::canUse)
                .max(Comparator.comparingInt(PrayerMap::getRequiredLevel))
                .orElse(null);

        return bestPrayer != null ? bestPrayer.getPrayer() : null;
    }

    @Override
    public Prayer getBestMageOffensive() {
        var bestPrayer = MAGE_OFFENIVE_PRAYERS
                .stream()
                .filter(PrayerMap::canUse)
                .max(Comparator.comparingInt(PrayerMap::getRequiredLevel))
                .orElse(null);

        return bestPrayer != null ? bestPrayer.getPrayer() : null;
    }

    @Override
    public Prayer getBestMeleeOffensive() {
        var bestPrayer = MELEE_OFFENIVE_PRAYERS
                .stream()
                .filter(PrayerMap::canUse)
                .max(Comparator.comparingInt(PrayerMap::getRequiredLevel))
                .orElse(null);

        return bestPrayer != null ? bestPrayer.getPrayer() : null;
    }

    @Override
    public boolean isQuickPrayerOpen() {
        var prayerInterface = widgets.get(InterfaceID.QUICKPRAYER, 4);
        return prayerInterface != null;
    }

    @Override
    public boolean openQuickPrayer() {
        var widget = widgets.get(InterfaceID.Orbs.PRAYERBUTTON);

        if (widget == null) {
            return false;
        }

        if (isQuickPrayerOpen()) {
            return true;
        }

        widget.interact("Setup");
        return true;
    }

    @Override
    public List<Prayer> getSelectedQuickPrayers() {
        return Arrays.stream(PrayerMap.values())
                .filter(PrayerMap::isQuickPrayerSelected)
                .map(PrayerMap::getPrayer)
                .collect(Collectors.toList());
    }

    @Override
    public List<Prayer> getActiveQuickPrayers() {
        return Arrays.stream(PrayerMap.values())
                .filter(PrayerMap::isQuickPrayerActive)
                .map(PrayerMap::getPrayer)
                .collect(Collectors.toList());
    }

    @Override
    public boolean setQuickPrayers(List<Prayer> prayers, boolean closeInterface) {
        final var selectedPrayers = getSelectedQuickPrayers();

        if (prayers.size() == selectedPrayers.size() && new HashSet<>(selectedPrayers).containsAll(prayers)) {
            if (!isQuickPrayerOpen() || !closeInterface) {
                return true;
            }

            final var done = widgets.get(InterfaceID.QUICKPRAYER, 5);

            if (done == null) {
                return true;
            }

            done.interact("Done");
            return true;
        }

        if (!isQuickPrayerOpen()) {
            openQuickPrayer();
            return false;
        }

        togglePrayers(prayers, selectedPrayers, false);
        togglePrayers(prayers, selectedPrayers, true);

        return false;
    }


    private void togglePrayers(List<Prayer> prayers, List<Prayer> selectedPrayers, boolean enabling) {
        for (var prayerMap : PrayerMap.values()) {
            var shouldBeSelected = prayers.contains(prayerMap.getPrayer());
            var isCurrentlySelected = selectedPrayers.contains(prayerMap.getPrayer());

            boolean shouldToggle = enabling ?
                    (shouldBeSelected && !isCurrentlySelected) :
                    (!shouldBeSelected && isCurrentlySelected);

            if (shouldToggle) {
                var widget = prayerMap.getWidget();
                if (widget == null) {
                    log.warn("Prayer widget for {} is null, cannot toggle quick prayer.", prayerMap.getPrayer());
                    continue;
                }

                widget.interact("Toggle");
            }
        }
    }

    private enum PrayerMap {
        THICK_SKIN(Prayer.THICK_SKIN, 0,1, 1L),
        BURST_OF_STRENGTH(Prayer.BURST_OF_STRENGTH, 1, 4, 2L),
        CLARITY_OF_THOUGHT(Prayer.CLARITY_OF_THOUGHT, 2, 7, 4L),
        ROCK_SKIN(Prayer.ROCK_SKIN, 3, 10, 8L),
        SUPERHUMAN_STRENGTH(Prayer.SUPERHUMAN_STRENGTH, 4, 13, 16L),
        IMPROVED_REFLEXES(Prayer.IMPROVED_REFLEXES, 5, 16, 32L),
        RAPID_RESTORE(Prayer.RAPID_RESTORE, 6, 19, 64L),
        RAPID_HEAL(Prayer.RAPID_HEAL, 7, 22, 128L),
        PROTECT_ITEM(Prayer.PROTECT_ITEM, 8, 25, 256L),
        STEEL_SKIN(Prayer.STEEL_SKIN, 9, 28, 512L),
        ULTIMATE_STRENGTH(Prayer.ULTIMATE_STRENGTH, 10, 31, 1024L),
        INCREDIBLE_REFLEXES(Prayer.INCREDIBLE_REFLEXES, 11, 34, 2048L),
        PROTECT_FROM_MAGIC(Prayer.PROTECT_FROM_MAGIC, 12, 37, 4096L),
        PROTECT_FROM_MISSILES(Prayer.PROTECT_FROM_MISSILES, 13, 40, 8192L),
        PROTECT_FROM_MELEE(Prayer.PROTECT_FROM_MELEE, 14, 43, 16384L),
        RETRIBUTION(Prayer.RETRIBUTION, 15, 46, 32768L),
        REDEMPTION(Prayer.REDEMPTION, 16, 49, 65536L),
        SMITE(Prayer.SMITE, 17, 52, 131072L),
        SHARP_EYE(Prayer.SHARP_EYE, 18, 8, 262144L),
        MYSTIC_WILL(Prayer.MYSTIC_WILL, 19, 9, 524288L),
        HAWK_EYE(Prayer.HAWK_EYE, 20, 26, 1048576L),
        MYSTIC_LORE(Prayer.MYSTIC_LORE, 21, 27, 2097152L),
        EAGLE_EYE(Prayer.EAGLE_EYE, 22, 44, 4194304L, Requirements.of(new VarRequirement(Comparison.EQUAL, VarType.VARBIT, VarbitID.PRAYER_DEADEYE_UNLOCKED, 0))),
        DEADEYE(Prayer.DEADEYE, 22, 62, 4194304L, Requirements.of(new VarRequirement(Comparison.GREATER_THAN_EQUAL, VarType.VARBIT, VarbitID.PRAYER_DEADEYE_UNLOCKED, 1))),
        MYSTIC_MIGHT(Prayer.MYSTIC_MIGHT, 23, 45, 8388608L, Requirements.of(new VarRequirement(Comparison.EQUAL, VarType.VARBIT, VarbitID.PRAYER_MYSTIC_VIGOUR_UNLOCKED, 0))),
        MYSTIC_VIGOUR(Prayer.MYSTIC_VIGOUR, 23, 63, 8388608, Requirements.of(new VarRequirement(Comparison.GREATER_THAN_EQUAL, VarType.VARBIT, VarbitID.PRAYER_MYSTIC_VIGOUR_UNLOCKED, 1))),
        RIGOUR(Prayer.RIGOUR, 24, 74, 16777216L, Requirements.of(new SkillRequirement(Skill.DEFENCE, 70, false), new VarRequirement(Comparison.GREATER_THAN_EQUAL, VarType.VARBIT, RIGOUR_UNLOCKED, 1))),
        CHIVALRY(Prayer.CHIVALRY, 25, 60, 33554432L, Requirements.of(new SkillRequirement(Skill.DEFENCE, 65, false), new VarRequirement(Comparison.GREATER_THAN_EQUAL, VarType.VARBIT, CAMELOT_TRAINING_ROOM_STATUS, 8))),
        PIETY(Prayer.PIETY, 26, 70, 67108864L, Requirements.of(new SkillRequirement(Skill.DEFENCE, 70, false), new VarRequirement(Comparison.GREATER_THAN_EQUAL, VarType.VARBIT, CAMELOT_TRAINING_ROOM_STATUS, 8))),
        AUGURY(Prayer.AUGURY, 27, 77, 134217728L, Requirements.of(new SkillRequirement(Skill.DEFENCE, 70, false), new VarRequirement(Comparison.GREATER_THAN_EQUAL, VarType.VARBIT, AUGURY_UNLOCKED, 1))),
        PRESERVE(Prayer.PRESERVE, 28, 55, 268435456L, Requirements.of(new VarRequirement(Comparison.GREATER_THAN_EQUAL, VarType.VARBIT, PRESERVE_UNLOCKED, 1)));

        @Getter
        private final Prayer prayer;
        @Getter
        private final int index;
        @Getter
        private final int requiredLevel;
        @Getter
        private final long bitValue;
        private final Requirements requirements;

        PrayerMap(Prayer prayer, int index, int requiredLevel, long bitValue) {
            this.prayer = prayer;
            this.index = index;
            this.requiredLevel = requiredLevel;
            this.bitValue = bitValue;
            this.requirements = Requirements.of();
        }

        PrayerMap(Prayer prayer, int index, int requiredLevel, long bitValue, Requirements requirements) {
            this.prayer = prayer;
            this.index = index;
            this.requiredLevel = requiredLevel;
            this.bitValue = bitValue;
            this.requirements = requirements;
        }

        public boolean canUse() {
            if (Static.getVars().getBit(VarbitID.BR_INGAME) == 1) {
                if (this == PrayerMap.MYSTIC_VIGOUR || this == PrayerMap.DEADEYE) {
                    return false;
                }

                if (this == PrayerMap.RIGOUR || this == PrayerMap.AUGURY || this == PrayerMap.PIETY || this == PrayerMap.CHIVALRY) {
                    return Static.getSkills().getBoostedLevel(Skill.DEFENCE) >= 75;
                }

                return true;
            }

            return Static.getSkills().getLevel(Skill.PRAYER) >= requiredLevel && requirements.fulfilled();
        }

        public boolean isQuickPrayerSelected() {
            return (Static.getVars().getBit(4102) & bitValue) != 0;
        }

        public boolean isQuickPrayerActive() {
            return (Static.getVars().getBit(4101) & bitValue) != 0;
        }

        public IWidget getWidget() {
            var prayerInterface = Static.getWidgets().get(InterfaceID.QUICKPRAYER, 4);

            if (prayerInterface == null) {
                return null;
            }

            var index = this.getIndex();
            var children = prayerInterface.getDynamicChildren();

            if (children == null || index >= children.length) {
                return null;
            }

            return children[index];
        }
    }
}
