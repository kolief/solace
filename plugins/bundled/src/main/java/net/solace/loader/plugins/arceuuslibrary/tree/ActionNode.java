package net.solace.loader.plugins.arceuuslibrary.tree;

import net.solace.api.Static;
import net.solace.api.domain.tiles.ITileObject;
import net.solace.loader.plugins.arceuuslibrary.SolaceArceuusLibrary;

public abstract class ActionNode extends TreeNode {
    public ActionNode(SolaceArceuusLibrary context) {
        super(context);
    }

    public int execute() {
        context.setCurrentAction(toString());
        return process();
    }

    public abstract int process();

    protected void searchBookshelf(ITileObject bookshelf) {
        var menu = bookshelf.generateMenu("Search");
        menu.setTarget("Bookshelf"); // so RL's plugin properly handles the event
        Static.getClient().interact(menu);
    }
}
