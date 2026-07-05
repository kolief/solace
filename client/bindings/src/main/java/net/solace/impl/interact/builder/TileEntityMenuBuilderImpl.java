package net.solace.impl.interact.builder;

import lombok.RequiredArgsConstructor;
import net.runelite.api.MenuAction;
import net.solace.api.coords.Coordinate;
import net.solace.api.interact.AutomatedMenu;
import net.solace.api.interact.builder.TileEntityMenuBuilder;

@RequiredArgsConstructor
public class TileEntityMenuBuilderImpl extends AbstractMenuBuilder<TileEntityMenuBuilder> implements TileEntityMenuBuilder {
    private final int objectId;
    private final int sceneX;
    private final int sceneY;
    private final boolean item;

    @Override
    public AutomatedMenu build(Coordinate clickPoint) {
        if (actionIndex == null && opcode == null) {
            throw new IllegalStateException("either actionIndex or opcode must be set");
        }

        var opcode = this.opcode != null ? this.opcode : getActionOpcode();
        var worldViewId = this.worldViewId != null ? this.worldViewId : -1;

        return AutomatedMenu.builder()
                .interactMethod(interactMethod)
                .identifier(objectId)
                .opcode(opcode)
                .param0(sceneX)
                .param1(sceneY)
                .worldViewId(worldViewId)
                .option(option)
                .target(target)
                .useItemId(useItemId)
                .useItemSlot(useItemSlot)
                .castSpell(castSpell)
                .clickPoint(clickPoint)
                .build();
    }

    private MenuAction getActionOpcode() {
        if (item) {
            return getTileItemOpcode();
        }

        return getTileObjectOpcode();
    }

    private MenuAction getTileObjectOpcode() {
        switch (actionIndex) {
            case 0:
                return MenuAction.GAME_OBJECT_FIRST_OPTION;
            case 1:
                return MenuAction.GAME_OBJECT_SECOND_OPTION;
            case 2:
                return MenuAction.GAME_OBJECT_THIRD_OPTION;
            case 3:
                return MenuAction.GAME_OBJECT_FOURTH_OPTION;
            case 4:
                return MenuAction.GAME_OBJECT_FIFTH_OPTION;
            default:
                throw new IllegalArgumentException("action = " + actionIndex);
        }
    }

    private MenuAction getTileItemOpcode() {
        switch (actionIndex) {
            case 0:
                return MenuAction.GROUND_ITEM_FIRST_OPTION;
            case 1:
                return MenuAction.GROUND_ITEM_SECOND_OPTION;
            case 2:
                return MenuAction.GROUND_ITEM_THIRD_OPTION;
            case 3:
                return MenuAction.GROUND_ITEM_FOURTH_OPTION;
            case 4:
                return MenuAction.GROUND_ITEM_FIFTH_OPTION;
            default:
                throw new IllegalArgumentException("action = " + actionIndex);
        }
    }
}
