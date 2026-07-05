package net.solace.api.game;

import java.util.function.Predicate;
import net.runelite.api.coords.WorldPoint;
import net.solace.api.commons.Predicates;
import net.solace.api.domain.actors.INPC;
import net.solace.api.game.AttackStyle;
import net.solace.api.util.WeaponStyle;

public interface ICombat {
    public int getSpecEnergy();

    public int getMissingHealth();

    public boolean isVenomed();

    public boolean isPoisoned();

    public boolean isAntiPoisoned();

    public boolean isAntiVenomed();

    public boolean isSpecEnabled();

    public int getAxeEnergy();

    public boolean isAntifired();

    public boolean isSuperAntifired();

    public boolean isRetaliating();

    default public void toggleAutoRetaliate() {
        this.toggleAutoRetaliate(!this.isRetaliating());
    }

    public void toggleAutoRetaliate(Boolean var1);

    public void toggleSpec();

    public AttackStyle getAttackStyle();

    public void setAttackStyle(AttackStyle var1);

    default public INPC getAttackableNPC(int ... ids) {
        return this.getAttackableNPC(Predicates.ids(ids));
    }

    default public INPC getAttackableNPC(String ... names) {
        return this.getAttackableNPC(Predicates.names(names));
    }

    public INPC getAttackableNPC(Predicate<INPC> var1);

    public int getCurrentHealth();

    public double getHealthPercent();

    public WorldPoint getTombWorldPoint();

    public WorldPoint getCannonWorldPoint();

    public WeaponStyle getCurrentWeaponStyle();
}

