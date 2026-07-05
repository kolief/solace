package net.solace.api.interact.builder;

import java.util.function.Supplier;
import net.runelite.api.MenuAction;
import net.solace.api.coords.Coordinate;
import net.solace.api.interact.AutomatedMenu;
import net.solace.api.interact.InteractMethod;
import net.solace.api.magic.Spell;

public interface MenuBuilder<B extends MenuBuilder<B>> {
    public B actionIndex(Integer var1);

    public B subOpId(Integer var1);

    public B identifier(Integer var1);

    public B option(String var1);

    public B target(String var1);

    public B opcode(MenuAction var1);

    public B param0(Integer var1);

    public B param1(Integer var1);

    public B itemId(Integer var1);

    public B worldViewId(Integer var1);

    public B interactMethod(InteractMethod var1);

    public B useItem(int var1, int var2);

    public B castSpell(Spell var1);

    public B clickPointSupplier(Supplier<Coordinate> var1);

    default public AutomatedMenu build(int clickX, int clickY) {
        return this.build(new Coordinate(clickX, clickY));
    }

    public AutomatedMenu build(Coordinate var1);
}

