package net.solace.api.interact.builder;

import net.solace.api.interact.builder.ActorMenuBuilder;
import net.solace.api.interact.builder.ItemMenuBuilder;
import net.solace.api.interact.builder.TileEntityMenuBuilder;
import net.solace.api.interact.builder.WidgetMenuBuilder;
import net.solace.api.widgets.EquipmentSlot;

public interface IMenuFactory {
    public ActorMenuBuilder player(int var1);

    public ActorMenuBuilder npc(int var1);

    public ItemMenuBuilder item(int var1, int var2, int var3);

    public ItemMenuBuilder inventoryItem(int var1, int var2);

    public ItemMenuBuilder equipmentItem(int var1, EquipmentSlot var2);

    public ItemMenuBuilder bankItem(int var1, int var2);

    public ItemMenuBuilder bankInventoryItem(int var1, int var2);

    public TileEntityMenuBuilder tileObject(int var1, int var2, int var3);

    public TileEntityMenuBuilder tileItem(int var1, int var2, int var3);

    public WidgetMenuBuilder widget(int var1);
}

