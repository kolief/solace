package net.solace.api.movement.pathfinder;

import net.solace.api.Static;
import net.solace.api.domain.widgets.IWidget;
import net.solace.api.movement.pathfinder.model.requirement.charges.UnlockRequirement;

public class UnlockRequirements {
    public static final UnlockRequirement DIGSITE_PENDANT_FOSSIL_ISLAND = UnlockRequirement.builder("digsite_pendant_fossil_island").itemIds(11190, 11191, 11192, 11193, 11194).menuActionId(131078).objectMenuId(30943).unlockTrigger("The necklace glows brightly\\. It seems to bind its magic to this place\\.").lockTrigger("You have not yet unlocked that destination\\.").lockTrigger("The necklace glows brightly\\. It seems to lose its connection to this place\\.").build();
    public static final UnlockRequirement DIGSITE_PENDANT_LITHKREN = UnlockRequirement.builder("digsite_pendant_lithkren").itemIds(11190, 11191, 11192, 11193, 11194).menuActionId(196614).unlockTrigger("The necklace glows brightly\\. It seems to bind its magic to this place\\.").lockTrigger("You have not yet unlocked that destination\\.").lockTrigger("The necklace glows brightly\\. It seems to lose its connection to this place\\.").build();
    public static final UnlockRequirement TITHE_FARM = UnlockRequirement.builder("tithe_farm").unlockBooleanSupplier(() -> {
        IWidget widgetInterface = Static.getWidgets().get(62324743);
        if (widgetInterface == null) {
            return false;
        }
        return widgetInterface.hasListener();
    }).lockBooleanSupplier(() -> {
        IWidget widgetInterface = Static.getWidgets().get(62324743);
        if (widgetInterface == null) {
            return false;
        }
        return !widgetInterface.hasListener();
    }).lockTrigger("You must have entered Farmer Gricoller's farm before you can use this teleport.").build();
    public static final UnlockRequirement[] ALL = new UnlockRequirement[]{DIGSITE_PENDANT_FOSSIL_ISLAND, DIGSITE_PENDANT_LITHKREN, TITHE_FARM};
}

