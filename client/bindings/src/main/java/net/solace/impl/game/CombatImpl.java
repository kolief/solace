package net.solace.impl.game;

import lombok.RequiredArgsConstructor;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.VarPlayerID;
import net.runelite.api.gameval.VarbitID;
import net.solace.api.commons.Predicates;
import net.solace.api.domain.actors.INPC;
import net.solace.api.entities.INPCs;
import net.solace.api.entities.IPlayers;
import net.solace.api.game.AttackStyle;
import net.solace.api.game.ICombat;
import net.solace.api.game.ISkills;
import net.solace.api.game.IVars;
import net.solace.api.items.IEquipment;
import net.solace.api.util.WeaponMap;
import net.solace.api.util.WeaponStyle;
import net.solace.api.widgets.EquipmentSlot;
import net.solace.api.widgets.IWidgets;

import java.util.Objects;
import java.util.function.Predicate;

@RequiredArgsConstructor
public class CombatImpl implements ICombat {
    private static final int SPEC_ENERGY_VARP = 300;
    private static final int VENOM_THRESHOLD = 1000000;
    private static final int ANTIVENOM_THRESHOLD = -38;
    private static final int SPEC_VARP = 301;
    private static final int AXE_ENERGY_VARP = 3784;
    private static final int TOMB_LOCATION_VARP = 3916;
    private static final int CANNON_LOCATION_VARP = 3551;
    private static final int AUTO_RETALIATE_VARP = 172;
    private static final int ANTIFIRE = 3981;
    private static final int SUPER_ANTIFIRE = 6101;

    private final IVars vars;
    private final ISkills skills;
    private final IPlayers players;
    private final INPCs npcs;
    private final IWidgets widgets;
    private final IEquipment equipment;

    @Override
    public int getSpecEnergy() {
        return vars.getVarp(SPEC_ENERGY_VARP) / 10;
    }

    @Override
    public int getMissingHealth() {
        return Math.max(0, skills.getLevel(Skill.HITPOINTS) - skills.getBoostedLevel(Skill.HITPOINTS));
    }

    @Override
    public boolean isVenomed() {
        return vars.getVarp(VarPlayerID.POISON) >= VENOM_THRESHOLD;
    }

    @Override
    public boolean isPoisoned() {
        return vars.getVarp(VarPlayerID.POISON) > 0;
    }

    @Override
    public boolean isAntiPoisoned() {
        return vars.getVarp(VarPlayerID.POISON) < 0;
    }

    @Override
    public boolean isAntiVenomed() {
        return vars.getVarp(VarPlayerID.POISON) < ANTIVENOM_THRESHOLD;
    }

    @Override
    public boolean isRetaliating() {
        return vars.getVarp(AUTO_RETALIATE_VARP) == 0;
    }

    @Override
    public boolean isSpecEnabled() {
        return vars.getVarp(SPEC_VARP) == 1;
    }

    @Override
    public int getAxeEnergy() {
        return vars.getVarp(AXE_ENERGY_VARP);
    }

    @Override
    public boolean isAntifired() {
        return vars.getBit(ANTIFIRE) > 0;
    }

    @Override
    public boolean isSuperAntifired() {
        return vars.getBit(SUPER_ANTIFIRE) > 0;
    }

    @Override
    public void toggleAutoRetaliate() {
        toggleAutoRetaliate(!isRetaliating());
    }

    @Override
    public void toggleAutoRetaliate(Boolean active) {
        if (active == isRetaliating()) {
            return;
        }

        var retaliate = widgets.get(InterfaceID.CombatInterface.RETALIATE);
        if (retaliate != null) {
            retaliate.interact(0);
        }
    }

    @Override
    public void toggleSpec() {
        var spec = widgets.get(593, 39);
        if (spec != null) {
            spec.interact(0);
        }
    }

    @Override
    public AttackStyle getAttackStyle() {
        if (vars.getBit(VarbitID.AUTOCAST_DEFMODE) == 1) {
            return AttackStyle.SPELLS_DEFENSIVE;
        }

        return AttackStyle.fromIndex(vars.getVarp(43));
    }

    @Override
    public void setAttackStyle(AttackStyle attackStyle) {
        if (attackStyle.getInterfaceAddress() == null) {
            return;
        }

        var widget = widgets.get(attackStyle.getInterfaceAddress());
        if (widget != null) {
            widget.interact(0);
        }
    }

    @Override
    public INPC getAttackableNPC(int... ids) {
        return getAttackableNPC(Predicates.ids(ids));
    }

    @Override
    public INPC getAttackableNPC(String... names) {
        return getAttackableNPC(Predicates.names(names));
    }

    @Override
    public INPC getAttackableNPC(Predicate<INPC> filter) {
        var local = players.getLocal();
        var npcAttackingMe = npcs.getNearest(npc -> npc.hasAction("Attack")
                && npc.getInteracting() == local
                && filter.test(npc)
        );

        if (npcAttackingMe != null) {
            var attacker = players.getNearest(p -> Objects.equals(p.getInteracting(), npcAttackingMe)
                    && !Objects.equals(p, local));
            if (attacker == null) {
                return npcAttackingMe;
            }
        }

        var nearest = npcs.getNearest(npc -> npc.hasAction("Attack")
                && npc.getInteracting() == null
                && filter.test(npc)
        );
        if (nearest != null) {
            var attacker = players.getNearest(p -> Objects.equals(p.getInteracting(), nearest)
                    && !Objects.equals(p, local));
            if (attacker == null) {
                return nearest;
            }
        }

        return null;
    }

    @Override
    public int getCurrentHealth() {
        return skills.getBoostedLevel(Skill.HITPOINTS);
    }

    @Override
    public double getHealthPercent() {
        return ((double) getCurrentHealth() / skills.getLevel(Skill.HITPOINTS)) * 100;
    }

    @Override
    public WorldPoint getTombWorldPoint() {
        var tombVarp = vars.getVarp(TOMB_LOCATION_VARP);

        if (tombVarp == -1) {
            return null;
        }

        return WorldPoint.fromCoord(tombVarp);
    }

    @Override
    public WorldPoint getCannonWorldPoint() {
        var cannonVarp = vars.getVarp(CANNON_LOCATION_VARP);

        if (cannonVarp == -1) {
            return null;
        }

        return WorldPoint.fromCoord(cannonVarp);
    }

    @Override
    public WeaponStyle getCurrentWeaponStyle() {
        var weapon = equipment.fromSlot(EquipmentSlot.WEAPON);

        if (weapon == null) {
            return WeaponStyle.MELEE;
        } else {
            return WeaponMap.StyleMap.getOrDefault(weapon.getId(), WeaponStyle.MELEE);
        }
    }
}
