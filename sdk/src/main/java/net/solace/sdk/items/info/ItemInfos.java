package net.solace.sdk.items.info;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import net.solace.sdk.items.info.ItemInfo;

public class ItemInfos {
    private static final Map<Integer, ItemInfo> DEFINITIONS = new HashMap<Integer, ItemInfo>();
    private static final List<Map.Entry<Integer, List<Integer>>> SKINS_LIST = List.of(Map.entry(22325, List.of(Integer.valueOf(22664), Integer.valueOf(25736), Integer.valueOf(25739))));
    private static final Map<Integer, Integer> SKIN_MAP = SKINS_LIST.stream()
            .flatMap(e -> e.getValue().stream().map(skinId -> Map.entry(skinId, e.getKey())))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    public static ItemInfo lookup(int itemId) {
        int overrideId = SKIN_MAP.getOrDefault(itemId, itemId);
        return DEFINITIONS.get(overrideId);
    }

    static {
        try (InputStreamReader reader = new InputStreamReader(Objects.requireNonNull(ItemInfos.class.getResourceAsStream("/ItemInfos.json")));){
            ItemInfo[] definitions;
            Gson gson = new Gson();
            for (ItemInfo def : definitions = (ItemInfo[])gson.fromJson((Reader)reader, ItemInfo[].class)) {
                DEFINITIONS.put(def.getId(), def);
            }
        }
        catch (IOException e2) {
            throw new RuntimeException(e2);
        }
    }
}

