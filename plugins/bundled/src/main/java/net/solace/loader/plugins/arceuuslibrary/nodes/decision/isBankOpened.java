package net.solace.loader.plugins.arceuuslibrary.nodes.decision;

import net.solace.loader.plugins.arceuuslibrary.SolaceArceuusLibrary;
import net.solace.loader.plugins.arceuuslibrary.tree.DecisionNode;
import net.solace.sdk.items.Bank;

public class isBankOpened extends DecisionNode {
    public isBankOpened(SolaceArceuusLibrary context) {
        super(context);
    }

    @Override
    public boolean decide() {
        return Bank.isOpen();
    }
}
