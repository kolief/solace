package net.solace.sdk.items;

import java.util.List;
import java.util.function.Predicate;
import net.solace.api.Static;
import net.solace.api.domain.items.IInventoryItem;
import net.solace.api.domain.items.IItem;
import net.solace.api.items.IInventory;

public class Inventory {
    private static final IInventory INVENTORY = Static.getInventory();

    public static List<IInventoryItem> getAll(Predicate<? super IInventoryItem> filter) {
        return INVENTORY.getAll(filter);
    }

    public static List<IInventoryItem> getAll() {
        return Inventory.getAll(x -> true);
    }

    public static List<IInventoryItem> getAll(int ... ids) {
        return INVENTORY.getAll(ids);
    }

    public static List<IInventoryItem> getAll(String ... names) {
        return INVENTORY.getAll(names);
    }

    public static IInventoryItem get(int slot) {
        return (IInventoryItem)INVENTORY.get(slot);
    }

    public static IInventoryItem getFirst(Predicate<? super IInventoryItem> filter) {
        return (IInventoryItem)INVENTORY.getFirst(filter);
    }

    public static IInventoryItem getFirst(int ... ids) {
        return (IInventoryItem)INVENTORY.getFirst(ids);
    }

    public static IInventoryItem getFirst(String ... names) {
        return (IInventoryItem)INVENTORY.getFirst(names);
    }

    public static IInventoryItem getLast(Predicate<? super IInventoryItem> filter) {
        return (IInventoryItem)INVENTORY.getLast(filter);
    }

    public static IInventoryItem getLast(int ... ids) {
        return (IInventoryItem)INVENTORY.getLast(ids);
    }

    public static IInventoryItem getLast(String ... names) {
        return (IInventoryItem)INVENTORY.getLast(names);
    }

    public static boolean contains(Predicate<? super IItem> filter) {
        return INVENTORY.contains(filter);
    }

    public static boolean contains(int ... id) {
        return INVENTORY.contains(id);
    }

    public static boolean contains(String ... name) {
        return INVENTORY.contains(name);
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

    public static int getCount(boolean stacks, Predicate<? super IInventoryItem> filter) {
        return INVENTORY.getCount(stacks, filter);
    }

    public static int getCount(boolean stacks, int ... ids) {
        return INVENTORY.getCount(stacks, ids);
    }

    public static int getCount(boolean stacks, String ... names) {
        return INVENTORY.getCount(stacks, names);
    }

    public static int getCount(Predicate<? super IInventoryItem> filter) {
        return INVENTORY.getCount(false, filter);
    }

    public static int getCount(int ... ids) {
        return INVENTORY.getCount(false, ids);
    }

    public static int getCount(String ... names) {
        return INVENTORY.getCount(false, names);
    }

    public static boolean isFull() {
        return INVENTORY.isFull();
    }

    public static boolean isEmpty() {
        return Inventory.getAll().isEmpty();
    }

    public static int getFreeSlots() {
        return INVENTORY.getFreeSlots();
    }
}

