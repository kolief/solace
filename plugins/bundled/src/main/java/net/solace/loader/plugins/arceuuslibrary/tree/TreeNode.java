package net.solace.loader.plugins.arceuuslibrary.tree;

import lombok.extern.slf4j.Slf4j;
import net.solace.loader.plugins.arceuuslibrary.SolaceArceuusLibrary;
import net.solace.sdk.utils.MessageUtils;

@Slf4j
public abstract class TreeNode {
    protected final SolaceArceuusLibrary context;
    private TreeNode noNode;
    private TreeNode yesNode;

    protected TreeNode(SolaceArceuusLibrary context) {
        this.context = context;
    }

    public TreeNode getNo() {
        return noNode;
    }

    public TreeNode setNo(TreeNode noNode) {
        this.noNode = noNode;
        return this;
    }

    public TreeNode getYes() {
        return yesNode;
    }

    public TreeNode setYes(TreeNode yesNode) {
        this.yesNode = yesNode;
        return this;
    }

    public boolean isLeaf() {
        return noNode == null && yesNode == null;
    }

    protected void error(String text, Object... replacements) {
        MessageUtils.addMessage(formatMessage("Error: " + text, replacements));
    }

    protected void debug(String text, Object... replacements) {
        MessageUtils.addMessage(formatMessage("Debug: " + text, replacements));
    }

    protected void info(String text, Object... replacements) {
        MessageUtils.addMessage(formatMessage("Info: " + text, replacements));
    }

    protected void warn(String text, Object... replacements) {
        MessageUtils.addMessage(formatMessage("Warning: " + text, replacements));
    }

    private String formatMessage(String text, Object... replacements) {
        return String.format("[" + this + "] " + text, replacements);
    }

    public abstract int execute();
}
