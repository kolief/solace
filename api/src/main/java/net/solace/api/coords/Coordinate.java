package net.solace.api.coords;

public final class Coordinate {
    private final int x;
    private final int y;

    public Coordinate dx(int dx) {
        return new Coordinate(this.x + dx, this.y);
    }

    public Coordinate dy(int dy) {
        return new Coordinate(this.x, this.y + dy);
    }

    public String toString() {
        return "X: " + this.x + ", Y: " + this.y;
    }

    public Coordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Coordinate)) {
            return false;
        }
        Coordinate other = (Coordinate)o;
        if (this.getX() != other.getX()) {
            return false;
        }
        return this.getY() == other.getY();
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getX();
        result = result * 59 + this.getY();
        return result;
    }
}

