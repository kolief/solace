package net.solace.rscache.map.cache;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.runelite.cache.fs.Store;
import net.runelite.cache.fs.jagex.DiskStorage;
import net.solace.rscache.map.xtea.XteaConfig;
import net.solace.rscache.util.FileUtils;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okio.Okio;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static net.solace.rscache.Constants.OPENRS2_CACHE_VERSION;

@Slf4j
@Data
public class GameCache {
    private static final String DOWNLOAD_URL = "https://archive.openrs2.org/caches/runescape/"
            + OPENRS2_CACHE_VERSION + "/disk.zip";

    private final OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .connectTimeout(Duration.ofMinutes(2))
            .callTimeout(Duration.ofMinutes(15))
            .readTimeout(Duration.ofMinutes(15))
            .build();

    private Store store;

    private ConfigArchive configs;
    private MapArchive maps;

    public void load(XteaConfig xteaConfig) throws IOException {
        var cacheZip = Files.createTempFile("cache", ".zip");
        downloadCacheZipFile(cacheZip);
        log.info("Downloaded cache zip");

        var unzipDir = Files.createTempDirectory("cache");
        unzipIntoDir(cacheZip, unzipDir);
        log.info("Unzipped cache into {}", unzipDir);

        var cacheDir = unzipDir.resolve("cache");

        try (var cache = new Store(cacheDir.toFile())) {
            store = cache;
            store.load();

            configs = ConfigArchive.load(this);
            log.info("Loaded {} objects", configs.getObjects().size());
            maps = MapArchive.load(this, xteaConfig);
            log.info("Loaded {} maps", maps.size());
        } catch (IOException e) {
            throw new RuntimeException("Failed to load gamecache", e);
        } finally {
            Files.deleteIfExists(cacheZip);
            FileUtils.deleteRecursively(unzipDir);
            log.info("Deleted cache files after usage");
        }
    }

    public DiskStorage getDisk() {
        return (DiskStorage) store.getStorage();
    }

    public int getArchiveCount() {
        return store.getIndexes().size();
    }

    private void downloadCacheZipFile(Path file) {
        var request = new Request.Builder()
                .url(DOWNLOAD_URL)
                .get()
                .build();

        log.info("Attempting to download cache from {}", DOWNLOAD_URL);
        try (var response = okHttpClient.newCall(request).execute()) {
            var body = response.body();
            if (body == null) {
                throw new IOException("No response body");
            }

            try (var bufferedSource = body.source();
                 var bufferedSink = Okio.buffer(Okio.sink(file))) {
                bufferedSink.writeAll(bufferedSource);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to download cache zip", e);
        } finally {
            okHttpClient.dispatcher().executorService().shutdown();
            okHttpClient.connectionPool().evictAll();
        }
    }

    private void unzipIntoDir(Path file, Path dir) {
        try (var zipInputStream = new ZipInputStream(Files.newInputStream(file))) {
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                var entryPath = dir.resolve(entry.getName());
                if (entry.isDirectory()) {
                    Files.createDirectories(entryPath);
                } else {
                    Files.createDirectories(entryPath.getParent());

                    try (var outputStream = Files.newOutputStream(entryPath)) {
                        zipInputStream.transferTo(outputStream);
                    }
                }

                zipInputStream.closeEntry();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to unzip file into directory", e);
        }
    }
}
