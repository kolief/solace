package net.solace.ui.plugins.items;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import net.solace.api.plugins.config.ItemConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class Definitions {
    private final Map<Integer, ItemConfig> items;
    private final Gson gson;

    public Definitions(Gson gson) {
        this.gson = gson;
        this.items = loadItems();
        log.debug("Loaded {} items", items.size());
    }

    public List<ItemConfig> searchItem(String name) {
        if (name == null || name.isEmpty()) {
            return Collections.emptyList();
        }

        return items.values()
                .stream()
                .filter(item -> item.getName().toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());
    }

    public ItemConfig getItem(int id) {
        return items.get(id);
    }

    private Map<Integer, ItemConfig> loadItems() {
        var resourceAsStream = Definitions.class.getResourceAsStream("items-min.json");
        if (resourceAsStream == null) {
            log.error("Failed to load items");
            return Collections.emptyMap();
        }

        try (var reader = new BufferedReader(new InputStreamReader(resourceAsStream))) {
            List<ItemConfig> items = gson.fromJson(reader, new TypeToken<List<ItemConfig>>() {
            }.getType());
            return items.stream().collect(Collectors.toMap(ItemConfig::getId, item -> item));
        } catch (IOException e) {
            log.error("Failed to load items", e);
            return Collections.emptyMap();
        }
    }
}
