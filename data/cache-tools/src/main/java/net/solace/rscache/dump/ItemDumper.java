package net.solace.rscache.dump;

import com.google.gson.GsonBuilder;
import lombok.Value;
import net.runelite.cache.ItemManager;
import net.runelite.cache.fs.Store;
import net.solace.rscache.Constants;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import static net.solace.rscache.Constants.OSRS_CACHE_DIR;

public class ItemDumper {
    public static void main(String[] args) throws IOException {
        var store = new Store(OSRS_CACHE_DIR);
        store.load();

        var itemsDir = new File(Constants.DUMP_DIR, "items");
        var dumper = new ItemManager(store);
        dumper.load();
        dumper.export(itemsDir);
        exportMin(dumper, itemsDir);
        dumper.java(itemsDir);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static void exportMin(ItemManager itemManager, File outDir) throws IOException {
        outDir.mkdirs();

        var minItems = new ArrayList<MinItem>();

        for (var def : itemManager.getItems()) {
            var noteId = def.getNotedID();
            var name = def.getName();
            while ("null".equalsIgnoreCase(name) && noteId != -1) {
                name = itemManager.getItem(noteId).getName();
            }

            if ("null".equalsIgnoreCase(name)) {
                continue;
            }

            minItems.add(new MinItem(def.getId(), name));
        }

        var targ = new File(outDir, "items-min.json");
        try (var fos = new FileOutputStream(targ);
             var writer = new OutputStreamWriter(fos);
             var bw = new BufferedWriter(writer)) {
            bw.write(new GsonBuilder().setPrettyPrinting().create().toJson(minItems));
        }
    }

    @Value
    private static class MinItem {
        int id;
        String name;
    }
}
