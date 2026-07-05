package net.solace.loader.plugins.arceuuslibrary.nodes.action;

import net.solace.api.plugins.exception.PluginStoppedException;
import net.solace.loader.plugins.arceuuslibrary.SolaceArceuusLibrary;
import net.solace.loader.plugins.arceuuslibrary.tree.ActionNode;
import net.solace.sdk.game.Game;
import net.solace.sdk.items.Bank;
import net.solace.sdk.items.GrandExchange;
import net.solace.sdk.widgets.Widgets;

public class Idle extends ActionNode {

    public Idle(SolaceArceuusLibrary context) {
        super(context);
    }

    @Override
    public int process() {
        return 600;
    }

    @Override
    public String toString() {
        return "Idle";
    }
}