package net.solace.loader.plugins.birdhouses.tasks;

import lombok.extern.slf4j.Slf4j;
import net.solace.loader.plugins.birdhouses.SolaceBirdHousesPlugin;
import net.solace.loader.plugins.birdhouses.SolaceBirdHousesConfig;
import net.solace.sdk.entities.TileObjects;
import net.solace.sdk.items.Inventory;
import net.solace.sdk.widgets.Production;

import javax.inject.Inject;

@Slf4j
public class SetupBirdHouse extends BirdHouseTask {
    @Inject
    private SolaceBirdHousesConfig config;

    public SetupBirdHouse(SolaceBirdHousesPlugin context) {
        super(context);
    }

    @Override
    public boolean validate() {
        return getNextBirdHouse().isPresent();
    }

    @Override
    public int execute() {
        getNextBirdHouse().ifPresent(birdHouse ->
        {
            switch (birdHouse.getState()) {
                case EMPTY:
                case UNKNOWN:
                    var birdHouseItem = Inventory.getFirst(config.type().getItemId());
                    if (birdHouseItem == null) {
                        var logs = Inventory.getFirst(config.type().getLogItemId());
                        var chisel = Inventory.getFirst("Chisel");
                        if (logs != null && chisel != null) {
                            if (Production.isOpen()) {
                                Production.chooseOption(1);
                            } else {
                                logs.useOn(chisel);
                            }
                        } else {
                            log.error("Logs & Chisel not found");
                        }
                    } else {
                        var spot = TileObjects.getFirstAt(birdHouse.getWorldPoint(), "Space");
                        if (spot == null) {
                            log.error("Bird house spot was null {}", birdHouse.getWorldPoint());
                            break;
                        }

                        spot.interact("Build");
                    }

                    break;

                case BUILT:
                    var seeds = Inventory.getFirst(config.seedType().getItemId());
                    if (seeds == null) {
                        log.error("Seeds not found");
                        break;
                    }

                    var emptyHouse = TileObjects.getFirstAt(birdHouse.getWorldPoint(), obj -> obj.hasAction("Seeds"));
                    if (emptyHouse == null) {
                        log.error("Empty bird house not found");
                        break;
                    }

                    seeds.useOn(emptyHouse);
                    break;

                case SEEDED:
                    var completedHouse = TileObjects.getFirstAt(birdHouse.getWorldPoint(), obj -> obj.hasAction("Empty"));
                    if (completedHouse == null) {
                        log.error("Couldn't find completed bird house");
                        break;
                    }

                    completedHouse.interact("Empty");
                    break;
            }
        });

        return -2;
    }

    @Override
    public boolean inject() {
        return true;
    }

    @Override
    public String toString() {
        return "Setup birdhouses";
    }
}
