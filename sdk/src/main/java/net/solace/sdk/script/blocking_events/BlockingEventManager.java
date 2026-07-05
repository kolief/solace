package net.solace.sdk.script.blocking_events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import net.solace.sdk.script.blocking_events.BlockingEvent;
import net.solace.sdk.script.blocking_events.DeathEvent;
import net.solace.sdk.script.blocking_events.LoginEvent;
import net.solace.sdk.script.blocking_events.WelcomeScreenEvent;

public class BlockingEventManager {
    private final Map<LoginEvent.Response, Function<LoginEvent.Response, Integer>> loginMessageHandlers = new HashMap<LoginEvent.Response, Function<LoginEvent.Response, Integer>>();
    private final List<BlockingEvent> blockingEvents = new ArrayList<BlockingEvent>();

    public BlockingEventManager() {
        this.blockingEvents.add(new LoginEvent(this));
        this.blockingEvents.add(new WelcomeScreenEvent());
        this.blockingEvents.add(new DeathEvent());
    }

    public void addLoginResponseHandler(LoginEvent.Response response, Function<LoginEvent.Response, Integer> handler) {
        this.loginMessageHandlers.put(response, handler);
    }

    public void removeLoginResponseHandler(LoginEvent.Response response) {
        this.loginMessageHandlers.remove((Object)response);
    }

    public LoginEvent getLoginEvent() {
        return this.blockingEvents.stream()
                .filter(LoginEvent.class::isInstance)
                .map(LoginEvent.class::cast)
                .findFirst()
                .orElse(null);
    }

    public boolean remove(Class<? extends BlockingEvent> clazz) {
        return this.blockingEvents.removeIf(e -> e.getClass().equals(clazz));
    }

    public boolean add(BlockingEvent event) {
        return this.blockingEvents.add(event);
    }

    public Map<LoginEvent.Response, Function<LoginEvent.Response, Integer>> getLoginMessageHandlers() {
        return this.loginMessageHandlers;
    }

    public List<BlockingEvent> getBlockingEvents() {
        return this.blockingEvents;
    }
}

