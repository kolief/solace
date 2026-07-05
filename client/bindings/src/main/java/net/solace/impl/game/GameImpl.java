package net.solace.impl.game;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.runelite.api.GameState;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.VarbitID;
import net.solace.api.account.GameAccount;
import net.solace.api.domain.game.IClient;
import net.solace.api.game.GameStateManager;
import net.solace.api.game.IGame;
import net.solace.api.game.IVars;
import net.solace.api.widgets.ITabs;
import net.solace.api.widgets.IWidgets;
import net.solace.api.widgets.Tab;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
public class GameImpl implements IGame {
    private static final int CUTSCENE_VARBIT = 542;
    private static final int LOADING_CUTSCENE_VARBIT = 6719;
    private static final int MEMBER_DAYS_VARP = 1780;
    private static final String LOGOUT_ACTION = "Logout";
    private static final List<Integer> blacklistedCutsceneRegions = Arrays.asList(10307, 14231);
    private final IClient client;
    private final IVars vars;
    private final IWidgets widgets;
    private final ITabs tabs;
    private final GameStateManager gameStateManager;

    @Getter
    @Setter
    private GameAccount gameAccount;

    @Override
    public boolean isLoggedIn() {
        return getState() == GameState.LOGGED_IN || getState() == GameState.LOADING;
    }

    @Override
    public GameState getState() {
        return client.getGameState();
    }

    @Override
    public boolean isInCutscene() {
        return vars.getBit(CUTSCENE_VARBIT) > 0
                || (vars.getBit(LOADING_CUTSCENE_VARBIT) > 0 && Arrays.stream(client.getMapRegions()).noneMatch(blacklistedCutsceneRegions::contains));
    }

    @Override
    public int getWildyLevel() {
        var wildyLevelWidget = widgets.get(InterfaceID.PvpIcons.WILDERNESSLEVEL);
        if (!widgets.isVisible(wildyLevelWidget)) {
            return 0;
        }

        var widgetText = wildyLevelWidget.getText();
        if (widgetText.isEmpty()
                || wildyLevelWidget.getText().contains("Guarded")
                || wildyLevelWidget.getText().contains("Protection")
                || wildyLevelWidget.getText().contains("Deadman")) {
            return 0;
        }
        if (widgetText.equals("Level: --")) {
            var local = client.getLocalPlayer();
            var localLocation = local.getLocalLocation();
            var y = WorldPoint.fromLocal(
                    client.getWrapped().getWorldView(localLocation.getWorldView()),
                    localLocation.getX(),
                    localLocation.getY(),
                    local.getWorldLocation().getPlane()
            ).getY();
            return 2 + (y - 3528) / 8;
        }
        var levelText = widgetText.contains("<br>") ? widgetText.substring(0, widgetText.indexOf("<br>")) : widgetText;
        return Integer.parseInt(levelText.replace("Level: ", ""));
    }

    @Override
    public boolean isInWilderness() {
        return vars.getBit(VarbitID.INSIDE_WILDERNESS) == 1;
    }

    @Override
    public int getDeadmanLevel() {
        var wildyLevelWidget = widgets.get(InterfaceID.PvpIcons.WILDERNESSLEVEL);
        if (wildyLevelWidget.getText().contains("Guarded")
                || wildyLevelWidget.getText().contains("Protection")) {
            return 0;
        }

        if (wildyLevelWidget.getText().contains("Deadman")) {
            return Integer.MAX_VALUE;
        }

        return 0;
    }

    @Override
    public int getMembershipDays() {
        return vars.getVarp(MEMBER_DAYS_VARP);
    }

    @Override
    public boolean isBlackScreen() {
        var blackScreen = widgets.get(174, 0);

        return widgets.isVisible(blackScreen);
    }

    @Override
    public void logout() {
        var logOutHopper = widgets.get(InterfaceID.WORLDSWITCHER, x -> x.hasAction(LOGOUT_ACTION));
        if (logOutHopper != null) {
            logOutHopper.interact(LOGOUT_ACTION);
            return;
        }

        var logOut = widgets.get(InterfaceID.LOGOUT, x -> x.hasAction(LOGOUT_ACTION));
        if (logOut != null) {
            logOut.interact(LOGOUT_ACTION);
            return;
        }

        if (!tabs.isOpen(Tab.LOG_OUT)) {
            tabs.open(Tab.LOG_OUT);
        }
    }

    @Override
    public Instant getLastLogin() {
        return gameStateManager.getLastLogin();
    }
}
