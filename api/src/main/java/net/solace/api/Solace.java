package net.solace.api;

public class Solace {
    private static final User user = User.local();

    public static boolean isUser() {
        return true;
    }

    public static User getUser() {
        return user;
    }
}
