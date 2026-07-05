package net.solace.api.plugins;

public interface Task {
    public boolean validate();

    public int execute();

    default public boolean isBlocking() {
        return true;
    }

    default public boolean subscribe() {
        return false;
    }

    default public boolean inject() {
        return false;
    }
}

