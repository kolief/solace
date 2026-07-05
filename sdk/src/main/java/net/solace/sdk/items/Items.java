package net.solace.sdk.items;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.solace.api.commons.Predicates;
import net.solace.api.domain.items.IItem;
import net.solace.sdk.items.Bank;
import net.solace.sdk.items.Equipment;
import net.solace.sdk.items.Inventory;

public class Items {
    public static List<IItem> getAll(Predicate<? super IItem> filter) {
        return Stream.of(Inventory.getAll(filter), Equipment.getAll(filter), Bank.getAll(filter), Bank.Inventory.getAll(filter)).flatMap(Collection::stream).collect(Collectors.toList());
    }

    public static List<IItem> getAll(String ... names) {
        return Items.getAll(Predicates.names((String[])names));
    }

    public static List<IItem> getAll(int ... ids) {
        return Items.getAll(Predicates.ids((int[])ids));
    }

    public static IItem getFirst(Predicate<? super IItem> filter) {
        return Items.getAll(filter).stream().findFirst().orElse(null);
    }

    public static IItem getFirst(int ... ids) {
        return Items.getFirst(Predicates.ids((int[])ids));
    }

    public static IItem getFirst(String ... names) {
        return Items.getFirst(Predicates.names((String[])names));
    }

    public static IItem getLast(Predicate<? super IItem> filter) {
        return Items.getAll(filter).stream().max(Comparator.comparingInt(IItem::getSlot)).orElse(null);
    }

    public static IItem getLast(int ... ids) {
        return Items.getLast(Predicates.ids((int[])ids));
    }

    public static IItem getLast(String ... names) {
        return Items.getLast(Predicates.names((String[])names));
    }

    public static boolean isCarrying(Predicate<? super IItem> filter) {
        return Inventory.contains(filter) || Equipment.contains(filter);
    }

    public static boolean isCarrying(String ... names) {
        return Items.isCarrying(Predicates.names((String[])names));
    }

    public static boolean isCarrying(int ... ids) {
        return Items.isCarrying(Predicates.ids((int[])ids));
    }

    public static boolean contains(Predicate<? super IItem> filter) {
        return Items.getFirst(filter) != null;
    }

    public static boolean contains(String ... name) {
        return Items.getFirst(name) != null;
    }

    public static boolean contains(int ... id) {
        return Items.getFirst(id) != null;
    }

    public static List<IItem> getCarrying(Predicate<? super IItem> filter) {
        return Stream.of(Inventory.getAll(filter), Equipment.getAll(filter), Bank.Inventory.getAll(filter)).flatMap(Collection::stream).collect(Collectors.toList());
    }

    public static List<IItem> getCarrying(String ... names) {
        return Items.getCarrying(Predicates.names((String[])names));
    }

    public static List<IItem> getCarrying(int ... ids) {
        return Items.getCarrying(Predicates.ids((int[])ids));
    }
}

