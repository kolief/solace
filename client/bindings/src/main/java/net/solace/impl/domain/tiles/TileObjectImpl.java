package net.solace.impl.domain.tiles;

import lombok.Getter;
import lombok.Setter;
import net.runelite.api.MenuAction;
import net.runelite.api.ObjectComposition;
import net.runelite.api.Point;
import net.runelite.api.TileObject;
import net.runelite.api.EntityOps;
import net.runelite.api.WorldView;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.util.Text;
import net.solace.api.commons.Calculations;
import net.solace.api.commons.JagStrings;
import net.solace.api.coords.Coordinate;
import net.solace.api.domain.tiles.IGameObject;
import net.solace.api.domain.tiles.ITile;
import net.solace.api.domain.tiles.ITileObject;
import net.solace.impl.domain.AbstractInteractable;
import net.solace.api.domain.game.IClient;
import net.solace.api.domain.items.IInventoryItem;
import net.solace.api.interact.AutomatedMenu;
import net.solace.api.interact.InteractMethod;
import net.solace.api.magic.Spell;
import net.solace.api.util.Randomizer;
import net.solace.impl.util.RuneLiteWrapperUtil;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;

@Getter
public abstract class TileObjectImpl<W extends TileObject> extends AbstractInteractable implements ITileObject {
    @Setter
    protected W wrapped;
    protected ObjectComposition transformedComposition;
    protected final ITile tile;
    protected final IClient client;

    protected TileObjectImpl(W wrapped, ITile tile, IClient client) {
        super(client);
        this.wrapped = wrapped;
        this.transformedComposition = transformAndCacheComposition(client, wrapped.getId());
        this.tile = tile;
        this.client = client;
    }

    private static ObjectComposition transformAndCacheComposition(IClient client, int id) {
        var objectComposition = client.getObjectComposition(id);
        if (objectComposition == null) {
            return null;
        }

        var ids = objectComposition.getImpostorIds();
        if (ids != null) {
            if (objectComposition.getVarbitId() != -1 || objectComposition.getVarPlayerId() != -1) {
                return objectComposition.getImpostor();
            }
        }

        return objectComposition;
    }

    @Override
    public long getHash() {
        return wrapped.getHash();
    }

    @Override
    public int getX() {
        return wrapped.getX();
    }

    @Override
    public int getY() {
        return wrapped.getY();
    }

    @Override
    public int getZ() {
        return wrapped.getZ();
    }

    @Override
    public int getPlane() {
        return wrapped.getPlane();
    }

    @Override
    public WorldView getWorldView() {
        return wrapped.getWorldView();
    }

    @Override
    public int getId() {
        return wrapped.getId();
    }

    @Nullable
    @Override
    public Point getCanvasLocation() {
        return wrapped.getCanvasLocation();
    }

    @Nullable
    @Override
    public Point getCanvasLocation(int zOffset) {
        return wrapped.getCanvasLocation(zOffset);
    }

    @Nullable
    @Override
    public Polygon getCanvasTilePoly() {
        return wrapped.getCanvasTilePoly();
    }

    @Nullable
    @Override
    public Point getCanvasTextLocation(Graphics2D graphics, String text, int zOffset) {
        return wrapped.getCanvasTextLocation(graphics, text, zOffset);
    }

    @Nullable
    @Override
    public Point getMinimapLocation() {
        return wrapped.getMinimapLocation();
    }

    @NotNull
    @Override
    public WorldPoint getWorldLocation() {
        return wrapped.getWorldLocation();
    }

    @NotNull
    @Override
    public LocalPoint getLocalLocation() {
        return wrapped.getLocalLocation();
    }

    @Override
    public ITile getTile() {
        return tile;
    }

    @Override
    public String getName() {
        return transformedComposition != null ? Text.removeTags(transformedComposition.getName()) : null;
    }

    @Override
    public int getActualId() {
        return transformedComposition != null ? transformedComposition.getId() : getId();
    }

    @Override
    public boolean isInteractable(WorldPoint from) {
        return Calculations.isInteractable(
                client.getWrapped(),
                from,
                this
        );
    }

    @Override
    public boolean isInteractable() {
        return isInteractable(client.getLocalPlayer());
    }

    @Override
    public String[] getActions() {
        if (transformedComposition == null) {
            return null;
        }

        var actions = transformedComposition.getActions();
        var sanitizedActions = new String[actions.length];
        for (int i = 0; i < actions.length; i++) {
            sanitizedActions[i] = JagStrings.standardize(actions[i]);
        }

        return sanitizedActions;
    }

    @Override
    public AutomatedMenu generateMenu(InteractMethod interactMethod, int actionIndex) {
        var loc = menuPoint();
        return MENU_FACTORY.tileObject(getId(), loc.getX(), loc.getY())
                .interactMethod(interactMethod)
                .actionIndex(actionIndex)
                .worldViewId(getWorldView().getId())
                .build(null);
    }

    @Override
    public AutomatedMenu generateMenu(InteractMethod interactMethod, MenuAction opcode) {
        var loc = menuPoint();
        return MENU_FACTORY.tileObject(getId(), loc.getX(), loc.getY())
                .interactMethod(interactMethod)
                .opcode(opcode)
                .worldViewId(getWorldView().getId())
                .build(null);
    }

    @Override
    public AutomatedMenu generateMenu(InteractMethod interactMethod, Spell spell) {
        var loc = menuPoint();
        return MENU_FACTORY.tileObject(getId(), loc.getX(), loc.getY())
                .interactMethod(interactMethod)
                .opcode(MenuAction.WIDGET_TARGET_ON_GAME_OBJECT)
                .castSpell(spell)
                .identifier(spell.getMenuIdentifier() != -1 ? spell.getMenuIdentifier() : 0)
                .worldViewId(getWorldView().getId())
                .build(null);
    }

    @Override
    public AutomatedMenu generateMenu(InteractMethod interactMethod, IInventoryItem item) {
        var loc = menuPoint();
        return MENU_FACTORY.tileObject(getId(), loc.getX(), loc.getY())
                .interactMethod(interactMethod)
                .opcode(MenuAction.WIDGET_TARGET_ON_GAME_OBJECT)
                .useItem(item.getId(), item.getSlot())
                .worldViewId(getWorldView().getId())
                .build(null);
    }

    @Override
    @Nullable
    public EntityOps getOps() {
        return null;
    }

    @Override
    public Shape getClickArea() {
        return getClickbox();
    }

    @Override
    public Coordinate getClickPoint() {
        return Randomizer.getRandomPointIn(getClickbox());
    }

    @Override
    @Nullable
    public Shape getClickbox() {
        return wrapped.getClickbox();
    }

    @Override
    public Point menuPoint() {
        if (this instanceof IGameObject) {
            IGameObject temp = (IGameObject) this;
            return temp.getSceneMinLocation();
        }

        return new Point(getLocalLocation().getSceneX(), getLocalLocation().getSceneY());
    }

    @Override
    public int hashCode() {
        return RuneLiteWrapperUtil.getHashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
        return RuneLiteWrapperUtil.isEqual(this, obj);
    }

    @Override
    public void updateComposition() {
        transformedComposition = transformAndCacheComposition(client, getId());
    }

    @Override
    public String getOpOverride(int i) {
        return wrapped.getOpOverride(i);
    }

    @Override
    public boolean isOpShown(int i) {
        return wrapped.isOpShown(i);
    }
}
