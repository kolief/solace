package net.solace.sdk.items;

import java.util.List;
import java.util.function.Predicate;
import net.solace.api.Static;
import net.solace.api.domain.items.IItem;
import net.solace.api.items.ITrade;

public class Trade {
    private static final ITrade TRADE = Static.getTrade();

    public static boolean isOpen() {
        return TRADE.isOpen();
    }

    public static boolean isSecondScreenOpen() {
        return TRADE.isSecondScreenOpen();
    }

    public static boolean isFirstScreenOpen() {
        return TRADE.isFirstScreenOpen();
    }

    public static void accept() {
        TRADE.accept();
    }

    public static void acceptFirstScreen() {
        TRADE.acceptFirstScreen();
    }

    public static void acceptSecondScreen() {
        TRADE.acceptSecondScreen();
    }

    public static void decline() {
        TRADE.decline();
    }

    public static void declineFirstScreen() {
        TRADE.declineFirstScreen();
    }

    public static void declineSecondScreen() {
        TRADE.declineSecondScreen();
    }

    public static boolean hasAccepted(boolean them) {
        return TRADE.hasAccepted(them);
    }

    public static boolean hasAcceptedFirstScreen(boolean them) {
        return TRADE.hasAcceptedFirstScreen(them);
    }

    public static boolean hasAcceptedSecondScreen(boolean them) {
        return TRADE.hasAcceptedSecondScreen(them);
    }

    public static void offer(Predicate<IItem> filter, int quantity, boolean quick) {
        TRADE.offer(filter, quantity, quick);
    }

    public static void offer(Predicate<IItem> filter, int quantity) {
        TRADE.offer(filter, quantity);
    }

    public static void offer(int id, int quantity) {
        TRADE.offer(id, quantity);
    }

    public static void offer(int id, int quantity, boolean quick) {
        TRADE.offer(id, quantity, quick);
    }

    public static void offer(String name, int quantity) {
        TRADE.offer(name, quantity);
    }

    public static void offer(String name, int quantity, boolean quick) {
        TRADE.offer(name, quantity, quick);
    }

    public static List<IItem> getAll(boolean theirs, Predicate<? super IItem> filter) {
        return TRADE.getAll(theirs, filter);
    }

    public static List<IItem> getInventory(Predicate<IItem> filter) {
        return TRADE.getInventory(filter);
    }

    public static List<IItem> getAll(boolean theirs) {
        return TRADE.getAll(theirs);
    }

    public static List<IItem> getAll(boolean theirs, int ... ids) {
        return TRADE.getAll(theirs, ids);
    }

    public static List<IItem> getAll(boolean theirs, String ... names) {
        return TRADE.getAll(theirs, names);
    }

    public static IItem getFirst(boolean theirs, Predicate<IItem> filter) {
        return TRADE.getFirst(theirs, filter);
    }

    public static IItem getFirst(boolean theirs, int ... ids) {
        return TRADE.getFirst(theirs, ids);
    }

    public static IItem getFirst(boolean theirs, String ... names) {
        return TRADE.getFirst(theirs, names);
    }

    public static boolean contains(boolean theirs, Predicate<IItem> filter) {
        return TRADE.contains(theirs, filter);
    }

    public static boolean contains(boolean theirs, int ... ids) {
        return TRADE.contains(theirs, ids);
    }

    public static boolean contains(boolean theirs, String ... names) {
        return TRADE.contains(theirs, names);
    }

    public static String getTradingPlayer() {
        return TRADE.getTradingPlayer();
    }
}

