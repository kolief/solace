package net.solace.impl.interact.builder;

import net.runelite.api.gameval.InterfaceID;
import net.solace.api.interact.builder.ActorMenuBuilder;
import net.solace.api.interact.builder.IMenuFactory;
import net.solace.api.interact.builder.ItemMenuBuilder;
import net.solace.api.interact.builder.TileEntityMenuBuilder;
import net.solace.api.interact.builder.WidgetMenuBuilder;
import net.solace.api.widgets.EquipmentSlot;

public class MenuFactoryImpl implements IMenuFactory {
    @Override
    public ActorMenuBuilder player(int index) {
        return new ActorMenuBuilderImpl(null, index);
    }

    @Override
    public ActorMenuBuilder npc(int index) {
        return new ActorMenuBuilderImpl(index, null);
    }

    @Override
    public ItemMenuBuilder item(int itemId, int slot, int widgetId) {
        return new ItemMenuBuilderImpl(widgetId, itemId, slot, null);
    }

    @Override
    public ItemMenuBuilder inventoryItem(int itemId, int slot) {
        return new ItemMenuBuilderImpl(InterfaceID.Inventory.ITEMS, itemId, slot, null);
    }

    @Override
    public ItemMenuBuilder equipmentItem(int itemId, EquipmentSlot equipmentSlot) {
        return new ItemMenuBuilderImpl(InterfaceID.WORNITEMS, itemId, null, equipmentSlot);
    }

    @Override
    public ItemMenuBuilder bankItem(int itemId, int slot) {
        return new ItemMenuBuilderImpl(InterfaceID.Bankmain.ITEMS, itemId, slot, null);
    }

    @Override
    public ItemMenuBuilder bankInventoryItem(int itemId, int slot) {
        return new ItemMenuBuilderImpl(InterfaceID.Bankside.ITEMS, itemId, slot, null);
    }

    @Override
    public TileEntityMenuBuilder tileObject(int id, int sceneX, int sceneY) {
        return new TileEntityMenuBuilderImpl(id, sceneX, sceneY, false);
    }

    @Override
    public TileEntityMenuBuilder tileItem(int id, int sceneX, int sceneY) {
        return new TileEntityMenuBuilderImpl(id, sceneX, sceneY, true);
    }

    @Override
    public WidgetMenuBuilder widget(int widgetId) {
        return new WidgetMenuBuilderImpl(widgetId);
    }
}
