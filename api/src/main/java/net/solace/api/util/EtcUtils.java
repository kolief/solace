package net.solace.api.util;

import java.util.Arrays;
import java.util.List;

public class EtcUtils {
    public static boolean containsItem(int[] array, List<Integer> values) {
        return values.stream().anyMatch(id -> Arrays.stream(array).anyMatch(itemId -> itemId == id));
    }
}

