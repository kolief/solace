package net.solace.api.events;

import net.solace.api.interact.AutomatedMenu;

public class AutomatedMenuQueued {
    AutomatedMenu automatedMenu;
    private boolean consumed;

    public AutomatedMenuQueued(AutomatedMenu automatedMenu) {
        this.automatedMenu = automatedMenu;
        this.consumed = false;
    }

    public AutomatedMenu getAutomatedMenu() {
        return this.automatedMenu;
    }

    public boolean isConsumed() {
        return this.consumed;
    }

    public void setAutomatedMenu(AutomatedMenu automatedMenu) {
        this.automatedMenu = automatedMenu;
    }

    public void setConsumed(boolean consumed) {
        this.consumed = consumed;
    }
}

