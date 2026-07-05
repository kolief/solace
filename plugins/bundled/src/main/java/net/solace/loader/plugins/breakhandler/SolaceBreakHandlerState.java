package net.solace.loader.plugins.breakhandler;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.solace.api.plugins.DoNotRename;

@Getter(AccessLevel.PACKAGE)
@AllArgsConstructor
@DoNotRename
public enum SolaceBreakHandlerState {
    NULL,

    LOGIN_SCREEN,
    INVENTORY,
    RESUME,

    LOGOUT,
    LOGOUT_TAB,
    LOGOUT_BUTTON,
    LOGOUT_WAIT,

    ;
}