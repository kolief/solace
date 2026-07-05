package net.solace.api.prayer;

import java.util.HashMap;
import java.util.Map;
import net.runelite.api.Prayer;
import net.solace.api.widgets.InterfaceAddress;

public enum PrayerInfo {
    THICK_SKIN(35454985, Prayer.THICK_SKIN),
    BURST_OF_STRENGTH(35454986, Prayer.BURST_OF_STRENGTH),
    CLARITY_OF_THOUGHT(35454987, Prayer.CLARITY_OF_THOUGHT),
    ROCK_SKIN(35454988, Prayer.ROCK_SKIN),
    SUPERHUMAN_STRENGTH(35454989, Prayer.SUPERHUMAN_STRENGTH),
    IMPROVED_REFLEXES(35454990, Prayer.IMPROVED_REFLEXES),
    RAPID_RESTORE(35454991, Prayer.RAPID_RESTORE),
    RAPID_HEAL(35454992, Prayer.RAPID_HEAL),
    PROTECT_ITEM(35454993, Prayer.PROTECT_ITEM),
    STEEL_SKIN(35454994, Prayer.STEEL_SKIN),
    ULTIMATE_STRENGTH(35454995, Prayer.ULTIMATE_STRENGTH),
    INCREDIBLE_REFLEXES(35454996, Prayer.INCREDIBLE_REFLEXES),
    PROTECT_FROM_MAGIC(35454997, Prayer.PROTECT_FROM_MAGIC),
    PROTECT_FROM_MISSILES(35454998, Prayer.PROTECT_FROM_MISSILES),
    PROTECT_FROM_MELEE(35454999, Prayer.PROTECT_FROM_MELEE),
    RETRIBUTION(35455000, Prayer.RETRIBUTION),
    REDEMPTION(35455001, Prayer.REDEMPTION),
    SMITE(35455002, Prayer.SMITE),
    SHARP_EYE(35455003, Prayer.SHARP_EYE),
    HAWK_EYE(35455004, Prayer.HAWK_EYE),
    EAGLE_EYE(35455005, Prayer.EAGLE_EYE),
    DEADEYE(35455005, Prayer.DEADEYE),
    MYSTIC_WILL(35455006, Prayer.MYSTIC_WILL),
    MYSTIC_LORE(35455007, Prayer.MYSTIC_LORE),
    MYSTIC_MIGHT(35455008, Prayer.MYSTIC_MIGHT),
    MYSTIC_VIGOUR(35455008, Prayer.MYSTIC_VIGOUR),
    RIGOUR(35455009, Prayer.RIGOUR),
    CHIVALRY(35455010, Prayer.CHIVALRY),
    PIETY(35455011, Prayer.PIETY),
    AUGURY(35455012, Prayer.AUGURY),
    PRESERVE(35455013, Prayer.PRESERVE);

    public static final Map<Prayer, PrayerInfo> MAP;
    private final int component;
    private final Prayer prayer;

    @Deprecated(forRemoval=true)
    public InterfaceAddress getInterfaceAddress() {
        return new InterfaceAddress(this.component);
    }

    private PrayerInfo(int component, Prayer prayer) {
        this.component = component;
        this.prayer = prayer;
    }

    public int getComponent() {
        return this.component;
    }

    public Prayer getPrayer() {
        return this.prayer;
    }

    static {
        MAP = new HashMap<Prayer, PrayerInfo>();
        for (PrayerInfo prayerInfo : PrayerInfo.values()) {
            MAP.put(prayerInfo.getPrayer(), prayerInfo);
        }
    }
}

