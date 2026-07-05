package net.solace.loader.plugins.arceuuslibrary.domain;


public final class LibraryEvent {
    private final String action;

    public LibraryEvent(String action) {
        this.action = action;
    }

    public String getAction() {
        return this.action;
    }

    public String toString() {
        return "Action: " + action;
    }
}
