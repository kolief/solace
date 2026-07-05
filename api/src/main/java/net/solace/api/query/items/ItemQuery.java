package net.solace.api.query.items;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import net.solace.api.commons.Predicates;
import net.solace.api.domain.items.IItem;
import net.solace.api.query.Query;
import net.solace.api.query.results.ItemQueryResults;
import org.apache.commons.lang3.ArrayUtils;

public class ItemQuery
extends Query<IItem, ItemQuery, ItemQueryResults> {
    private int[] ids = null;
    private int[] notedIds = null;
    private int[] slots = null;
    private String[] names = null;
    private String[] actions = null;
    private Boolean tradable = null;
    private Boolean stackable = null;
    private Boolean members = null;
    private Boolean noted = null;

    public ItemQuery(Supplier<List<IItem>> supplier) {
        super(supplier);
    }

    public ItemQuery ids(int ... ids) {
        this.ids = ids;
        return this;
    }

    public ItemQuery notedIds(int ... notedIds) {
        this.notedIds = notedIds;
        return this;
    }

    public ItemQuery slots(int ... slots) {
        this.slots = slots;
        return this;
    }

    public ItemQuery names(String ... names) {
        this.names = names;
        return this;
    }

    public ItemQuery actions(String ... actions) {
        this.actions = actions;
        return this;
    }

    public ItemQuery tradable(boolean tradable) {
        this.tradable = tradable;
        return this;
    }

    public ItemQuery stackable(boolean stackable) {
        this.stackable = stackable;
        return this;
    }

    public ItemQuery members(boolean members) {
        this.members = members;
        return this;
    }

    public ItemQuery noted(boolean noted) {
        this.noted = noted;
        return this;
    }

    @Override
    protected ItemQueryResults results(List<IItem> list) {
        return new ItemQueryResults(list);
    }

    @Override
    public boolean test(IItem item) {
        if (this.ids != null && !ArrayUtils.contains((int[])this.ids, (int)item.getId())) {
            return false;
        }
        if (this.notedIds != null && !ArrayUtils.contains((int[])this.notedIds, (int)item.getId())) {
            return false;
        }
        if (this.slots != null && !ArrayUtils.contains((int[])this.slots, (int)item.getSlot())) {
            return false;
        }
        if (this.names != null && !ArrayUtils.contains((Object[])this.names, (Object)item.getName())) {
            return false;
        }
        if (this.actions != null && Arrays.stream(this.actions).noneMatch(Predicates.texts(item.getActions()))) {
            return false;
        }
        if (this.tradable != null && this.tradable.booleanValue() != item.isTradable()) {
            return false;
        }
        if (this.stackable != null && this.stackable.booleanValue() != item.isStackable()) {
            return false;
        }
        if (this.members != null && this.members.booleanValue() != item.isMembers()) {
            return false;
        }
        if (this.noted != null && this.noted.booleanValue() != item.isNoted()) {
            return false;
        }
        return super.test(item);
    }
}

