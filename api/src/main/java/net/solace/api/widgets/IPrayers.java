package net.solace.api.widgets;

import java.util.List;
import net.runelite.api.Prayer;
import net.solace.api.interact.InteractMethod;

public interface IPrayers {
    public int getMissingPoints();

    public boolean isEnabled(Prayer var1);

    public void toggle(InteractMethod var1, Prayer var2);

    default public void toggle(Prayer prayer) {
        this.toggle(null, prayer);
    }

    public int getPoints();

    public void toggleQuickPrayer(InteractMethod var1, boolean var2);

    public void toggleQuickPrayer(InteractMethod var1);

    default public void toggleQuickPrayer(boolean enabled) {
        this.toggleQuickPrayer(null, enabled);
    }

    default public void toggleQuickPrayer() {
        this.toggleQuickPrayer(null);
    }

    public boolean isQuickPrayerEnabled();

    public boolean anyActive();

    public void disableAll(InteractMethod var1);

    default public void disableAll() {
        this.disableAll(null);
    }

    public boolean canUse(Prayer var1);

    public Prayer getBestRangeOffensive();

    public Prayer getBestMageOffensive();

    public Prayer getBestMeleeOffensive();

    public boolean isQuickPrayerOpen();

    public boolean openQuickPrayer();

    public List<Prayer> getSelectedQuickPrayers();

    public List<Prayer> getActiveQuickPrayers();

    public boolean setQuickPrayers(List<Prayer> var1, boolean var2);

    default public boolean setQuickPrayers(List<Prayer> prayers) {
        return this.setQuickPrayers(prayers, true);
    }

    default public boolean isQuickPrayerSelected(Prayer prayer) {
        return this.getSelectedQuickPrayers().contains(prayer);
    }

    default public boolean isQuickPrayerSelected(List<Prayer> prayers) {
        return prayers.stream().allMatch(this::isQuickPrayerSelected);
    }

    default public boolean isQuickPrayerActive(Prayer prayer) {
        return this.getActiveQuickPrayers().contains(prayer);
    }

    default public boolean isQuickPrayerActive(List<Prayer> prayers) {
        return prayers.stream().allMatch(this::isQuickPrayerActive);
    }
}

