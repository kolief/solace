package net.solace.api.widgets;

import java.util.List;
import java.util.function.Predicate;
import net.runelite.api.Friend;

public interface IFriends {
    public List<Friend> getAll(Predicate<Friend> var1);

    default public List<Friend> getAll(String ... names) {
        return this.getAll((Friend x) -> {
            if (x.getName() == null) {
                return false;
            }
            for (String name : names) {
                if (!name.equals(x.getName())) continue;
                return true;
            }
            return false;
        });
    }

    default public List<Friend> getAll(int ... worlds) {
        return this.getAll((Friend x) -> {
            for (int world : worlds) {
                if (world != x.getWorld()) continue;
                return true;
            }
            return false;
        });
    }

    default public Friend getFirst(Predicate<Friend> filter) {
        return this.getAll(filter).stream().findFirst().orElse(null);
    }

    default public Friend getFirst(String ... names) {
        return this.getFirst((Friend x) -> {
            if (x.getName() == null) {
                return false;
            }
            for (String name : names) {
                if (!name.equals(x.getName())) continue;
                return true;
            }
            return false;
        });
    }

    default public Friend getFirst(int ... worlds) {
        return this.getFirst((Friend x) -> {
            for (int world : worlds) {
                if (world != x.getWorld()) continue;
                return true;
            }
            return false;
        });
    }

    public boolean isAdded(String var1);

    default public boolean isOnline(Friend friend) {
        return this.isOnline(friend.getName());
    }

    public boolean isOnline(String var1);
}

