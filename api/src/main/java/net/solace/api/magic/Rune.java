package net.solace.api.magic;

import java.util.Arrays;
import net.solace.api.Static;
import net.solace.api.domain.items.IInventoryItem;
import net.solace.api.magic.RunePouch;

public enum Rune {
    AIR(556, "Air", "Smoke", "Mist", "Dust"),
    EARTH(557, "Earth", "Lava", "Mud", "Dust"),
    FIRE(554, "Fire", "Lava", "Smoke", "Steam", "Sunfire"),
    WATER(555, "Water", "Mud", "Steam", "Mist"),
    MIND(558, "Mind"),
    BODY(559, "Body"),
    COSMIC(564, "Cosmic", "Aether"),
    CHAOS(562, "Chaos"),
    NATURE(561, "Nature"),
    LAW(563, "Law"),
    DEATH(560, "Death"),
    ASTRAL(9075, "Astral"),
    BLOOD(565, "Blood"),
    SOUL(566, "Soul", "Aether"),
    WRATH(21880, "Wrath");

    private final int runeId;
    private final String[] runeNames;

    private Rune(int runeId, String ... runeNames) {
        this.runeId = runeId;
        this.runeNames = runeNames;
    }

    public int getQuantity() {
        if (this.isKodaiEquipped() && this == WATER) {
            return Integer.MAX_VALUE;
        }
        if (this.isTwinflameEquipped() && (this == WATER || this == FIRE)) {
            return Integer.MAX_VALUE;
        }
        if (this.isByroEquipped() && this == NATURE) {
            return Integer.MAX_VALUE;
        }
        if (this.isStaffEquipped() || this.isTomeEquipped()) {
            return Integer.MAX_VALUE;
        }
        IInventoryItem rune = Static.getInventory().getFirst(x -> x.getName() != null && x.getName().contains("rune") && Arrays.stream(this.runeNames).anyMatch(name -> x.getId() == this.runeId || x.getName().contains((CharSequence)name)));
        if (rune == null) {
            return RunePouch.getQuantity(this);
        }
        return rune.getQuantity() + RunePouch.getQuantity(this);
    }

    private boolean isStaffEquipped() {
        return Static.getEquipment().contains(x -> x.getName() != null && x.getName().toLowerCase().contains("staff") && Arrays.stream(this.runeNames).anyMatch(n -> x.getName().toLowerCase().contains(n.toLowerCase())));
    }

    private boolean isTomeEquipped() {
        return Static.getEquipment().contains(x -> x.getName() != null && x.getName().startsWith("Tome of") && !x.getName().endsWith("(empty") && Arrays.stream(this.runeNames).anyMatch(n -> x.getName().toLowerCase().contains(n.toLowerCase())));
    }

    private boolean isKodaiEquipped() {
        return Static.getEquipment().contains(21006, 23626);
    }

    private boolean isTwinflameEquipped() {
        return Static.getEquipment().contains(30634);
    }

    private boolean isByroEquipped() {
        return Static.getEquipment().contains(22370);
    }

    public int getRuneId() {
        return this.runeId;
    }

    public String[] getRuneNames() {
        return this.runeNames;
    }
}

