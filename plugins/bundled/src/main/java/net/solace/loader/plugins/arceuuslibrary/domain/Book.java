package net.solace.loader.plugins.arceuuslibrary.domain;

import lombok.Getter;
import net.solace.api.domain.items.IItem;
import net.solace.sdk.items.Inventory;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public enum Book {
    RADAS_CENSUS(13524, "Rada's census", "Census of King Rada III, by Matthias Vorseth."),
    RICKTORS_DIARY_7(13525, "Ricktor's diary (7)", "Diary of Steklan Ricktor, volume 7."),
    EATHRAM_RADA_EXTRACT(13526, "Eathram & rada extract", "An extract from Eathram & Rada, by Anonymous."),
    KILLING_OF_A_KING(13527, "Killing of a king", "Killing of a King, by Griselle."),
    HOSIDIUS_LETTER(13528, "Hosidius letter", "A letter from Lord Hosidius to the Council of Elders."),
    WINTERTODT_PARABLE(13529, "Wintertodt parable", "The Parable of the Wintertodt, by Anonymous."),
    TWILL_ACCORD(13530, "Twill accord", "The Royal Accord of Twill."),
    BYRNES_CORONATION_SPEECH(13531, "Byrne's coronation speech", "Speech of King Byrne I, on the occasion of his coronation."),
    IDEOLOGY_OF_DARKNESS(13532, "Ideology of darkness", "The Ideology of Darkness, by Philophaire."),
    RADAS_JOURNEY(13533, "Rada's journey", "The Journey of Rada, by Griselle."),
    TRANSVERGENCE_THEORY(13534, "Transvergence theory", "The Theory of Transvergence, by Amon Ducot."),
    TRISTESSAS_TRAGEDY(13535, "Tristessa's tragedy", "The Tragedy of Tristessa."),
    TREACHERY_OF_ROYALTY(13536, "Treachery of royalty", "The Treachery of Royalty, by Professor Answith."),
    TRANSPORTATION_INCANTATIONS(13537, "Transportation incantations", "Transportation Incantations, by Amon Ducot."),
    SOUL_JOURNEY(19637, "Soul journey", "The Journey of Souls, by Aretha."),
    VARLAMORE_ENVOY(21756, "Varlamore envoy", "The Envoy to Varlamore, by Deryk Paulson.");

    private static final Map<Integer, Book> BY_ID = buildById();
    private static final Map<String, Book> BY_NAME = buildByName();
    private final int item;
    private final String name;
    private final String shortName;

    Book(int id, String shortName, String name) {
        this.item = id;
        this.shortName = shortName;
        this.name = name;
    }

    private static Map<Integer, Book> buildById() {
        HashMap<Integer, Book> byId = new HashMap<>();
        for (Book b : Book.values()) {
            byId.put(b.item, b);
        }

        return Collections.unmodifiableMap(byId);
    }

    private static Map<String, Book> buildByName() {
        HashMap<String, Book> byName = new HashMap<>();
        for (Book b : Book.values()) {
            byName.put(b.name, b);
        }

        return Collections.unmodifiableMap(byName);
    }

    public static Book byId(int id) {
        return BY_ID.get(id);
    }

    public static Book byName(String name) {
        return BY_NAME.get(name);
    }

    public static List<Book> getBooksInInventory() {
        return Inventory.getAll().stream()
                .map(IItem::getId)
                .map(Book::byId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public static List<Book> getBooksNotInInventory() {
        var invItems = Inventory.getAll().stream()
                .map(IItem::getName)
                .collect(Collectors.toList());
        return Stream.of(Book.values())
                .filter(b -> b != VARLAMORE_ENVOY)
                .filter(b -> !invItems.contains(b.shortName))
                .collect(Collectors.toList());
    }

    public boolean isInInventory() {
        return Inventory.contains(shortName);
    }

    @Override
    public String toString() {
        return "id: " + item + ", name: " + shortName;
    }
}