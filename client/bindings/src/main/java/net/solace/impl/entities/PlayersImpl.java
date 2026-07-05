package net.solace.impl.entities;

import lombok.RequiredArgsConstructor;
import net.solace.api.containers.PlayerContainer;
import net.solace.api.domain.actors.IPlayer;
import net.solace.api.entities.IPlayers;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class PlayersImpl implements IPlayers {
    private final PlayerContainer playerContainer;

    @Override
    public IPlayer get(int i) {
        return playerContainer.get(i);
    }

    @Override
    public IPlayer getLocal() {
        return playerContainer.getLocalPlayer();
    }

    @Override
    public List<IPlayer> getAll(Predicate<? super IPlayer> filter) {
        return playerContainer.getAll().stream()
                .filter(Objects::nonNull)
                .filter(filter)
                .collect(Collectors.toList());
    }

    @Override
    public IPlayer getNearest(Predicate<? super IPlayer> filter) {
        return getNearest(playerContainer.getLocalPlayer().getWorldLocation(), filter);
    }

    @Override
    public IPlayer getNearest(String... names) {
        return getNearest(playerContainer.getLocalPlayer().getWorldLocation(), names);
    }

    @Override
    public IPlayer getNearest(int... ids) {
        return getNearest(playerContainer.getLocalPlayer().getWorldLocation(), ids);
    }

    @Override
    public IPlayer getHintArrowed() {
        return playerContainer.getHintArrowed();
    }
}
