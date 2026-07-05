package net.solace.api.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public class CollectionUtils {
    public static <T> T getFirst(Collection<T> collection) {
        return collection.stream().findFirst().orElse(null);
    }

    public static <T> T getLast(Collection<T> collection) {
        return collection.stream().reduce((first, second) -> second).orElse(null);
    }

    public static <T> T getFirst(Collection<T> collection, Predicate<T> filter) {
        return collection.stream().filter(filter).findFirst().orElse(null);
    }

    public static <T> T getLast(Collection<T> collection, Predicate<T> filter) {
        return collection.stream().filter(filter).reduce((first, second) -> second).orElse(null);
    }

    public static <T> T getFirst(T[] array) {
        return array.length > 0 ? (T)array[0] : null;
    }

    public static <T> T getLast(T[] array) {
        return array.length > 0 ? (T)array[array.length - 1] : null;
    }

    public static <T> T getFirst(T[] array, Predicate<T> filter) {
        for (T t : array) {
            if (!filter.test(t)) continue;
            return t;
        }
        return null;
    }

    public static <T> T getLast(T[] array, Predicate<T> filter) {
        for (int i = array.length - 1; i >= 0; --i) {
            if (!filter.test(array[i])) continue;
            return array[i];
        }
        return null;
    }

    public static <T> T getFirst(Iterable<T> iterable) {
        return iterable.iterator().next();
    }

    public static <T> T getLast(Iterable<T> iterable) {
        T last = null;
        for (T t : iterable) {
            last = t;
        }
        return last;
    }

    public static <T> T getFirst(Iterable<T> iterable, Predicate<T> filter) {
        for (T t : iterable) {
            if (!filter.test(t)) continue;
            return t;
        }
        return null;
    }

    public static <T> T getLast(Iterable<T> iterable, Predicate<T> filter) {
        T last = null;
        for (T t : iterable) {
            if (!filter.test(t)) continue;
            last = t;
        }
        return last;
    }

    public static <T> List<T> reversedList(List<T> collection) {
        ArrayList<T> reversed = new ArrayList<T>(collection);
        Collections.reverse(reversed);
        return reversed;
    }

    public static <T> List<T> reversedList(T[] array) {
        ArrayList<T> reversed = new ArrayList<T>(array.length);
        for (int i = array.length - 1; i >= 0; --i) {
            reversed.add(array[i]);
        }
        return reversed;
    }

    public static <T> List<T> reversedList(Iterable<T> iterable) {
        ArrayList<T> reversed = new ArrayList<T>();
        for (T t : iterable) {
            reversed.add(0, t);
        }
        return reversed;
    }

    public static <T> List<T> joinToList(List<T> first, List<T> second) {
        ArrayList<T> joined = new ArrayList<T>(first.size() + second.size());
        joined.addAll(first);
        joined.addAll(second);
        return joined;
    }

    public static <T> List<T> joinToList(T[] first, T[] second) {
        ArrayList joined = new ArrayList(first.length + second.length);
        Collections.addAll(joined, first);
        Collections.addAll(joined, second);
        return joined;
    }

    public static <T> boolean arrayContains(T[] array, T value) {
        for (T t : array) {
            if (!t.equals(value)) continue;
            return true;
        }
        return false;
    }

    public static <T> boolean arrayContains(T[] array, Predicate<T> filter) {
        for (T t : array) {
            if (!filter.test(t)) continue;
            return true;
        }
        return false;
    }
}

