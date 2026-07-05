package net.solace.api.items.loadouts;

import java.util.Collection;
import net.solace.api.items.loadouts.Loadout;
import net.solace.api.items.loadouts.LoadoutItem;

public interface LoadoutBuilder {
    public LoadoutItem[] getInventory();

    public LoadoutItem[] getEquipment();

    public LoadoutItem[] getRunePouch();

    public LoadoutBuilder item(int var1, int var2, boolean var3, boolean var4, LoadoutItem.Type var5, int var6);

    public LoadoutBuilder item(int var1, int var2, int var3, boolean var4, boolean var5, LoadoutItem.Type var6, int var7);

    public LoadoutBuilder item(LoadoutItem var1);

    public LoadoutBuilder items(LoadoutItem ... var1);

    public LoadoutBuilder items(Collection<LoadoutItem> var1);

    public LoadoutBuilder item(int var1, int var2, boolean var3, LoadoutItem.Type var4, int var5);

    public LoadoutBuilder item(int var1, int var2, int var3, boolean var4, LoadoutItem.Type var5, int var6);

    public LoadoutBuilder item(int var1, int var2, LoadoutItem.Type var3, int var4);

    public LoadoutBuilder item(int var1, int var2, int var3, LoadoutItem.Type var4, int var5);

    public LoadoutBuilder item(int var1, LoadoutItem.Type var2, int var3);

    public LoadoutBuilder item(int var1, boolean var2, LoadoutItem.Type var3, int var4);

    public LoadoutBuilder inventoryItem(int var1, int var2, boolean var3, boolean var4, int var5);

    public LoadoutBuilder inventoryItem(int var1, int var2, int var3, boolean var4, boolean var5, int var6);

    public LoadoutBuilder inventoryItem(int var1, int var2, boolean var3, int var4);

    public LoadoutBuilder inventoryItem(int var1, int var2, int var3, boolean var4, int var5);

    public LoadoutBuilder inventoryItem(int var1, int var2, int var3);

    public LoadoutBuilder inventoryItem(int var1, int var2, int var3, int var4);

    public LoadoutBuilder inventoryItem(int var1, int var2);

    public LoadoutBuilder equipmentItem(int var1, int var2, boolean var3, int var4);

    public LoadoutBuilder equipmentItem(int var1, int var2, int var3, boolean var4, int var5);

    public LoadoutBuilder equipmentItem(int var1, int var2, int var3);

    public LoadoutBuilder equipmentItem(int var1, int var2, int var3, int var4);

    public LoadoutBuilder equipmentItem(int var1, int var2);

    public LoadoutBuilder runePouchItem(int var1, int var2, int var3);

    public LoadoutBuilder runePouchItem(int var1, int var2, int var3, int var4);

    public LoadoutBuilder disableInventory();

    public LoadoutBuilder disableEquipment();

    public LoadoutBuilder disableRunePouch();

    public Loadout build();
}

