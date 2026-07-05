package net.solace.sdk.game;

import java.util.function.Predicate;
import net.runelite.api.coords.WorldPoint;
import net.solace.api.Static;
import net.solace.api.domain.actors.INPC;
import net.solace.api.game.AttackStyle;
import net.solace.api.game.ICombat;
import net.solace.api.util.WeaponStyle;

public class Combat {
    private static final ICombat COMBAT = Static.getCombat();

    public static boolean isPoisoned() {
        return COMBAT.isPoisoned();
    }

    public static boolean isVenomed() {
        return COMBAT.isVenomed();
    }

    public static boolean isAntiPoisoned() {
        return COMBAT.isAntiPoisoned();
    }

    public static boolean isAntiVenomed() {
        return COMBAT.isAntiVenomed();
    }

    public static int getSpecEnergy() {
        return COMBAT.getSpecEnergy();
    }

    public static int getMissingHealth() {
        return COMBAT.getMissingHealth();
    }

    public static boolean isRetaliating() {
        return COMBAT.isRetaliating();
    }

    public static boolean isSpecEnabled() {
        return COMBAT.isSpecEnabled();
    }

    public static int getAxeEnergy() {
        return COMBAT.getAxeEnergy();
    }

    public static boolean isAntifired() {
        return COMBAT.isAntifired();
    }

    public static boolean isSuperAntifired() {
        return COMBAT.isSuperAntifired();
    }

    public static void toggleAutoRetaliate() {
        COMBAT.toggleAutoRetaliate();
    }

    public static void toggleAutoRetaliate(Boolean active) {
        COMBAT.toggleAutoRetaliate(active);
    }

    public static void toggleSpec() {
        COMBAT.toggleSpec();
    }

    public static AttackStyle getAttackStyle() {
        return COMBAT.getAttackStyle();
    }

    public static void setAttackStyle(AttackStyle attackStyle) {
        COMBAT.setAttackStyle(attackStyle);
    }

    public static INPC getAttackableNPC(int ... ids) {
        return COMBAT.getAttackableNPC(ids);
    }

    public static INPC getAttackableNPC(String ... names) {
        return COMBAT.getAttackableNPC(names);
    }

    public static INPC getAttackableNPC(Predicate<INPC> filter) {
        return COMBAT.getAttackableNPC(filter);
    }

    public static int getCurrentHealth() {
        return COMBAT.getCurrentHealth();
    }

    public static double getHealthPercent() {
        return COMBAT.getHealthPercent();
    }

    public static WorldPoint getTombWorldPoint() {
        return COMBAT.getTombWorldPoint();
    }

    public static WorldPoint getCannonWorldPoint() {
        return COMBAT.getCannonWorldPoint();
    }

    public static WeaponStyle getCurrentWeaponStyle() {
        return COMBAT.getCurrentWeaponStyle();
    }
}

