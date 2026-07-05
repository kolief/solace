package net.solace.impl.movement.pathfinder;

import net.solace.api.movement.pathfinder.IChargeManager;
import net.solace.api.movement.pathfinder.model.requirement.charges.ChargeRequirement;
import net.solace.api.movement.pathfinder.model.requirement.charges.UnlockRequirement;

public class ChargeManagerImpl implements IChargeManager {
    @Override
    public int getCharges(ChargeRequirement requirement) {
        return -1;
    }

    @Override
    public boolean hasCharges(ChargeRequirement requirement) {
        return false;
    }

    @Override
    public void setCharges(ChargeRequirement requirement, int charges) {
    }

    @Override
    public boolean isUnlocked(UnlockRequirement requirement) {
        return false;
    }

    @Override
    public void setUnlocked(UnlockRequirement requirement, boolean unlocked) {
    }

    @Override
    public void registerChargeRequirement(ChargeRequirement requirement) {
    }

    @Override
    public void registerUnlockRequirement(UnlockRequirement requirement) {
    }

    @Override
    public void unregisterChargeRequirement(ChargeRequirement requirement) {
    }

    @Override
    public void unregisterUnlockRequirement(UnlockRequirement requirement) {
    }
}
