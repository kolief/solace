package net.solace.loader.plugins.arceuuslibrary.tree;

import lombok.extern.slf4j.Slf4j;
import net.solace.loader.plugins.arceuuslibrary.SolaceArceuusLibrary;

@Slf4j
public abstract class DecisionNode extends TreeNode {
    public DecisionNode(SolaceArceuusLibrary context) {
        super(context);
    }

    public abstract boolean decide();

    @Override
    public int execute() {
        TreeNode yes = getYes();
        TreeNode no = getNo();
        if (yes == null) {
            log.debug("Yes node is null, executing no node: {}", no.getClass().getSimpleName());
            return no.execute();
        }

        if (no == null) {
            log.debug("No node is null, executing yes node: {}", yes.getClass().getSimpleName());
            return yes.execute();
        }

        boolean decide = decide();
        log.debug("Decision node: {} decided: {}, executing: {}", getClass().getSimpleName(), decide, decide ? yes.getClass().getSimpleName() : no.getClass().getSimpleName());
        return decide ? yes.execute() : no.execute();
    }
}
