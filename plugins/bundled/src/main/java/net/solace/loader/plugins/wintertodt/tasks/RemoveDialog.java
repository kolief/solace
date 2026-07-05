package net.solace.loader.plugins.wintertodt.tasks;

import net.solace.loader.plugins.wintertodt.SolaceWintertodtPlugin;
import net.solace.sdk.widgets.Dialog;

public class RemoveDialog extends WintertodtTask {
    public RemoveDialog(SolaceWintertodtPlugin context) {
        super(context);
    }

    @Override
    public boolean validate() {
        return Dialog.canContinue() || Dialog.isViewingOptions() || Dialog.isEnterInputOpen();
    }

    @Override
    public int execute() {
        if (Dialog.canContinue()) {
            Dialog.continueSpace();
            return -2;
        }

        if (Dialog.isViewingOptions()) {
            Dialog.chooseOption(0);
            return -2;
        }

        return -1;
    }

    @Override
    public boolean isBlocking() {
        return false;
    }

    @Override
    public String toString() {
        return "Removing dialog";
    }
}
