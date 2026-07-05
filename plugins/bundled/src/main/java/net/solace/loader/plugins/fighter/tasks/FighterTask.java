package net.solace.loader.plugins.fighter.tasks;

import lombok.Getter;
import lombok.Setter;
import net.runelite.api.gameval.ItemID;
import net.runelite.client.util.Text;
import net.runelite.client.util.WildcardMatcher;
import net.solace.api.domain.actors.INPC;
import net.solace.api.domain.items.IInventoryItem;
import net.solace.api.domain.items.IItem;
import net.solace.api.domain.tiles.ITileItem;
import net.solace.api.plugins.PluginTask;
import net.solace.loader.plugins.fighter.data.BoostPotions;
import net.solace.loader.plugins.fighter.SolaceFighterConfig;
import net.solace.loader.plugins.fighter.SolaceFighterPlugin;
import net.solace.sdk.entities.NPCs;
import net.solace.sdk.entities.Players;
import net.solace.sdk.entities.TileItems;
import net.solace.sdk.game.Prices;
import net.solace.sdk.game.Skills;
import net.solace.sdk.items.Inventory;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Setter
@Getter
public abstract class FighterTask extends PluginTask<SolaceFighterPlugin> {

    public FighterTask(SolaceFighterPlugin context) {
        super(context);
    }

    private int nextPrayerRestore = 10;
    private int nextEatPercent = 55;
    private int nextCannonballCount = 10;

    public static final String[] WAIT_FOR_DEAD_MONSTERS = {
            "gargoyle", "rockslug", "lizard", "zygomite"
    };

    public static final int[] GOADING_POTION_IDS = {
            ItemID._1DOSEGOADING, ItemID._2DOSEGOADING, ItemID._3DOSEGOADING,
            ItemID._4DOSEGOADING
    };

    public SolaceFighterConfig getConfig() {
        return getContext().getConfig();
    }

    public IItem getAntifire() {
        return Inventory.getFirst(
                getConfig().antifireType().getDoses()
        );
    }

    public IItem getGoadingPotion() {
        return Inventory.getFirst(GOADING_POTION_IDS);
    }

    private boolean shouldNotLoot(ITileItem item) {
        return textMatches(getDontLoot(), item.getName());
    }

    private boolean shouldLootUntradable(ITileItem item) {
        return getConfig().untradables()
                && (!isTradeable(item) || item.hasInventoryAction("Destroy"))
                && item.getId() != ItemID.COINS;
    }

    private boolean isTradeable(ITileItem item) {
        if (item.getName().contains("Ensouled")) {
            return true;
        }

        if (item.isNoted()) {
            return true;
        }

        return item.isTradable();
    }

    private boolean shouldLootByValue(ITileItem item) {
        if (!getConfig().lootByValue()) {
            return false;
        }

        var itemPrice = Prices.getItemPrice(!item.isNoted() ? item.getId() : item.getComposition().getLinkedNoteId()) * item.getQuantity();

        if (item.isStackable()) {
            return getConfig().stackableLootValue() > 0
                    && itemPrice >= getConfig().stackableLootValue();
        }

        return getConfig().lootValue() > 0
                && itemPrice >= getConfig().lootValue();
    }

    private boolean shouldLootByName(ITileItem item) {
        return textMatches(getLoot(), item.getName());
    }

    private boolean textMatches(List<String> itemNames, String itemName) {
        return itemNames.stream().anyMatch(name -> WildcardMatcher.matches(name.toLowerCase(), itemName.toLowerCase()));
    }

    public ITileItem getItemToLoot() {
        var center = getContext().getCenter();
        return TileItems.getAllMine(x -> x.distanceTo(center) <= getConfig().attackRange()
                        && x.canPick()
                        && !shouldNotLoot(x)
                        && (shouldLootByName(x) || shouldLootUntradable(x) || shouldLootByValue(x)))
                .stream()
                .min(Comparator.comparingInt(x -> x.distanceTo(Players.getLocal())))
                .orElse(null);
    }

    public IInventoryItem getItemToAlch() {
        return Inventory.getFirst(x -> x.getName() != null && textMatches(getAlchs(), x.getName()));
    }

    public boolean shouldBoost(BoostPotions potion) {
        if (potion == null || !Inventory.contains(potion.getIds())) {
            return false;
        }

        int level = Skills.getBoostedLevel(potion.getSkill());
        int baseLevel = Skills.getLevel(potion.getSkill());
        var boost = level - baseLevel;

        return boost <= getConfig().useCombatPotionsAtLevel();
    }

    public boolean shouldBoost() {
        return getConfig().boostPotions() != null && getConfig().boostPotions().stream().anyMatch(this::shouldBoost);
    }

    public IItem getBoost() {
        return Inventory.getFirst(getConfig().boostPotions().stream()
                .filter(this::shouldBoost)
                .findFirst()
                .map(BoostPotions::getIds)
                .orElse(null));
    }

    private boolean isNpcTargeted(INPC npc) {
        var players = Players.getAll(x -> x != Players.getLocal());
        return players.stream().anyMatch(x -> Objects.equals(x.getInteracting(), npc));
    }

    public List<INPC> getAttackableNpcs(Predicate<INPC> filter) {
        return NPCs.getAll(npc -> npc.hasAction("Attack", "Disturb", "Awaken")
                && npc.getName() != null
                && !npc.isDead()
                && !isNpcTargeted(npc)
                && filter.test(npc)
                && (textMatches(getMonsters(), npc.getName()) && npc.distanceTo(getContext().getCenter()) <= getConfig().attackRange()
                    || (getConfig().prioritizeAggroOutOfRange() && Objects.equals(npc.getInteracting(), Players.getLocal()) && npc.isAnimating() && npc.getWorldArea().hasLineOfSightTo(Players.getLocal().getWorldView(), Players.getLocal().getWorldArea())))
                && (!getConfig().enableSafespot()
                || getContext().getSafespot().toWorldArea().hasLineOfSightTo(npc.getWorldView(), npc.getWorldArea()))
        );
    }

    public INPC getBestAttackableNpc(Predicate<INPC> filter) {
        var npcs = getAttackableNpcs(filter);

        return npcs.stream().min(Comparator.comparingInt(x -> x.getWorldLocation().distanceTo(Players.getLocal().getWorldLocation()))).orElse(null);
    }

    public INPC getBestTarget() {
        var monsters = getMonsters();
        var currentInteracting = Players.getLocal().getInteracting();
        var allNpcs = getAttackableNpcs(x -> true);

        var targetingMe = allNpcs.stream().filter(x -> Objects.equals(x.getInteracting(), Players.getLocal()))
                .min(Comparator.comparingInt(x -> x.getWorldLocation().distanceTo(Players.getLocal().getWorldLocation())))
                .stream().collect(Collectors.toList());

        if (!targetingMe.isEmpty()) {
            if (currentInteracting instanceof INPC) {
                var npc = (INPC) currentInteracting;
                if (allNpcs.contains(npc)) {
                    return npc;
                }
            }

            return targetingMe.stream().findFirst().orElse(null);
        }

        if (currentInteracting instanceof INPC) {
            var npc = (INPC) currentInteracting;
            if (!monsters.contains(Objects.requireNonNull(currentInteracting.getName()).toLowerCase())) {
                return npc;
            }

            if (npc.isDead() && Arrays.stream(WAIT_FOR_DEAD_MONSTERS).anyMatch(x -> npc.getName().toLowerCase().contains(x))) {
                return npc;
            }
        }

        if (!getConfig().prioritizeMonsterListedEarlier()) {
            if (currentInteracting instanceof INPC && monsters.contains(currentInteracting.getName().toLowerCase())) {
                return (INPC) currentInteracting;
            }

            return getBestAttackableNpc(x -> true);
        }

        var allMonsters = getAttackableNpcs(x -> true);
        var bestMonsters = allMonsters.stream()
                .collect(Collectors.groupingBy(x -> monsters.indexOf(Objects.requireNonNull(x.getName()).toLowerCase())))
                .get(0);

        if (bestMonsters == null) {
            return getBestAttackableNpc(x -> true);
        }

        var bestNpc = getBestAttackableNpc(bestMonsters::contains);

        if (bestNpc != null) {
            return bestNpc;
        }

        if (currentInteracting instanceof INPC) {
            return (INPC) currentInteracting;
        }

        return getBestAttackableNpc(x -> true);
    }

    public List<String> getMonsters() {
        return Text.fromCSV(getContext().getConfig().monster().trim()).stream().map(String::toLowerCase).collect(Collectors.toList());
    }

    public List<String> getAlchs() {
        return Text.fromCSV(getContext().getConfig().alchItems().trim()).stream().map(String::toLowerCase).collect(Collectors.toList());
    }

    public List<String> getLoot() {
        return Text.fromCSV(getContext().getConfig().loots().trim()).stream().map(String::toLowerCase).collect(Collectors.toList());
    }

    public List<String> getDontLoot() {
        return Text.fromCSV(getContext().getConfig().dontLoot().trim()).stream().map(String::toLowerCase).collect(Collectors.toList());
    }

    public boolean shouldSafespot() {
        var safespot = getContext().getSafespot();

        return getConfig().enableSafespot() && safespot != null && !Players.getLocal().getWorldLocation().equals(safespot);
    }
}
