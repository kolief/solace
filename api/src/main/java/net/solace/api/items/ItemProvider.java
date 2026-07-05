package net.solace.api.items;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import net.solace.api.commons.Predicates;
import net.solace.api.domain.items.IItem;

public interface ItemProvider<T extends IItem> {
    public List<T> getAll(Predicate<? super T> var1);

    default public List<T> getAll(int ... ids) {
        return this.getAll(Predicates.ids(ids));
    }

    default public List<T> getAll(String ... names) {
        return this.getAll(Predicates.names(names));
    }

    default public T getFirst(Predicate<? super T> filter) {
        return (T)((IItem)this.getAll(filter).stream().findFirst().orElse(null));
    }

    default public T getFirst(int ... ids) {
        return this.getFirst(Predicates.ids(ids));
    }

    default public T getFirst(String ... names) {
        return this.getFirst(Predicates.names(names));
    }

    default public T getLast(Predicate<? super T> filter) {
        return (T)((IItem)this.getAll(filter).stream().max(Comparator.comparingInt(IItem::getSlot)).orElse(null));
    }

    default public T getLast(int ... ids) {
        return this.getLast(Predicates.ids(ids));
    }

    default public T getLast(String ... names) {
        return this.getLast(Predicates.names(names));
    }

    default public int getCount(boolean stacks, Predicate<? super T> filter) {
        return this.getAll(filter).stream().mapToInt(x -> stacks ? x.getQuantity() : 1).sum();
    }

    default public int getCount(boolean stacks, int ... ids) {
        return this.getAll(ids).stream().mapToInt(x -> stacks ? x.getQuantity() : 1).sum();
    }

    default public int getCount(boolean stacks, String ... names) {
        return this.getAll(names).stream().mapToInt(x -> stacks ? x.getQuantity() : 1).sum();
    }

    default public int getCount(Predicate<? super T> filter) {
        return this.getCount(true, filter);
    }

    default public int getCount(int ... ids) {
        return this.getCount(true, ids);
    }

    default public int getCount(String ... names) {
        return this.getCount(true, names);
    }

    default public boolean contains(Predicate<? super T> filter) {
        return this.getFirst(filter) != null;
    }

    default public boolean contains(int ... ids) {
        return this.getFirst(ids) != null;
    }

    default public boolean contains(String ... names) {
        return this.getFirst(names) != null;
    }

    default public boolean containsAll(int ... ids) {
        for (int id : ids) {
            if (this.contains(id)) continue;
            return false;
        }
        return true;
    }

    default public boolean containsAll(String ... names) {
        for (String name : names) {
            if (this.contains(name)) continue;
            return false;
        }
        return true;
    }

    default public boolean containsAll(Predicate<? super T> filter) {
        return !this.getAll(filter).isEmpty();
    }

    public T get(int var1);
}

