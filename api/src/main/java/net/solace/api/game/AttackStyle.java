package net.solace.api.game;

import java.util.Arrays;
import net.solace.api.widgets.InterfaceAddress;

public enum AttackStyle {
    FIRST(0, 38862854),
    SECOND(1, 38862858),
    THIRD(2, 38862862),
    FOURTH(3, 38862866),
    SPELLS(4, 38862876),
    SPELLS_DEFENSIVE(4, 38862871),
    UNKNOWN(-1, -1);

    private final int index;
    private final int component;

    public static AttackStyle fromIndex(int index) {
        return Arrays.stream(AttackStyle.values()).filter(x -> x.index == index).findFirst().orElse(UNKNOWN);
    }

    @Deprecated(forRemoval=true)
    public InterfaceAddress getInterfaceAddress() {
        return new InterfaceAddress(this.component);
    }

    public int getIndex() {
        return this.index;
    }

    public int getComponent() {
        return this.component;
    }

    private AttackStyle(int index, int component) {
        this.index = index;
        this.component = component;
    }
}

