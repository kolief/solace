package net.solace.impl.widgets;

import lombok.RequiredArgsConstructor;
import net.runelite.api.Friend;
import net.solace.api.domain.game.IClient;
import net.solace.api.widgets.IFriends;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class FriendsImpl implements IFriends {
    private final IClient client;

    @Override
    public List<Friend> getAll(Predicate<Friend> filter) {
        return Arrays.stream(client.getFriendContainer().getMembers())
                .filter(filter)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isAdded(String name) {
        return client.isFriended(name, false);
    }

    @Override
    public boolean isOnline(String name) {
        return client.isFriended(name, true);
    }
}
