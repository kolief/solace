package net.solace.sdk.widgets;

import java.util.List;
import java.util.function.Predicate;
import net.runelite.api.Friend;
import net.solace.api.Static;
import net.solace.api.widgets.IFriends;

public class Friends {
    private static final IFriends FRIENDS = Static.getFriends();

    public static List<Friend> getAll(Predicate<Friend> filter) {
        return FRIENDS.getAll(filter);
    }

    public static List<Friend> getAll(String ... names) {
        return FRIENDS.getAll(names);
    }

    public static List<Friend> getAll(int ... worlds) {
        return FRIENDS.getAll(worlds);
    }

    public static Friend getFirst(Predicate<Friend> filter) {
        return FRIENDS.getFirst(filter);
    }

    public static Friend getFirst(String ... names) {
        return FRIENDS.getFirst(names);
    }

    public static Friend getFirst(int ... worlds) {
        return FRIENDS.getFirst(worlds);
    }

    public static boolean isAdded(String name) {
        return FRIENDS.isAdded(name);
    }

    public static boolean isOnline(Friend friend) {
        return FRIENDS.isOnline(friend);
    }

    public static boolean isOnline(String name) {
        return FRIENDS.isOnline(name);
    }
}

