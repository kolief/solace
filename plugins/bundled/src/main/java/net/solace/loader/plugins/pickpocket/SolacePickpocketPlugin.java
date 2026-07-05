package net.solace.loader.plugins.pickpocket;

import com.google.inject.Inject;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.client.util.Text;
import net.runelite.client.util.WildcardMatcher;
import net.solace.api.breaks.BreakHandler;
import net.solace.api.commons.Rand;
import net.solace.api.coords.Area;
import net.solace.api.domain.actors.INPC;
import net.solace.api.domain.items.IItem;
import net.solace.api.domain.tiles.ITileObject;
import net.solace.api.movement.pathfinder.model.BankLocation;
import net.solace.api.plugins.LoopedPlugin;
import net.solace.api.plugins.PluginDescriptor;
import net.solace.api.plugins.config.ConfigManager;
import net.solace.api.plugins.exception.PluginStoppedException;
import net.solace.api.widgets.EquipmentSlot;
import net.solace.sdk.entities.NPCs;
import net.solace.sdk.entities.Players;
import net.solace.sdk.entities.TileObjects;
import net.solace.sdk.game.Combat;
import net.solace.sdk.game.Game;
import net.solace.sdk.items.Bank;
import net.solace.sdk.items.Equipment;
import net.solace.sdk.items.Inventory;
import net.solace.sdk.movement.Movement;
import net.solace.sdk.widgets.Dialog;

import java.util.List;
import java.util.Objects;

@PluginDescriptor(name = "Solace Pickpocket")
@Slf4j
public class SolacePickpocketPlugin extends LoopedPlugin {
    private static final int bankChestId = 26711;
    private static final int bankBookId = 10583;
    private static final WorldPoint FARMING_GUILD_SOUTH_BANK_TILE = new WorldPoint(1253, 3742, 0);
    private static final WorldPoint FARMING_GUILD_NORTH_BANK_TILE = new WorldPoint(1248, 3759, 0);
    private static final WorldPoint FARMING_GUILD_SOUTH_PICKPOCKET_TILE = new WorldPoint(1265, 3730, 0);
    @Inject
    private SolacePickpocketConfig config;
    @Inject
    public BreakHandler breakHandler;

    private WorldPoint lastNpcPosition = null;
    private int maxPouches = 5;

    @Override
    public void startUp() throws Exception {
        breakHandler.registerPlugin(this);
        breakHandler.startPlugin(this);
    }

    @Override
    public void shutDown() throws Exception {
        breakHandler.unregisterPlugin(this);
        breakHandler.stopPlugin(this);
    }

    @Override
    public int loop() {
        if (!Game.isLoggedIn() || breakHandler.isBreakActive(this)) {
            return -1;
        }

        if (breakHandler.shouldBreak(this)) {
            breakHandler.startBreak(this);
            return -1;
        }

        var junk = Inventory.getFirst(item -> shouldDrop(Text.fromCSV(config.junk()), item.getName()));
        if (junk != null) {
            junk.interact("Drop");
            log.debug("Dropping junk");
            return -1;
        }

        var pouch = Inventory.getFirst("Coin pouch");
        if (pouch != null && pouch.getQuantity() >= maxPouches && !Inventory.isFull()) {
            pouch.interact("Open-all");
            maxPouches = Rand.nextInt(1, 28);
            log.debug("Opening pouches");
            return -1;
        }

        if (config.eat()) {
            if (Combat.getMissingHealth() >= config.eatHp()) {
                var food = Inventory.getFirst(item -> item.getName().toLowerCase().contains(config.foodName().toLowerCase()) && !item.isNoted());
                if (food != null) {
                    food.interact(1);
                    log.debug("Eating food");
                    return -2;
                } else {
                    var bankersNote = Inventory.getFirst(28767);
                    var notedFood = Inventory.getFirst(item -> item.getName().toLowerCase().contains(config.foodName().toLowerCase()) && item.isNoted());
                    if (bankersNote != null && notedFood != null) {
                        notedFood.useOn(bankersNote);
                        return -2;
                    }
                }
            }
        }

        if (config.shouldEquipDodgy()) {
            IItem dodgyNecklace;
            if (!isPlayerWearingDodgyNecklace() && (dodgyNecklace = Inventory.getFirst(ItemID.DODGY_NECKLACE)) != null) {
                dodgyNecklace.interact("Wear");
                log.debug("Equipping Dodgy Necklace");
                return -1;
            }
        }

        if (Bank.isOpen()) {
            var unneeded = Inventory.getAll(item ->
                    (!config.eat() || !item.getName().toLowerCase().contains(config.foodName().toLowerCase()))
                            && item.getId() != ItemID.COINS
                            && !Objects.equals(item.getName(), "Coin pouch")
                            && item.getId() != ItemID.DODGY_NECKLACE
            );
            if (!unneeded.isEmpty()) {
                if (config.bankStyle() == BankStyle.DEPOSIT_ALL) {
                    Bank.depositInventory();
                    return -1;
                }

                for (var item : unneeded) {
                    Bank.depositAll(item.getId());
                }

                return -1;
            }

            if (config.eat()) {
                int sleepTicks = handleBankingForNamedItem(config.foodName(), config.foodAmount());
                if (sleepTicks != 0) {
                    return sleepTicks;
                }
            }

            if (config.shouldEquipDodgy()) {
                int sleepTicks = handleBankingForNamedItem("Dodgy necklace", config.necklaceQuantity());
                if (sleepTicks != 0) {
                    return sleepTicks;
                }
            }
        }

        BankLocation bankLocation = config.bankLocation();
        if (shouldBank()) {
            if (Movement.isWalking()) {
                return -4;
            }

            if (!bankLocation.getArea().contains(Players.getLocal().getWorldLocation())) {
                Movement.walkTo(bankLocation);
                return -1;
            }

            //Farming guild contains two banks within one area, so they get snowflake handling
            ITileObject bank;
            if (bankLocation == BankLocation.FARMING_GUILD_SOUTH) {
                bank = TileObjects.getFirstAt(FARMING_GUILD_SOUTH_BANK_TILE, bankChestId);
            } else if (bankLocation == BankLocation.FARMING_GUILD_NORTH) {
                bank = TileObjects.getFirstAt(FARMING_GUILD_NORTH_BANK_TILE, bankBookId);
            } else {
                bank = TileObjects.getNearestIn(Area.offsetFrom(bankLocation.getArea(), 2), x -> x.hasAction("Collect"));
            }

            if (bank != null && bank.isInteractable()) {
                bank.interact("Bank", "Use");
                return -4;
            }

            var banker = NPCs.getNearest("Banker");
            if (banker != null && banker.isInteractable()) {
                banker.interact("Bank");
                return -4;
            }

            Movement.walkTo(bankLocation);
            return -4;
        }

        INPC target;
        //More snowflake handling for farming guild
        //When banking at south guild, the north guild farmer is actually closer to you
        if (bankLocation.equals(BankLocation.FARMING_GUILD_SOUTH)) {
            //To make the snowflake handling even worse, the southern can actually wander out of render distance
            //So we'll start moving towards it
            if (FARMING_GUILD_SOUTH_PICKPOCKET_TILE.distanceTo(Players.getLocal().getWorldLocation()) > 11) {
                Movement.walkTo(FARMING_GUILD_SOUTH_PICKPOCKET_TILE);
            }
            target = NPCs.getNearest(FARMING_GUILD_SOUTH_PICKPOCKET_TILE, config.npcName());
        } else {
            target = findClosestNameByPathLength(config.npcName());
        }
        if (target != null) {
            lastNpcPosition = target.getWorldLocation();
            if (!target.isInteractable()) {
                if (Movement.isWalking()) {
                    return -4;
                }

                Movement.walkTo(target);
                return -4;
            }

            var local = Players.getLocal();
            if (local.getGraphic() == 245 && !Dialog.isOpen()) {
                return -1;
            }

            if (local.isMoving() && target.distanceTo(local) > 3) {
                return -1;
            }

            if (Inventory.isFull()) {
                var food = Inventory.getFirst(x -> x.hasAction("Eat"));
                if (food != null) {
                    food.interact("Eat");
                    return -2;
                } else {
                    throw new PluginStoppedException("Inventory is full and no food is available");
                }
            }

            target.interact("Pickpocket");
            return Rand.nextInt(222, 333);
        }

        if (Movement.isWalking()) {
            return -4;
        }

        if (lastNpcPosition != null) {
            Movement.walkTo(lastNpcPosition);
            return -4;
        }

        log.info("Idle");
        return -1;
    }

    private INPC findClosestNameByPathLength(String npcName) {
        var matchingNpcs = NPCs.getAll(x ->
                Objects.requireNonNull(x.getName()).equalsIgnoreCase(npcName)
                        && x.hasAction("Pickpocket", "Steal-from", "Steal from", "Steal-From"));

        if (matchingNpcs.isEmpty()) {
            return null;
        }
        if (matchingNpcs.size() == 1) {
            return matchingNpcs.get(0);
        }
        int shortestPath = -1;
        INPC bestNpc = null;
        for (var candidate : matchingNpcs) {
            int pathLength = Movement.calculateDistance(candidate.getWorldLocation());
            log.debug("Calculate distance to: {} is : {}", candidate.getWorldLocation(), pathLength);
            if ((pathLength < shortestPath) || shortestPath == -1) {
                bestNpc = candidate;
                shortestPath = pathLength;
            }
        }
        return bestNpc;
    }

    @Provides
    SolacePickpocketConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(SolacePickpocketConfig.class);
    }

    private boolean shouldDrop(List<String> itemNames, String itemName) {
        return itemName != null && itemNames.stream().anyMatch(name -> WildcardMatcher.matches(name, itemName));
    }

    private boolean isPlayerWearingDodgyNecklace() {
        var amuletSlot = Equipment.fromSlot(EquipmentSlot.AMULET);
        if (amuletSlot == null) {
            return false;
        }
        return amuletSlot.getId() == ItemID.DODGY_NECKLACE;
    }

    private boolean shouldBank() {
        //Banking is Enable AND (inventory is full OR inventory has no food OR (player isnt wearing a dodgy necklace AND inventory doesnt contain a dodgy necklace))
        return config.bank()
                && (Inventory.isFull() || !Inventory.contains(item -> item.getName().toLowerCase().contains(config.foodName().toLowerCase()))
                || (!isPlayerWearingDodgyNecklace() && !Inventory.contains(ItemID.DODGY_NECKLACE) && config.shouldEquipDodgy())
        );
    }

    private int handleBankingForNamedItem(String namedItem, int expectedQuantity) {
        int namedItemCount = Inventory.getCount(x -> x.getName().toLowerCase().contains(namedItem.toLowerCase()));

        if (namedItemCount > expectedQuantity) {
            Bank.depositAll(x -> x.getName() != null && x.getName().toLowerCase().contains(namedItem.toLowerCase()));
            return -2;
        }

        int namedItemMissing = expectedQuantity - namedItemCount;
        var bankItem = Bank.getFirst(item -> !item.isPlaceholder()
                && !item.getName().toLowerCase().contains("raw")
                && item.getName().toLowerCase().contains(namedItem.toLowerCase()));

        if (namedItemMissing > 0) {
            if (bankItem == null) {
                log.debug("Missing {} x {}", namedItem, namedItemMissing);
                return -2;
            }

            Bank.withdraw(bankItem.getId(), namedItemMissing);
            log.debug("Withdrawing {} x {}", namedItem, namedItemMissing);
            return -2;
        }
        return 0;
    }
}