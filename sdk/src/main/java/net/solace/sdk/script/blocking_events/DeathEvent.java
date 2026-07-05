package net.solace.sdk.script.blocking_events;

import java.util.List;
import java.util.stream.Collectors;
import net.solace.api.domain.actors.INPC;
import net.solace.api.domain.tiles.ITileObject;
import net.solace.sdk.entities.NPCs;
import net.solace.sdk.entities.Players;
import net.solace.sdk.entities.TileObjects;
import net.solace.sdk.game.Client;
import net.solace.sdk.script.blocking_events.BlockingEvent;
import net.solace.sdk.widgets.Dialog;

public class DeathEvent
extends BlockingEvent {
    @Override
    public boolean validate() {
        return Client.isInInstancedRegion() && NPCs.getNearest("Death") != null;
    }

    @Override
    public int loop() {
        if (Players.getLocal().isMoving()) {
            return 1000;
        }
        INPC death = NPCs.getNearest("Death");
        if (!Dialog.isOpen()) {
            death.interact("Talk-to");
            return 1000;
        }
        if (Dialog.canContinue()) {
            Dialog.continueSpace();
            return 1000;
        }
        if (Dialog.isViewingOptions()) {
            ITileObject portal;
            List completedDialogs = Dialog.getOptions().stream().filter(x -> x.getText() != null && x.getText().contains("<str>")).collect(Collectors.toList());
            if (completedDialogs.size() >= 4 && (portal = TileObjects.getNearest("Portal")) != null) {
                portal.interact("Use");
                return 1000;
            }
            Dialog.getOptions().stream().filter(x -> !completedDialogs.contains(x)).findFirst().ifPresent(incompleteDialog -> Dialog.chooseOption(Dialog.getOptions().indexOf(incompleteDialog) + 1));
        }
        return 1000;
    }
}

