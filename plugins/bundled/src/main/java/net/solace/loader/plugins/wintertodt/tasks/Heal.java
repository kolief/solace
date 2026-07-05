package net.solace.loader.plugins.wintertodt.tasks;

import net.solace.loader.plugins.wintertodt.SolaceWintertodtPlugin;

public class Heal extends WintertodtTask {
    public Heal(SolaceWintertodtPlugin context) {
        super(context);
    }

    @Override
    public boolean validate() {
        return getFoodCount() != 0 && getWarmthPercent() <= getConfig().eatHp();
    }

    @Override
    public int execute() {
        getFoodItem().interact("Eat", "Drink");
        return -1;
    }

    @Override
    public String toString() {
        return "Healing";
    }
}
