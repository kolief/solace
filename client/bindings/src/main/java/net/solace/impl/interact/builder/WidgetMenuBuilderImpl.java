package net.solace.impl.interact.builder;

import lombok.RequiredArgsConstructor;
import net.runelite.api.MenuAction;
import net.solace.api.coords.Coordinate;
import net.solace.api.interact.AutomatedMenu;
import net.solace.api.interact.builder.WidgetMenuBuilder;

@RequiredArgsConstructor
public class WidgetMenuBuilderImpl extends AbstractMenuBuilder<WidgetMenuBuilder> implements WidgetMenuBuilder {
    private final int widgetId;
    private Integer childId;
    private boolean resume;

    @Override
    public WidgetMenuBuilder childId(Integer childId) {
        this.childId = childId;
        return this;
    }

    @Override
    public WidgetMenuBuilder resume(boolean resume) {
        this.resume = resume;
        return this;
    }

    @Override
    public AutomatedMenu build(Coordinate clickPoint) {
        if (actionIndex == null && identifier == null && opcode == null) {
            throw new IllegalStateException("actionIndex or identifier or opcode must be set");
        }

        var opcode = getActionOpcode();
        if (opcode == MenuAction.WIDGET_CONTINUE) {
            resume = true;
        }

        var id = getMenuIdentifier();
        var param0 = childId != null ? childId : -1;
        var itemId = this.itemId != null ? this.itemId : -1;
        var worldViewId = this.worldViewId != null ? this.worldViewId : -1;

        return AutomatedMenu.builder()
                .interactMethod(interactMethod)
                .identifier(id)
                .opcode(opcode)
                .param0(param0)
                .param1(widgetId)
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

    private int getMenuIdentifier() {
        if (identifier != null) {
            return identifier;
        }

        if (resume) {
            return 0;
        }

        return actionIndex + 1;
    }

    private MenuAction getActionOpcode() {
        if (opcode != null) {
            return opcode;
        }

        if (resume) {
            return MenuAction.WIDGET_CONTINUE;
        }

        return MenuAction.CC_OP;
    }
}
