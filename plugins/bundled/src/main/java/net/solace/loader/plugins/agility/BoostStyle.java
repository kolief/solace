package net.solace.loader.plugins.agility;

public enum BoostStyle {
    TARGET_LEVEL("Target Level"),
    BOOST_AMOUNT("Boost Amount");

    private String pretty;

    BoostStyle(String pretty) {
        this.pretty = pretty;
    }

    @Override
    public String toString() {
        return pretty;
    }
}
