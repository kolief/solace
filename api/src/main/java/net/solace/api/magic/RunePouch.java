package net.solace.api.magic;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.solace.api.Static;
import net.solace.api.domain.items.IItem;
import net.solace.api.magic.Rune;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RunePouch {
    private static final Logger log = LoggerFactory.getLogger(RunePouch.class);
    private static final int SLOT_1_TYPE_BIT = 29;
    private static final int SLOT_1_QUANTITY_BIT = 1624;
    private static final int SLOT_2_TYPE_BIT = 1622;
    private static final int SLOT_2_QUANTITY_BIT = 1625;
    private static final int SLOT_3_TYPE_BIT = 1623;
    private static final int SLOT_3_QUANTITY_BIT = 1626;
    private static final int SLOT_4_TYPE_BIT = 14285;
    private static final int SLOT_4_QUANTITY_BIT = 14286;

    public static int getQuantity(Rune rune) {
        if (!RunePouch.hasPouch()) {
            return 0;
        }
        RuneSlot runeSlot = Arrays.stream(RuneSlot.values()).filter(x -> Arrays.stream(rune.getRuneNames()).anyMatch(name -> x.getRuneName() != null && x.getRuneName().startsWith((String)name))).findFirst().orElse(null);
        if (runeSlot == null) {
            return 0;
        }
        return runeSlot.getQuantity();
    }

    public static IItem getPouch() {
        return Static.getInventory().getFirst(item -> item.getName().toLowerCase().contains("rune pouch") && !item.getName().contains("note"));
    }

    public static boolean hasPouch() {
        return RunePouch.getPouch() != null;
    }

    public static boolean isDivine() {
        IItem pouch = RunePouch.getPouch();
        return pouch != null && pouch.getName().toLowerCase().contains("divine");
    }

    public static List<Integer> getRunes() {
        return Arrays.stream(RuneSlot.values()).filter(x -> x.getVarbit() != 0).map(RuneSlot::getRuneId).collect(Collectors.toList());
    }

    public static Map<Integer, Integer> getRuneQuantities() {
        return Arrays.stream(RuneSlot.values()).filter(x -> x.getVarbit() != 0).collect(Collectors.toMap(RuneSlot::getRuneId, RuneSlot::getQuantity));
    }

    public static int getQuantity(int itemId) {
        return RunePouch.getRuneQuantities().getOrDefault(itemId, 0);
    }

    public static boolean isFull() {
        for (RuneSlot s : RuneSlot.values()) {
            if (!RunePouch.isDivine() && s.getType() == 14285 || s.getType() != 0) continue;
            return false;
        }
        return true;
    }

    public static boolean isEmpty() {
        for (RuneSlot s : RuneSlot.values()) {
            if (s.getVarbit() == 0) continue;
            return false;
        }
        return true;
    }

    public static enum RuneSlot {
        FIRST(29, 1624),
        SECOND(1622, 1625),
        THIRD(1623, 1626),
        FOURTH(14285, 14286);

        private final int type;
        private final int quantityVarbitIdx;

        private RuneSlot(int type, int quantityVarbitIdx) {
            this.type = type;
            this.quantityVarbitIdx = quantityVarbitIdx;
        }

        public int getType() {
            return this.type;
        }

        public int getQuantityVarbitIdx() {
            return this.quantityVarbitIdx;
        }

        public int getVarbit() {
            return Static.getVars().getBit(this.type);
        }

        public String getRuneName() {
            switch (this.getVarbit()) {
                case 1: {
                    return "Air rune";
                }
                case 2: {
                    return "Water rune";
                }
                case 3: {
                    return "Earth rune";
                }
                case 4: {
                    return "Fire rune";
                }
                case 5: {
                    return "Mind rune";
                }
                case 6: {
                    return "Chaos rune";
                }
                case 7: {
                    return "Death rune";
                }
                case 8: {
                    return "Blood rune";
                }
                case 9: {
                    return "Cosmic rune";
                }
                case 10: {
                    return "Nature rune";
                }
                case 11: {
                    return "Law rune";
                }
                case 12: {
                    return "Body rune";
                }
                case 13: {
                    return "Soul rune";
                }
                case 14: {
                    return "Astral rune";
                }
                case 15: {
                    return "Mist rune";
                }
                case 16: {
                    return "Mud rune";
                }
                case 17: {
                    return "Dust rune";
                }
                case 18: {
                    return "Lava rune";
                }
                case 19: {
                    return "Steam rune";
                }
                case 20: {
                    return "Smoke rune";
                }
                case 21: {
                    return "Wrath rune";
                }
                case 22: {
                    return "Sunfire rune";
                }
                case 23: {
                    return "Aether rune";
                }
            }
            return null;
        }

        public int getRuneId() {
            switch (this.getVarbit()) {
                case 1: {
                    return 556;
                }
                case 2: {
                    return 555;
                }
                case 3: {
                    return 557;
                }
                case 4: {
                    return 554;
                }
                case 5: {
                    return 558;
                }
                case 6: {
                    return 562;
                }
                case 7: {
                    return 560;
                }
                case 8: {
                    return 565;
                }
                case 9: {
                    return 564;
                }
                case 10: {
                    return 561;
                }
                case 11: {
                    return 563;
                }
                case 12: {
                    return 559;
                }
                case 13: {
                    return 566;
                }
                case 14: {
                    return 9075;
                }
                case 15: {
                    return 4695;
                }
                case 16: {
                    return 4698;
                }
                case 17: {
                    return 4696;
                }
                case 18: {
                    return 4699;
                }
                case 19: {
                    return 4694;
                }
                case 20: {
                    return 4697;
                }
                case 21: {
                    return 21880;
                }
                case 22: {
                    return 28929;
                }
                case 23: {
                    return 30843;
                }
            }
            return -1;
        }

        public int getQuantity() {
            return Static.getVars().getBit(this.quantityVarbitIdx);
        }
    }
}

