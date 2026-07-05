package net.solace.loader.plugins.agility;

import com.google.inject.Inject;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Skill;
import net.runelite.api.events.GameTick;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.client.eventbus.Subscribe;
import net.solace.api.breaks.BreakHandler;
import net.solace.api.commons.Rand;
import net.solace.api.coords.Area;
import net.solace.api.domain.actors.IPlayer;
import net.solace.api.domain.tiles.ITileObject;
import net.solace.api.magic.SpellBook;
import net.solace.api.plugins.LoopedPlugin;
import net.solace.api.plugins.PluginDescriptor;
import net.solace.api.plugins.config.ConfigManager;
import net.solace.api.plugins.exception.PluginStoppedException;
import net.solace.sdk.entities.Players;
import net.solace.sdk.entities.TileItems;
import net.solace.sdk.entities.TileObjects;
import net.solace.sdk.game.Combat;
import net.solace.sdk.game.Game;
import net.solace.sdk.game.Skills;
import net.solace.sdk.game.Vars;
import net.solace.sdk.items.Inventory;
import net.solace.sdk.magic.Magic;
import net.solace.sdk.movement.Movement;
import net.solace.sdk.plugins.Plugins;
import net.solace.sdk.utils.MessageUtils;
import net.solace.sdk.widgets.Dialog;

import javax.swing.SwingUtilities;

@PluginDescriptor(name = "Solace Agility")
@Slf4j
public class SolaceAgilityPlugin extends LoopedPlugin {
    @Inject
    private SolaceAgilityConfig config;
    @Inject
    private Client client;

    @Inject
    private BreakHandler breakHandler;

    private int energyAmount;

    private int alchCooldown = 0;

    private boolean justAlched = false;

    @Provides
    public SolaceAgilityConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(SolaceAgilityConfig.class);
    }

    @Override
    public void startUp() throws Exception {
        breakHandler.registerPlugin(this);
        breakHandler.startPlugin(this);
        energyAmount = Rand.nextInt(config.minEnergyAmount(), config.maxEnergyAmount());
    }

    @Override
    public void shutDown() throws Exception {
        breakHandler.unregisterPlugin(this);
        breakHandler.stopPlugin(this);
    }

    @Subscribe
    private void onGameTick(GameTick event) {
        if (alchCooldown > 0) {
            alchCooldown--;
        }
    }

    @Override
    public int loop() {
        if (!Game.isLoggedIn() || client.getLocalPlayer() == null || breakHandler.isBreakActive(this)) {
            return -1;
        }

        if (breakHandler.shouldBreak(this)) {
            breakHandler.startBreak(this);
            return -1;
        }

        if (Dialog.canContinue()) {
            Dialog.continueSpace();
            return -1;
        }

        int currentLevel = Skills.getLevel(Skill.AGILITY);

        if (currentLevel >= config.stopAtLevel()) {
            throw new PluginStoppedException("Reached target level");
        }

        if (config.useSummerPies()
                && config.stopWhenOutOfSummerPies()
                && !Inventory.contains(ItemID.SUMMER_PIE, ItemID.HALF_SUMMER_PIE)) {
            MessageUtils.addMessage("Ran out of Summer Pies, stopping plugin");
            SwingUtilities.invokeLater(() -> Plugins.stopPlugin(this));
            return -1;
        }

        if (config.useSummerPies()
                && config.summerPieStyle() == BoostStyle.BOOST_AMOUNT
                && Skills.getBoostedLevel(Skill.AGILITY) - Skills.getLevel(Skill.AGILITY) < config.minBoostAmount()
                && Inventory.contains(ItemID.SUMMER_PIE, ItemID.HALF_SUMMER_PIE)) {
            Inventory.getFirst(ItemID.SUMMER_PIE, ItemID.HALF_SUMMER_PIE).interact("Eat");
            return -1;
        }
        if (config.useSummerPies()
                && config.summerPieStyle() == BoostStyle.TARGET_LEVEL
                && Skills.getBoostedLevel(Skill.AGILITY) < config.targetBoostLevel()
                && Inventory.contains(ItemID.SUMMER_PIE, ItemID.HALF_SUMMER_PIE)) {
            Inventory.getFirst(ItemID.SUMMER_PIE, ItemID.HALF_SUMMER_PIE).interact("Eat");
            return -1;
        }

        if (config.useStaminas()
                && Movement.getRunEnergy() < energyAmount
                && Inventory.contains(item -> item.getName().contains("Stamina"))
                && Vars.getBit(VarbitID.STAMINA_ACTIVE) == 0) {
            Inventory.getFirst(item -> item.getName().contains("Stamina")).interact("Drink");
            energyAmount = Rand.nextInt(config.minEnergyAmount(), config.maxEnergyAmount());
            return -1;
        }

        if (Combat.getHealthPercent() <= config.eatHp()) {
            var itemToEat = Inventory.getFirst(x -> x.hasAction("Eat")
                    || x.getName().equals("Jug of wine"));

            if (itemToEat == null) {
                if (config.stopIfNoFood()) {
                    MessageUtils.addMessage("Ran out of food, stopping plugin");
                    SwingUtilities.invokeLater(() -> Plugins.stopPlugin(this));
                    return -1;
                }
            } else {
                itemToEat.interact("Eat", "Drink");
                return -1;
            }
        }

        boolean hasPaid = Vars.getBit(10674) == 1;
        boolean shouldCollect = Vars.getBit(10675) == 1;

        var dispenser = TileObjects.getNearest("Agility dispenser");

        IPlayer local = Players.getLocal();
        if (dispenser != null && dispenser.isInteractable(local.getWorldLocation())) {
            if (!hasPaid) {
                if (Dialog.isOpen()) {
                    if (Dialog.chooseOption("Yes.")) {
                        log.info("Paid for agility course");
                        return -1;
                    }
                }

                if (Inventory.getCount(true, "Coins") >= 150_000) {
                    if (local.isMoving()) {
                        return -1;
                    }

                    dispenser.interact("Pay");
                    return -1;
                }
            }

            if (shouldCollect) {
                if (local.isMoving()) {
                    return -1;
                }

                dispenser.interact("Tag");
                return -1;
            }

            if (config.redeemTickets() && Inventory.getCount(true, "Wilderness agility ticket") >= config.redeemAmount()) {
                if (local.isMoving()) {
                    return -1;
                }

                dispenser.interact("Redeem");
                return -1;
            }
        }

        Course course = getCourse();
        Obstacle obstacle = course.getNext(local);
        if (obstacle == null) {
            log.error("No obstacle detected");
            return -1;
        }

        SpellBook.Standard spell = config.alchSpell().getSpell();
        if (spell != null && alchCooldown == 0 && spell.canCast()) {
            // if its been 5 ticks since last alch
            // if we have item and nature runes
            var itemConfig = config.itemToAlch();
            if (itemConfig != null) {
                var alchItem = Inventory.getFirst(itemConfig.getId());
                if (alchItem != null) {
                    Magic.cast(spell, alchItem);
                    alchCooldown = 5;
                    justAlched = true;
                    return -1;
                }
            }
        }

        var obs = findProperObstacle(obstacle);
        if (Movement.getRunEnergy() > Rand.nextInt(5, 55) && !Movement.isRunEnabled()) {
            Movement.toggleRun();
            return -1;
        }

        var mark = TileItems.getNearestIn(Area.offsetFrom(obstacle.getArea(), 2), "Mark of grace");
        if (mark != null) {
            var shouldPick = config.course() != Course.ARDY_COURSE || mark.getQuantity() >= config.minimumMarkCount();
            if (shouldPick && mark.canPick() && mark.isInteractable(local.getWorldLocation())) {
                var gold = TileItems.getFirstAt(mark.getWorldLocation(), ItemID.COINS);
                if (gold != null && gold.canPick()) {
                    gold.pickup();
                    return -1;
                }

                mark.pickup();
                return -1;
            }
        }

        if (obs != null) {
            if (justAlched) {
                obs.interact(obstacle.getAction());
                justAlched = false;
                return -1;
            }

            if (local.getAnimation() != -1 || local.isMoving()) {
                return -1;
            }

            obs.interact(obstacle.getAction());
            return -1;
        }

        log.error("Obstacle was null");
        return -1;
    }

    public ITileObject findProperObstacle(Obstacle obstacle) {
        try {
            return TileObjects.query(obstacle.getTile(), 3)
                    .names(obstacle.getName())
                    .actions(obstacle.getAction())
                    .results()
                    .nearest(Players.getLocal());
        } catch (Exception exception) {
            return obstacle.getId() != 0 ? TileObjects.getNearest(obstacle.getId())
                    : TileObjects.getNearest(x -> x.hasAction(obstacle.getAction()) && x.getName().equals(obstacle.getName()));
        }
    }

    private Course getCourse() {
        return config.course() == Course.NEAREST ? Course.getNearest() : config.course();
    }
}
