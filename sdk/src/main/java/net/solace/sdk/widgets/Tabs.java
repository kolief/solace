package net.solace.sdk.widgets;

import net.solace.api.Static;
import net.solace.api.widgets.ITabs;
import net.solace.api.widgets.Tab;

public class Tabs {
    private static final ITabs TABS = Static.getTabs();

    public static void open(Tab tab) {
        TABS.open(tab);
    }

    public static boolean isOpen(Tab tab) {
        return TABS.isOpen(tab);
    }
}

