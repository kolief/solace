package net.solace.sdk.items;

import java.util.List;
import java.util.function.Predicate;
import net.solace.api.Static;
import net.solace.api.domain.items.IItem;
import net.solace.api.items.IEquipment;
import net.solace.api.widgets.EquipmentSlot;

public class Equipment {
    private static final IEquipment EQUIPMENT = Static.getEquipment();

    public static IItem fromSlot(EquipmentSlot slot) {
        return EQUIPMENT.fromSlot(slot);
    }

    public static List<IItem> getAll(Predicate<? super IItem> filter) {
        return EQUIPMENT.getAll(filter);
    }

    public static List<IItem> getAll(int ... ids) {
        return EQUIPMENT.getAll(ids);
    }

    public static List<IItem> getAll(String ... names) {
        return EQUIPMENT.getAll(names);
    }

    public static List<IItem> getAll() {
        return EQUIPMENT.getAll(x -> true);
    }

    public static IItem get(int slot) {
        return EQUIPMENT.get(slot);
    }

    public static IItem getFirst(Predicate<? super IItem> filter) {
        return EQUIPMENT.getFirst(filter);
    }

    public static IItem getFirst(int ... ids) {
        return EQUIPMENT.getFirst(ids);
    }

    public static IItem getFirst(String ... names) {
        return EQUIPMENT.getFirst(names);
    }

    public static IItem getLast(Predicate<? super IItem> filter) {
        return EQUIPMENT.getLast(filter);
    }

    public static IItem getLast(int ... ids) {
        return EQUIPMENT.getLast(ids);
    }

    public static IItem getLast(String ... names) {
        return EQUIPMENT.getLast(names);
    }

    public static int getCount(boolean stacks, Predicate<? super IItem> filter) {
        return EQUIPMENT.getCount(stacks, filter);
    }

    public static int getCount(boolean stacks, int ... ids) {
        return EQUIPMENT.getCount(stacks, ids);
    }

    public static int getCount(boolean stacks, String ... names) {
        return EQUIPMENT.getCount(stacks, names);
    }

    public static int getCount(Predicate<? super IItem> filter) {
        return EQUIPMENT.getCount(filter);
    }

    public static int getCount(int ... ids) {
        return EQUIPMENT.getCount(ids);
    }

    public static int getCount(String ... names) {
        return EQUIPMENT.getCount(names);
    }

    public static boolean contains(Predicate<? super IItem> filter) {
        return EQUIPMENT.contains(filter);
    }

    public static boolean contains(int ... id) {
        return EQUIPMENT.contains(id);
    }

    public static boolean contains(String ... name) {
        return EQUIPMENT.contains(name);
    }

    public static boolean containsAll(int ... ids) {
        return EQUIPMENT.containsAll(ids);
    }

    public static boolean containsAll(String ... names) {
        return EQUIPMENT.containsAll(names);
    }

    public static boolean containsAll(Predicate<? super IItem> filter) {
        return EQUIPMENT.containsAll(filter);
    }
}

