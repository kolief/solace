package net.solace.rscache.dump;

import com.google.common.io.Files;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import net.runelite.cache.ConfigType;
import net.runelite.cache.IndexType;
import net.runelite.cache.definitions.loaders.EnumLoader;
import net.runelite.cache.fs.Store;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import static net.solace.rscache.Constants.DUMP_DIR;
import static net.solace.rscache.Constants.OSRS_CACHE_DIR;

@Slf4j
public class EnumDumper {
    @SuppressWarnings({"UnstableApiUsage", "ResultOfMethodCallIgnored"})
    public static void main(String[] args) throws IOException {
        var dumpDir = new File(DUMP_DIR, "enums");
        dumpDir.mkdir();

        var gson = new GsonBuilder().setPrettyPrinting().create();

        var count = 0;

        try (var store = new Store(OSRS_CACHE_DIR)) {
            store.load();

            var storage = store.getStorage();
            var index = store.getIndex(IndexType.CONFIGS);
            var archive = index.getArchive(ConfigType.ENUM.getId());

            var archiveData = storage.loadArchive(archive);
            var files = archive.getFiles(archiveData);

            var loader = new EnumLoader();

            for (var file : files.getFiles()) {
                var b = file.getContents();

                var def = loader.load(file.getFileId(), b);

                if (def != null) {
                    Files.asCharSink(new File(dumpDir, file.getFileId() + ".json"), Charset.defaultCharset()).write(gson.toJson(def));
                    ++count;
                }
            }
        }

        log.info("Dumped {} enums to {}", count, dumpDir);
    }
}
