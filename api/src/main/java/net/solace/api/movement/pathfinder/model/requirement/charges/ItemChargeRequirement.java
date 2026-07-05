package net.solace.api.movement.pathfinder.model.requirement.charges;

import java.util.List;
import java.util.regex.Pattern;

public abstract class ItemChargeRequirement {
    protected final String id;
    protected final int[] itemIds;
    protected final List<Pattern> chatTriggers;

    protected ItemChargeRequirement(String id, int[] itemIds, List<Pattern> chatTriggers) {
        this.id = id;
        this.itemIds = itemIds;
        this.chatTriggers = chatTriggers;
    }

    public String getId() {
        return this.id;
    }

    public int[] getItemIds() {
        return this.itemIds;
    }

    public List<Pattern> getChatTriggers() {
        return this.chatTriggers;
    }
}

