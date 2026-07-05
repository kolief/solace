package net.solace.api.domain.tiles;

import java.util.Arrays;
import java.util.List;
import net.runelite.api.ItemComposition;
import net.runelite.api.TileItem;
import net.solace.api.domain.RuneLiteWrapper;
import net.solace.api.domain.tiles.TileEntity;

public interface ITileItem
extends TileItem,
TileEntity,
RuneLiteWrapper<TileItem> {
    public boolean canPick();

    public boolean isTradable();

    public boolean isStackable();

    public boolean isNoted();

    public boolean isMembers();

    public String[] getInventoryActions();

    default public List<String> inventoryActions() {
        return Arrays.asList(this.getInventoryActions());
    }

    default public boolean hasInventoryAction(String action) {
        return this.inventoryActions().contains(action);
    }

    public void pickup();

    public ItemComposition getComposition();

    public int getHaPrice();

    public int getWorldViewId();
}

