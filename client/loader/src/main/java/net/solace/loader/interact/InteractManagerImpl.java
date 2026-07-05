package net.solace.loader.interact;

import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Actor;
import net.runelite.api.GameObject;
import net.runelite.api.MenuAction;
import net.runelite.api.TileObject;
import net.runelite.api.VarClientInt;
import net.runelite.api.VarClientStr;
import net.runelite.api.events.PostClientTick;
import net.runelite.api.events.PostMenuSort;
import net.runelite.client.eventbus.Subscribe;
import net.solace.api.coords.Coordinate;
import net.solace.api.domain.game.IClient;
import net.solace.api.events.AutomatedClick;
import net.solace.api.events.AutomatedInteraction;
import net.solace.api.events.PostAutomatedClick;
import net.solace.api.events.PostAutomatedInteraction;
import net.solace.api.events.PreClientTick;
import net.solace.api.interact.AutomatedMenu;
import net.solace.api.interact.AutomatedMouse;
import net.solace.api.interact.Automation;
import net.solace.api.interact.InputDialogAction;
import net.solace.api.interact.InteractManager;
import net.solace.api.interact.InteractMethod;
import net.solace.api.interact.RunnableAction;
import net.solace.api.interact.SleepAction;
import net.solace.api.interact.WidgetAction;
import net.solace.api.interact.mouse.MouseManager;
import net.solace.api.interact.mouse.MouseMovementStrategy;
import net.solace.api.items.IInventory;
import net.solace.api.magic.IMagic;
import net.solace.api.util.Randomizer;
import net.solace.impl.reflection.ReflectionManager;
import org.jetbrains.annotations.NotNull;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
public class InteractManagerImpl implements InteractManager {
    private static final int MINIMAP_DISABLED = 1;
    private static final int MINIMAP_ENABLED = 0;

    private final Queue<Automation> automations = new ConcurrentLinkedQueue<>();
    private Automation current;
    private boolean shouldRelease;
    private Point currentMousePosition;

    // Path-based mouse movement state
    private MouseMovementStrategy.MousePath currentPath;
    private int currentPathIndex;

    @Setter
    @Getter
    private InteractMethod interactMethodOverride;

    private MouseManager mouseManager;

    private final IClient client;
    private final IMagic magic;
    private final IInventory inventory;

    public InteractManagerImpl(IClient client, IMagic magic, IInventory inventory) {
        this.client = client;
        this.magic = magic;
        this.inventory = inventory;
        this.mouseManager = new LoaderMouseManager(client);
    }

    @Subscribe
    protected void onPreClientTick(PreClientTick e) {
        // Continue executing current path if one is in progress
        if (currentPath != null) {
            executeNextPathSegment();
            return;
        }

        // Poll next automation from queue
        current = automations.poll();
        if (current == null || current instanceof SleepAction || current instanceof WidgetAction
                || current instanceof InputDialogAction || current instanceof RunnableAction) {
            return;
        }

        var automation = current;
        int x;
        int y;
        boolean click;
        boolean isInvoke = false;

        if (automation instanceof AutomatedMenu) {
            var menu = (AutomatedMenu) automation;
            var clickPoint = getClickPoint(menu);
            x = clickPoint.getX();
            y = clickPoint.getY();
            click = true;

            if (menu.getInteractMethod() == null && interactMethodOverride != null) {
                menu.setInteractMethod(interactMethodOverride);
            }

            isInvoke = menu.getInteractMethod() == InteractMethod.INVOKE;
            if (isInvoke) {
                handleWidgetTargetOp(menu);
                client.invokeMenuAction(
                        menu.getParam0(),
                        menu.getParam1(),
                        menu.getOpcode().getId(),
                        menu.getIdentifier(),
                        menu.getItemId(),
                        menu.getWorldViewId(),
                        menu.getOption(),
                        menu.getTarget()
                );
            } else {
                log.debug("[{}] Clicking at {} {} with action {}", client.getWrapped().getGameCycle(), x, y, automation);
            }
        } else if (automation instanceof AutomatedMouse) {
            var mouse = (AutomatedMouse) automation;
            x = mouse.getX();
            y = mouse.getY();
            click = mouse.isClick();
            log.debug("[{}] Clicking at {} {}", client.getWrapped().getGameCycle(), x, y);
        } else {
            log.warn("Unknown automation type: {}", automation);
            return;
        }

        if (!isInvoke) {
            client.setMenuOpened(false);
            client.setMinimapState(MINIMAP_DISABLED);

            var event = new AutomatedClick(x, y, click);
            client.getWrapped().getCallbacks().post(event);

            var finalX = event.getX();
            var finalY = event.getY();
            var finalClick = event.isClick();

            // Generate path and start execution
            startMouseMovement(finalX, finalY, finalClick);
        }
    }

    /**
     * Starts mouse movement by generating a path and executing the first segment.
     */
    private void startMouseMovement(int targetX, int targetY, boolean shouldClick) {
        Point target = new Point(targetX, targetY);

        currentPath = getMouseManager().getMouseMovementStrategy().generatePath(currentMousePosition, target);
        currentPathIndex = 0;

        log.debug("Generated path with {} points", currentPath.getPoints().size());

        // Execute first segment
        executeNextPathSegment();

        // If path is instant (completed in one tick), handle click immediately
        if (currentPath == null && shouldClick) {
            click(targetX, targetY);
            client.getWrapped().getCallbacks().post(new PostAutomatedClick(targetX, targetY, true));
            shouldRelease = true;
        }
    }

    /**
     * Executes the next segment of the current path (one or more points based on pointsPerTick).
     */
    private void executeNextPathSegment() {
        if (currentPath == null) {
            return;
        }

        var points = currentPath.getPoints();

        Point point = points.get(currentPathIndex);
        moveToPoint(point);
        currentMousePosition = point;
        currentPathIndex++;

        // Check if path is complete
        if (currentPathIndex >= points.size()) {
            log.debug("Mouse path completed");

            // Path complete - handle click on next tick
            // Get target position (last point in path)
            Point finalPoint = points.get(points.size() - 1);

            // Clear path state
            currentPath = null;
            currentPathIndex = 0;

            // Queue the click for this same tick
            if (current instanceof AutomatedMenu ||
                    (current instanceof AutomatedMouse && ((AutomatedMouse) current).isClick())) {
                click(finalPoint.x, finalPoint.y);
                client.getWrapped().getCallbacks().post(
                        new PostAutomatedClick(finalPoint.x, finalPoint.y, true));
                shouldRelease = true;
            }
        }
    }

    /**
     * Dispatches a single MOUSE_MOVED event to the specified point.
     */
    private void moveToPoint(Point point) {
        var translated = translateCoordinateToPoint(point.x, point.y);
        client.getCanvas().dispatchEvent(new MouseEvent(
                client.getCanvas().getParent(),
                MouseEvent.MOUSE_MOVED,
                System.currentTimeMillis(),
                0,
                translated.x,
                translated.y,
                0,
                false
        ));
    }

    @Subscribe
    private void onPostMenuSort(PostMenuSort e) {
        if (current instanceof AutomatedMenu) {
            var initialMenu = (AutomatedMenu) current;
            var event = new AutomatedInteraction(initialMenu);
            var rlClient = client.getWrapped();
            rlClient.getCallbacks().post(event);

            var finalMenu = event.getMenu();

            log.debug("Processing menu: {}", finalMenu);
            if (finalMenu.getInteractMethod() == InteractMethod.INVOKE) {
                return;
            }

            client.setDraggedWidget(null);

            handleWidgetTargetOp(finalMenu);

            var opcode = finalMenu.getOpcode();
            var menu = rlClient.getMenu().createMenuEntry(-1)
                    .setOption(finalMenu.getOption())
                    .setTarget(finalMenu.getTarget())
                    .setType(opcode)
                    .setIdentifier(finalMenu.getIdentifier())
                    .setParam0(finalMenu.getParam0())
                    .setParam1(finalMenu.getParam1())
                    .setWorldViewId(finalMenu.getWorldViewId())
                    .setItemId(finalMenu.getItemId());

            if (opcode == MenuAction.WALK) {
                client.setSelectedSceneTileX(menu.getParam0());
                client.setSelectedSceneTileY(menu.getParam1());
                client.setViewportWalking(true);

                menu.setType(MenuAction.CANCEL)
                        .setIdentifier(0)
                        .setParam0(0)
                        .setParam1(0)
                        .setItemId(0);
            }

            rlClient.getCallbacks().post(new PostAutomatedInteraction(finalMenu));
        } else if (current instanceof WidgetAction) {
            var widgetAction = (WidgetAction) current;
            log.debug("Processing widgetAction: ID: {} COMP: {} IDX: {} ITEM: {}", widgetAction.getIdentifier(),
                    widgetAction.getComponentId(), widgetAction.getIndex(), widgetAction.getItemId());
            client.invokeWidgetAction(widgetAction.getIdentifier(), widgetAction.getComponentId(),
                    widgetAction.getIndex(), widgetAction.getItemId(), "", "");
        } else if (current instanceof InputDialogAction) {
            var inputDialogAction = (InputDialogAction) current;
            log.debug("Processing inputDialogAction: TYPE: {} TEXT: {}", inputDialogAction.getInputType(),
                    inputDialogAction.getInputText());
            client.setVarcIntValue(VarClientInt.INPUT_TYPE, inputDialogAction.getInputType());
            client.setVarcStrValue(VarClientStr.INPUT_TEXT, inputDialogAction.getInputText());
            client.runScript(681);
        } else if (current instanceof RunnableAction) {
            var runnableAction = (RunnableAction) current;
            runnableAction.getRunnable().run();
        }
    }

    @Subscribe
    protected void onPostClientTick(PostClientTick e) {
        if (ReflectionManager.hasStatic("minimapState")) {
            if (client.getMinimapState() == MINIMAP_DISABLED) {
                client.setMinimapState(MINIMAP_ENABLED);
            }
        }

        if (shouldRelease) {
            release();
            shouldRelease = false;
        }
    }

    public void queue(Automation automation) {
        automations.add(automation);
    }

    @Override
    public Queue<Automation> getQueue() {
        return automations;
    }

    private void click(int x, int y) {
        var translated = translateCoordinateToCanvas(x, y);
        client.getCanvas().dispatchEvent(new MouseEvent(
                client.getCanvas().getParent(),
                MouseEvent.MOUSE_PRESSED,
                System.currentTimeMillis(),
                0,
                translated.getX(),
                translated.getY(),
                0,
                false,
                1
        ));
    }

    private void release() {
        client.getCanvas().dispatchEvent(new MouseEvent(
                client.getCanvas().getParent(),
                MouseEvent.MOUSE_RELEASED,
                System.currentTimeMillis(),
                0,
                0,
                0,
                0,
                false
        ));
    }

    private Coordinate getClickPoint(AutomatedMenu menu) {
        var clickPoint = menu.getClickPoint();
        if (clickPoint == null) {
            var tile = getTileObjectFromMenu(menu);
            if (tile != null) {
                clickPoint = Randomizer.getRandomPointIn(tile.getClickbox());
            }
        }

        if (clickPoint == null) {
            var actor = getActorFromMenu(menu);
            if (actor != null) {
                clickPoint = Randomizer.getRandomPointIn(actor.getConvexHull());
            }
        }

        if (clickPoint == null) {
            clickPoint = new Coordinate(-1, -1);
        }

        var canvas = client.getCanvas();
        int width = canvas.getWidth();
        int height = canvas.getHeight();

        int x = clickPoint.getX();
        if (x <= 0 || x >= width) {
            clickPoint = new Coordinate(ThreadLocalRandom.current().nextInt(width), clickPoint.getY());
        }

        int y = clickPoint.getY();
        if (y < 0 || y >= height) {
            clickPoint = new Coordinate(clickPoint.getX(), ThreadLocalRandom.current().nextInt(height));
        }

        return clickPoint;
    }

    private TileObject getTileObjectFromMenu(AutomatedMenu menu) {
        var x = menu.getParam0();
        var y = menu.getParam1();
        var topLevelWorldView = client.getWrapped().getWorldView(menu.getWorldViewId());
        var tile = topLevelWorldView.getScene().getTiles()[topLevelWorldView.getPlane()][x][y];
        if (tile == null) {
            return null;
        }

        var id = menu.getIdentifier();
        var gameObjects = tile.getGameObjects();
        if (gameObjects != null) {
            for (GameObject gameObject : gameObjects) {
                if (gameObject != null && gameObject.getId() == id) {
                    return gameObject;
                }
            }
        }

        var decor = tile.getDecorativeObject();
        if (decor != null && decor.getId() == id) {
            return decor;
        }

        var wall = tile.getWallObject();
        if (wall != null && wall.getId() == id) {
            return wall;
        }

        var ground = tile.getGroundObject();
        if (ground != null && ground.getId() == id) {
            return ground;
        }

        return null;
    }

    private Actor getActorFromMenu(AutomatedMenu menu) {
        var topLevelWorldView = client.getWrapped().getWorldView(menu.getWorldViewId());
        switch (menu.getOpcode()) {
            case PLAYER_FIRST_OPTION:
            case PLAYER_SECOND_OPTION:
            case PLAYER_THIRD_OPTION:
            case PLAYER_FOURTH_OPTION:
            case PLAYER_FIFTH_OPTION:
            case PLAYER_SIXTH_OPTION:
            case PLAYER_SEVENTH_OPTION:
            case PLAYER_EIGHTH_OPTION:
            case WIDGET_TARGET_ON_PLAYER:
                return topLevelWorldView.players().byIndex(menu.getIdentifier());

            case NPC_FIRST_OPTION:
            case NPC_SECOND_OPTION:
            case NPC_THIRD_OPTION:
            case NPC_FOURTH_OPTION:
            case NPC_FIFTH_OPTION:
            case WIDGET_TARGET_ON_NPC:
                return topLevelWorldView.npcs().byIndex(menu.getIdentifier());
        }

        return null;
    }

    private Coordinate translateCoordinateToCanvas(int x, int y) {
        var wrapped = client.getWrapped();
        if (wrapped.isStretchedEnabled()) {
            var real = wrapped.getRealDimensions();
            var stretched = wrapped.getStretchedDimensions();

            var xRatio = (double) stretched.width / real.width;
            var yRatio = (double) stretched.height / real.height;

            return new Coordinate(
                    (int) (x * xRatio),
                    (int) (y * yRatio)
            );
        }

        return new Coordinate(x, y);
    }

    private Point translateCoordinateToPoint(int x, int y) {
        var coordinate = translateCoordinateToCanvas(x, y);
        return new Point(coordinate.getX(), coordinate.getY());
    }

    private void handleWidgetTargetOp(AutomatedMenu menu) {
        var useItemId = menu.getUseItemId();
        var useItemSlot = menu.getUseItemSlot();
        if (useItemId != null && useItemSlot != null) {
            var item = inventory.get(useItemSlot);
            if (item != null && item.getId() == useItemId) {
                item.use();
            }
        } else if (menu.getCastSpell() != null) {
            magic.selectSpell(menu.getCastSpell());
        }
    }

    @Override
    public MouseManager getMouseManager() {
        return mouseManager;
    }

    @Override
    public void setMouseManager(MouseManager mouseManager) {
        this.mouseManager = Preconditions.checkNotNull(mouseManager, "Mouse manager cannot be null");
    }

    @Override
    public boolean isInputIdle() {
        return current == null && automations.isEmpty() && currentPath == null && !shouldRelease;
    }

    public void setMouseMovementStrategy(@NotNull MouseMovementStrategy mouseMovementStrategy) {
        getMouseManager().setMouseMovementStrategy(mouseMovementStrategy);
    }

    public MouseMovementStrategy getMouseMovementStrategy() {
        return getMouseManager().getMouseMovementStrategy();
    }
}


