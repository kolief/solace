package net.solace.sdk.movement.pathfinder;

import net.solace.api.Static;
import net.solace.api.movement.pathfinder.IChargeManager;
import net.solace.api.movement.pathfinder.model.requirement.charges.ChargeRequirement;
import net.solace.api.movement.pathfinder.model.requirement.charges.UnlockRequirement;

public class ChargeManager {
    private static final IChargeManager CHARGE_MANAGER = Static.getChargeManager();

    public static int getCharges(ChargeRequirement requirement) {
        return CHARGE_MANAGER.getCharges(requirement);
    }

    public static boolean hasCharges(ChargeRequirement requirement) {
        return CHARGE_MANAGER.hasCharges(requirement);
    }

    public static void setCharges(ChargeRequirement requirement, int charges) {
        CHARGE_MANAGER.setCharges(requirement, charges);
    }

    public static boolean isUnlocked(UnlockRequirement requirement) {
        return CHARGE_MANAGER.isUnlocked(requirement);
    }

    public static void setUnlocked(UnlockRequirement requirement, boolean unlocked) {
        CHARGE_MANAGER.setUnlocked(requirement, unlocked);
    }

    public static void registerChargeRequirement(ChargeRequirement requirement) {
        CHARGE_MANAGER.registerChargeRequirement(requirement);
    }

    public static void registerUnlockRequirement(UnlockRequirement requirement) {
        CHARGE_MANAGER.registerUnlockRequirement(requirement);
    }

    public static void unregisterChargeRequirement(ChargeRequirement requirement) {
        CHARGE_MANAGER.unregisterChargeRequirement(requirement);
    }

    public static void unregisterUnlockRequirement(UnlockRequirement requirement) {
        CHARGE_MANAGER.unregisterUnlockRequirement(requirement);
    }
}

