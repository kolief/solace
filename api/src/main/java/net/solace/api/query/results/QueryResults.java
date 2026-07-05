package net.solace.api.query.results;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class QueryResults<T, R>
implements Collection<T> {
    protected final List<T> results;

    public QueryResults(List<T> results) {
        this.results = results;
    }

    public T get(int index) {
        return this.results.get(index);
    }

    @Override
    public boolean isEmpty() {
        return this.size() == 0;
    }

    public boolean isNotEmpty() {
        return !this.isEmpty();
    }

    public final R sorted(Comparator<? super T> comparator) {
        this.results.sort(comparator);
        return (R)this;
    }

    public int lastIndexOf(T o) {
        return this.results.lastIndexOf(o);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        return this.results.addAll(c);
    }

    public final R limit(int startIndex, int amount) {
        ArrayList<T> limit = new ArrayList<T>(amount);
        for (int i = startIndex; i < this.size() && i - startIndex < amount; ++i) {
            limit.add(this.get(i));
        }
        this.results.retainAll(limit);
        return (R)this;
    }

    public List<T> list() {
        return this.results;
    }

    @Override
    public void clear() {
        this.results.clear();
    }

    @Override
    public int size() {
        return this.results.size();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return this.results.removeAll(c);
    }

    @Override
    public boolean remove(Object o) {
        return this.results.remove(o);
    }

    @Override
    public boolean add(T t) {
        return this.results.add(t);
    }

    public final R reversed() {
        Collections.reverse(this.results);
        return (R)this;
    }

    public T first() {
        return this.size() == 0 ? null : (T)this.get(0);
    }

    public int indexOf(T o) {
        return this.results.indexOf(o);
    }

    public final boolean accept(Consumer<T> consumer, Function<R, T> target) {
        T value = target.apply((R) this);
        if (value != null) {
            consumer.accept(value);
            return true;
        }
        return false;
    }

    public final T random() {
        int index = this.size() - 1;
        return index != -1 ? (T)this.get(ThreadLocalRandom.current().nextInt(0, this.size())) : null;
    }

    @Override
    public boolean contains(Object o) {
        return this.results.contains(o);
    }

    public final R shuffled() {
        Collections.shuffle(this.results);
        return (R)this;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return new HashSet<T>(this.results).containsAll(c);
    }

    @Override
    public Iterator<T> iterator() {
        return this.results.iterator();
    }

    public final R limit(int entries) {
        return this.limit(0, entries);
    }

    public final T last() {
        int index = this.size() - 1;
        return index != -1 ? (T)this.get(index) : null;
    }

    @Override
    public Object[] toArray() {
        return this.results.toArray();
    }

    @Override
    @SuppressWarnings("unchecked")
    public T[] toArray(Object[] o) {
        return (T[]) this.results.toArray(o);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return this.results.retainAll(c);
    }
}

