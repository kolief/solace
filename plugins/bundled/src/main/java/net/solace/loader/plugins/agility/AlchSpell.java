package net.solace.loader.plugins.agility;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.solace.api.magic.SpellBook;

@RequiredArgsConstructor
@Getter
public enum AlchSpell {
    HIGH(SpellBook.Standard.HIGH_LEVEL_ALCHEMY),
    LOW(SpellBook.Standard.LOW_LEVEL_ALCHEMY),
    OFF(null);

    private final SpellBook.Standard spell;
}
