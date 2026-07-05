package net.solace.rscache.map.engine.solace;

public record Transport(
        WorldPoint source,
        WorldPoint destination,
        String action,
        Integer objectId
) {
}
