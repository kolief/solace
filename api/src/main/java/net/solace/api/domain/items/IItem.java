package net.solace.api.domain.items;

import net.runelite.api.Item;
import net.runelite.api.ItemComposition;
import net.solace.api.domain.Identifiable;
import net.solace.api.domain.Interactable;
import net.solace.api.domain.Nameable;
import net.solace.api.domain.RuneLiteWrapper;
import net.solace.api.domain.widgets.IWidget;

public interface IItem
extends Interactable,
Identifiable,
Nameable,
RuneLiteWrapper<Item> {
    public ItemComposition getComposition();

    public int getSlot();

    public IWidget getWidget();

    public void setWidget(IWidget var1);

    public int getQuantity();

    public boolean isPlaceholder();

    public int getNotedId();

    public boolean isNoted();

    public boolean isStackable();

    public boolean isTradable();

    public boolean isMembers();

    public String[][] getSubOptions();

    public boolean hasSubOption(String var1);

    public int[] getSubOptionIndex(String var1);
}

