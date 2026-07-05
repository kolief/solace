package net.solace.api.events;

public final class PluginToggleHiddenChanged {
    private final boolean hide;

    public PluginToggleHiddenChanged(boolean hide) {
        this.hide = hide;
    }

    public boolean isHide() {
        return this.hide;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof PluginToggleHiddenChanged)) {
            return false;
        }
        PluginToggleHiddenChanged other = (PluginToggleHiddenChanged)o;
        return this.isHide() == other.isHide();
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + (this.isHide() ? 79 : 97);
        return result;
    }

    public String toString() {
        return "PluginToggleHiddenChanged(hide=" + this.isHide() + ")";
    }
}

