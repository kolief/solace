package net.solace.rscache.dump;

import net.runelite.cache.ObjectManager;
import net.runelite.cache.fs.Store;
import net.solace.rscache.Constants;

import java.io.File;
import java.io.IOException;

import static net.solace.rscache.Constants.OSRS_CACHE_DIR;

public class ObjectDumper {
    public static void main(String[] args) throws IOException {
        var store = new Store(OSRS_CACHE_DIR);
        store.load();

        var objectsDir = new File(Constants.DUMP_DIR, "objects");

        var dumper = new ObjectManager(store);
        dumper.load();
        dumper.dump(objectsDir);
        dumper.java(objectsDir);
    }
}
