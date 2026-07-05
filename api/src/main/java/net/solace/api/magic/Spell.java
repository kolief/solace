package net.solace.api.magic;

import net.solace.api.Static;
import net.solace.api.domain.Interactable;
import net.solace.api.domain.actors.INPC;
import net.solace.api.domain.actors.IPlayer;
import net.solace.api.domain.items.IItem;
import net.solace.api.domain.tiles.ITileItem;
import net.solace.api.domain.tiles.ITileObject;
import net.solace.api.magic.RuneRequirement;
import net.solace.api.magic.SpellBook;
import net.solace.api.widgets.InterfaceAddress;

public interface Spell {
    public int getLevel();

    public int getComponent();

    @Deprecated(forRemoval=true)
    default public InterfaceAddress getInterfaceAddress() {
        return new InterfaceAddress(this.getComponent());
    }

    public boolean canCast();

    public RuneRequirement[] getRequirements();

    public SpellBook getSpellBook();

    default public void cast() {
        Static.getMagic().cast(this);
    }

    default public void cast(String action) {
        Static.getMagic().cast(this, action);
    }

    default public void castOn(Interactable interactable) {
        if (interactable instanceof IItem) {
            Static.getMagic().cast(this, (IItem)interactable);
            return;
        }
        if (interactable instanceof INPC) {
            Static.getMagic().cast(this, (INPC)interactable);
            return;
        }
        if (interactable instanceof IPlayer) {
            Static.getMagic().cast(this, (IPlayer)interactable);
            return;
        }
        if (interactable instanceof ITileItem) {
            Static.getMagic().cast(this, (ITileItem)interactable);
            return;
        }
        if (interactable instanceof ITileObject) {
            Static.getMagic().cast(this, (ITileObject)interactable);
        }
    }

    public int getMenuIdentifier();

    public int getAutocastIndex();
}

