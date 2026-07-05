package net.solace.api.movement.pathfinder.model.sailing;

import net.solace.api.Static;
import net.solace.api.movement.pathfinder.model.sailing.SailingDock;

public enum SailingBoatSlot {
    ONE(1, 19258, 19259, 19260, 19270),
    TWO(2, 19296, 19297, 19298, 19308),
    THREE(3, 19334, 19335, 19336, 19346),
    FOUR(4, 19372, 19373, 19374, 19384),
    FIVE(5, 19410, 19411, 19412, 19422);

    public static final int UNOWNED_TYPE = -1;
    public static final int TELEPORT_FOCUS = 1;
    public static final int GREATER_TELEPORT_FOCUS = 2;
    private final int slot;
    private final int ownedVarbit;
    private final int typeVarbit;
    private final int portVarbit;
    private final int teleportFocusVarbit;

    public boolean isOwned() {
        return Static.getVars().getBit(this.ownedVarbit) == 1;
    }

    public int getType() {
        return Static.getVars().getBit(this.typeVarbit);
    }

    public boolean hasType() {
        return this.getType() != -1;
    }

    public int getPortId() {
        return Static.getVars().getBit(this.portVarbit);
    }

    public SailingDock getDock() {
        return SailingDock.fromId(this.getPortId());
    }

    public int getTeleportFocus() {
        return Static.getVars().getBit(this.teleportFocusVarbit);
    }

    public boolean hasTeleportFocus() {
        return this.getTeleportFocus() >= 1;
    }

    public boolean hasGreaterTeleportFocus() {
        return this.getTeleportFocus() >= 2;
    }

    public boolean canTeleportBoatToPlayer() {
        return this.isOwned() && this.hasType() && this.hasTeleportFocus() && this.getDock() != null;
    }

    public boolean canTeleportPlayerToBoat() {
        return this.isOwned() && this.hasType() && this.hasGreaterTeleportFocus() && this.getDock() != null;
    }

    public boolean isAtDock(SailingDock dock) {
        return dock != null && this.getPortId() == dock.getId();
    }

    public static SailingBoatSlot fromSlot(int slot) {
        for (SailingBoatSlot boatSlot : SailingBoatSlot.values()) {
            if (boatSlot.slot != slot) continue;
            return boatSlot;
        }
        return null;
    }

    public int getSlot() {
        return this.slot;
    }

    public int getOwnedVarbit() {
        return this.ownedVarbit;
    }

    public int getTypeVarbit() {
        return this.typeVarbit;
    }

    public int getPortVarbit() {
        return this.portVarbit;
    }

    public int getTeleportFocusVarbit() {
        return this.teleportFocusVarbit;
    }

    private SailingBoatSlot(int slot, int ownedVarbit, int typeVarbit, int portVarbit, int teleportFocusVarbit) {
        this.slot = slot;
        this.ownedVarbit = ownedVarbit;
        this.typeVarbit = typeVarbit;
        this.portVarbit = portVarbit;
        this.teleportFocusVarbit = teleportFocusVarbit;
    }
}

