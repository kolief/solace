package net.solace.api.query.results;

import java.util.List;
import net.solace.api.domain.items.IItem;
import net.solace.api.query.results.QueryResults;

public class ItemQueryResults
extends QueryResults<IItem, ItemQueryResults> {
    public ItemQueryResults(List<IItem> results) {
        super(results);
    }
}

