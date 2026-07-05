package net.solace.api.items;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import net.runelite.api.coords.WorldArea;
import net.solace.api.commons.Predicates;
import net.solace.api.domain.items.IBankItem;
import net.solace.api.domain.items.IItem;
import net.solace.api.domain.widgets.IWidget;
import net.solace.api.items.IItems;
import net.solace.api.items.WithdrawMode;
import net.solace.api.movement.pathfinder.model.BankLocation;

public interface IBank
extends IItems<IBankItem> {
    public boolean isOpen();

    public void open(WorldArea var1);

    public void open(BankLocation var1);

    public void close();

    public boolean isMainTabOpen();

    public boolean isTabOpen(int var1);

    public void openMainTab();

    public void openTab(int var1);

    public List<IWidget> getTabs();

    public void setWithdrawMode(boolean var1);

    public boolean isNotedWithdrawMode();

    public void depositInventory();

    public void depositEquipment();

    public void emptyContainers();

    public void withdraw(Predicate<IItem> var1, int var2, WithdrawMode var3, boolean var4);

    public void deposit(Predicate<? super IItem> var1, int var2, boolean var3);

    default public void depositAll(String ... names) {
        this.depositAll(Predicates.names(names));
    }

    default public void depositAll(int ... ids) {
        this.depositAll(Predicates.ids(ids));
    }

    default public void depositAll(Predicate<? super IItem> filter) {
        this.deposit(filter, Integer.MAX_VALUE, false);
    }

    default public void depositAllExcept(String ... names) {
        this.depositAllExcept(Predicates.names(names));
    }

    default public void depositAllExcept(int ... ids) {
        this.depositAllExcept(Predicates.ids(ids));
    }

    default public void depositAllExcept(Predicate<? super IItem> filter) {
        this.depositAll(filter.negate());
    }

    default public void withdrawAll(String name) {
        this.withdrawAll(name, WithdrawMode.DEFAULT);
    }

    default public void withdrawAll(String name, WithdrawMode withdrawMode) {
        this.withdrawAll((IItem x) -> Objects.equals(x.getName(), name), withdrawMode);
    }

    default public void withdrawAll(int id) {
        this.withdrawAll(id, WithdrawMode.DEFAULT);
    }

    default public void withdrawAll(int id, WithdrawMode withdrawMode) {
        this.withdrawAll((IItem x) -> x.getId() == id, withdrawMode);
    }

    default public void withdrawAll(Predicate<IItem> filter) {
        this.withdrawAll(filter, WithdrawMode.DEFAULT);
    }

    default public void withdrawAll(Predicate<IItem> filter, WithdrawMode withdrawMode) {
        this.withdraw(filter, Integer.MAX_VALUE, withdrawMode, false);
    }

    default public void withdraw(String name, int amount, boolean quick) {
        this.withdraw(name, amount, WithdrawMode.DEFAULT, quick);
    }

    default public void withdraw(String name, int amount) {
        this.withdraw(name, amount, WithdrawMode.DEFAULT, false);
    }

    default public void withdraw(String name, int amount, WithdrawMode withdrawMode, boolean quick) {
        this.withdraw((IItem x) -> Objects.equals(x.getName(), name), amount, withdrawMode, quick);
    }

    default public void withdraw(String name, int amount, WithdrawMode withdrawMode) {
        this.withdraw((IItem x) -> Objects.equals(x.getName(), name), amount, withdrawMode, false);
    }

    default public void withdraw(int id, int amount, boolean quick) {
        this.withdraw(id, amount, WithdrawMode.DEFAULT, quick);
    }

    default public void withdraw(int id, int amount) {
        this.withdraw(id, amount, WithdrawMode.DEFAULT, false);
    }

    default public void withdraw(int id, int amount, WithdrawMode withdrawMode, boolean quick) {
        this.withdraw((IItem x) -> x.getId() == id, amount, withdrawMode, quick);
    }

    default public void withdraw(int id, int amount, WithdrawMode withdrawMode) {
        this.withdraw((IItem x) -> x.getId() == id, amount, withdrawMode, false);
    }

    default public void withdraw(Predicate<IItem> filter, int amount, boolean quick) {
        this.withdraw(filter, amount, WithdrawMode.DEFAULT, quick);
    }

    default public void withdraw(Predicate<IItem> filter, int amount) {
        this.withdraw(filter, amount, WithdrawMode.DEFAULT, false);
    }

    default public void withdraw(Predicate<IItem> filter, int amount, WithdrawMode withdrawMode) {
        this.withdraw(filter, amount, withdrawMode, false);
    }

    default public void deposit(String name, int amount, boolean quick) {
        this.deposit(x -> Objects.equals(x.getName(), name), amount, quick);
    }

    default public void deposit(int id, int amount, boolean quick) {
        this.deposit(x -> x.getId() == id, amount, quick);
    }

    default public void deposit(String name, int amount) {
        this.deposit(x -> Objects.equals(x.getName(), name), amount, false);
    }

    default public void deposit(int id, int amount) {
        this.deposit(x -> x.getId() == id, amount, false);
    }

    default public void deposit(Predicate<? super IItem> filter, int amount) {
        this.deposit(filter, amount, false);
    }
}

