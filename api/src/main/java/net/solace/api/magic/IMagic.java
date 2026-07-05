package net.solace.api.magic;

import java.time.Instant;
import net.solace.api.domain.actors.INPC;
import net.solace.api.domain.actors.IPlayer;
import net.solace.api.domain.items.IItem;
import net.solace.api.domain.tiles.ITileItem;
import net.solace.api.domain.tiles.ITileObject;
import net.solace.api.interact.InteractMethod;
import net.solace.api.magic.Spell;

public interface IMagic {
    public boolean isAutoCasting(Spell var1);

    public boolean isAutoCasting();

    public boolean isDefensiveAutoCasting();

    public boolean isSpellSelected(Spell var1);

    default public void cast(InteractMethod interactMethod, Spell spell, IItem target) {
        this.cast(interactMethod, spell, target, false);
    }

    public void cast(InteractMethod var1, Spell var2, IItem var3, boolean var4);

    default public void cast(Spell spell, IItem target) {
        this.cast(null, spell, target);
    }

    default public void cast(Spell spell, IItem target, boolean queued) {
        this.cast(null, spell, target, queued);
    }

    default public void cast(InteractMethod interactMethod, Spell spell, INPC target) {
        this.cast(interactMethod, spell, target, false);
    }

    public void cast(InteractMethod var1, Spell var2, INPC var3, boolean var4);

    default public void cast(Spell spell, INPC target) {
        this.cast(null, spell, target);
    }

    default public void cast(Spell spell, INPC target, boolean queued) {
        this.cast(null, spell, target, queued);
    }

    default public void cast(InteractMethod interactMethod, Spell spell, IPlayer target) {
        this.cast(interactMethod, spell, target, false);
    }

    public void cast(InteractMethod var1, Spell var2, IPlayer var3, boolean var4);

    default public void cast(Spell spell, IPlayer target) {
        this.cast(null, spell, target);
    }

    default public void cast(Spell spell, IPlayer target, boolean queued) {
        this.cast(null, spell, target, queued);
    }

    default public void cast(InteractMethod interactMethod, Spell spell, ITileItem target) {
        this.cast(interactMethod, spell, target, false);
    }

    public void cast(InteractMethod var1, Spell var2, ITileItem var3, boolean var4);

    default public void cast(Spell spell, ITileItem target) {
        this.cast(null, spell, target);
    }

    default public void cast(Spell spell, ITileItem target, boolean queued) {
        this.cast(null, spell, target, queued);
    }

    default public void cast(InteractMethod interactMethod, Spell spell, ITileObject target) {
        this.cast(interactMethod, spell, target, false);
    }

    public void cast(InteractMethod var1, Spell var2, ITileObject var3, boolean var4);

    default public void cast(Spell spell, ITileObject target) {
        this.cast(null, spell, target);
    }

    default public void cast(Spell spell, ITileObject target, boolean queued) {
        this.cast(null, spell, target, queued);
    }

    public void cast(InteractMethod var1, Spell var2);

    default public void cast(Spell spell) {
        this.cast(null, spell);
    }

    public void cast(InteractMethod var1, Spell var2, String var3);

    default public void cast(Spell spell, String action) {
        this.cast(null, spell, action);
    }

    public void cast(InteractMethod var1, Spell var2, int var3);

    default public void cast(Spell spell, int actionIndex) {
        this.cast(null, spell, actionIndex);
    }

    public void selectSpell(Spell var1);

    public Instant getLastHomeTeleportUsage();

    public boolean isHomeTeleportOnCooldown();

    public void setAutoCast(Spell var1, boolean var2);

    public void deselectAutoCast();
}

