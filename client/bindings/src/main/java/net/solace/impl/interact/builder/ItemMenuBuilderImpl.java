package net.solace.impl.interact.builder;

import lombok.RequiredArgsConstructor;
import net.runelite.api.MenuAction;
import net.solace.api.coords.Coordinate;
import net.solace.api.interact.AutomatedMenu;
import net.solace.api.interact.builder.ItemMenuBuilder;
import net.solace.api.widgets.EquipmentSlot;

@RequiredArgsConstructor
public class ItemMenuBuilderImpl extends AbstractMenuBuilder<ItemMenuBuilder> implements ItemMenuBuilder {
    private final int widgetId;
    private final int itemId;
    private final Integer slot;
    private final EquipmentSlot equipmentSlot;

    @Override
    public AutomatedMenu build(Coordinate clickPoint) {
        if (actionIndex == null && identifier == null && castSpell == null && useItemId == null) {
            throw new IllegalStateException("either actionIndex or identifier must be set");
        }

        var opcode = this.opcode != null ? this.opcode : MenuAction.CC_OP;
        var id = getIdentifier();
        var worldViewId = this.worldViewId != null ? this.worldViewId : -1;

        return AutomatedMenu.builder()
                .interactMethod(interactMethod)
                .identifier(id)
                .opcode(opcode)
                .param0(getSlot())
                .param1(getWidget())
                .itemId(itemId)
                .worldViewId(worldViewId)
                .option(option)
                .target(target)
                .useItemId(useItemId)
                .useItemSlot(useItemSlot)
                .castSpell(castSpell)
                .clickPoint(clickPoint)
                .build();
    }

    private int getSlot() {
        if (equipmentSlot != null) {
            return -1;
        }

        return slot;
    }

    private int getWidget() {
        if (equipmentSlot != null) {
            return equipmentSlot.getInterfaceAddress().getPackedId();
        }

        return widgetId;
    }

    private int getIdentifier() {
        if (identifier != null) {
            return identifier;
        }

        if (castSpell != null) {
            return castSpell.getMenuIdentifier() != -1 ? castSpell.getMenuIdentifier() : 0;
        }

        if (useItemId != null) {
            return 0;
        }

        return actionIndex + 1;
    }
}
