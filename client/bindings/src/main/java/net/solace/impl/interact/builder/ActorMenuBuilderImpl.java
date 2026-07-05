package net.solace.impl.interact.builder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.MenuAction;
import net.solace.api.coords.Coordinate;
import net.solace.api.interact.AutomatedMenu;
import net.solace.api.interact.builder.ActorMenuBuilder;

@Slf4j
@RequiredArgsConstructor
public class ActorMenuBuilderImpl extends AbstractMenuBuilder<ActorMenuBuilder> implements ActorMenuBuilder {
    private final Integer npcIndex;
    private final Integer playerIndex;

    @Override
    public AutomatedMenu build(Coordinate clickPoint) {
        if (actionIndex == null && opcode == null) {
            throw new IllegalStateException("either actionIndex or opcode must be set");
        }

        if (npcIndex == null && playerIndex == null) {
            throw new IllegalStateException("npcIndex or playerIndex must be set");
        }

        var id = npcIndex != null ? npcIndex : playerIndex;
        var opcode = this.opcode != null ? this.opcode : getActionOpcode(actionIndex);
        var worldViewId = this.worldViewId != null ? this.worldViewId : -1;

        return AutomatedMenu.builder()
                .interactMethod(interactMethod)
                .identifier(id)
                .opcode(opcode)
                .param0(0)
                .param1(0)
                .worldViewId(worldViewId)
                .option(option)
                .target(target)
                .useItemId(useItemId)
                .useItemSlot(useItemSlot)
                .castSpell(castSpell)
                .clickPoint(clickPoint)
                .build();
    }

    private MenuAction getActionOpcode(int action) {
        if (npcIndex != null) {
            return getNpcActionOpcode(action);
        } else {
            return getPlayerActionOpcode(action);
        }
    }

    private MenuAction getNpcActionOpcode(int action) {
        switch (action) {
            case 0:
                return MenuAction.NPC_FIRST_OPTION;
            case 1:
                return MenuAction.NPC_SECOND_OPTION;
            case 2:
                return MenuAction.NPC_THIRD_OPTION;
            case 3:
                return MenuAction.NPC_FOURTH_OPTION;
            case 4:
                return MenuAction.NPC_FIFTH_OPTION;
            default:
                throw new IllegalArgumentException("unrecognized action = " + action);
        }
    }

    private MenuAction getPlayerActionOpcode(int action) {
        switch (action) {
            case 0:
                return MenuAction.PLAYER_FIRST_OPTION;
            case 1:
                return MenuAction.PLAYER_SECOND_OPTION;
            case 2:
                return MenuAction.PLAYER_THIRD_OPTION;
            case 3:
                return MenuAction.PLAYER_FOURTH_OPTION;
            case 4:
                return MenuAction.PLAYER_FIFTH_OPTION;
            case 5:
                return MenuAction.PLAYER_SIXTH_OPTION;
            case 6:
                return MenuAction.PLAYER_SEVENTH_OPTION;
            case 7:
                return MenuAction.PLAYER_EIGHTH_OPTION;
            default:
                throw new IllegalArgumentException("unrecognized action = " + action);
        }
    }
}
