package net.solace.sdk.items;

import java.util.List;
import java.util.Objects;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.runelite.api.MenuAction;
import net.runelite.api.coords.WorldArea;
import net.solace.api.Static;
import net.solace.api.commons.Predicates;
import net.solace.api.coords.Area;
import net.solace.api.domain.items.IBankInventoryItem;
import net.solace.api.domain.items.IBankItem;
import net.solace.api.domain.items.IItem;
import net.solace.api.domain.widgets.IWidget;
import net.solace.api.items.IBank;
import net.solace.api.items.IBankInventory;
import net.solace.api.items.WithdrawMode;
import net.solace.api.movement.pathfinder.model.BankLocation;
import net.solace.sdk.commons.Time;
import net.solace.sdk.game.Client;
import net.solace.sdk.game.Vars;
import net.solace.sdk.widgets.Widgets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Bank {
    private static final Logger log = LoggerFactory.getLogger(Bank.class);
    private static final IBank BANK = Static.getBank();
    private static final IBankInventory BANK_INVENTORY = Static.getBankInventory();
    private static final int QUANTITY_MODE_VARP = 6590;
    private static final Supplier<IWidget> SETTINGS_CONTAINER = () -> Widgets.get(786492);

    public static void open() {
        Bank.open(BankLocation.getNearest());
    }

    public static void open(WorldArea area, int offset) {
        Bank.open(area, offset, () -> true);
    }

    public static void open(WorldArea area, int offset, BooleanSupplier requirements) {
        if (Bank.isOpen()) {
            return;
        }
        if (!requirements.getAsBoolean()) {
            log.error("Requirements not met to open bank.");
            return;
        }
        WorldArea offsetArea = Area.offsetFrom((WorldArea)area, (int)offset);
        Bank.open(offsetArea);
    }

    public static void open(WorldArea area) {
        BANK.open(area);
    }

    public static void open(BankLocation bankLocation) {
        BANK.open(bankLocation);
    }

    public static QuantityMode getQuantityMode() {
        return QuantityMode.getCurrent();
    }

    public static void setQuantityMode(QuantityMode quantityMode) {
        IWidget component;
        if (Bank.getQuantityMode() != quantityMode && Widgets.isVisible(component = Widgets.get(quantityMode.widget.groupId, quantityMode.widget.childId))) {
            component.interact(0);
        }
    }

    public static int getFreeSlots() {
        if (!Bank.isOpen()) {
            return -1;
        }
        return Bank.getCapacity() - Bank.getOccupiedSlots();
    }

    public static int getCapacity() {
        IWidget widget = Widgets.get(786440);
        if (Widgets.isVisible(widget)) {
            return Integer.parseInt(widget.getText());
        }
        return -1;
    }

    public static int getOccupiedSlots() {
        IWidget widget = Widgets.get(786437);
        if (Widgets.isVisible(widget)) {
            return Integer.parseInt(widget.getText());
        }
        return -1;
    }

    public static void releasePlaceholders() {
        IWidget widget;
        if (!Bank.isSettingsOpen()) {
            Bank.toggleSettings();
            Time.sleepUntil(Bank::isSettingsOpen, 5000);
        }
        if ((widget = Widgets.get(786572)) != null) {
            widget.interact(5);
        }
    }

    public static void toggleSettings() {
        IWidget settingsButton = Widgets.get(786538);
        if (settingsButton != null) {
            settingsButton.interact(0);
        }
    }

    public static boolean isSettingsOpen() {
        return Widgets.isVisible(SETTINGS_CONTAINER.get());
    }

    public static void depositInventory() {
        BANK.depositInventory();
    }

    public static void depositEquipment() {
        BANK.depositEquipment();
    }

    public static void emptyContainers() {
        BANK.emptyContainers();
    }

    public static boolean isOpen() {
        return BANK.isOpen();
    }

    public static boolean isEmpty() {
        return Bank.getAll().isEmpty();
    }

    public static void depositAll(String ... names) {
        BANK.depositAll(names);
    }

    public static void depositAll(int ... ids) {
        BANK.depositAll(ids);
    }

    public static void depositAll(Predicate<? super IItem> filter) {
        BANK.depositAll(filter);
    }

    public static void depositAllExcept(String ... names) {
        BANK.depositAllExcept(names);
    }

    public static void depositAllExcept(int ... ids) {
        BANK.depositAllExcept(ids);
    }

    public static void depositAllExcept(Predicate<? super IItem> filter) {
        BANK.depositAllExcept(filter);
    }

    public static void deposit(String name, int amount) {
        BANK.deposit(name, amount);
    }

    public static void deposit(int id, int amount) {
        BANK.deposit(id, amount);
    }

    public static void deposit(Predicate<? super IItem> filter, int amount) {
        BANK.deposit(filter, amount);
    }

    public static void withdrawAll(String name) {
        BANK.withdrawAll(name);
    }

    public static void withdrawAll(String name, WithdrawMode withdrawMode) {
        BANK.withdrawAll(name, withdrawMode);
    }

    public static void withdrawAll(int id) {
        BANK.withdrawAll(id);
    }

    public static void withdrawAll(int id, WithdrawMode withdrawMode) {
        BANK.withdrawAll(id, withdrawMode);
    }

    public static void withdrawAll(Predicate<IItem> filter) {
        BANK.withdrawAll(filter);
    }

    public static void withdrawAll(Predicate<IItem> filter, WithdrawMode withdrawMode) {
        BANK.withdrawAll(filter, withdrawMode);
    }

    public static void withdraw(String name, int amount) {
        BANK.withdraw(name, amount);
    }

    public static void withdraw(String name, int amount, WithdrawMode withdrawMode) {
        BANK.withdraw(name, amount, withdrawMode);
    }

    public static void withdraw(int id, int amount) {
        BANK.withdraw(id, amount);
    }

    public static void withdraw(int id, int amount, WithdrawMode withdrawMode) {
        BANK.withdraw(id, amount, withdrawMode);
    }

    public static void withdraw(Predicate<IItem> filter, int amount) {
        BANK.withdraw(filter, amount);
    }

    public static void withdraw(Predicate<IItem> filter, int amount, WithdrawMode withdrawMode) {
        BANK.withdraw(filter, amount, withdrawMode);
    }

    public static void quickWithdraw(String name, int amount) {
        BANK.withdraw(name, amount, true);
    }

    public static void quickWithdraw(String name, int amount, WithdrawMode withdrawMode) {
        BANK.withdraw(name, amount, withdrawMode, true);
    }

    public static void quickWithdraw(int id, int amount) {
        BANK.withdraw(id, amount, true);
    }

    public static void quickWithdraw(int id, int amount, WithdrawMode withdrawMode) {
        BANK.withdraw(id, amount, withdrawMode, true);
    }

    public static void quickWithdraw(Predicate<IItem> filter, int amount) {
        BANK.withdraw(filter, amount, true);
    }

    public static void quickWithdraw(Predicate<IItem> filter, int amount, WithdrawMode withdrawMode) {
        BANK.withdraw(filter, amount, withdrawMode, true);
    }

    public static void quickDeposit(String name, int amount) {
        BANK.deposit(name, amount, true);
    }

    public static void quickDeposit(int id, int amount) {
        BANK.deposit(id, amount, true);
    }

    public static void quickDeposit(Predicate<IItem> filter, int amount) {
        BANK.deposit(filter, amount, true);
    }

    public static void withdrawLastQuantity(String name) {
        Bank.withdrawLastQuantity(name, WithdrawMode.DEFAULT);
    }

    public static void withdrawLastQuantity(String name, WithdrawMode withdrawMode) {
        Bank.withdrawLastQuantity((IItem x) -> Objects.equals(name, x.getName()), withdrawMode);
    }

    public static void withdrawLastQuantity(int id) {
        Bank.withdrawLastQuantity(id, WithdrawMode.DEFAULT);
    }

    public static void withdrawLastQuantity(int id, WithdrawMode withdrawMode) {
        Bank.withdrawLastQuantity((IItem x) -> x.getId() == id, withdrawMode);
    }

    public static void withdrawLastQuantity(Predicate<IItem> filter) {
        Bank.withdrawLastQuantity(filter, WithdrawMode.DEFAULT);
    }

    public static void withdrawLastQuantity(Predicate<IItem> filter, WithdrawMode withdrawMode) {
        IBankItem item = Bank.getFirst(filter.and(x -> !x.isPlaceholder()));
        if (item == null) {
            return;
        }
        WithdrawOption withdrawOption = WithdrawOption.LAST_QUANTITY;
        if (withdrawMode == WithdrawMode.NOTED && !Bank.isNotedWithdrawMode()) {
            Bank.setWithdrawMode(true);
        }
        if (withdrawMode == WithdrawMode.ITEM && Bank.isNotedWithdrawMode()) {
            Bank.setWithdrawMode(false);
        }
        item.interact(withdrawOption.getMenuIndex());
    }

    public static void setWithdrawMode(boolean noted) {
        BANK.setWithdrawMode(noted);
    }

    public static boolean isNotedWithdrawMode() {
        return BANK.isNotedWithdrawMode();
    }

    public static List<IBankItem> getAll(Predicate<? super IItem> filter) {
        return BANK.getAll(filter);
    }

    public static List<IBankItem> getAll() {
        return Bank.getAll(x -> true);
    }

    public static List<IBankItem> getAll(int ... ids) {
        return BANK.getAll(ids);
    }

    public static List<IBankItem> getAll(String ... names) {
        return BANK.getAll(names);
    }

    public static IBankItem get(int slot) {
        return (IBankItem)BANK.get(slot);
    }

    public static IBankItem getFirst(Predicate<? super IItem> filter) {
        return (IBankItem)BANK.getFirst(filter);
    }

    public static IBankItem getFirst(int ... ids) {
        return (IBankItem)BANK.getFirst(ids);
    }

    public static IBankItem getFirst(String ... names) {
        return (IBankItem)BANK.getFirst(names);
    }

    public static IBankItem getLast(Predicate<? super IItem> filter) {
        return (IBankItem)BANK.getLast(filter);
    }

    public static IBankItem getLast(int ... ids) {
        return (IBankItem)BANK.getLast(ids);
    }

    public static IBankItem getLast(String ... names) {
        return (IBankItem)BANK.getLast(names);
    }

    public static boolean contains(Predicate<? super IItem> filter) {
        return BANK.contains(filter);
    }

    public static boolean contains(int ... id) {
        return BANK.contains(id);
    }

    public static boolean contains(String ... name) {
        return BANK.contains(name);
    }

    public static boolean containsAll(int ... ids) {
        for (int id : ids) {
            if (Bank.contains(id)) continue;
            return false;
        }
        return true;
    }

    public static boolean containsAll(String ... names) {
        for (String name : names) {
            if (Bank.contains(name)) continue;
            return false;
        }
        return true;
    }

    public static int getCount(boolean stacks, Predicate<IItem> filter) {
        return BANK.getCount(stacks, filter);
    }

    public static int getCount(boolean stacks, int ... ids) {
        return BANK.getCount(stacks, ids);
    }

    public static int getCount(boolean stacks, String ... names) {
        return BANK.getCount(stacks, names);
    }

    public static int getCount(Predicate<IItem> filter) {
        return BANK.getCount(false, filter);
    }

    public static int getCount(int ... ids) {
        return BANK.getCount(false, ids);
    }

    public static int getCount(String ... names) {
        return BANK.getCount(false, names);
    }

    public static List<IWidget> getTabs() {
        return BANK.getTabs();
    }

    public static boolean hasTabs() {
        return !Bank.getTabs().isEmpty();
    }

    public static void collapseTabs() {
        for (int i = 0; i < Bank.getTabs().size(); ++i) {
            IWidget tab = Bank.getTabs().get(i);
            Client.interact(6, MenuAction.CC_OP_LOW_PRIORITY.getId(), 11 + i, tab.getId());
        }
    }

    public static void collapseTab(int index) {
        IWidget tabContainer = Widgets.get(786442);
        if (!Widgets.isVisible(tabContainer)) {
            return;
        }
        int tabIdx = 11 + index;
        IWidget tab = tabContainer.getChild(tabIdx);
        if (!Widgets.isVisible(tab)) {
            return;
        }
        Client.interact(6, MenuAction.CC_OP_LOW_PRIORITY.getId(), tabIdx, tab.getId());
    }

    public static boolean isMainTabOpen() {
        return BANK.isMainTabOpen();
    }

    public static boolean isTabOpen(int index) {
        return BANK.isTabOpen(index);
    }

    public static void openMainTab() {
        BANK.openMainTab();
    }

    public static void openTab(int index) {
        BANK.openTab(index);
    }

    public static boolean isPinScreenOpen() {
        IWidget bankPinContainer = Widgets.get(0xD50000);
        return bankPinContainer != null && !bankPinContainer.isHidden();
    }

    public static void close() {
        BANK.close();
    }

    public static class Inventory {
        public static List<IBankInventoryItem> getAll() {
            return Inventory.getAll(x -> true);
        }

        public static List<IBankInventoryItem> getAll(Predicate<? super IItem> filter) {
            return BANK_INVENTORY.getAll(filter);
        }

        public static List<IBankInventoryItem> getAll(int ... ids) {
            return BANK_INVENTORY.getAll(ids);
        }

        public static List<IBankInventoryItem> getAll(String ... names) {
            return BANK_INVENTORY.getAll(Predicates.names((String[])names));
        }

        public static IBankInventoryItem getFirst(Predicate<? super IItem> filter) {
            return (IBankInventoryItem)BANK_INVENTORY.getFirst(filter);
        }

        public static IBankInventoryItem getFirst(int ... ids) {
            return (IBankInventoryItem)BANK_INVENTORY.getFirst(ids);
        }

        public static IBankInventoryItem getFirst(String ... names) {
            return (IBankInventoryItem)BANK_INVENTORY.getFirst(names);
        }

        public static IBankInventoryItem getLast(Predicate<? super IItem> filter) {
            return (IBankInventoryItem)BANK_INVENTORY.getLast(filter);
        }

        public static IBankInventoryItem getLast(int ... ids) {
            return (IBankInventoryItem)BANK_INVENTORY.getLast(ids);
        }

        public static IBankInventoryItem getLast(String ... names) {
            return (IBankInventoryItem)BANK_INVENTORY.getLast(names);
        }

        public static int getCount(boolean stacks, Predicate<? super IItem> filter) {
            return BANK_INVENTORY.getCount(stacks, filter);
        }

        public static int getCount(boolean stacks, int ... ids) {
            return BANK_INVENTORY.getCount(stacks, ids);
        }

        public static int getCount(boolean stacks, String ... names) {
            return BANK_INVENTORY.getCount(stacks, names);
        }

        public static int getCount(Predicate<? super IItem> filter) {
            return BANK_INVENTORY.getCount(false, filter);
        }

        public static int getCount(int ... ids) {
            return BANK_INVENTORY.getCount(false, ids);
        }

        public static int getCount(String ... names) {
            return BANK_INVENTORY.getCount(false, names);
        }

        public static boolean contains(Predicate<? super IItem> filter) {
            return BANK_INVENTORY.contains(filter);
        }

        public static boolean contains(String ... name) {
            return BANK_INVENTORY.contains(Predicates.names((String[])name));
        }

        public static boolean contains(int ... id) {
            return BANK_INVENTORY.contains(id);
        }

        public static boolean containsAll(int ... ids) {
            for (int id : ids) {
                if (Inventory.contains(id)) continue;
                return false;
            }
            return true;
        }

        public static boolean containsAll(String ... names) {
            for (String name : names) {
                if (Inventory.contains(name)) continue;
                return false;
            }
            return true;
        }
    }

    private static enum WithdrawOption {
        ONE(2),
        FIVE(3),
        TEN(4),
        LAST_QUANTITY(5),
        X(6),
        ALL(7),
        ALL_BUT_1(8);

        private final int menuIndex;

        private WithdrawOption(int menuIndex) {
            this.menuIndex = menuIndex;
        }

        public static WithdrawOption ofAmount(IItem item, int amount) {
            if (amount <= 1) {
                return ONE;
            }
            if (amount == 5) {
                return FIVE;
            }
            if (amount == 10) {
                return TEN;
            }
            if (amount > item.getQuantity()) {
                return ALL;
            }
            return X;
        }

        public int getMenuIndex() {
            if (Bank.getQuantityMode() == QuantityMode.ONE && this == ONE) {
                return 1;
            }
            return this.menuIndex;
        }
    }

    public static enum QuantityMode {
        ONE(Component.BANK_QUANTITY_ONE, 0),
        FIVE(Component.BANK_QUANTITY_FIVE, 1),
        TEN(Component.BANK_QUANTITY_TEN, 2),
        X(Component.BANK_QUANTITY_X, 3),
        ALL(Component.BANK_QUANTITY_ALL, 4),
        UNKNOWN(Component.EMPTY, -1);

        private final Component widget;
        private final int bitValue;

        private QuantityMode(Component widget, int bitValue) {
            this.widget = widget;
            this.bitValue = bitValue;
        }

        public static QuantityMode getCurrent() {
            switch (Vars.getBit(6590)) {
                case 0: {
                    return ONE;
                }
                case 1: {
                    return FIVE;
                }
                case 2: {
                    return TEN;
                }
                case 3: {
                    return X;
                }
                case 4: {
                    return ALL;
                }
            }
            return UNKNOWN;
        }
    }

    public static enum Component {
        BANK_REARRANGE_SWAP(12, 19),
        BANK_REARRANGE_INSERT(12, 21),
        BANK_WITHDRAW_ITEM(12, 24),
        BANK_WITHDRAW_NOTE(12, 26),
        BANK_QUANTITY_BUTTONS_CONTAINER(12, 28),
        BANK_QUANTITY_ONE(12, 30),
        BANK_QUANTITY_FIVE(12, 32),
        BANK_QUANTITY_TEN(12, 34),
        BANK_QUANTITY_X(12, 36),
        BANK_QUANTITY_ALL(12, 38),
        BANK_PLACEHOLDERS_BUTTON(12, 40),
        EMPTY(-1, -1);

        private final int groupId;
        private final int childId;

        private Component(int groupId, int childId) {
            this.groupId = groupId;
            this.childId = childId;
        }
    }
}

