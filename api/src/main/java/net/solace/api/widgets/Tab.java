package net.solace.api.widgets;

import net.runelite.api.widgets.WidgetInfo;

public enum Tab {
    COMBAT(WidgetInfo.FIXED_VIEWPORT_COMBAT_TAB, WidgetInfo.FIXED_VIEWPORT_COMBAT_TAB.getChildId(), 0, 4675),
    SKILLS(WidgetInfo.FIXED_VIEWPORT_STATS_TAB, WidgetInfo.FIXED_VIEWPORT_STATS_TAB.getChildId(), 1, 4676),
    QUESTS(WidgetInfo.FIXED_VIEWPORT_QUESTS_TAB, WidgetInfo.FIXED_VIEWPORT_QUESTS_TAB.getChildId(), 2, 4677),
    INVENTORY(WidgetInfo.FIXED_VIEWPORT_INVENTORY_TAB, WidgetInfo.FIXED_VIEWPORT_INVENTORY_TAB.getChildId(), 3, 4678),
    EQUIPMENT(WidgetInfo.FIXED_VIEWPORT_EQUIPMENT_TAB, WidgetInfo.FIXED_VIEWPORT_EQUIPMENT_TAB.getChildId(), 4, 4679),
    PRAYER(WidgetInfo.FIXED_VIEWPORT_PRAYER_TAB, WidgetInfo.FIXED_VIEWPORT_PRAYER_TAB.getChildId(), 5, 4680),
    MAGIC(WidgetInfo.FIXED_VIEWPORT_MAGIC_TAB, WidgetInfo.FIXED_VIEWPORT_MAGIC_TAB.getChildId(), 6, 4682),
    CLAN_CHAT(WidgetInfo.FIXED_VIEWPORT_FRIENDS_CHAT_TAB, WidgetInfo.FIXED_VIEWPORT_FRIENDS_CHAT_TAB.getChildId(), 7, 4683),
    ACCOUNT(WidgetInfo.FIXED_VIEWPORT_IGNORES_TAB, WidgetInfo.FIXED_VIEWPORT_IGNORES_TAB.getChildId(), 8, 6517),
    FRIENDS(WidgetInfo.FIXED_VIEWPORT_FRIENDS_TAB, WidgetInfo.FIXED_VIEWPORT_FRIENDS_TAB.getChildId(), 9, 4684),
    LOG_OUT(WidgetInfo.FIXED_VIEWPORT_LOGOUT_TAB, WidgetInfo.FIXED_VIEWPORT_LOGOUT_TAB.getChildId(), 10, 4689),
    OPTIONS(WidgetInfo.FIXED_VIEWPORT_OPTIONS_TAB, WidgetInfo.FIXED_VIEWPORT_OPTIONS_TAB.getChildId(), 11, 4686),
    EMOTES(WidgetInfo.FIXED_VIEWPORT_EMOTES_TAB, WidgetInfo.FIXED_VIEWPORT_EMOTES_TAB.getChildId(), 12, 4687),
    MUSIC(WidgetInfo.FIXED_VIEWPORT_MUSIC_TAB, WidgetInfo.FIXED_VIEWPORT_MUSIC_TAB.getChildId(), 13, 4688);

    private final WidgetInfo widgetInfo;
    private final int childId;
    private final int index;
    private final int keyVarbitId;

    public static int keyVarbitToKeyCode(int varbitValue) {
        if (varbitValue >= 1 && varbitValue <= 12) {
            return 112 + (varbitValue - 1);
        }
        if (varbitValue == 13) {
            return 27;
        }
        return -1;
    }

    private Tab(WidgetInfo widgetInfo, int childId, int index, int keyVarbitId) {
        this.widgetInfo = widgetInfo;
        this.childId = childId;
        this.index = index;
        this.keyVarbitId = keyVarbitId;
    }

    public WidgetInfo getWidgetInfo() {
        return this.widgetInfo;
    }

    public int getChildId() {
        return this.childId;
    }

    public int getIndex() {
        return this.index;
    }

    public int getKeyVarbitId() {
        return this.keyVarbitId;
    }
}

