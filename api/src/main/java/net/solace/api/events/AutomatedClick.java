package net.solace.api.events;

public class AutomatedClick {
    private int x;
    private int y;
    private boolean click;

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public boolean isClick() {
        return this.click;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setClick(boolean click) {
        this.click = click;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof AutomatedClick)) {
            return false;
        }
        AutomatedClick other = (AutomatedClick)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (this.getX() != other.getX()) {
            return false;
        }
        if (this.getY() != other.getY()) {
            return false;
        }
        return this.isClick() == other.isClick();
    }

    protected boolean canEqual(Object other) {
        return other instanceof AutomatedClick;
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
        return "AutomatedClick(x=" + this.getX() + ", y=" + this.getY() + ", click=" + this.isClick() + ")";
    }

    public AutomatedClick(int x, int y, boolean click) {
        this.x = x;
        this.y = y;
        this.click = click;
    }
}

