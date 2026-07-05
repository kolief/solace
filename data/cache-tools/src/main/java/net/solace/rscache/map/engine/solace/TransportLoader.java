package net.solace.rscache.map.engine.solace;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import net.solace.api.movement.pathfinder.ITransportLoader;

import java.io.IOException;
import java.util.List;

@Slf4j
public class TransportLoader {
    private static final Gson GSON = new Gson();

    public static List<Transport> loadTransports() {
        log.info("Loading transports");
        try (var stream = ITransportLoader.class.getResourceAsStream("/transports.json")) {
            if (stream == null) {
                throw new RuntimeException("transports.json file not found");
            }

            var json = GSON.fromJson(new String(stream.readAllBytes()), Transport[].class);

            var transports = List.of(json);
            log.info("Loaded {} transports", transports.size());
            return transports;
        } catch (IOException e) {
            throw new RuntimeException("Failed to load transports.", e);
        }
    }
}
