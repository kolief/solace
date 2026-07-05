package net.solace.loader.plugins.wintertodt.tasks;

import lombok.extern.slf4j.Slf4j;
import net.solace.api.items.WithdrawMode;
import net.solace.loader.plugins.wintertodt.SolaceWintertodtPlugin;
import net.solace.loader.plugins.wintertodt.WintertodtConstants;
import net.solace.sdk.commons.Time;
import net.solace.sdk.entities.Players;
import net.solace.sdk.entities.TileObjects;
import net.solace.sdk.items.Bank;
import net.solace.sdk.items.Inventory;
import net.solace.sdk.movement.Movement;
import net.solace.sdk.utils.MessageUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class Banking extends WintertodtTask {
    public Banking(SolaceWintertodtPlugin context) {
        super(context);
    }

    @Override
    public boolean validate() {
        return !isInside() && (!getRequiredItems().isEmpty() || Inventory.getFreeSlots() <= getConfig().minInvSpace());
    }

    @Override
    public int execute() {
        if (Bank.isOpen()) {
            var unneeded = Inventory.getAll(item -> !getInventorySetup().contains(item.getName()));
            if (!unneeded.isEmpty()) {
                for (var item : unneeded) {
                    Bank.depositAll(item.getId());
                    Time.sleep(100);
                }

                return -2;
            }

            for (Map.Entry<String, Integer> entry : getRequiredItems().entrySet()) {
                String itemName = entry.getKey();
                int requiredCount = entry.getValue();
                if (requiredCount < 0) {
                    log.info("Depositing {} {}s", Math.abs(requiredCount), itemName);
                    Bank.deposit(itemName, Math.abs(requiredCount));
                } else {
                    log.info("Withdrawing {} {}s", requiredCount, itemName);
                    var i = Bank.getFirst(itemName);
                    if (i == null || i.isPlaceholder() || i.getQuantity() < requiredCount) {
                        MessageUtils.addMessage(String.format("Missing [%s]x%d", itemName, requiredCount));

                    }
                    Bank.withdraw(itemName, requiredCount, WithdrawMode.ITEM);
                }

                Time.sleep(100);
            }

            return -2;
        }

        if (Movement.isWalking()) {
            return -2;
        }

        var chest = TileObjects.getFirstAt(WintertodtConstants.BANK_COORD, WintertodtConstants.BANK_CHEST_ID);
        if (chest == null || !chest.isInteractable(Players.getLocal())) {
            Movement.walkTo(WintertodtConstants.BANK_COORD.dx(-1));
            return -3;
        }

        chest.interact("Bank");
        return -5;
    }

    private List<String> getInventorySetup() {
        List<String> items = new ArrayList<>();
        items.add(getConfig().axe());
        items.add(getFoodName());

        if (getConfig().fletch()) {
            items.add("Knife");
        }

        if (getConfig().repair()) {
            items.add("Hammer");
        }

        if (canUseTorch()) {
            items.add("Bruma torch");
        } else {
            items.add("Tinderbox");
        }

        return items;
    }

    @Override
    public String toString() {
        return "Banking";
    }
}
