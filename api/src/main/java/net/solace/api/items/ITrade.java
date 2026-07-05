package net.solace.api.items;

import java.util.List;
import java.util.function.Predicate;
import net.solace.api.commons.Predicates;
import net.solace.api.domain.items.IItem;

public interface ITrade {
    default public boolean isOpen() {
        return this.isFirstScreenOpen() || this.isSecondScreenOpen();
    }

    public boolean isSecondScreenOpen();

    public boolean isFirstScreenOpen();

    default public void accept() {
        this.acceptFirstScreen();
        this.acceptSecondScreen();
    }

    public void acceptFirstScreen();

    public void acceptSecondScreen();

    default public void decline() {
        this.declineFirstScreen();
        this.declineSecondScreen();
    }

    public void declineFirstScreen();

    public void declineSecondScreen();

    default public boolean hasAccepted(boolean them) {
        return this.hasAcceptedFirstScreen(them) || this.hasAcceptedSecondScreen(them);
    }

    public boolean hasAcceptedFirstScreen(boolean var1);

    public boolean hasAcceptedSecondScreen(boolean var1);

    public void offer(Predicate<IItem> var1, int var2, boolean var3);

    default public void offer(Predicate<IItem> filter, int quantity) {
        this.offer(filter, quantity, false);
    }

    default public void offer(int id, int quantity) {
        this.offer((IItem x) -> x.getId() == id, quantity);
    }

    default public void offer(int id, int quantity, boolean quick) {
        this.offer((IItem x) -> x.getId() == id, quantity, quick);
    }

    default public void offer(String name, int quantity) {
        this.offer((IItem x) -> x.getName() != null && x.getName().equals(name), quantity);
    }

    default public void offer(String name, int quantity, boolean quick) {
        this.offer((IItem x) -> x.getName() != null && x.getName().equals(name), quantity, quick);
    }

    public List<IItem> getAll(boolean var1, Predicate<? super IItem> var2);

    public List<IItem> getInventory(Predicate<IItem> var1);

    default public List<IItem> getAll(boolean theirs) {
        return this.getAll(theirs, x -> true);
    }

    default public List<IItem> getAll(boolean theirs, int ... ids) {
        return this.getAll(theirs, Predicates.ids(ids));
    }

    default public List<IItem> getAll(boolean theirs, String ... names) {
        return this.getAll(theirs, Predicates.names(names));
    }

    default public IItem getFirst(boolean theirs, Predicate<IItem> filter) {
        return this.getAll(theirs, filter).stream().findFirst().orElse(null);
    }

    default public IItem getFirst(boolean theirs, int ... ids) {
        return this.getFirst(theirs, Predicates.ids(ids));
    }

    default public IItem getFirst(boolean theirs, String ... names) {
        return this.getFirst(theirs, Predicates.names(names));
    }

    default public boolean contains(boolean theirs, Predicate<IItem> filter) {
        return this.getFirst(theirs, filter) != null;
    }

    default public boolean contains(boolean theirs, int ... ids) {
        return this.contains(theirs, Predicates.ids(ids));
    }

    default public boolean contains(boolean theirs, String ... names) {
        return this.contains(theirs, Predicates.names(names));
    }

    public String getTradingPlayer();
}

