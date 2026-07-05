package net.solace.loader.plugins.arceuuslibrary.nodes.decision;

import net.solace.loader.plugins.arceuuslibrary.SolaceArceuusLibrary;
import net.solace.loader.plugins.arceuuslibrary.tree.DecisionNode;
import net.solace.sdk.game.Game;

public class shouldIdle extends DecisionNode {

    public shouldIdle(SolaceArceuusLibrary context) {
        super(context);
    }

    @Override
    public boolean decide() {
        return !Game.isLoggedIn();
    }
}
