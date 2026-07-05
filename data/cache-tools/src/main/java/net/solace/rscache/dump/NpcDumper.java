package net.solace.rscache.dump;

import net.runelite.cache.NpcManager;
import net.runelite.cache.fs.Store;
import net.solace.rscache.Constants;

import java.io.File;
import java.io.IOException;

import static net.solace.rscache.Constants.OSRS_CACHE_DIR;

public class NpcDumper {
    public static void main(String[] args) throws IOException {
        var store = new Store(OSRS_CACHE_DIR);
        store.load();

        var npcsDir = new File(Constants.DUMP_DIR, "npcs");

        var dumper = new NpcManager(store);
        dumper.load();
        dumper.dump(npcsDir);
        dumper.java(npcsDir);
    }
}
