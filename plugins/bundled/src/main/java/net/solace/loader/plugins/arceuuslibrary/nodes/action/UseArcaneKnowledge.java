package net.solace.loader.plugins.arceuuslibrary.nodes.action;

import net.runelite.api.MenuAction;
import net.runelite.api.gameval.InterfaceID;
import net.solace.loader.plugins.arceuuslibrary.SolaceArceuusLibrary;
import net.solace.loader.plugins.arceuuslibrary.domain.LibraryItem;
import net.solace.loader.plugins.arceuuslibrary.tree.ActionNode;
import net.solace.sdk.game.Client;
import net.solace.sdk.items.Inventory;

public class UseArcaneKnowledge extends ActionNode {
    public UseArcaneKnowledge(SolaceArceuusLibrary context) {
        super(context);
    }

    @Override
    public int process() {
        var menuIndex = context.getConfig().bookReward().ordinal() + 1;

        var items = Inventory.getAll(i -> i.getName().equals(LibraryItem.BOOK_OF_ARCANE_KNOWLEDGE));
        if (!items.isEmpty()) {
            for (var item : items) {
                item.interact("Read");
                Client.interact(0, MenuAction.WIDGET_CONTINUE.getId(), menuIndex, InterfaceID.Chatmenu.OPTIONS);
            }
        } else {
            error("No arcane books in inventory");
        }

        return -1;
    }

    @Override
    public String toString() {
        return "Read arcane book";
    }
}
