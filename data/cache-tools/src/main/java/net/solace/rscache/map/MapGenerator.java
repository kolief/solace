package net.solace.rscache.map;

import lombok.extern.slf4j.Slf4j;
import net.solace.rscache.map.cache.GameCache;
import net.solace.rscache.map.world.World;
import net.solace.rscache.map.xtea.XteaConfig;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


@Slf4j
public class MapGenerator {
    public void start(File outputDir) throws IOException {
        var xteaConfig = new XteaConfig();
        xteaConfig.load();

        var gameCache = new GameCache();
        gameCache.load(xteaConfig);

        var world = new World(gameCache);
        world.load(xteaConfig);

        var gzipped = world.dumpCollisionMap(xteaConfig);
        var file = new File(outputDir, "regions");

        log.info("Writing collision map");

        try (var os = new FileOutputStream(file);
             var bis = new ByteArrayInputStream(gzipped)) {
            bis.transferTo(os);
            os.flush();
            log.info("Wrote collision map to {}", file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write to file", e);
        }
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            throw new IllegalArgumentException("Output directory must be specified as first argument");
        }

        var mapGen = new MapGenerator();
        var outputDir = new File(args[0]);
        mapGen.start(outputDir);

        log.info("Map generation completed");
    }
}
