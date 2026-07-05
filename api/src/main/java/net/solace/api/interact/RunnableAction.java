package net.solace.api.interact;

import net.solace.api.interact.Automation;

public final class RunnableAction
implements Automation {
    private final Runnable runnable;

    public RunnableAction(Runnable runnable) {
        this.runnable = runnable;
    }

    public Runnable getRunnable() {
        return this.runnable;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof RunnableAction)) {
            return false;
        }
        RunnableAction other = (RunnableAction)o;
        Runnable this$runnable = this.getRunnable();
        Runnable other$runnable = other.getRunnable();
        return !(this$runnable == null ? other$runnable != null : !this$runnable.equals(other$runnable));
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Runnable $runnable = this.getRunnable();
        result = result * 59 + ($runnable == null ? 43 : $runnable.hashCode());
        return result;
    }

    public String toString() {
        return "RunnableAction(runnable=" + String.valueOf(this.getRunnable()) + ")";
    }
}

