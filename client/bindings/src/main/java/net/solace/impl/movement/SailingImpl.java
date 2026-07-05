package net.solace.impl.movement;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.MenuAction;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.VarbitID;
import net.solace.api.domain.game.IClient;
import net.solace.api.entities.IPlayers;
import net.solace.api.game.IVars;
import net.solace.api.interact.AutomatedMenu;
import net.solace.api.interact.InteractMethod;
import net.solace.api.movement.ISailing;
import net.solace.api.movement.pathfinder.model.sailing.MoveMode;
import net.solace.api.movement.pathfinder.model.sailing.SailingDirection;
import net.solace.api.movement.pathfinder.model.sailing.SailingTab;
import net.solace.api.sailing.IShips;
import net.solace.api.sailing.Ship;
import net.solace.api.widgets.ITabs;
import net.solace.api.widgets.IWidgets;
import net.solace.api.widgets.Tab;

import java.util.Objects;

@RequiredArgsConstructor
@Slf4j
public class SailingImpl implements ISailing {

    public static final int NAVIGATING_VARBIT = 19105;
    public static final int SPEED_VARBIT = 19175;
    public static final int DIRECTION_VARBIT = 19129;

    private final IClient client;
    private final IVars vars;
    private final IShips ships;
    private final IWidgets widgets;
    private final IPlayers players;
    private final ITabs tabs;

    @Override
    public SailingDirection getDirection() {
        int currentDirection = vars.getBit(DIRECTION_VARBIT);
        return SailingDirection.fromAngle(currentDirection);
    }

    @Override
    public boolean isNavigating() {
        return vars.getBit(NAVIGATING_VARBIT) != 0;
    }

    @Override
    public boolean isMoving() {
        return vars.getBit(SPEED_VARBIT) != 0;
    }

    @Override
    public void setDirection(InteractMethod interactMethod, SailingDirection sailingDirection) {
        client.interact(
                AutomatedMenu.builder()
                        .interactMethod(interactMethod)
                        .identifier(sailingDirection.getCode())
                        .opcode(MenuAction.SET_HEADING)
                        .param0(0)
                        .param1(0)
                        .build()
        );
    }

    @Override
    public void setDirection(InteractMethod interactMethod, WorldPoint worldPoint) {
        var currentDirection = getDirection();
        var direction = SailingDirection.getOptimalHeading(worldPoint);

        if (direction == currentDirection) {
            return;
        }

        setDirection(interactMethod, direction);
    }

    @Override
    public boolean navigate(InteractMethod interactMethod) {
        if (isNavigating()) {
            return true;
        }

        var player = players.getLocal();
        var ship = getShip();

        if (ship == null) {
            return false;
        }

        var helm = ship.getTileObjectManager().getNearest(player.getWorldLocation(), x -> Objects.equals(x.getName(), "Helm") && x.hasAction("Navigate"));

        if (helm == null) {
            return false;
        }

        helm.interact(interactMethod, "Navigate");
        return true;
    }

    @Override
    public boolean stopNavigating(InteractMethod interactMethod) {
        if (!isNavigating()) {
            return true;
        }

        var player = players.getLocal();
        var ship = getShip();

        if (ship == null) {
            return false;
        }

        var helm = ship.getTileObjectManager().getNearest(player.getWorldLocation(), x -> Objects.equals(x.getName(), "Helm") && x.hasAction("Stop-navigating"));

        if (helm == null) {
            return false;
        }

        helm.interact(interactMethod, "Stop-navigating");
        return true;
    }

    @Override
    public boolean setSails(InteractMethod interactMethod) {
        if (!isNavigating()) {
            return false;
        }

        if (!tabs.isOpen(Tab.COMBAT)) {
            tabs.open(Tab.COMBAT);
            return false;
        }

        if (!SailingTab.FACILITIES.isOpen()) {
            SailingTab.FACILITIES.open();
            return false;
        }

        if (MoveMode.getCurrent() != MoveMode.NONE) {
            return false;
        }

        var sailClickContainer = widgets.get(InterfaceID.SailingSidepanel.FACILITIES_CONTENT_CLICKLAYER);

        if (sailClickContainer == null || !widgets.isVisible(sailClickContainer)) {
            return false;
        }

        var setSailButton = sailClickContainer.getChild(0);

        if (setSailButton == null || !widgets.isVisible(setSailButton)) {
            return false;
        }

        setSailButton.interact(interactMethod, 0);
        return true;
    }

    @Override
    public boolean unsetSails(InteractMethod interactMethod) {
        if (!isNavigating()) {
            return false;
        }

        if (!tabs.isOpen(Tab.COMBAT)) {
            tabs.open(Tab.COMBAT);
            return false;
        }

        if (!SailingTab.FACILITIES.isOpen()) {
            SailingTab.FACILITIES.open();
            return false;
        }

        if (MoveMode.getCurrent() == MoveMode.NONE) {
            return false;
        }

        var sailClickContainer = widgets.get(InterfaceID.SailingSidepanel.FACILITIES_CONTENT_CLICKLAYER);

        if (sailClickContainer == null || !widgets.isVisible(sailClickContainer)) {
            return false;
        }

        var setSailButton = sailClickContainer.getChild(0);

        if (setSailButton == null || !widgets.isVisible(setSailButton)) {
            return false;
        }

        setSailButton.interact(interactMethod, 0);
        return true;
    }

    @Override
    public boolean increaseSpeed(InteractMethod interactMethod) {
        var mode = MoveMode.getCurrent();
        if (mode == MoveMode.FAST) {
            return true;
        }

        if (!tabs.isOpen(Tab.COMBAT)) {
            tabs.open(Tab.COMBAT);
            return false;
        }

        if (!SailingTab.FACILITIES.isOpen()) {
            SailingTab.FACILITIES.open();
            return false;
        }

        var sailClickContainer = widgets.get(InterfaceID.SailingSidepanel.FACILITIES_CONTENT_CLICKLAYER);

        if (sailClickContainer == null || !widgets.isVisible(sailClickContainer)) {
            return false;
        }

        var speedWidget = sailClickContainer.getChild(2);
        if (speedWidget == null || !widgets.isVisible(speedWidget)) {
            return false;
        }

        speedWidget.interact(interactMethod, 0);
        return true;
    }

    @Override
    public boolean decreaseSpeed(InteractMethod interactMethod) {
        var mode = MoveMode.getCurrent();
        if (mode == MoveMode.REVERSE) {
            return true;
        }

        if (!tabs.isOpen(Tab.COMBAT)) {
            tabs.open(Tab.COMBAT);
            return false;
        }

        if (!SailingTab.FACILITIES.isOpen()) {
            SailingTab.FACILITIES.open();
            return false;
        }

        var sailClickContainer = widgets.get(InterfaceID.SailingSidepanel.FACILITIES_CONTENT_CLICKLAYER);
        if (sailClickContainer == null || !widgets.isVisible(sailClickContainer)) {
            return false;
        }

        var speedWidget = sailClickContainer.getChild(1);

        if (speedWidget == null || !widgets.isVisible(speedWidget)) {
            return false;
        }

        speedWidget.interact(interactMethod, 0);
        return true;
    }

    @Override
    public boolean reverse(InteractMethod interactMethod) {
        var mode = MoveMode.getCurrent();
        if (mode == MoveMode.REVERSE) {
            return true;
        }

        var speed = vars.getBit(SPEED_VARBIT);

        for (int i = 0; i < speed; i++) {
            decreaseSpeed(interactMethod);
        }

        return true;
    }

    @Override
    public Ship getShip() {
        var player = players.getLocal();

        if (player == null) {
            return null;
        }

        var worldView = client.getWrapped().findWorldViewFromWorldPoint(player.getWorldLocation());

        return ships.getByWorldViewId(worldView.getId());
    }

    public boolean isOnBoat() {
        return vars.getBit(VarbitID.SAILING_PLAYER_IS_ON_PLAYER_BOAT) == 1;
    }
}
