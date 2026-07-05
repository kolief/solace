package net.solace.sdk.magic;

import java.time.Instant;
import net.solace.api.Static;
import net.solace.api.domain.actors.INPC;
import net.solace.api.domain.actors.IPlayer;
import net.solace.api.domain.items.IItem;
import net.solace.api.domain.tiles.ITileItem;
import net.solace.api.domain.tiles.ITileObject;
import net.solace.api.interact.InteractMethod;
import net.solace.api.magic.IMagic;
import net.solace.api.magic.Spell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Magic {
    private static final Logger log = LoggerFactory.getLogger(Magic.class);
    private static final IMagic MAGIC = Static.getMagic();

    public static boolean isAutoCasting(Spell spell) {
        return MAGIC.isAutoCasting(spell);
    }

    public static boolean isAutoCasting() {
        return MAGIC.isAutoCasting();
    }

    public static boolean isDefensiveAutoCasting() {
        return MAGIC.isDefensiveAutoCasting();
    }

    public static boolean isSpellSelected(Spell spell) {
        return MAGIC.isSpellSelected(spell);
    }

    public static void cast(InteractMethod interactMethod, Spell spell, IItem target) {
        MAGIC.cast(interactMethod, spell, target);
    }

    public static void cast(Spell spell, IItem target) {
        MAGIC.cast(spell, target);
    }

    public static void cast(InteractMethod interactMethod, Spell spell, IItem target, boolean queued) {
        MAGIC.cast(interactMethod, spell, target, queued);
    }

    public static void cast(Spell spell, IItem target, boolean queued) {
        MAGIC.cast(spell, target, queued);
    }

    public static void cast(InteractMethod interactMethod, Spell spell, INPC target) {
        MAGIC.cast(interactMethod, spell, target);
    }

    public static void cast(Spell spell, INPC target) {
        MAGIC.cast(spell, target);
    }

    public static void cast(InteractMethod interactMethod, Spell spell, INPC target, boolean queued) {
        MAGIC.cast(interactMethod, spell, target, queued);
    }

    public static void cast(Spell spell, INPC target, boolean queued) {
        MAGIC.cast(spell, target, queued);
    }

    public static void cast(InteractMethod interactMethod, Spell spell, IPlayer target) {
        MAGIC.cast(interactMethod, spell, target);
    }

    public static void cast(Spell spell, IPlayer target) {
        MAGIC.cast(spell, target);
    }

    public static void cast(InteractMethod interactMethod, Spell spell, IPlayer target, boolean queued) {
        MAGIC.cast(interactMethod, spell, target, queued);
    }

    public static void cast(Spell spell, IPlayer target, boolean queued) {
        MAGIC.cast(spell, target, queued);
    }

    public static void cast(InteractMethod interactMethod, Spell spell, ITileItem target) {
        MAGIC.cast(interactMethod, spell, target);
    }

    public static void cast(Spell spell, ITileItem target) {
        MAGIC.cast(spell, target);
    }

    public static void cast(InteractMethod interactMethod, Spell spell, ITileItem target, boolean queued) {
        MAGIC.cast(interactMethod, spell, target, queued);
    }

    public static void cast(Spell spell, ITileItem target, boolean queued) {
        MAGIC.cast(spell, target, queued);
    }

    public static void cast(InteractMethod interactMethod, Spell spell, ITileObject target) {
        MAGIC.cast(interactMethod, spell, target);
    }

    public static void cast(Spell spell, ITileObject target) {
        MAGIC.cast(spell, target);
    }

    public static void cast(InteractMethod interactMethod, Spell spell, ITileObject target, boolean queued) {
        MAGIC.cast(interactMethod, spell, target, queued);
    }

    public static void cast(Spell spell, ITileObject target, boolean queued) {
        MAGIC.cast(spell, target, queued);
    }

    public static void cast(InteractMethod interactMethod, Spell spell) {
        MAGIC.cast(interactMethod, spell);
    }

    public static void cast(Spell spell) {
        MAGIC.cast(spell);
    }

    public static void cast(InteractMethod interactMethod, Spell spell, String action) {
        MAGIC.cast(interactMethod, spell, action);
    }

    public static void cast(Spell spell, String action) {
        MAGIC.cast(spell, action);
    }

    public static void cast(Spell spell, int actionIndex) {
        MAGIC.cast(spell, actionIndex);
    }

    public static void cast(InteractMethod interactMethod, Spell spell, int actionIndex) {
        MAGIC.cast(interactMethod, spell, actionIndex);
    }

    public static void selectSpell(Spell spell) {
        MAGIC.selectSpell(spell);
    }

    public static Instant getLastHomeTeleportUsage() {
        return MAGIC.getLastHomeTeleportUsage();
    }

    public static boolean isHomeTeleportOnCooldown() {
        return MAGIC.isHomeTeleportOnCooldown();
    }

    public static void setAutoCast(Spell spell, boolean defensive) {
        MAGIC.setAutoCast(spell, defensive);
    }

    public static void deselectAutoCast() {
        MAGIC.deselectAutoCast();
    }
}

