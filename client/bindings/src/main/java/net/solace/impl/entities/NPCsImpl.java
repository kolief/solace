package net.solace.impl.entities;

import lombok.RequiredArgsConstructor;
import net.solace.api.containers.NpcContainer;
import net.solace.api.containers.PlayerContainer;
import net.solace.api.domain.actors.INPC;
import net.solace.api.entities.INPCs;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class NPCsImpl implements INPCs {
    private final NpcContainer npcContainer;
    private final PlayerContainer playerContainer;

    @Override
    public List<INPC> getAll(Predicate<? super INPC> filter) {
        return npcContainer.getAll().stream()
                .filter(Objects::nonNull)
                .filter(filter)
                .collect(Collectors.toList());
    }

    @Override
    public INPC getNearest(Predicate<? super INPC> predicate) {
        return getNearest(playerContainer.getLocalPlayer().getWorldLocation(), predicate);
    }

    @Override
    public INPC getNearest(String... strings) {
        return getNearest(playerContainer.getLocalPlayer().getWorldLocation(), strings);
    }

    @Override
    public INPC getNearest(int... ints) {
        return getNearest(playerContainer.getLocalPlayer().getWorldLocation(), ints);
    }

    @Override
    public INPC getHintArrowed() {
        return npcContainer.getHintArrowed();
    }

    @Override
    public INPC get(int i) {
        return npcContainer.get(i);
    }
}
