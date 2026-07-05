package net.solace.loader.plugins.pickpocket;

public enum BankStyle {
    PER_ITEM("Per Item"),
    DEPOSIT_ALL("Deposit All");

    private final String pretty;

    BankStyle(String pretty) {
        this.pretty = pretty;
    }

    @Override
    public String toString() {
        return pretty;
    }
}
