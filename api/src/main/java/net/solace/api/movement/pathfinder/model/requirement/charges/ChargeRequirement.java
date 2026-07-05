package net.solace.api.movement.pathfinder.model.requirement.charges;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import net.solace.api.movement.pathfinder.model.requirement.charges.ItemChargeRequirement;

public class ChargeRequirement
extends ItemChargeRequirement {
    private final int maxCharges;
    private final boolean dailyReset;
    private final int graphicId;
    private final int animationId;

    private ChargeRequirement(Builder builder) {
        super(builder.id, builder.itemIds, builder.chatTriggers);
        this.maxCharges = builder.maxCharges;
        this.dailyReset = builder.dailyReset;
        this.graphicId = builder.graphicId;
        this.animationId = builder.animationId;
    }

    public boolean hasGraphicTrigger() {
        return this.graphicId > 0;
    }

    public boolean hasAnimationTrigger() {
        return this.animationId > 0;
    }

    public static Builder builder(String id) {
        return new Builder(id);
    }

    public int getMaxCharges() {
        return this.maxCharges;
    }

    public boolean isDailyReset() {
        return this.dailyReset;
    }

    public int getGraphicId() {
        return this.graphicId;
    }

    public int getAnimationId() {
        return this.animationId;
    }

    public static class Builder {
        private final String id;
        private int[] itemIds = new int[0];
        private List<Pattern> chatTriggers = new ArrayList<Pattern>();
        private int maxCharges = -1;
        private boolean dailyReset = false;
        private int graphicId = -1;
        private int animationId = -1;

        public Builder(String id) {
            this.id = id;
        }

        public Builder itemIds(int ... itemIds) {
            this.itemIds = itemIds;
            return this;
        }

        public Builder chatTriggers(List<Pattern> chatTriggers) {
            this.chatTriggers = chatTriggers;
            return this;
        }

        public Builder chatTrigger(String regex) {
            this.chatTriggers.add(Pattern.compile(regex));
            return this;
        }

        public Builder maxCharges(int maxCharges) {
            this.maxCharges = maxCharges;
            return this;
        }

        public Builder dailyReset(boolean dailyReset) {
            this.dailyReset = dailyReset;
            return this;
        }

        public Builder graphicId(int graphicId) {
            this.graphicId = graphicId;
            return this;
        }

        public Builder animationId(int animationId) {
            this.animationId = animationId;
            return this;
        }

        public ChargeRequirement build() {
            return new ChargeRequirement(this);
        }
    }
}

