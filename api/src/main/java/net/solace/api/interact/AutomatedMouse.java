package net.solace.api.interact;

import net.solace.api.interact.Automation;

public final class AutomatedMouse
implements Automation {
    private final int x;
    private final int y;
    private final boolean click;

    public AutomatedMouse(int x, int y, boolean click) {
        this.x = x;
        this.y = y;
        this.click = click;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public boolean isClick() {
        return this.click;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof AutomatedMouse)) {
            return false;
        }
        AutomatedMouse other = (AutomatedMouse)o;
        if (this.getX() != other.getX()) {
            return false;
        }
        if (this.getY() != other.getY()) {
            return false;
        }
        return this.isClick() == other.isClick();
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getX();
        result = result * 59 + this.getY();
        result = result * 59 + (this.isClick() ? 79 : 97);
        return result;
    }

    public String toString() {
        return "AutomatedMouse(x=" + this.getX() + ", y=" + this.getY() + ", click=" + this.isClick() + ")";
    }
}

