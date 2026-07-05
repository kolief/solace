package net.solace.impl.magic;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.MenuAction;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.VarPlayerID;
import net.solace.api.domain.actors.INPC;
import net.solace.api.domain.actors.IPlayer;
import net.solace.api.domain.game.IClient;
import net.solace.api.domain.items.IItem;
import net.solace.api.domain.tiles.ITileItem;
import net.solace.api.domain.tiles.ITileObject;
import net.solace.api.game.IVars;
import net.solace.api.interact.InteractManager;
import net.solace.api.interact.InteractMethod;
import net.solace.api.interact.WidgetAction;
import net.solace.api.magic.IMagic;
import net.solace.api.magic.Spell;
import net.solace.api.widgets.IWidgets;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

@RequiredArgsConstructor
@Slf4j
public class MagicImpl implements IMagic {
    private static final int AUTOCAST_VARP = 108;
    private static final int AUTOCAST_SPELL = 276;
    private static final int DEFENSIVE_CASTING_MODE = 2668;

    private final InteractManager interactManager;
    private final IClient client;
    private final IVars vars;
    private final IWidgets widgets;

    @Override
    public boolean isAutoCasting(Spell spell) {
        if (!isAutoCasting()) {
            return false;
        }

        return vars.getBit(AUTOCAST_SPELL) == spell.getAutocastIndex();
    }

    @Override
    public boolean isAutoCasting() {
        return vars.getVarp(AUTOCAST_VARP) != 0;
    }

    @Override
    public boolean isDefensiveAutoCasting() {
        return vars.getBit(DEFENSIVE_CASTING_MODE) != 0;
    }

    @Override
    public boolean isSpellSelected(Spell spell) {
        var widget = widgets.get(spell.getInterfaceAddress());
        if (widget != null) {
            return widget.getBorderType() == 2;
        }

        return false;
    }

    @Override
    public void cast(InteractMethod interactMethod, Spell spell, IItem item) {
        item.interact(interactMethod, spell);
    }

    @Override
    public void cast(InteractMethod interactMethod, Spell spell, INPC npc) {
        npc.interact(interactMethod, spell);
    }

    @Override
    public void cast(InteractMethod interactMethod, Spell spell, IPlayer player) {
        player.interact(interactMethod, spell);
    }

    @Override
    public void cast(InteractMethod interactMethod, Spell spell, ITileItem tileItem) {
        tileItem.interact(interactMethod, spell);
    }

    @Override
    public void cast(InteractMethod interactMethod, Spell spell, ITileObject tileObject) {
        tileObject.interact(interactMethod, spell);
    }

    @Override
    public void cast(InteractMethod interactMethod, Spell spell) {
        cast(interactMethod, spell, "Cast");
    }

    @Override
    public void cast(InteractMethod interactMethod, Spell spell, String action) {
        var widget = widgets.get(spell.getInterfaceAddress());
        if (widget != null) {
            var actionIndex = widget.getActionIndex(action);
            cast(interactMethod, spell, actionIndex);
        }
    }

    @Override
    public void cast(InteractMethod interactMethod, Spell spell, int actionIndex) {
        var widget = widgets.get(spell.getInterfaceAddress());
        if (widget != null) {
            var menu = widget.generateMenu(interactMethod, actionIndex);
            var isTargetOnly = !widget.hasAction(Objects::nonNull);
            if (isTargetOnly || actionIndex == -1) {
                menu.setOpcode(MenuAction.WIDGET_TARGET);
            }

            client.interact(menu);
        }
    }

    @Override
    public void cast(InteractMethod interactMethod, Spell spell, IItem item, boolean queued) {
        cast(interactMethod, spell, item);
    }

    @Override
    public void cast(InteractMethod interactMethod, Spell spell, INPC npc, boolean queued) {
        cast(interactMethod, spell, npc);
    }

    @Override
    public void cast(InteractMethod interactMethod, Spell spell, IPlayer player, boolean queued) {
        cast(interactMethod, spell, player);
    }

    @Override
    public void cast(InteractMethod interactMethod, Spell spell, ITileItem tileItem, boolean queued) {
        cast(interactMethod, spell, tileItem);
    }

    @Override
    public void cast(InteractMethod interactMethod, Spell spell, ITileObject tileObject, boolean queued) {
        cast(interactMethod, spell, tileObject);
    }

    @Override
    public void selectSpell(Spell spell) {
        var widget = widgets.get(spell.getInterfaceAddress());
        if (widget != null) {
            client.getWrapped().menuAction(
                    -1,
                    widget.getId(),
                    MenuAction.WIDGET_TARGET,
                    0,
                    -1,
                    "Automated",
                    ""
            );
        }
    }

    @Override
    public Instant getLastHomeTeleportUsage() {
        return Instant.ofEpochSecond(vars.getVarp(VarPlayerID.AIDE_TELE_TIMER) * 60L);
    }

    @Override
    public boolean isHomeTeleportOnCooldown() {
        return getLastHomeTeleportUsage().plus(30, ChronoUnit.MINUTES).isAfter(Instant.now());
    }

    @Override
    public void setAutoCast(Spell spell, boolean defensive) {
        interactManager.queue(new WidgetAction(1, defensive ? InterfaceID.CombatInterface.AUTOCAST_DEFENSIVE : InterfaceID.CombatInterface.AUTOCAST_NORMAL, -1, -1));
        interactManager.queue(new WidgetAction(1, 13172737, spell.getAutocastIndex(), -1));
    }

    @Override
    public void deselectAutoCast() {
        interactManager.queue(new WidgetAction(1, InterfaceID.CombatInterface.AUTOCAST_NORMAL, -1, -1));
        interactManager.queue(new WidgetAction(1, 13172737, 0, -1));
    }
}
