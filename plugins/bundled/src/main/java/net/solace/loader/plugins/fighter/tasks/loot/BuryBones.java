package net.solace.loader.plugins.fighter.tasks.loot;

import lombok.extern.slf4j.Slf4j;
import net.solace.api.domain.items.IInventoryItem;
import net.solace.api.magic.SpellBook;
import net.solace.loader.plugins.fighter.data.BuryType;
import net.solace.loader.plugins.fighter.SolaceFighterPlugin;
import net.solace.loader.plugins.fighter.tasks.FighterTask;
import net.solace.sdk.items.Inventory;
import net.solace.sdk.magic.Magic;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class BuryBones extends FighterTask {
    public BuryBones(SolaceFighterPlugin context) {
        super(context);
    }

    @Override
    public boolean validate() {
        if (shouldSafespot()) {
            return false;
        }

        if (shouldOffer()) {
            if (getContext().getOfferCooldown() > 0) {
                return false;
            }
            return true;
        }

        return !getBones().isEmpty() && getConfig().buryBones() == BuryType.BURY;
    }

    @Override
    public int execute() {
        getContext().setCurrentTaskName("Burying bones");
        var bones = getBones();
        var bone = bones.stream().findFirst().orElse(null);

        if (bone == null) {
            log.warn("No bones found to bury");
            return -1;
        }

        //Clean this and utilize enum more effectively
        if (getConfig().buryBones() == BuryType.OFFER_SPELL) {
            if (shouldCastDemonic() && SpellBook.Necromancy.DEMONIC_OFFERING.canCast()) {
                Magic.cast(SpellBook.Necromancy.DEMONIC_OFFERING);
                getContext().setOfferCooldown(9);
                return -1;
            }

            if (shouldCastSinister() && SpellBook.Necromancy.SINISTER_OFFERING.canCast()) {
                Magic.cast(SpellBook.Necromancy.SINISTER_OFFERING);
                getContext().setOfferCooldown(9);
                return -1;
            }
        }

        bone.interact("Bury", "Scatter");
        return -2;
    }

    @Override
    public boolean subscribe() {
        return true;
    }

    private List<IInventoryItem> getBones() {
        return Inventory.getAll(x -> (x.getName() != null && !x.getName().equalsIgnoreCase("long bone") && !x.getName().equalsIgnoreCase("curved bone")) && (x.hasAction("Bury") || x.hasAction("Scatter")));
    }

    private boolean shouldOffer() {
        return shouldCastDemonic() || shouldCastSinister();
    }

    private boolean shouldCastDemonic() {
        var bones = getBones();
        var ashes = bones.stream().filter(x -> x.hasAction("Scatter")).collect(Collectors.toList());

        if (bones.isEmpty() || getConfig().buryBones() != BuryType.OFFER_SPELL) {
            return false;
        }

        return Inventory.isFull() || ashes.size() >= 3;
    }

    private boolean shouldCastSinister() {
        var bones = getBones().stream().filter(x -> x.hasAction("Bury")).collect(Collectors.toList());

        if (bones.isEmpty() || getConfig().buryBones() != BuryType.OFFER_SPELL) {
            return false;
        }

        return Inventory.isFull() || bones.size() >= 3;
    }
}
