package net.solace.api.query.results;

import java.util.List;
import net.solace.api.domain.widgets.IWidget;
import net.solace.api.query.results.QueryResults;

public class WidgetQueryResults
extends QueryResults<IWidget, WidgetQueryResults> {
    public WidgetQueryResults(List<IWidget> results) {
        super(results);
    }
}

