package net.solace.api.commons;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.Predicate;
import net.solace.api.domain.Identifiable;
import net.solace.api.domain.Nameable;
import net.solace.api.domain.Transformable;

public class Predicates {
    public static <T extends Nameable> Predicate<T> names(String ... names) {
        return t -> {
            if (t.getName() == null) {
                return false;
            }
            for (String name : names) {
                if (!t.getName().equals(name)) continue;
                return true;
            }
            return false;
        };
    }

    public static <T extends Nameable> Predicate<T> allNames(String ... names) {
        return t -> {
            if (t.getName() == null) {
                return false;
            }
            for (String name : names) {
                if (t.getName().equals(name)) continue;
                return false;
            }
            return true;
        };
    }

    public static Predicate<String> texts(String ... texts) {
        return t -> {
            for (String text : texts) {
                if (t == null || !t.equals(text)) continue;
                return true;
            }
            return false;
        };
    }

    public static Predicate<String> allTexts(String ... texts) {
        return t -> {
            for (String text : texts) {
                if (t != null && t.equals(text)) continue;
                return false;
            }
            return true;
        };
    }

    public static Predicate<String> textContains(String subString, boolean caseSensitive) {
        return t -> {
            if (caseSensitive) {
                return t.contains(subString);
            }
            return t.toLowerCase().contains(subString.toLowerCase());
        };
    }

    public static Predicate<String> textContains(String subString) {
        return Predicates.textContains(subString, true);
    }

    public static <T extends Nameable> Predicate<T> names(Collection<String> names) {
        return t -> names.contains(t.getName());
    }

    public static <T extends Nameable> Predicate<T> allNames(Collection<String> names) {
        return t -> {
            if (t.getName() == null) {
                return false;
            }
            for (String name : names) {
                if (t.getName().equals(name)) continue;
                return false;
            }
            return true;
        };
    }

    public static <T extends Nameable> Predicate<T> nameContains(String subString, boolean caseSensitive) {
        return t -> {
            if (t.getName() == null) {
                return false;
            }
            if (caseSensitive) {
                return t.getName().contains(subString);
            }
            return t.getName().toLowerCase().contains(subString.toLowerCase());
        };
    }

    public static <T extends Nameable> Predicate<T> nameContains(String subString) {
        return Predicates.nameContains(subString, true);
    }

    public static <T extends Nameable> Predicate<T> nameContains(Collection<String> subStrings, boolean caseSensitive) {
        return t -> {
            if (t.getName() == null) {
                return false;
            }
            for (String subString : subStrings) {
                if (!(caseSensitive ? t.getName().contains(subString) : t.getName().toLowerCase().contains(subString.toLowerCase()))) continue;
                return true;
            }
            return false;
        };
    }

    public static <T extends Nameable> Predicate<T> nameContains(Collection<String> subStrings) {
        return Predicates.nameContains(subStrings, true);
    }

    public static <T extends Identifiable> Predicate<T> ids(int ... ids) {
        return t -> {
            for (int id : ids) {
                if (t.getId() != id && (!(t instanceof Transformable) || ((Transformable)((Object)t)).getActualId() != id)) continue;
                return true;
            }
            return false;
        };
    }

    public static <T extends Identifiable> Predicate<T> allIds(int ... ids) {
        return t -> {
            for (int id : ids) {
                if (t.getId() == id || !(t instanceof Transformable) || ((Transformable)((Object)t)).getActualId() == id) continue;
                return false;
            }
            return true;
        };
    }

    public static <T extends Identifiable> Predicate<T> ids(Collection<Integer> ids) {
        return t -> t instanceof Transformable ? ids.contains(((Transformable)((Object)t)).getActualId()) || ids.contains(t.getId()) : ids.contains(t.getId());
    }

    public static <T extends Identifiable> Predicate<T> allIds(Collection<Integer> ids) {
        return t -> {
            Iterator iterator = ids.iterator();
            while (iterator.hasNext()) {
                int id = (Integer)iterator.next();
                if (t.getId() == id || !(t instanceof Transformable) || ((Transformable)((Object)t)).getActualId() == id) continue;
                return false;
            }
            return true;
        };
    }
}

