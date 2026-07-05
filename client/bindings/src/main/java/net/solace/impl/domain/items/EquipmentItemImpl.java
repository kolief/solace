package net.solace.impl.domain.items;

import net.runelite.api.Item;
import net.runelite.api.MenuAction;
import net.runelite.api.gameval.InventoryID;
import net.solace.api.domain.game.IClient;
import net.solace.api.domain.widgets.IWidget;
import net.solace.api.interact.AutomatedMenu;
import net.solace.api.interact.InteractMethod;
import net.solace.api.widgets.EquipmentSlot;

public class EquipmentItemImpl extends ItemImpl {
    private static final int BASE_ACTION_PARAM = 451;
    private static final int MAX_CUSTOM_ACTIONS = 8;
    private final String[] actions;
    private final EquipmentSlot equipmentSlot;

    public EquipmentItemImpl(Item wrapped, int slot, IWidget widget, IClient client) {
        super(wrapped, slot, widget, client, InventoryID.WORN);
        this.actions = generateActions();
        this.equipmentSlot = EquipmentSlot.fromSlotIndex(slot);
    }

    private String[] generateActions() {
        var composition = getComposition();

        if (composition == null) {
            return null;
        }

        var actions = new String[8];
        actions[0] = "Remove";

        var index = 1;
        for (var param = BASE_ACTION_PARAM; param < (BASE_ACTION_PARAM + MAX_CUSTOM_ACTIONS) - 1; param++) {
            actions[index++] = composition.getStringValue(param);
        }

        return actions;
    }

    @Override
    public String[] getActions() {
        return actions;
    }

    @Override
    public AutomatedMenu generateMenu(InteractMethod interactMethod, int actionIndex) {
        return MENU_FACTORY.equipmentItem(getId(), equipmentSlot)
                .interactMethod(interactMethod)
                .actionIndex(actionIndex)
                .build(getClickPoint());
    }

    @Override
    public AutomatedMenu generateMenu(InteractMethod interactMethod, MenuAction opcode) {
        return MENU_FACTORY.equipmentItem(getId(), equipmentSlot)
                .interactMethod(interactMethod)
                .opcode(opcode)
                .build(getClickPoint());
    }
}
