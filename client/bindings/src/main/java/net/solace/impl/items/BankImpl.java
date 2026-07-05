package net.solace.impl.items;

import net.runelite.api.Item;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.InventoryID;
import net.runelite.api.gameval.VarbitID;
import net.solace.api.coords.Area;
import net.solace.api.domain.game.IClient;
import net.solace.api.domain.items.IBankItem;
import net.solace.api.domain.items.IItem;
import net.solace.api.domain.widgets.IWidget;
import net.solace.api.entities.INPCs;
import net.solace.api.entities.IPlayers;
import net.solace.api.entities.ITileObjects;
import net.solace.api.game.IVars;
import net.solace.api.interact.InteractManager;
import net.solace.api.interact.WidgetAction;
import net.solace.api.items.IBank;
import net.solace.api.items.IBankInventory;
import net.solace.api.items.WithdrawMode;
import net.solace.api.movement.IMovement;
import net.solace.api.movement.pathfinder.model.BankLocation;
import net.solace.api.widgets.IDialog;
import net.solace.api.widgets.IWidgets;
import net.solace.impl.domain.items.BankItemImpl;

import java.util.List;
import java.util.function.Predicate;

public class BankImpl extends ItemsImpl<IBankItem> implements IBank {
    private static final Predicate<IItem> PLACEHOLDERS_FILTER = x -> !x.isPlaceholder();
    private static final int WITHDRAW_MODE_VARBIT = 3958;

    private final IPlayers players;
    private final ITileObjects tileObjects;
    private final IMovement movement;
    private final IVars vars;
    private final IDialog dialog;
    private final IBankInventory bankInventory;
    private final InteractManager interactManager;
    private final INPCs npcs;

    public BankImpl(
            IWidgets widgets,
            IClient client,
            IPlayers players,
            ITileObjects tileObjects,
            IMovement movement,
            IVars vars,
            IDialog dialog,
            IBankInventory bankInventory,
            InteractManager interactManager,
            INPCs npcs
    ) {
        super(widgets, client, InventoryID.BANK, IBankItem.class, IBankItem[].class, BankImpl::map, 1220);
        this.players = players;
        this.tileObjects = tileObjects;
        this.movement = movement;
        this.vars = vars;
        this.dialog = dialog;
        this.bankInventory = bankInventory;
        this.interactManager = interactManager;
        this.npcs = npcs;
    }

    private static BankItemImpl map(IWidgets widgets, IClient client, Item item, int slot) {
        if (item == null) {
            return null;
        }

        var bankWidget = widgets.get(InterfaceID.Bankmain.ITEMS);
        if (bankWidget == null) {
            return null;
        }

        return new BankItemImpl(item, slot, bankWidget, client);
    }

    @Override
    public boolean isOpen() {
        return widgets.isVisible(InterfaceID.Bankmain.ITEMS);
    }

    @Override
    public void open(BankLocation location) {
        open(location.getArea());
    }

    @Override
    public void open(WorldArea worldArea) {
        if (isOpen()) {
            return;
        }

        var center = Area.centerOf(worldArea);
        if (WorldPoint.isInScene(client.getTopLevelWorldView(), center.getX(), center.getY())) {
            var local = players.getLocal();
            var obj = tileObjects.getNearestIn(worldArea, x -> x.hasAction("Collect")
                                                     && x.hasAction("Bank", "Use")
                                                     && x.distanceTo(local) <= 20);
            if (obj != null && obj.isInteractable(local.getWorldLocation())) {
                if (local.isMoving()) {
                    return;
                }

                if (dialog.isOpen() && dialog.canContinue()) {
                    var text = dialog.getText();

                    if (text != null && text.contains("The ghost banker")) {
                        dialog.continueSpace();
                        return;
                    }
                }

                obj.interact("Bank", "Use");
                return;
            }

            var bankNpc = npcs.getNearest(x -> worldArea.contains(x.getWorldLocation())
                                            && x.hasAction("Bank")
                                            && x.hasAction("Collect")
                                            && x.distanceTo(local) <= 20);

            if (bankNpc != null && bankNpc.isInteractable(local.getWorldLocation())) {
                if (local.isMoving()) {
                    return;
                }

                bankNpc.interact("Bank");
                return;
            }
        }

        if (movement.isWalking()) {
            return;
        }

        movement.walkTo(center);
    }

    @Override
    public void close() {
        var exitBank = widgets.get(InterfaceID.BANKMAIN, 2, 11);
        if (!widgets.isVisible(exitBank)) {
            return;
        }

        exitBank.interact("Close");
    }

    @Override
    public boolean isMainTabOpen() {
        return isTabOpen(0);
    }

    @Override
    public boolean isTabOpen(int index) {
        return vars.getBit(VarbitID.BANK_CURRENTTAB) == index;
    }

    @Override
    public void openMainTab() {
        openTab(0);
    }

    @Override
    public void openTab(int index) {
        if (index < 0 || index > getTabs().size()) {
            return;
        }

        var tabContainer = widgets.get(InterfaceID.Bankmain.TABS);

        if (widgets.isVisible(tabContainer) && !isTabOpen(index)) {
            tabContainer.getChild(10 + index).interact(0);
        }
    }

    @Override
    public List<IWidget> getTabs() {
        return widgets.getChildren(InterfaceID.Bankmain.TABS, x -> x.hasAction("Collapse tab"));
    }

    @Override
    public void withdraw(Predicate<IItem> filter, int amount, WithdrawMode withdrawMode, boolean quick) {
        var item = getFirst(filter.and(PLACEHOLDERS_FILTER));

        if (item == null) {
            return;
        }

        var action = getAction(item, amount, true);
        var actionIndex = item.getActionIndex(action);

        if (withdrawMode == WithdrawMode.NOTED && !isNotedWithdrawMode()) {
            setWithdrawMode(true);
        }

        if (withdrawMode == WithdrawMode.ITEM && isNotedWithdrawMode()) {
            setWithdrawMode(false);
        }

        if (!action.equals("Withdraw-X")) {
            item.interact(actionIndex);
            return;
        }

        if (quick) {
            interactManager.queue(new WidgetAction(6, InterfaceID.Bankmain.ITEMS, item.getSlot(), item.getId()));
            dialog.enterAmount(amount);
            return;
        }

        if (dialog.isEnterInputOpen()) {
            dialog.enterAmount(amount);
            return;
        }

        item.interact(actionIndex);
    }

    @Override
    public void deposit(Predicate<? super IItem> filter, int amount, boolean quick) {
        var item = bankInventory.getFirst(filter);
        if (item == null) {
            return;
        }

        var action = getAction(item, amount, false);
        var actionIndex = item.getActionIndex(action);

        if (!action.equals("Deposit-X")) {
            item.interact(actionIndex);
            return;
        }

        if (quick) {
            var identifier = 6;
            if (vars.getBit(VarbitID.BANK_QUANTITY_TYPE) > 0) {
                identifier = 7;
            }

            interactManager.queue(new WidgetAction(identifier, InterfaceID.Bankside.ITEMS, item.getSlot(), item.getId()));
            dialog.enterAmount(amount);
            return;
        }

        if (dialog.isEnterInputOpen()) {
            dialog.enterAmount(amount);
            return;
        }

        item.interact(actionIndex);
    }

    @Override
    public void emptyContainers() {
    }

    @Override
    public void setWithdrawMode(boolean noted) {
        var widget = noted ? widgets.get(InterfaceID.BANKMAIN, 26) : widgets.get(InterfaceID.BANKMAIN, 24);
        if (widget != null) {
            widget.interact(0);
        }
    }

    @Override
    public boolean isNotedWithdrawMode() {
        return vars.getBit(WITHDRAW_MODE_VARBIT) == 1;
    }

    @Override
    public void depositInventory() {
        var widget = widgets.get(InterfaceID.Bankmain.DEPOSITINV);
        if (widget != null) {
            widget.interact("Deposit inventory");
        }
    }

    @Override
    public void depositEquipment() {
        var widget = widgets.get(InterfaceID.Bankmain.DEPOSITWORN);
        if (widget != null) {
            widget.interact("Deposit worn items");
        }
    }

    private String getAction(IItem item, int amount, boolean withdraw) {
        var action = withdraw ? "Withdraw" : "Deposit";
        if (amount == 1) {
            action += "-1";
        } else if (amount == 5) {
            action += "-5";
        } else if (amount == 10) {
            action += "-10";
        } else if (withdraw && amount >= item.getQuantity()) {
            action += "-All";
        } else if (!withdraw && amount >= bankInventory.getCount(true, item.getId())) {
            action += "-All";
        } else {
            if (item.hasAction(action + "-" + amount)) {
                action += "-" + amount;
            } else {
                action += "-X";
            }
        }
        return action;
    }
}
