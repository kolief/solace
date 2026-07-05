package net.solace.loader.plugins.fighter.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum AntifireType {
    NONE(-1, -1, -1, -1),
    ANTIFIRE(2458, 2456, 2454, 2452),
    EXTENDED_ANTIFIRE(11957, 11955, 11953, 11951),
    SUPER_ANTIFIRE(21987, 21984, 21981, 21987),
    EXTENDED_SUPER_ANTIFIRE(22218, 22215, 22212, 22209);

    private final int dose1;
    private final int dose2;
    private final int dose3;
    private final int dose4;

    public int[] getDoses() {
        return new int[]{dose1, dose2, dose3, dose4};
    }
}