package net.solace.impl.interact.builder;

import net.runelite.api.MenuAction;
import net.solace.api.coords.Coordinate;
import net.solace.api.interact.InteractMethod;
import net.solace.api.interact.builder.MenuBuilder;
import net.solace.api.magic.Spell;

import java.util.function.Supplier;

public abstract class AbstractMenuBuilder<B extends MenuBuilder<B>> implements MenuBuilder<B> {
    protected Integer actionIndex;
    protected Integer identifier;
    protected String option = "Automated";
    protected String target = "";
    protected MenuAction opcode;
    protected Integer param0;
    protected Integer param1;
    protected Integer itemId;
    protected Integer worldViewId;
    protected InteractMethod interactMethod;
    protected Integer useItemId;
    protected Integer useItemSlot;
    protected Spell castSpell;
    protected Supplier<Coordinate> clickPointSupplier;
    protected Integer subOpId;

    @Override
    public B subOpId(Integer subOpId) {
        this.subOpId = subOpId;
        return self();
    }

    @Override
    public B clickPointSupplier(Supplier<Coordinate> supplier) {
        this.clickPointSupplier = supplier;
        return self();
    }

    @Override
    public B actionIndex(Integer actionIndex) {
        this.actionIndex = actionIndex;
        return self();
    }

    @Override
    public B identifier(Integer integer) {
        this.identifier = integer;
        return self();
    }

    @Override
    public B option(String s) {
        this.option = s;
        return self();
    }

    @Override
    public B target(String s) {
        this.target = s;
        return self();
    }

    @Override
    public B opcode(MenuAction op) {
        this.opcode = op;
        return self();
    }

    @Override
    public B param0(Integer integer) {
        this.param0 = integer;
        return self();
    }

    @Override
    public B param1(Integer integer) {
        this.param1 = integer;
        return self();
    }

    @Override
    public B itemId(Integer integer) {
        this.itemId = integer;
        return self();
    }

    @Override
    public B worldViewId(Integer integer) {
        this.worldViewId = integer;
        return self();
    }

    @Override
    public B interactMethod(InteractMethod interactMethod) {
        this.interactMethod = interactMethod;
        return self();
    }

    @Override
    public B useItem(int itemid, int slot) {
        this.useItemId = itemid;
        this.useItemSlot = slot;
        return self();
    }

    @Override
    public B castSpell(Spell spell) {
        this.castSpell = spell;
        return self();
    }

    public B self() {
        return (B) this;
    }
}
