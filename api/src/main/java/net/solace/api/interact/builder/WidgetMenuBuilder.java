package net.solace.api.interact.builder;

import net.solace.api.interact.builder.MenuBuilder;

public interface WidgetMenuBuilder
extends MenuBuilder<WidgetMenuBuilder> {
    public WidgetMenuBuilder childId(Integer var1);

    public WidgetMenuBuilder resume(boolean var1);
}

