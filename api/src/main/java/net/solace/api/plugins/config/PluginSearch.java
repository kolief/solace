package net.solace.api.plugins.config;

import com.google.common.base.Splitter;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import net.runelite.client.util.Text;
import net.solace.api.plugins.config.SearchablePlugin;
import org.apache.commons.lang3.StringUtils;

public class PluginSearch {
    private static final Splitter SPLITTER = Splitter.on((String)" ").trimResults().omitEmptyStrings();

    public static <T extends SearchablePlugin> List<T> search(Collection<T> searchablePlugins, String query) {
        return searchablePlugins.stream().filter(plugin -> Text.matchesSearchTerms((Iterable)SPLITTER.split((CharSequence)query.toLowerCase()), plugin.getKeywords())).sorted(PluginSearch.comparator(query)).collect(Collectors.toList());
    }

    private static Comparator<SearchablePlugin> comparator(String query) {
        if (StringUtils.isBlank((CharSequence)query)) {
            return Comparator.comparing(SearchablePlugin::isPinned, Comparator.reverseOrder()).thenComparing(SearchablePlugin::getSearchableName);
        }
        Iterable queryPieces = SPLITTER.split((CharSequence)query.toLowerCase());
        return Comparator.comparing(SearchablePlugin::isPinned).thenComparing(sp -> query.equalsIgnoreCase(sp.getSearchableName())).thenComparing(sp -> PluginSearch.stream(queryPieces).anyMatch(queryPiece -> PluginSearch.stream(SPLITTER.split((CharSequence)sp.getSearchableName().toLowerCase())).anyMatch(namePiece -> namePiece.startsWith((String)queryPiece)))).thenComparing(sp -> PluginSearch.stream(queryPieces).allMatch(queryPiece -> PluginSearch.stream(SPLITTER.split((CharSequence)sp.getSearchableName().toLowerCase())).anyMatch(namePiece -> namePiece.contains((CharSequence)queryPiece)))).thenComparingInt(SearchablePlugin::installs).reversed().thenComparing(SearchablePlugin::getSearchableName);
    }

    private static Stream<String> stream(Iterable<String> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false);
    }
}

