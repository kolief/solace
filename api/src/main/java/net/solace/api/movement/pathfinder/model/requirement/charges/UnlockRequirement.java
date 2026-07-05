package net.solace.api.movement.pathfinder.model.requirement.charges;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.regex.Pattern;
import net.solace.api.movement.pathfinder.model.requirement.charges.ItemChargeRequirement;

public class UnlockRequirement
extends ItemChargeRequirement {
    private final int widgetGroupId;
    private final int widgetChildId;
    private final String widgetTextMatch;
    private final int menuActionId;
    private final int objectMenuId;
    private final List<Pattern> unlockTriggers;
    private final List<Pattern> lockTriggers;
    private final List<BooleanSupplier> unlockBooleanSuppliers = new ArrayList<BooleanSupplier>();
    private final List<BooleanSupplier> lockBooleanSuppliers = new ArrayList<BooleanSupplier>();

    private UnlockRequirement(Builder builder) {
        super(builder.id, builder.itemIds, builder.chatTriggers);
        this.widgetGroupId = builder.widgetGroupId;
        this.widgetChildId = builder.widgetChildId;
        this.widgetTextMatch = builder.widgetTextMatch;
        this.menuActionId = builder.menuActionId;
        this.objectMenuId = builder.objectMenuId;
        this.unlockTriggers = builder.unlockTriggers;
        this.lockTriggers = builder.lockTriggers;
        this.unlockBooleanSuppliers.addAll(builder.unlockBooleanSuppliers);
        this.lockBooleanSuppliers.addAll(builder.lockBooleanSuppliers);
    }

    public boolean hasWidgetCheck() {
        return this.widgetGroupId > 0;
    }

    public boolean hasMenuActionId() {
        return this.menuActionId > 0;
    }

    public boolean hasObjectMenuId() {
        return this.objectMenuId > 0;
    }

    public boolean hasUnlockTriggers() {
        return !this.unlockTriggers.isEmpty();
    }

    public boolean hasLockTriggers() {
        return !this.lockTriggers.isEmpty();
    }

    public boolean hasUnlockBooleanSuppliers() {
        return !this.unlockBooleanSuppliers.isEmpty();
    }

    public boolean hasLockBooleanSuppliers() {
        return !this.lockBooleanSuppliers.isEmpty();
    }

    public static Builder builder(String id) {
        return new Builder(id);
    }

    public int getWidgetGroupId() {
        return this.widgetGroupId;
    }

    public int getWidgetChildId() {
        return this.widgetChildId;
    }

    public String getWidgetTextMatch() {
        return this.widgetTextMatch;
    }

    public int getMenuActionId() {
        return this.menuActionId;
    }

    public int getObjectMenuId() {
        return this.objectMenuId;
    }

    public List<Pattern> getUnlockTriggers() {
        return this.unlockTriggers;
    }

    public List<Pattern> getLockTriggers() {
        return this.lockTriggers;
    }

    public List<BooleanSupplier> getUnlockBooleanSuppliers() {
        return this.unlockBooleanSuppliers;
    }

    public List<BooleanSupplier> getLockBooleanSuppliers() {
        return this.lockBooleanSuppliers;
    }

    public static class Builder {
        private final String id;
        private int[] itemIds = new int[0];
        private List<Pattern> chatTriggers = new ArrayList<Pattern>();
        private int widgetGroupId = -1;
        private int widgetChildId = -1;
        private String widgetTextMatch = null;
        private int menuActionId = -1;
        private int objectMenuId = -1;
        private List<Pattern> unlockTriggers = new ArrayList<Pattern>();
        private List<Pattern> lockTriggers = new ArrayList<Pattern>();
        private List<BooleanSupplier> unlockBooleanSuppliers = new ArrayList<BooleanSupplier>();
        private List<BooleanSupplier> lockBooleanSuppliers = new ArrayList<BooleanSupplier>();

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

        public Builder widget(int groupId, int childId, String textMatch) {
            this.widgetGroupId = groupId;
            this.widgetChildId = childId;
            this.widgetTextMatch = textMatch;
            return this;
        }

        public Builder menuActionId(int menuActionId) {
            this.menuActionId = menuActionId;
            return this;
        }

        public Builder objectMenuId(int objectMenuId) {
            this.objectMenuId = objectMenuId;
            return this;
        }

        public Builder unlockTrigger(String regex) {
            this.unlockTriggers.add(Pattern.compile(regex));
            return this;
        }

        public Builder lockTrigger(String regex) {
            this.lockTriggers.add(Pattern.compile(regex));
            return this;
        }

        public Builder unlockBooleanSupplier(BooleanSupplier supplier) {
            this.unlockBooleanSuppliers.add(supplier);
            return this;
        }

        public Builder lockBooleanSupplier(BooleanSupplier supplier) {
            this.lockBooleanSuppliers.add(supplier);
            return this;
        }

        public UnlockRequirement build() {
            return new UnlockRequirement(this);
        }
    }
}

