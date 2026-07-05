package net.solace.loader.plugins.cannonballer.tasks;

import net.runelite.api.gameval.ItemID;
import net.solace.api.Static;
import net.solace.api.plugins.PluginTask;
import net.solace.loader.plugins.cannonballer.SolaceCannonballerPlugin;
import net.solace.sdk.entities.Players;
import net.solace.sdk.items.Inventory;
import net.solace.sdk.movement.Movement;
import net.solace.sdk.widgets.Production;

import java.util.List;

public class SmeltBalls extends PluginTask<SolaceCannonballerPlugin> {
    private static final List<Integer> SMELT_ANIMATIONS = List.of(827, 899, 832);

    public SmeltBalls(SolaceCannonballerPlugin context) {
        super(context);
    }

    private int lastAnim = 0;

    @Override
    public boolean validate() {
        return Inventory.contains(ItemID.STEEL_BAR) && Inventory.contains(getContext().getConfig().mould().getItemId());
    }

    @Override
    public int execute() {
        var animation = Players.getLocal().getAnimation();
        if (SMELT_ANIMATIONS.contains(animation)) {
            lastAnim = Static.getClient().getTickCount();
        }

        if (Inventory.contains(ItemID.STEEL_BAR) && lastAnim + 6 > Static.getClient().getTickCount()) {
            return -2;
        }

        if (Production.isOpen()) {
            Production.selectItem("Steel cannonball");
            return -2;
        }

        if (getContext().breakHandler.shouldBreak(getContext())) {
            getContext().breakHandler.startBreak(getContext());
            return -1;
        }

        var selectedFurnace = getContext().getSelectedFurnace();
        var furnace = selectedFurnace.get();
        var isCloseToFurnace = furnace != null && selectedFurnace.getWorldPoint().distanceTo(Players.getLocal().getWorldLocation()) < 20;
        if (furnace == null || !isCloseToFurnace) {
            if (Movement.isWalking()) {
                return -2;
            }

            Movement.walkTo(selectedFurnace.getWorldPoint());
            return -4;
        }

        if (Players.getLocal().isMoving()) {
            return -2;
        }

        furnace.interact("Smelt");
        return -4;
    }

    @Override
    public boolean subscribe() {
        return true;
    }
}
