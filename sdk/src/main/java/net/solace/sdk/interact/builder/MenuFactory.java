package net.solace.sdk.interact.builder;

import net.solace.api.Static;
import net.solace.api.interact.builder.ActorMenuBuilder;
import net.solace.api.interact.builder.IMenuFactory;
import net.solace.api.interact.builder.ItemMenuBuilder;
import net.solace.api.interact.builder.TileEntityMenuBuilder;
import net.solace.api.interact.builder.WidgetMenuBuilder;
import net.solace.api.widgets.EquipmentSlot;

public class MenuFactory {
    private static final IMenuFactory MENU_FACTORY = Static.getMenuFactory();

    public static ActorMenuBuilder player(int index) {
        return MENU_FACTORY.player(index);
    }

    public static ActorMenuBuilder npc(int index) {
        return MENU_FACTORY.npc(index);
    }

    public static ItemMenuBuilder item(int itemId, int slot, int widgetId) {
        return MENU_FACTORY.item(itemId, slot, widgetId);
    }

    public static ItemMenuBuilder inventoryItem(int itemId, int slot) {
        return MENU_FACTORY.inventoryItem(itemId, slot);
    }

    public static ItemMenuBuilder equipmentItem(int itemId, EquipmentSlot slot) {
        return MENU_FACTORY.equipmentItem(itemId, slot);
    }

    public static ItemMenuBuilder bankItem(int itemId, int slot) {
        return MENU_FACTORY.bankItem(itemId, slot);
    }

    public static ItemMenuBuilder bankInventoryItem(int itemId, int slot) {
        return MENU_FACTORY.bankInventoryItem(itemId, slot);
    }

    public static TileEntityMenuBuilder tileObject(int objectId, int sceneX, int sceneY) {
        return MENU_FACTORY.tileObject(objectId, sceneX, sceneY);
    }

    public static TileEntityMenuBuilder tileItem(int itemId, int sceneX, int sceneY) {
        return MENU_FACTORY.tileItem(itemId, sceneX, sceneY);
    }

    public static WidgetMenuBuilder widget(int widgetId) {
        return MENU_FACTORY.widget(widgetId);
    }
}

