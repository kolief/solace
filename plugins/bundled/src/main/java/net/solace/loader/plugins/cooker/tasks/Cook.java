package net.solace.loader.plugins.cooker.tasks;

import lombok.Getter;
import net.runelite.api.gameval.AnimationID;
import net.runelite.client.chat.ChatColorType;
import net.solace.api.domain.items.IInventoryItem;
import net.solace.api.domain.items.IItem;
import net.solace.api.domain.tiles.ITileObject;
import net.solace.api.items.WithdrawMode;
import net.solace.api.plugins.exception.PluginStoppedException;
import net.solace.api.widgets.WidgetGroup;
import net.solace.loader.plugins.cooker.Meat;
import net.solace.loader.plugins.cooker.SolaceCookerPlugin;
import net.solace.sdk.entities.NPCs;
import net.solace.sdk.entities.Players;
import net.solace.sdk.entities.TileObjects;
import net.solace.sdk.game.Client;
import net.solace.sdk.items.Bank;
import net.solace.sdk.items.Inventory;
import net.solace.sdk.utils.MessageUtils;
import net.solace.sdk.widgets.Production;
import net.solace.sdk.widgets.Widgets;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Cook extends CookerTask {
    public Cook(SolaceCookerPlugin context) {
        super(context);
    }

    @Override
    public boolean validate() {
        return true;
    }

    @Override
    public int execute() {
        var meat = getConfig().item();
        var local = Players.getLocal();

        var allRawIds = Arrays.stream(Meat.values())
                .filter(Meat::canCook)
                .map(Meat::getRawId)
                .collect(Collectors.toList());

        var result = findRawFood(meat, allRawIds);
        var raw = result.getItem();

        meat = result.getMeat();

        if (raw == null) {
            if (Bank.isOpen()) {
                return handleBanking(meat, allRawIds);
            }

            var banker = NPCs.getNearest(npc -> npc.hasAction("Collect"));
            if (banker != null) {
                banker.interact("Bank");
                return -3;
            }

            var worldLocation = Players.getLocal().getWorldLocation();
            var bank = TileObjects.query(worldLocation, 20)
                    .actions("Collect")
                    .filter(o -> Objects.requireNonNull(o.getName()).startsWith("Bank"))
                    .results()
                    .nearest(worldLocation);
            if (bank != null) {
                bank.interact("Bank", "Use");
                return -3;
            }

            MessageUtils.addMessage("Bank not found.", ChatColorType.HIGHLIGHT);
            return -1;
        }

        if (local.getAnimation() == AnimationID.HUMAN_COOKING || local.getAnimation() == AnimationID.HUMAN_FIRECOOKING) {
            taskCooldown = Client.getTickCount() + meat.getCookTicks();
            return -1;
        }

        if (Client.getTickCount() < taskCooldown) {
            return -1;
        }

        var cookingObject = getCookingObject();

        if (cookingObject == null) {
            MessageUtils.addMessage("Fire/cooking range not found.", ChatColorType.HIGHLIGHT);
            return -1;
        }

        if (Production.isOpen()) {
            Widgets.get(WidgetGroup.MULTISKILL_MENU_GROUP_ID, 15 + meat.getProductionIndex()).interact(0);
            return -meat.getCookTicks();
        }

        if (cookingObject.hasAction("Cook")) {
            cookingObject.interact("Cook");
        } else {
            raw.useOn(cookingObject);
        }

        return 1000;
    }

    @Override
    public boolean subscribe() {
        return true;
    }

    private ITileObject getCookingObject() {
        final var wintertodtBonfire = 29300;
        final var temporossCampFire = 41316;

        return TileObjects.getFirstSurrounding(
                Players.getLocal().getWorldLocation(), 20,
                object -> object.hasAction("Cook")
                        || object.getId() == wintertodtBonfire
                        || object.getId() == temporossCampFire
        );
    }

    private RawFoodResult findRawFood(Meat configMeat, List<Integer> allRawIds) {
        IInventoryItem rawItem = Inventory.getFirst(configMeat.getRawId());
        Meat updatedMeat = configMeat;

        if (configMeat == Meat.ALL) {
            rawItem = Inventory.getFirst(item -> allRawIds.contains(item.getId()));

            if (rawItem != null) {
                final IItem foundRaw = rawItem;
                updatedMeat = Arrays.stream(Meat.values())
                        .filter(Meat::canCook)
                        .filter(m -> m.getRawId() == foundRaw.getId())
                        .findFirst()
                        .orElse(Meat.ALL);
            }
        }

        return new RawFoodResult(rawItem, updatedMeat);
    }

    private int handleBanking(Meat configMeat, List<Integer> allRawIds) {
        Predicate<IItem> isRawFood = item ->
                (configMeat != Meat.ALL && item.getId() == configMeat.getRawId())
                        || allRawIds.contains(item.getId());

        if (Inventory.contains(isRawFood.negate())) {
            Bank.depositInventory();
            return -2;
        }

        Meat meatToWithdraw = configMeat;
        if (configMeat == Meat.ALL) {
            meatToWithdraw = Arrays.stream(Meat.values())
                    .filter(Meat::canCook)
                    .filter(m -> Bank.contains(m.getRawId()))
                    .findFirst()
                    .orElse(configMeat);
        }

        int amount = meatToWithdraw.getWithdrawAmount();

        if (!Bank.contains(meatToWithdraw.getRawId())) {
            throw new PluginStoppedException("Out of raw food.");
        }

        if (amount == 28) {
            Bank.withdrawAll(meatToWithdraw.getRawId(), WithdrawMode.ITEM);
        } else {
            Bank.withdraw(meatToWithdraw.getRawId(), amount);
        }

        return -2;
    }

    @Getter
    private static class RawFoodResult {
        private final IInventoryItem item;
        private final Meat meat;

        public RawFoodResult(IInventoryItem item, Meat meat) {
            this.item = item;
            this.meat = meat;
        }
    }

    @Getter
    private static class BankingResult {
        private final Meat meat;
        private final int delay;

        public BankingResult(Meat meat, int delay) {
            this.meat = meat;
            this.delay = delay;
        }

    }
}
