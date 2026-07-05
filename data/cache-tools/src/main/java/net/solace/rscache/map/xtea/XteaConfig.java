package net.solace.rscache.map.xtea;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okio.Okio;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.solace.rscache.Constants.OPENRS2_CACHE_VERSION;

@Slf4j
public class XteaConfig {
    private static final String DOWNLOAD_URL = "https://archive.openrs2.org/caches/runescape/"
            + OPENRS2_CACHE_VERSION + "/keys.json";

    private final Gson gson = new Gson();
    private final OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .connectTimeout(Duration.ofSeconds(60))
            .callTimeout(Duration.ofSeconds(60))
            .readTimeout(Duration.ofSeconds(60))
            .build();
    private final Map<Integer, int[]> xteaKeys = new HashMap<>();

    public void load() throws IOException {
        var jsonFile = Files.createTempFile("xtea", ".json");
        downloadXteaKeys(jsonFile);

        try (var is = Files.newInputStream(jsonFile)) {
            try (var reader = new InputStreamReader(is)) {
                var xteas = gson.fromJson(reader, Xtea[].class);
                for (var xtea : xteas) {
                    xteaKeys.put(xtea.mapsquare(), xtea.key());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load xtea keys", e);
        } finally {
            Files.deleteIfExists(jsonFile);
            log.info("Loaded {} XTEA keys and deleted cache files after usage", xteaKeys.size());
        }
    }

    public int[] getRegionKey(int region) {
        var keys = xteaKeys.get(region);
        if (keys == null) {
            return new int[]{0, 0, 0, 0};
        }

        return keys;
    }

    public List<Integer> getRegionIds() {
        return List.copyOf(xteaKeys.keySet());
    }

    private void downloadXteaKeys(Path file) {
        var request = new Request.Builder()
                .url(DOWNLOAD_URL)
                .get()
                .build();

        log.info("Attempting to download XTEA keys from {}", DOWNLOAD_URL);
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
            throw new RuntimeException("Failed to download XTEA keys", e);
        } finally {
            okHttpClient.dispatcher().executorService().shutdown();
            okHttpClient.connectionPool().evictAll();
        }
    }
}
