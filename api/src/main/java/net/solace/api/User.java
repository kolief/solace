package net.solace.api;

public final class User {
    private static final User INSTANCE = new User();

    private User() {
    }

    public static User local() {
        return INSTANCE;
    }
}
