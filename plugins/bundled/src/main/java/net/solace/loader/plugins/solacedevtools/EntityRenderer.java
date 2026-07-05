package net.solace.loader.plugins.solacedevtools;

import lombok.Getter;
import lombok.Setter;
import net.runelite.api.DynamicObject;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.ui.overlay.tooltip.Tooltip;
import net.runelite.client.ui.overlay.tooltip.TooltipManager;
import net.solace.api.domain.SceneEntity;
import net.solace.api.domain.actors.IActor;
import net.solace.api.domain.actors.INPC;
import net.solace.api.domain.actors.IPlayer;
import net.solace.api.domain.game.IClient;
import net.solace.api.domain.tiles.IDecorativeObject;
import net.solace.api.domain.tiles.IGameObject;
import net.solace.api.domain.tiles.IGroundObject;
import net.solace.api.domain.tiles.ITile;
import net.solace.api.domain.tiles.ITileItem;
import net.solace.api.domain.tiles.ITileObject;
import net.solace.api.domain.tiles.IWallObject;
import net.solace.api.entities.INPCs;
import net.solace.api.entities.IPlayers;
import net.solace.api.entities.ITileItems;
import net.solace.api.entities.ITileObjects;
import net.solace.api.widgets.IWidgets;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
public class EntityRenderer {
    private static final Font FONT = FontManager.getRunescapeFont().deriveFont(Font.BOLD, 16);
    private static final Color RED = new Color(221, 44, 0);
    private static final Color GREEN = new Color(0, 200, 83);
    private static final Color TURQOISE = new Color(0, 200, 157);
    private static final Color ORANGE = new Color(255, 109, 0);
    private static final Color YELLOW = new Color(255, 214, 0);
    private static final Color CYAN = new Color(0, 184, 212);
    private static final Color BLUE = new Color(41, 98, 255);
    private static final Color DEEP_PURPLE = new Color(98, 0, 234);
    private static final Color PURPLE = new Color(170, 0, 255);
    private static final Color GRAY = new Color(158, 158, 158);

    private final IClient client;
    private final TooltipManager tooltipManager;
    private final IPlayers iPlayers;
    private final INPCs inpcs;
    private final ITileObjects itileObjects;
    private final ITileItems iTileItems;

    private final IWidgets widgets;

    @Getter
    @Setter
    private boolean groundObjects;
    @Getter
    @Setter
    private boolean wallObjects;
    @Getter
    @Setter
    private boolean decorativeObjects;
    @Getter
    @Setter
    private boolean gameObjects;
    @Getter
    @Setter
    private boolean graphicsObjects;
    @Getter
    @Setter
    private boolean inventory;
    @Getter
    @Setter
    private boolean npcs;
    @Getter
    @Setter
    private boolean players;
    @Getter
    @Setter
    private boolean tileItems;
    @Getter
    @Setter
    private boolean projectiles;
    @Getter
    @Setter
    private boolean tileLocation;
    @Getter
    @Setter
    private boolean path;
    // Configs
    @Getter
    @Setter
    private boolean ids = true;
    @Getter
    @Setter
    private boolean names = true;
    @Getter
    @Setter
    private boolean indexes = true;
    @Getter
    @Setter
    private boolean animations = true;
    @Getter
    @Setter
    private boolean graphics = true;
    @Getter
    @Setter
    private boolean actions = true;
    @Getter
    @Setter
    private boolean worldLocations = true;
    @Getter
    @Setter
    private boolean quantities = true;
    @Getter
    @Setter
    private boolean trueWorldLocations = true;
    @Setter
    private List<WorldPoint> currentPath = null;
    @Getter
    @Setter
    private int radius = 0;

    @Inject
    public EntityRenderer(IClient client, TooltipManager tooltipManager, IPlayers iPlayers, INPCs inpcs, ITileObjects itileObjects, ITileItems iTileItems, IWidgets widgets) {
        this.client = client;
        this.tooltipManager = tooltipManager;
        this.iPlayers = iPlayers;
        this.inpcs = inpcs;
        this.itileObjects = itileObjects;
        this.iTileItems = iTileItems;
        this.widgets = widgets;
    }

    public void render(Graphics2D g) {
        g.setFont(FONT);

        var hovered = getHoveredEntities();
        for (var entity : hovered) {
            renderTileObject(g, entity);
            renderTileItem(g, entity);
            renderNpc(g, entity);
            renderPlayer(g, entity);
        }

        if (inventory) {
            renderInventory(g);
        }

        var hoveredTile = client.getSelectedSceneTile();
        if (tileLocation) {
            renderTileTooltip(g, hoveredTile);
        }
    }

    private List<? extends SceneEntity> getHoveredEntities() {
        if (radius <= 0) {
            return client.getHoveredEntities().stream()
                    .distinct()
                    .collect(Collectors.toList());
        }

        var hoveredTile = client.getSelectedSceneTile();
        if (hoveredTile == null) {
            return List.of();
        }

        var out = new ArrayList<SceneEntity>();
        var players = iPlayers.getAll(x ->
                x.getWorldLocation().distanceTo(hoveredTile.getWorldLocation()) <= radius);
        var npcs = inpcs.getAll(x ->
                x.getWorldLocation().distanceTo(hoveredTile.getWorldLocation()) <= radius);
        var tileObjects = itileObjects.getSurrounding(hoveredTile, radius, x -> true);
        var tileItems = iTileItems.getSurrounding(hoveredTile, radius, x -> true);

        out.addAll(players);
        out.addAll(npcs);
        out.addAll(tileObjects);
        out.addAll(tileItems);

        return out;
    }

    public void renderPlayer(Graphics2D graphics, SceneEntity entity) {
        if (players) {
            var local = iPlayers.getLocal();
            if (entity instanceof IPlayer) {
                var p = (IPlayer) entity;
                if (p.getConvexHull() != null) {
                    graphics.setColor(BLUE);
                    graphics.draw(p.getConvexHull());

                    OverlayUtil.renderActorOverlay(graphics, p, "", BLUE);
                    tooltipManager.add(new Tooltip(createInfo(p)));
                }

                graphics.setColor(CYAN);

                OverlayUtil.renderActorOverlay(graphics, local, createInfo(local), CYAN);
                renderPlayerWireframe(graphics, local, CYAN);
            }
        }
    }

    public void renderNpc(Graphics2D graphics, SceneEntity entity) {
        if (entity instanceof INPC && npcs) {
            var npc = (INPC) entity;
            var color = npc.getCombatLevel() > 1 ? YELLOW : ORANGE;
            graphics.setColor(color);

            if (npc.getConvexHull() != null) {
                graphics.draw(npc.getConvexHull());
                tooltipManager.add(new Tooltip(createInfo(npc)));
            }
        }
    }

    public void renderTileObject(Graphics2D graphics, SceneEntity entity) {
        if (entity instanceof IGameObject && gameObjects) {
            renderGameObjects(graphics, (IGameObject) entity);
        } else if (entity instanceof IWallObject && wallObjects) {
            renderWallObject(graphics, (IWallObject) entity);
        } else if (entity instanceof IGroundObject && groundObjects) {
            renderGroundObject(graphics, (IGroundObject) entity);
        } else if (entity instanceof IDecorativeObject && decorativeObjects) {
            renderDecorObject(graphics, (IDecorativeObject) entity);
        }
    }

    public void renderTileTooltip(Graphics2D graphics, ITile tile) {
        if (tile == null) {
            return;
        }

        var wrapped = client.getWrapped();
        var poly = Perspective.getCanvasTilePoly(wrapped, tile.getLocalLocation());
        if (poly != null && poly.contains(wrapped.getMouseCanvasPosition().getX(), wrapped.getMouseCanvasPosition().getY())) {
            var worldLocation = tile.getWorldLocation();
            var scenePoint = scenePointFromWorld(worldLocation);
            String tooltip;

            if (trueWorldLocations && client.isInInstancedRegion()) {
                var trueWorldPoint = WorldPoint.fromLocalInstance(
                        wrapped,
                        LocalPoint.fromWorld(wrapped, worldLocation)
                );

                tooltip = String.format("World location: %d, %d, %d</br>" +
                                        "Region ID: %d location: %d, %d</br>" +
                                        "Scene location: %d, %d</br>" +
                                        "True location: %d, %d, %d</br>"
                        ,
                        worldLocation.getX(), worldLocation.getY(), worldLocation.getPlane(),
                        worldLocation.getRegionID(), worldLocation.getRegionX(), worldLocation.getRegionY(),
                        scenePoint.getX(), scenePoint.getY(),
                        trueWorldPoint.getX(), trueWorldPoint.getY(), trueWorldPoint.getPlane()
                );
            } else {
                tooltip = String.format("World location: %d, %d, %d</br>" +
                                        "Region ID: %d location: %d, %d</br>" +
                                        "Scene location: %d, %d</br>"
                        ,
                        worldLocation.getX(), worldLocation.getY(), worldLocation.getPlane(),
                        worldLocation.getRegionID(), worldLocation.getRegionX(), worldLocation.getRegionY(),
                        scenePoint.getX(), scenePoint.getY());
            }

            tooltipManager.add(new Tooltip(tooltip));
            OverlayUtil.renderPolygon(graphics, poly, GREEN);
        }
    }

    public void renderTileItem(Graphics2D graphics, SceneEntity entity) {
        if (entity instanceof ITileItem && tileItems) {
            var tileItemPile = ((ITileItem) entity).getTile().getItemLayer();
            if (tileItemPile != null) {
                OverlayUtil.renderTileOverlay(graphics, tileItemPile, "", RED);
                tooltipManager.add(new Tooltip(createInfo(entity)));
            }
        }
    }

    public void renderGameObjects(Graphics2D graphics, IGameObject go) {
        if (go == null) {
            return;
        }

        var hull = go.getConvexHull();
        if (hull == null) {
            return;
        }

        var entity = go.getRenderable();

        var color = entity instanceof DynamicObject ? TURQOISE : GREEN;

        graphics.setColor(color);
        graphics.draw(hull);

        OverlayUtil.renderTileOverlay(graphics, go, "", color);
        tooltipManager.add(new Tooltip(createInfo(go)));
    }

    public void renderGroundObject(Graphics2D graphics, IGroundObject gr) {
        if (gr == null) {
            return;
        }

        OverlayUtil.renderTileOverlay(graphics, gr, "", PURPLE);
        tooltipManager.add(new Tooltip(createInfo(gr)));
    }

    public void renderWallObject(Graphics2D graphics, IWallObject w) {
        if (w == null) {
            return;
        }

        var hull = w.getConvexHull();
        if (hull == null) {
            return;
        }

        OverlayUtil.renderTileOverlay(graphics, w, "", GRAY);
        tooltipManager.add(new Tooltip(createInfo(w)));
    }

    public void renderDecorObject(Graphics2D graphics, IDecorativeObject deo) {
        if (deo == null) {
            return;
        }

        var hull = deo.getConvexHull();
        if (hull == null) {
            return;
        }

        graphics.draw(hull);

        hull = deo.getConvexHull2();
        if (hull != null) {
            graphics.draw(hull);
        }

        OverlayUtil.renderTileOverlay(graphics, deo, "", DEEP_PURPLE);
        tooltipManager.add(new Tooltip(createInfo(deo)));
    }

    public void renderInventory(Graphics2D graphics) {
        var inventoryWidget = widgets.get(InterfaceID.Inventory.ITEMS);
        if (!widgets.isVisible(inventoryWidget)) {
            return;
        }

        for (var item : inventoryWidget.getDynamicChildren()) {
            var slotBounds = item.getBounds();
            var itemId = item.getItemId();

            if (itemId == 6512) {
                continue;
            }

            var idText = "" + itemId;

            var fm = graphics.getFontMetrics();
            var textBounds = fm.getStringBounds(idText, graphics);

            var textX = (int) (slotBounds.getX() + (slotBounds.getWidth() / 2) - (textBounds.getWidth() / 2));
            var textY = (int) (slotBounds.getY() + (slotBounds.getHeight() / 2) + (textBounds.getHeight() / 2));

            graphics.setColor(new Color(255, 255, 255, 65));
            graphics.fill(slotBounds);

            graphics.setColor(Color.BLACK);
            graphics.drawString(idText, textX + 1, textY + 1);
            graphics.setColor(YELLOW);
            graphics.drawString(idText, textX, textY);
        }
    }

    public void renderPlayerWireframe(Graphics2D graphics, IPlayer player, Color color) {
        var poly = player.getCanvasTilePoly();

        if (poly == null) {
            return;
        }

        graphics.setColor(color);
        graphics.drawPolygon(poly);
    }

    public String createInfo(SceneEntity interactable) {
        var sb = new StringBuilder();
        if (interactable instanceof IActor) {
            if (indexes) {
                if (interactable instanceof IPlayer) {
                    sb.append("Index: ").append(((IPlayer) interactable).getIndex()).append("</br>");
                }

                if (interactable instanceof INPC) {
                    sb.append("Index: ").append(((INPC) interactable).getIndex()).append("</br>");
                }

                var interacting = ((IActor) interactable).getInteracting();
                if (interacting != null) {
                    sb.append("Interacting: ").append(interacting.getIndex()).append("</br>");
                }

                sb.append("Angle: ").append(((IActor) interactable).getOrientation()).append("</br>");
            }

            appendCommonFields(sb, interactable);

            if (animations) {
                sb.append("Animations: ").append(((IActor) interactable).getAnimation()).append("</br>");
            }

            if (graphics) {
                sb.append("Graphic: ").append(((IActor) interactable).getGraphic()).append("</br>");
            }

            return sb.toString();
        }

        if (interactable instanceof ITileObject) {
            if (ids) {
                sb.append("Base ID: ").append(interactable.getId()).append("</br>");
                sb.append("Actual ID: ").append(((ITileObject) interactable).getActualId()).append("</br>");
            }

            appendCommonFields(sb, interactable);

            if (animations) {
                if (interactable instanceof IGameObject
                    && ((IGameObject) interactable).getRenderable() instanceof DynamicObject) {
                    var animation = ((DynamicObject) ((IGameObject) interactable).getRenderable()).getAnimation();
                    if (animation != null) {
                        sb.append("Animations: ").append(animation.getId()).append("</br>");
                    }
                }
            }

            return sb.toString();
        }

        if (interactable instanceof ITileItem) {
            if (ids) {
                sb.append("ID: ").append(interactable.getId()).append("</br>");
            }

            if (quantities) {
                sb.append("Quantity: ").append(((ITileItem) interactable).getQuantity()).append("</br>");
            }

            appendCommonFields(sb, interactable);
            return sb.toString();
        }

        return sb.toString();
    }

    private void appendCommonFields(StringBuilder sb, SceneEntity interactable) {
        if (interactable instanceof IActor) {
            if (interactable instanceof INPC && ids) {
                sb.append("ID: ").append(interactable.getId()).append("</br>");
            }

            if (names) {
                sb.append("Name: ").append(interactable.getName()).append("</br>");
            }

            if (actions) {
                sb.append("Actions: ").append(Arrays.toString(interactable.getActions())).append("</br>");
            }

            if (worldLocations) {
                var location = interactable.getWorldLocation();
                sb.append("Location: ").append(location).append("</br>");
                sb.append("WorldView: ").append(interactable.getWorldView().getId()).append("</br>");
                var regionPoint = regionPointFromWorld(location);
                sb.append("Region: ")
                        .append(regionPoint.getX())
                        .append(", ")
                        .append(regionPoint.getY())
                        .append(" (")
                        .append(location.getRegionID())
                        .append(")")
                        .append("</br>");
                var scenePoint = scenePointFromWorld(location);
                if (scenePoint != null) {
                    sb.append("Scene: ")
                            .append(scenePoint.getX())
                            .append(", ")
                            .append(scenePoint.getY())
                            .append("</br>");
                }
            }

            return;
        }

        if (interactable instanceof ITileObject) {
            if (names) {
                sb.append("Name: ").append(interactable.getName()).append("</br>");
            }

            if (actions) {
                sb.append("Actions: ").append(Arrays.toString(interactable.getActions())).append("</br>");
            }

            if (worldLocations) {
                var location = interactable.getWorldLocation();
                sb.append("Location: ").append(location).append("</br>");
                sb.append("WorldView: ").append(interactable.getWorldView().getId()).append("</br>");
                var tile = ((ITileObject) interactable).getTile();
                if (tile != null) {
                    sb.append("Tile Location: ").append(tile.getWorldLocation()).append("</br>");
                }

                var regionPoint = regionPointFromWorld(location);
                sb.append("Region: ")
                        .append(regionPoint.getX())
                        .append(", ")
                        .append(regionPoint.getY())
                        .append(" (")
                        .append(location.getRegionID())
                        .append(")")
                        .append("</br>");
                var local = LocalPoint.fromWorld(client.getWrapped(), location);
                if (local != null) {
                    var scenePoint = scenePointFromWorld(location);
                    if (scenePoint != null) {
                        sb.append("Scene: ")
                                .append(scenePoint.getX())
                                .append(", ")
                                .append(scenePoint.getY())
                                .append("</br>");
                    }
                }
            }

            var comp = client.getObjectComposition(interactable.getId());
            if (comp != null) {
                sb.append("Varbit: ").append(comp.getVarbitId()).append("</br>");
                sb.append("Varp: ").append(comp.getVarPlayerId()).append("</br>");
            }

            return;
        }

        if (interactable instanceof ITileItem) {
            if (names) {
                sb.append("Name: ").append(interactable.getName()).append("</br>");
            }

            if (actions) {
                sb.append("Actions: ").append(Arrays.toString(interactable.getActions())).append("</br>");
            }

            if (worldLocations) {
                var location = interactable.getWorldLocation();
                sb.append("Location: ").append(location).append("</br>");
                sb.append("WorldView: ").append(interactable.getWorldView().getId()).append("</br>");
                var regionPoint = regionPointFromWorld(location);
                sb.append("Region: ")
                        .append(regionPoint.getX())
                        .append(", ")
                        .append(regionPoint.getY())
                        .append(" (")
                        .append(location.getRegionID())
                        .append(")")
                        .append("</br>");
                var tile = ((ITileItem) interactable).getTile();
                if (tile != null) {
                    var scenePoint = scenePointFromWorld(location);
                    if (scenePoint != null) {
                        sb.append("Scene: ")
                                .append(scenePoint.getX())
                                .append(", ")
                                .append(scenePoint.getY())
                                .append("</br>");
                    }
                }
            }
        }
    }

    private Point regionPointFromWorld(WorldPoint worldPoint) {
        return new Point(worldPoint.getRegionX(), worldPoint.getRegionY());
    }

    private Point scenePointFromWorld(WorldPoint worldPoint) {
        var localPoint = LocalPoint.fromWorld(client.getWrapped(), worldPoint);
        if (localPoint == null) {
            return null;
        }

        return new Point(localPoint.getSceneX(), localPoint.getSceneY());
    }
}
