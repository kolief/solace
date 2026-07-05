package net.solace.api.movement.pathfinder;

import net.solace.api.movement.pathfinder.model.requirement.charges.ChargeRequirement;
import net.solace.api.movement.pathfinder.model.requirement.charges.UnlockRequirement;

public interface IChargeManager {
    public int getCharges(ChargeRequirement var1);

    public boolean hasCharges(ChargeRequirement var1);

    public void setCharges(ChargeRequirement var1, int var2);

    public boolean isUnlocked(UnlockRequirement var1);

    public void setUnlocked(UnlockRequirement var1, boolean var2);

    public void registerChargeRequirement(ChargeRequirement var1);

    public void registerUnlockRequirement(UnlockRequirement var1);

    public void unregisterChargeRequirement(ChargeRequirement var1);

    public void unregisterUnlockRequirement(UnlockRequirement var1);
}

