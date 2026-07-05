package net.solace.rscache.map.cache.config;

import net.runelite.cache.ObjectManager;
import net.runelite.cache.definitions.ObjectDefinition;
import net.solace.rscache.map.cache.GameCache;

import java.io.IOException;
import java.util.HashMap;

public class ObjectConfig extends HashMap<Integer, ObjectDefinition> {
    public ObjectConfig(HashMap<Integer, ObjectDefinition> entries) {
        super(entries);
    }

    public static ObjectConfig load(GameCache cache) {
        try {
            var entries = new HashMap<Integer, ObjectDefinition>();
            var manager = new ObjectManager(cache.getStore());
            manager.load();
            for (var object : manager.getObjects()) {
                entries.put(object.getId(), object);
            }

            return new ObjectConfig(entries);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load objects", e);
        }
    }
}
