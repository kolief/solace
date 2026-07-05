package net.solace.api.events;

import net.solace.api.interact.Automation;

public class AutomationQueued {
    Automation automation;
    private boolean consumed;

    public AutomationQueued(Automation automation) {
        this.automation = automation;
        this.consumed = false;
    }

    public Automation getAutomation() {
        return this.automation;
    }

    public boolean isConsumed() {
        return this.consumed;
    }

    public void setAutomation(Automation automation) {
        this.automation = automation;
    }

    public void setConsumed(boolean consumed) {
        this.consumed = consumed;
    }
}

