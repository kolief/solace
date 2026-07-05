package net.solace.impl.items.loadouts;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.ItemID;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemVariationMapping;
import net.solace.api.coords.Area;
import net.solace.api.domain.game.IClient;
import net.solace.api.domain.items.IItem;
import net.solace.api.interact.InteractManager;
import net.solace.api.interact.RunnableAction;
import net.solace.api.items.IBank;
import net.solace.api.items.IBankInventory;
import net.solace.api.items.IEquipment;
import net.solace.api.items.IInventory;
import net.solace.api.items.WithdrawMode;
import net.solace.api.items.loadouts.Loadout;
import net.solace.api.items.loadouts.LoadoutItem;
import net.solace.api.items.loadouts.LoadoutManager;
import net.solace.api.magic.RunePouch;
import net.solace.api.movement.IMovement;
import net.solace.api.plugins.config.SolaceConfig;
import net.solace.api.widgets.EquipmentSlot;
import net.solace.api.widgets.IBankWornItems;
import net.solace.api.widgets.IDialog;
import net.solace.api.widgets.IWidgets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@RequiredArgsConstructor
@Slf4j
public class LoadoutManagerImpl implements LoadoutManager {
    public static final int[] RUNE_POUCH_IDS = new int[]{ItemID.BH_RUNE_POUCH, ItemID.BH_RUNE_POUCH_TROUVER,
            ItemID.DIVINE_RUNE_POUCH, ItemID.DIVINE_RUNE_POUCH_TROUVER};
    private static final int MAX_RUNES_PER_SLOT = 16000;
    private static final int RUNE_POUCH_INV_SLOTS_CHILD_ID = 26;
    private static final int RUNE_POUCH_INV_FIRST_SLOT_CHILD_ID = 1;
    private static final Executor executor = Executors.newSingleThreadExecutor();
    private final ConcurrentLinkedQueue<Runnable> nextTickActions = new ConcurrentLinkedQueue<>();

    private final IBank bank;
    private final IBankInventory bankInventory;
    private final IEquipment equipment;
    private final IInventory inventory;
    private final IMovement movement;
    private final IDialog dialog;
    private final IWidgets widgets;
    private final IClient client;
    private final IBankWornItems bankWornItems;
    private final SolaceConfig solaceConfig;
    private final InteractManager interactManager;

    public void fetchFromBank(Loadout loadout, WorldArea worldArea) {
        if (client.isClientThread()) {
            executor.execute(() -> fetchLoadoutFromBank(loadout, worldArea));
            return;
        }

        fetchLoadoutFromBank(loadout, worldArea);
    }

    public boolean isLoadoutCompleted(Loadout loadout) {
        return !dialog.isEnterInputOpen() && isEquipmentCompleted(loadout)
               && isInventoryCompleted(loadout) && isRunePouchCompleted(loadout);
    }

    public boolean isEquipmentCompleted(Loadout loadout) {
        var loadoutEquipment = loadout.getEquipment();
        for (int slot = 0; slot < loadoutEquipment.length; slot++) {
            var loadoutItem = loadoutEquipment[slot];
            var equipped = equipment.get(slot);
            if (loadoutItem == null) {
                if (equipped != null) {
                    return false;
                }

                continue;
            }

            if (equipped == null) {
                log.debug("Equipment is not completed, expected {} at slot {}", loadoutItem.getId(), loadoutItem.getSlot());
                return false;
            }

            if (!isEquippedItemCompleted(loadoutItem)) {
                log.debug("Equipment is not completed: {} {}x != {} {}x", equipped.getId(), equipped.getQuantity(),
                        loadoutItem.getId(), loadoutItem.getQuantity());
                return false;
            }
        }

        return true;
    }

    public boolean isInventoryCompleted(Loadout loadout) {
        var loadoutInventory = loadout.getInventory();
        for (int slot = 0; slot < loadoutInventory.length; slot++) {
            var loadoutItem = loadoutInventory[slot];
            var invItem = bank.isOpen() ? bankInventory.get(slot) : inventory.get(slot);
            if (loadoutItem == null) {
                if (invItem != null) {
                    return false;
                }

                continue;
            }

            if (invItem == null) {
                log.debug("Inventory is not completed, expected {} at slot {}", loadoutItem.getId(), slot);
                return false;
            }

            if (!isItemEqual(invItem, loadoutItem) || loadoutItem.getQuantity() > invItem.getQuantity()) {
                log.debug("Inventory is not completed: {} {}x != {} {}x", invItem.getId(), invItem.getQuantity(),
                        loadoutItem.getId(), loadoutItem.getQuantity());
                return false;
            }
        }

        return true;
    }

    public boolean isRunePouchCompleted(Loadout loadout) {
        if (!loadout.hasRunePouch()) {
            return true;
        }

        var loadoutRunePouch = loadout.getRunePouch();
        for (int slot = 0; slot < loadoutRunePouch.length; slot++) {
            RunePouch.RuneSlot runeSlot;
            switch (slot) {
                case 0:
                    runeSlot = RunePouch.RuneSlot.FIRST;
                    break;
                case 1:
                    runeSlot = RunePouch.RuneSlot.SECOND;
                    break;
                case 2:
                    runeSlot = RunePouch.RuneSlot.THIRD;
                    break;
                case 3:
                    runeSlot = RunePouch.RuneSlot.FOURTH;
                    break;
                default:
                    throw new IllegalArgumentException("Invalid rune pouch slot: " + slot);
            }

            if (slot == 3 && !RunePouch.isDivine()) {
                continue;
            }

            var loadoutItem = loadoutRunePouch[slot];
            var runeId = runeSlot.getRuneId();
            var runeQuantity = runeSlot.getQuantity();

            if (loadoutItem == null) {
                if (runeQuantity != 0) {
                    log.debug("Rune pouch is not completed, expected {} at slot {} but found {} {}", runeId, slot,
                            runeSlot.getRuneName(), runeQuantity);
                    return false;
                }

                continue;
            }

            if (loadoutItem.getId() != runeId || loadoutItem.getQuantity() > runeQuantity) {
                log.debug("Rune pouch is not completed: {} {}x != {} {}x", runeId, runeQuantity,
                        loadoutItem.getId(), loadoutItem.getQuantity());
                return false;
            }
        }

        return true;
    }

    public void fetchEquipment(Loadout loadout, WorldArea worldArea) {
        if (client.isClientThread()) {
            executor.execute(() -> fetchEquipmentLoadout(loadout, worldArea));
            return;
        }

        fetchEquipmentLoadout(loadout, worldArea);
    }

    public void fetchInventory(Loadout loadout, WorldArea worldArea) {
        if (client.isClientThread()) {
            executor.execute(() -> fetchInventoryLoadout(loadout, worldArea));
            return;
        }

        fetchInventoryLoadout(loadout, worldArea);
    }

    public void fetchRunePouch(Loadout loadout, WorldArea worldArea) {
        if (client.isClientThread()) {
            try {
                executor.execute(() -> fetchRunePouchLoadout(loadout, worldArea));
            } catch (Exception e) {
                throw e;
            }
            return;
        }

        fetchRunePouchLoadout(loadout, worldArea);
    }

    private void fetchLoadoutFromBank(Loadout loadout, WorldArea worldArea) {
        if (dialog.isEnterInputOpen()) {
            dialog.forceClose();
            return;
        }

        var center = Area.centerOf(worldArea);
        if (!bank.isOpen() && !WorldPoint.isInScene(client.getTopLevelWorldView(), center.getX(), center.getY())) {
            log.debug("Bank location is not in scene, moving to it");
            movement.walkTo(center);
            return;
        }

        if (widgets.isVisible(widgets.get(InterfaceID.BankpinKeypad.UNIVERSE))) {
            log.debug("Bank pin container is visible, waiting");
            return;
        }

        if (!interactManager.getQueue().isEmpty()) {
            return;
        }

        if (!loadout.isEquipmentDisabled() && !isEquipmentCompleted(loadout)) {
            fetchEquipment(loadout, worldArea);
            return;
        } else {
            log.debug("Equipment is completed");
        }

        if (!loadout.isInventoryDisabled() && !isInventoryCompleted(loadout)) {
            fetchInventory(loadout, worldArea);
            return;
        } else {
            log.debug("Inventory is completed");
        }

        if (!loadout.isRunePouchDisabled() && !isRunePouchCompleted(loadout)) {
            fetchRunePouch(loadout, worldArea);
        } else {
            log.debug("Rune pouch is completed");
        }

        interactManager.queue(new RunnableAction(() -> queueForNextTick(dialog::forceClose)));
    }

    private void fetchEquipmentLoadout(Loadout loadout, WorldArea worldArea) {
        if (!bank.isOpen() && !bankWornItems.isOpen()) {
            bank.open(worldArea);
            return;
        }

        if (!bank.isMainTabOpen()) {
            bank.openMainTab();
            return;
        }

        var inInventory = new HashMap<Integer, LoadoutItem>();
        var inventoryItems = inventory.getAll(x -> true);
        for (var inventoryItem : inventoryItems) {
            var slot = inventoryItem.getSlot();
            var loadoutItem = findEquipmentItem(loadout, inventoryItem);
            if (loadoutItem != null) {
                inInventory.put(slot, loadoutItem);
            }
        }

        if (!inInventory.isEmpty()) {
            log.debug("In inventory: {}", inInventory);
            for (var entry : inInventory.entrySet()) {
                var slot = entry.getKey();
                var loadoutItem = entry.getValue();

                var bankInventoryItem = bankInventory.get(slot);
                if (bankInventoryItem == null) {
                    throw new IllegalArgumentException("Bank inventory does not contain item " + loadoutItem);
                }

                if (bankInventoryItem.getQuantity() > loadoutItem.getMaxQuantity() || isEquippedItemCompleted(loadoutItem)) {
                    executeActionAndSleep(bankInventoryItem::depositAll);
                } else {
                    executeActionAndSleep(() -> bankInventoryItem.interact("Wield", "Wear", "Equip"));
                }
            }

            return;
        }

        var toDeposit = new ArrayList<EquipmentSlot>();
        var equippedItems = equipment.getAll(x -> true);
        var equipmentLoadout = loadout.getEquipment();
        for (var item : equippedItems) {
            var slot = item.getSlot();
            var loadoutItem = equipmentLoadout[slot];
            if (loadoutItem == null || !isItemEqual(item, loadoutItem) || item.getQuantity() > loadoutItem.getMaxQuantity()) {
                toDeposit.add(EquipmentSlot.fromSlotIndex(slot));
            }
        }

        if (!toDeposit.isEmpty()) {
            log.debug("Should deposit {}", toDeposit);
            if (!bankWornItems.isOpen()) {
                bankWornItems.open();
                return;
            }

            for (var equipmentSlot : toDeposit) {
                var wornItem = bankWornItems.fromSlot(equipmentSlot);
                if (wornItem != null) {
                    executeActionAndSleep(wornItem::deposit);
                }
            }

            return;
        }

        if (bankWornItems.isOpen()) {
            bankWornItems.close();
            return;
        }

        var toWithdraw = new ArrayList<LoadoutItem>();
        for (var loadoutItem : equipmentLoadout) {
            if (loadoutItem == null || isEquippedItemCompleted(loadoutItem)) {
                continue;
            }

            if (isLoadoutItemMissingFromBank(loadoutItem)) {
                throw new IllegalStateException("Bank does not contain item " + loadoutItem);
            }

            log.debug("Should withdraw {}", loadoutItem);

            toWithdraw.add(loadoutItem);
        }

        if (!toWithdraw.isEmpty()) {
            log.debug("Should withdraw {}", toWithdraw);
            if (inventory.isFull() || inventory.getFreeSlots() < toWithdraw.size()) {
                bank.depositInventory();
                return;
            }

            for (var loadoutItem : toWithdraw) {
                if (isLoadoutItemMissingFromBank(loadoutItem)) {
                    throw new IllegalArgumentException("Bank does not contain item " + loadoutItem);
                }

                executeActionAndSleep(() -> withdrawLoadoutItem(loadoutItem, loadoutItem.getMaxQuantity()));
            }
        }

        interactManager.queue(new RunnableAction(() -> queueForNextTick(dialog::forceClose)));
    }

    private void fetchInventoryLoadout(Loadout loadout, WorldArea worldArea) {
        if (!bank.isOpen()) {
            bank.open(worldArea);
            return;
        }

        if (!bank.isMainTabOpen()) {
            bank.openMainTab();
            return;
        }

        var deposited = false;
        var loadoutInventory = loadout.getInventory();
        for (int i = 0; i < loadoutInventory.length; i++) {
            var loadoutItem = loadoutInventory[i];
            var invItem = bankInventory.get(i);

            if (loadoutItem == null) {
                if (invItem != null) {
                    executeActionAndSleep(invItem::depositAll);
                    deposited = true;
                }

                continue;
            }

            if (invItem != null && !isItemEqual(invItem, loadoutItem)) {
                executeActionAndSleep(invItem::depositAll);
                deposited = true;
            }
        }

        if (deposited) {
            return;
        }

        for (int i = 0; i < loadoutInventory.length; i++) {
            var loadoutItem = loadoutInventory[i];
            if (loadoutItem == null) {
                continue;
            }

            var invItem = bankInventory.get(i);

            if (invItem == null || invItem.getQuantity() < loadoutItem.getQuantity()) {
                if (isLoadoutItemMissingFromBank(loadoutItem)) {
                    throw new IllegalStateException("Bank does not contain item " + loadoutItem);
                }

                if (loadoutItem.isStackable()) {
                    var remaining = loadoutItem.getMaxQuantity() - (invItem == null ? 0 : invItem.getQuantity());
                    executeActionAndSleep(() -> withdrawLoadoutItem(loadoutItem, remaining));
                } else {
                    executeActionAndSleep(() -> withdrawLoadoutItem(loadoutItem, 1));
                }
            }
        }

        interactManager.queue(new RunnableAction(() -> queueForNextTick(dialog::forceClose)));
    }



    private void fetchRunePouchLoadout(Loadout loadout, WorldArea worldArea) {
        if (!bank.isOpen()) {
            bank.open(worldArea);
            return;
        }

        if (!bank.isMainTabOpen()) {
            bank.openMainTab();
            return;
        }

        var runePouch = bankInventory.getFirst(RUNE_POUCH_IDS);
        if (runePouch == null) {
            throw new IllegalStateException("Rune pouch was configured, but not found in the inventory");
        }

        if (!widgets.isVisible(InterfaceID.BANKSIDE, RUNE_POUCH_INV_SLOTS_CHILD_ID, RUNE_POUCH_INV_FIRST_SLOT_CHILD_ID)) {
            runePouch.interact("Configure");
            return;
        }

        var loadoutRunePouch = loadout.getRunePouch();
        for (int i = 0; i < RunePouch.RuneSlot.values().length; i++) {
            if (i == 3 && !RunePouch.isDivine()) {
                continue;
            }

            var loadoutItem = loadoutRunePouch[i];
            var runeWidget = widgets.get(InterfaceID.BANKSIDE, RUNE_POUCH_INV_SLOTS_CHILD_ID, RUNE_POUCH_INV_FIRST_SLOT_CHILD_ID + i);
            var runeSlot = RunePouch.RuneSlot.values()[i];
            var isSlotEmpty = runeSlot.getQuantity() <= 0;
            if (loadoutItem == null) {
                if (!isSlotEmpty) {
                    runeWidget.interact("Deposit-All");
                    return;
                }

                continue;
            }

            if (!isSlotEmpty && runeSlot.getRuneId() != loadoutItem.getId()) {
                runeWidget.interact("Deposit-All");
                return;
            }
        }

        for (int i = 0; i < RunePouch.RuneSlot.values().length; i++) {
            if (i == 3 && !RunePouch.isDivine()) {
                continue;
            }

            var loadoutItem = loadoutRunePouch[i];
            var runeSlot = RunePouch.RuneSlot.values()[i];
            var isSlotEmpty = runeSlot.getQuantity() <= 0;
            var max = loadoutItem.getMaxQuantity();
            if (isSlotEmpty) {
                var maxAmount = Math.min(MAX_RUNES_PER_SLOT, max);
                maxAmount = Math.min(maxAmount, getLoadoutItemBankQuantity(loadoutItem));

                if (maxAmount < loadoutItem.getQuantity()) {
                    throw new IllegalStateException("Bank does not contain enough of item " + loadoutItem);
                }

                withdrawLoadoutItem(loadoutItem, maxAmount);
                return;
            }

            if (runeSlot.getQuantity() < loadoutItem.getQuantity()) {
                var remaining = max - runeSlot.getQuantity();
                remaining = Math.min(remaining, getLoadoutItemBankQuantity(loadoutItem));
                if (remaining + runeSlot.getQuantity() < loadoutItem.getQuantity()) {
                    throw new IllegalStateException("Bank does not contain enough of item " + loadoutItem);
                }

                withdrawLoadoutItem(loadoutItem, remaining);
                return;
            }
        }

        interactManager.queue(new RunnableAction(() -> queueForNextTick(dialog::forceClose)));
    }

    private boolean isItemEqual(IItem item, LoadoutItem loadoutItem) {
        var base = ItemVariationMapping.map(item.getId());
        var loadoutBase = ItemVariationMapping.map(loadoutItem.getId());
        return !loadoutItem.isStrict() && base == loadoutBase || item.getId() == loadoutItem.getId();
    }

    private boolean isEquippedItemCompleted(LoadoutItem loadoutItem) {
        var equipped = equipment.get(loadoutItem.getSlot());
        return equipped != null && isItemEqual(equipped, loadoutItem)
               && equipped.getQuantity() >= loadoutItem.getQuantity();
    }

    private boolean isLoadoutItemMissingFromBank(LoadoutItem loadoutItem) {
        return isLoadoutItemMissingFromBank(loadoutItem, loadoutItem.getQuantity());
    }

    private boolean isLoadoutItemMissingFromBank(LoadoutItem loadoutItem, int minAmount) {
        var base = ItemVariationMapping.map(loadoutItem.getId());
        var variations = ItemVariationMapping.getVariations(base);
        if (loadoutItem.isNoted()) {
            return bank.getCount(true, x -> x.getNotedId() == loadoutItem.getId()) < minAmount;
        }
        if (loadoutItem.isStrict()) {
            return bank.getCount(true, loadoutItem.getId()) < minAmount;
        }
        return variations.stream().noneMatch(variation -> bank.getCount(true, variation) >= minAmount);
    }

    private int getLoadoutItemBankQuantity(LoadoutItem loadoutItem) {
        if (loadoutItem.isNoted()) {
            return bank.getCount(true, x -> x.getNotedId() == loadoutItem.getId());
        }

        return bank.getCount(true, loadoutItem.getId());
    }


    private void withdrawLoadoutItem(LoadoutItem loadoutItem, int amount) {
        if (loadoutItem.isNoted()) {
            var notedBankItem = bank.getFirst(x -> x.getNotedId() == loadoutItem.getId());
            if (notedBankItem != null)  {
                log.debug("Withdrawing {}x noted {}", amount, loadoutItem);
                bank.withdraw(notedBankItem.getId(), amount, WithdrawMode.NOTED, true);
                return;
            }
        }

        if (bank.contains(loadoutItem.getId())) {
            log.debug("Withdrawing {}x {}", amount, loadoutItem);
            bank.withdraw(loadoutItem.getId(), amount,
                    loadoutItem.isNoted() ? WithdrawMode.NOTED : WithdrawMode.ITEM, true);
            return;
        }

        var base = ItemVariationMapping.map(loadoutItem.getId());
        var variations = ItemVariationMapping.getVariations(base);
        for (var variation : variations) {
            if (bank.contains(variation)) {
                log.debug("Withdrawing {}x variation {} {}", amount, variation, loadoutItem);
                bank.withdraw(variation, amount,
                        loadoutItem.isNoted() ? WithdrawMode.NOTED : WithdrawMode.ITEM, true);
                return;
            }
        }
    }

    private LoadoutItem findEquipmentItem(Loadout loadout, IItem item) {
        for (var loadoutItem : loadout.getEquipment()) {
            if (loadoutItem == null) {
                continue;
            }

            var base = ItemVariationMapping.map(item.getId());
            var loadoutBase = ItemVariationMapping.map(loadoutItem.getId());
            if (base == loadoutBase) {
                return loadoutItem;
            }
        }

        return null;
    }

    private void executeActionAndSleep(Runnable runnable) {
        runnable.run();
        sleep();
    }

    private void sleep() {
        var delay = solaceConfig.loadoutActionDelay();
        if (delay > 0) {
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void queueForNextTick(Runnable action) {
        nextTickActions.offer(action);
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        Runnable action;
        while ((action = nextTickActions.poll()) != null) {
            action.run();
        }
    }
}