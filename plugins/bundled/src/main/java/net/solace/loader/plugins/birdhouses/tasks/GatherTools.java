package net.solace.loader.plugins.birdhouses.tasks;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.gameval.ItemID;
import net.solace.api.domain.items.IItem;
import net.solace.api.items.WithdrawMode;
import net.solace.loader.plugins.birdhouses.SolaceBirdHousesPlugin;
import net.solace.loader.plugins.birdhouses.model.BirdHouseState;
import net.solace.loader.plugins.birdhouses.SolaceBirdHousesConfig;
import net.solace.sdk.entities.Players;
import net.solace.sdk.entities.TileObjects;
import net.solace.sdk.items.Bank;
import net.solace.sdk.items.Inventory;
import net.solace.sdk.movement.Movement;
import net.solace.sdk.movement.Reachable;

import javax.inject.Inject;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class GatherTools extends BirdHouseTask {
    @Inject
    private SolaceBirdHousesConfig config;

    private final int[] staminaPotionIDs = new int[]{
            ItemID._1DOSESTAMINA,
            ItemID._2DOSESTAMINA,
            ItemID._3DOSESTAMINA,
            ItemID._4DOSESTAMINA
    };

    public GatherTools(SolaceBirdHousesPlugin context) {
        super(context);
    }

    @Override
    public boolean validate() {
        return Inventory.isFull() || !getRequiredItems().isEmpty();
    }

    @Override
    public int execute() {
        if (Movement.isWalking()) {
            return -1;
        }

        if (Bank.isOpen()) {
            var unneededItem = Inventory.getFirst(item -> !getTools().contains(item.getId())
                    && getAllowedItems().getOrDefault(item.getId(), 0) < Inventory.getCount(true, item.getId()));
            if (unneededItem != null) {
                Bank.depositAll(unneededItem.getId());
                return -2;
            }

            if (config.drinkStamina()) {
                if (staminaInBankAndNotInventory() && notBoostedWithLowEnergy()) {
                    withdrawStamina();
                    return -2;
                }

                if (staminaInInventoryAndLowEnergy()) {
                    Bank.Inventory.getFirst(staminaPotionIDs).interact("Drink");
                    return -2;
                }

                if (staminaInInventoryAndBoosted()) {
                    Bank.depositAll(staminaPotionIDs);
                    return -2;
                }
            }

            for (Map.Entry<Integer, Integer> entry : getRequiredItems().entrySet()) {
                int itemId = entry.getKey();
                int quantity = entry.getValue();
                Bank.withdraw(itemId, quantity, WithdrawMode.ITEM);
                return -3;
            }
            return -1;
        }

        var local = Players.getLocal();
        if (local.distanceTo(FOSSIL_ISLAND_CHEST_POINT) > 10) {
            Movement.walkTo(FOSSIL_ISLAND_CHEST_POINT.dx(-1));
            return -3;
        }

        var chest = TileObjects.getFirstAt(FOSSIL_ISLAND_CHEST_POINT, obj -> obj.hasAction("Collect"));
        if (chest == null) {
            printMessage("Bank chest not found, is it unlocked?");
            return -10;
        }

        if (!Reachable.isInteractable(chest)) {
            Movement.walkTo(FOSSIL_ISLAND_CHEST_POINT.dx(-1));
            return -3;
        }

        chest.interact("Use");
        return -3;
    }

    private Map<Integer, Integer> getAllowedItems() {
        return Map.of(
                ItemID._1DOSESTAMINA, 1,
                ItemID._2DOSESTAMINA, 1,
                ItemID._3DOSESTAMINA, 1,
                ItemID._4DOSESTAMINA, 1,
                ItemID.POH_CLOCKWORK_MECHANISM, 4,
                config.type().getItemId(), 4,
                config.type().getLogItemId(), 4,
                config.seedType().getItemId(), config.seedType().getQuantity() * 4
        );
    }

    private Map<Integer, Integer> getRequiredItems() {
        Map<Integer, Integer> out = new HashMap<>();

        int logs = getRequiredLogs();
        if (logs > 0) {
            out.put(config.type().getLogItemId(), logs);
        }

        int seeds = getRequiredSeeds();
        if (seeds > 0) {
            out.put(config.seedType().getItemId(), seeds);
        }

        for (Integer toolId : getTools()) {
            if (!Inventory.contains(toolId)) {
                out.put(toolId, 1);
            }
        }

        return out;
    }

    private int getRequiredLogs() {
        return (int) getBirdHouses().stream()
                .filter(birdHouse -> (birdHouse.getState() != BirdHouseState.SEEDED
                        && birdHouse.getState() != BirdHouseState.BUILT)
                        || birdHouse.isComplete())
                .count()
                - Inventory.getCount(config.type().getLogItemId())
                - Inventory.getCount(config.type().getItemId());
    }

    private int getRequiredSeeds() {
        return (int) (getBirdHouses().stream()
                .filter(birdHouse -> birdHouse.getState() != BirdHouseState.SEEDED || birdHouse.isComplete())
                .count() * config.seedType().getQuantity())
                - Inventory.getCount(true, config.seedType().getItemId());
    }

    @Override
    public boolean inject() {
        return true;
    }

    private boolean notBoostedWithLowEnergy() {
        return !Movement.isStaminaBoosted() && Movement.getRunEnergy() <= config.minimumEnergy();
    }

    private boolean staminaInBankAndNotInventory() {
        return Bank.contains(staminaPotionIDs) && !Inventory.contains(staminaPotionIDs);
    }

    private boolean staminaInInventoryAndBoosted() {
        return Inventory.contains(staminaPotionIDs) && Movement.isStaminaBoosted();
    }

    private boolean staminaInInventoryAndLowEnergy() {
        return Inventory.contains(staminaPotionIDs) && Movement.getRunEnergy() <= config.minimumEnergy();
    }

    private void withdrawStamina() {
        var lowestDose = Bank.getAll(staminaPotionIDs)
                .stream()
                .max(Comparator.comparing(IItem::getId)).orElse(null);

        if (lowestDose != null) {
            Bank.withdraw(lowestDose.getId(), 1);
        }
    }

    @Override
    public String toString() {
        return "Gathering tools";
    }
}
