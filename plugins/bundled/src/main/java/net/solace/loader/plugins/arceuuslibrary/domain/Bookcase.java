package net.solace.loader.plugins.arceuuslibrary.domain;


import lombok.Getter;
import net.runelite.api.coords.WorldPoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Bookcase {
    @Getter
    private final WorldPoint WorldPoint;
    @Getter
    private final List<Integer> index;
    @Getter
    private final Set<Book> possibleBooks = new HashSet<>();
    private boolean isBookSet;
    @Getter
    private Book book;

    Bookcase(WorldPoint WorldPoint) {
        this.WorldPoint = WorldPoint;
        this.index = new ArrayList<>();
    }

    public boolean isBookSet() {
        return isBookSet;
    }

    void setBook(Book book) {
        this.book = book;
        this.isBookSet = true;
    }

    void clearBook() {
        book = null;
        isBookSet = false;
    }

    @Override
    public String toString() {
        return WorldPoint + ", index: " + Arrays.toString(index.toArray()) + ", isBookSet: " + isBookSet + ", book: " + book + ", possibleBooks: " + Arrays.toString(possibleBooks.toArray());
    }
}
