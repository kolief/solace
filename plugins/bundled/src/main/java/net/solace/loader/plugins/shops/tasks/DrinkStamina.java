package net.solace.loader.plugins.shops.tasks;

import net.runelite.api.gameval.ItemID;
import net.solace.api.domain.items.IBankItem;
import net.solace.api.plugins.PluginTask;
import net.solace.loader.plugins.shops.SolaceShopsPlugin;
import net.solace.sdk.items.Bank;
import net.solace.sdk.movement.Movement;

public class DrinkStamina extends PluginTask<SolaceShopsPlugin> {
    private static final int[] STAMINA_POTIONS = new int[]{ItemID._1DOSESTAMINA, ItemID._2DOSESTAMINA,
            ItemID._3DOSESTAMINA, ItemID._4DOSESTAMINA};

    public DrinkStamina(SolaceShopsPlugin context) {
        super(context);
    }

    @Override
    public boolean validate() {
        return getContext().getConfig().drinkStamina()
               && Bank.isOpen()
               && Bank.contains(STAMINA_POTIONS)
               && !Movement.isStaminaBoosted();
    }

    @Override
    public int execute() {
        var stamina = Bank.Inventory.getFirst(STAMINA_POTIONS);
        if (stamina != null) {
            stamina.interact("Drink");
            return -1;
        }

        var stam = getStamina();
        if (stam != null) {
            stam.withdraw(1);
        }

        return -1;
    }

    private IBankItem getStamina() {
        var stamina1 = Bank.getFirst(ItemID._1DOSESTAMINA);
        if (stamina1 != null) {
            return stamina1;
        }

        var stamina2 = Bank.getFirst(ItemID._2DOSESTAMINA);
        if (stamina2 != null) {
            return stamina2;
        }

        var stamina3 = Bank.getFirst(ItemID._3DOSESTAMINA);
        if (stamina3 != null) {
            return stamina3;
        }

        return Bank.getFirst(ItemID._4DOSESTAMINA);
    }
}
