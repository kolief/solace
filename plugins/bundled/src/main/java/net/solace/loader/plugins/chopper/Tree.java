package net.solace.loader.plugins.chopper;

import lombok.Getter;

@Getter
public enum Tree {
    REGULAR(1, "Tree", "Evergreen"),
    OAK(15, "Oak", "Oak tree"),
    WILLOW(30, "Willow", "Willow tree"),
    TEAK(35, "Teak", "Teak tree"),
    MAPLE(45, "Maple tree"),
    MAHOGANY(50, "Mahogany", "Mahogany tree"),
    YEW(60, "Yew", "Yew tree"),
    BLISTERWOOD(62, "Blisterwood tree"),
    MAGIC(75, "Magic tree"),
    REDWOOD(90, "Redwood tree");

    private final int level;
    private final String[] names;

    Tree(int level, String... names) {
        this.level = level;
        this.names = names;
    }
}
