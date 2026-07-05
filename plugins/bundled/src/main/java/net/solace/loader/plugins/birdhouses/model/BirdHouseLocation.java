package net.solace.loader.plugins.birdhouses.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.VarPlayerID;

@RequiredArgsConstructor
@Getter
public enum BirdHouseLocation {
    MEADOW_SOUTH(new WorldPoint(3679, 3815, 0), VarPlayerID.BIRDHOUSE_TRANSMIT_B),
    MEADOW_NORTH(new WorldPoint(3677, 3882, 0), VarPlayerID.BIRDHOUSE_TRANSMIT_A),
    VALLEY_SOUTH(new WorldPoint(3763, 3755, 0), VarPlayerID.BIRDHOUSE_TRANSMIT_D),
    VALLEY_NORTH(new WorldPoint(3768, 3761, 0), VarPlayerID.BIRDHOUSE_TRANSMIT_C);

    private final WorldPoint worldPoint;
    private final int varp;


    @Override
    public String toString() {
        return name().substring(0, 1).toUpperCase() + name().toLowerCase().substring(1).replace("_", " ");
    }
}
