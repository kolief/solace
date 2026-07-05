package net.solace.sdk.widgets;

import java.util.List;
import net.runelite.api.Prayer;
import net.solace.api.Static;
import net.solace.api.interact.InteractMethod;
import net.solace.api.widgets.IPrayers;

public class Prayers {
    private static final IPrayers PRAYERS = Static.getPrayers();

    public static boolean isEnabled(Prayer prayer) {
        return PRAYERS.isEnabled(prayer);
    }

    public static void toggle(InteractMethod interactMethod, Prayer prayer) {
        PRAYERS.toggle(interactMethod, prayer);
    }

    public static void toggle(Prayer prayer) {
        PRAYERS.toggle(prayer);
    }

    public static int getPoints() {
        return PRAYERS.getPoints();
    }

    public static int getMissingPoints() {
        return PRAYERS.getMissingPoints();
    }

    public static void toggleQuickPrayer(InteractMethod interactMethod, boolean enabled) {
        PRAYERS.toggleQuickPrayer(interactMethod, enabled);
    }

    public static void toggleQuickPrayer(boolean enabled) {
        PRAYERS.toggleQuickPrayer(enabled);
    }

    public static void toggleQuickPrayer() {
        PRAYERS.toggleQuickPrayer();
    }

    public static void toggleQuickPrayer(InteractMethod interactMethod) {
        PRAYERS.toggleQuickPrayer(interactMethod);
    }

    public static boolean isQuickPrayerEnabled() {
        return PRAYERS.isQuickPrayerEnabled();
    }

    public static boolean anyActive() {
        return PRAYERS.anyActive();
    }

    public static void disableAll(InteractMethod interactMethod) {
        PRAYERS.disableAll(interactMethod);
    }

    public static void disableAll() {
        PRAYERS.disableAll();
    }

    public static boolean canUse(Prayer prayer) {
        return PRAYERS.canUse(prayer);
    }

    public static Prayer getBestRangeOffensive() {
        return PRAYERS.getBestRangeOffensive();
    }

    public static Prayer getBestMageOffensive() {
        return PRAYERS.getBestMageOffensive();
    }

    public static Prayer getBestMeleeOffensive() {
        return PRAYERS.getBestMeleeOffensive();
    }

    public static boolean isQuickPrayerOpen() {
        return PRAYERS.isQuickPrayerOpen();
    }

    public static boolean openQuickPrayer() {
        return PRAYERS.openQuickPrayer();
    }

    public static List<Prayer> getSelectedQuickPrayers() {
        return PRAYERS.getSelectedQuickPrayers();
    }

    public static List<Prayer> getActiveQuickPrayers() {
        return PRAYERS.getActiveQuickPrayers();
    }

    public static boolean setQuickPrayers(List<Prayer> prayers, boolean closeInterface) {
        return PRAYERS.setQuickPrayers(prayers, closeInterface);
    }

    public static boolean setQuickPrayers(List<Prayer> prayers) {
        return PRAYERS.setQuickPrayers(prayers);
    }

    public static boolean isQuickPrayerSelected(Prayer prayer) {
        return PRAYERS.isQuickPrayerSelected(prayer);
    }

    public static boolean isQuickPrayerSelected(List<Prayer> prayers) {
        return PRAYERS.isQuickPrayerSelected(prayers);
    }

    public static boolean isQuickPrayerActive(Prayer prayer) {
        return PRAYERS.isQuickPrayerActive(prayer);
    }

    public static boolean isQuickPrayerActive(List<Prayer> prayers) {
        return PRAYERS.isQuickPrayerActive(prayers);
    }
}

