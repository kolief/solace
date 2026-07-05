package net.solace.api.movement.pathfinder.model.sailing;

import net.solace.api.Static;
import net.solace.api.domain.widgets.IWidget;
import net.solace.api.widgets.IWidgets;

public enum SailingTab {
    FACILITIES(61407265, "Facilities", 0),
    STATS(61407266, "Stats", 1),
    CREWMATES(61407267, "Crewmates", 2);

    private final int widgetId;
    private final String action;
    private final int index;

    public void open() {
        IWidgets widgets = Static.getWidgets();
        if (!SailingTab.sidePanelVisible()) {
            IWidget switchWidget = widgets.get(38862894);
            if (!widgets.isVisible(switchWidget)) {
                return;
            }
            switchWidget.interact("View");
        }
        if (this.isOpen()) {
            return;
        }
        IWidget tabWidget = widgets.get(this.widgetId);
        if (!widgets.isVisible(tabWidget)) {
            return;
        }
        tabWidget.interact(this.action);
    }

    public boolean isOpen() {
        return SailingTab.sidePanelVisible() && Static.getVars().getBit(19152) == this.index;
    }

    public static boolean sidePanelVisible() {
        return Static.getVars().getBit(19151) == 1;
    }

    private SailingTab(int widgetId, String action, int index) {
        this.widgetId = widgetId;
        this.action = action;
        this.index = index;
    }
}

