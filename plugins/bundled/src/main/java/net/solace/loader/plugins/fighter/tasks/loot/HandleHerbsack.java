package net.solace.loader.plugins.fighter.tasks.loot;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.gameval.ItemID;
import net.runelite.client.eventbus.Subscribe;
import net.solace.api.domain.items.IItem;
import net.solace.api.domain.tiles.ITileItem;
import net.solace.loader.plugins.fighter.SolaceFighterPlugin;
import net.solace.loader.plugins.fighter.tasks.FighterTask;
import net.solace.sdk.entities.Players;
import net.solace.sdk.entities.TileItems;
import net.solace.sdk.items.Inventory;
import net.solace.sdk.movement.Movement;

import java.util.Comparator;

@Slf4j
public class HandleHerbsack extends FighterTask {
    public HandleHerbsack(SolaceFighterPlugin context) {
        super(context);
    }

    @Override
    public boolean validate() {
        var sack = getSack();

        if (!shouldUseSack || !getConfig().useHerbSack() || sack == null) {
            return false;
        }

        var herb = getGrimyHerb();

        if (herb != null && herb.canPick()) {
            return true;
        }

        return Inventory.contains(x -> x.getName().toLowerCase().contains("grimy"));
    }

    @Override
    public int execute() {
        getContext().setCurrentTaskName("Handling herbsack");

        var sack = getSack();

        if (sack.getId() == ItemID.SLAYER_HERB_SACK) {
            sack.interact("Open");
            return -1;
        }

        var invHerb = Inventory.getFirst(x -> x.getName().toLowerCase().contains("grimy"));

        if (invHerb != null) {
            invHerb.useOn(sack);
            return -1;
        }

        var herb = getGrimyHerb();

        if (herb == null) {
            log.warn("No loot found");
            return -1;
        }

        if (!herb.isInteractable()) {
            if (Movement.isWalking()) {
                return -1;
            }

            Movement.walkTo(herb.getTile());
            return -1;
        }

        if (Players.getLocal().isMoving()) {
            return -1;
        }

        herb.interact("Take");
        return -2;
    }

    @Override
    public boolean subscribe() {
        return true;
    }

    @Subscribe
    public void onChatMessage(ChatMessage message) {
        // HANDLE FULL HERBSACK HERE
    }

    @Getter
    @Setter
    private boolean shouldUseSack = false;

    private ITileItem getGrimyHerb() {
        return TileItems.getAllMine(x -> x.distanceTo(getContext().getCenter()) <= getConfig().attackRange()
                        && x.getName().toLowerCase().contains("grimy"))
                .stream()
                .min(Comparator.comparingInt(x -> x.distanceTo(Players.getLocal())))
                .orElse(null);
    }

    private IItem getSack() {
        return Inventory.getFirst(ItemID.SLAYER_HERB_SACK, ItemID.SLAYER_HERB_SACK_OPEN);
    }

}
