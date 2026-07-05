package net.solace.loader.plugins.fighter.data;

import net.solace.api.magic.SpellBook;

public enum AlchSpell {
    HIGH(SpellBook.Standard.HIGH_LEVEL_ALCHEMY),
    LOW(SpellBook.Standard.LOW_LEVEL_ALCHEMY),
    NONE(null);

    private final SpellBook.Standard spell;

    AlchSpell(SpellBook.Standard spell) {
        this.spell = spell;
    }

    public SpellBook.Standard getSpell() {
        return spell;
    }
}
