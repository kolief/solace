package net.solace.rscache.map.engine.model.collision;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
public enum Direction {
    NORTH(0x2),
    WEST(0x80),
    SOUTH(0x20),
    EAST(0x8)
    ;

    private final int flag;
}
