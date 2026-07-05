package net.solace.impl.domain.tiles;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ItemComposition;
import net.runelite.api.MenuAction;
import net.runelite.api.Model;
import net.runelite.api.Node;
import net.runelite.api.Perspective;
import net.runelite.api.TileItem;
import net.runelite.api.WorldView;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.InventoryID;
import net.solace.api.commons.Calculations;
import net.solace.api.commons.JagStrings;
import net.solace.api.coords.Coordinate;
import net.solace.api.domain.tiles.ITile;
import net.solace.api.domain.tiles.ITileItem;
import net.solace.impl.domain.AbstractInteractable;
import net.solace.api.domain.game.IClient;
import net.solace.api.domain.items.IInventoryItem;
import net.solace.api.interact.AutomatedMenu;
import net.solace.api.interact.InteractMethod;
import net.solace.api.magic.Spell;
import net.solace.impl.reflection.ReflectionManager;
import net.solace.api.util.Randomizer;
import net.solace.impl.util.RuneLiteWrapperUtil;

@Slf4j
@Getter
public class TileItemImpl extends AbstractInteractable implements ITileItem {
    private final TileItem wrapped;
    private final ITile tile;
    private final ItemComposition composition;
    private final IClient client;

    private TileItemImpl(TileItem wrapped, ITile tile, IClient client) {
        super(client);
        this.wrapped = wrapped;
        this.tile = tile;
        this.composition = client.getItemComposition(wrapped.getId());
        this.client = client;
    }

    public static TileItemImpl of(TileItem tileItem, ITile tile, IClient client) {
        if (tileItem == null) {
            return null;
        }

        return new TileItemImpl(tileItem, tile, client);
    }

    @Override
    public int getId() {
        return wrapped.getId();
    }

    @Override
    public int getRenderMode() {
        return wrapped.getRenderMode();
    }

    @Override
    public String getName() {
        return composition.getName();
    }

    @Override
    public int getQuantity() {
        return wrapped.getQuantity();
    }

    @Override
    public int getVisibleTime() {
        return wrapped.getVisibleTime();
    }

    @Override
    public int getDespawnTime() {
        return wrapped.getDespawnTime();
    }

    @Override
    public int getOwnership() {
        return wrapped.getOwnership();
    }

    @Override
    public boolean isPrivate() {
        return wrapped.isPrivate();
    }

    @Override
    public ITile getTile() {
        return tile;
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
    public String[] getActions() {
        String[] actions = ReflectionManager.getField(composition, "ItemComposition", "groundActions");
        var sanitized = new String[actions.length];
        for (int i = 0; i < actions.length; i++) {
            sanitized[i] = JagStrings.standardize(actions[i]);
        }

        return sanitized;
    }

    @Override
    public boolean isTradable() {
        return composition.isTradeable();
    }

    @Override
    public boolean isStackable() {
        return composition.isStackable();
    }

    @Override
    public boolean isNoted() {
        return composition.getNote() != -1;
    }

    @Override
    public boolean isMembers() {
        return composition.isMembers();
    }

    @Override
    public int getHaPrice() {
        return composition.getHaPrice();
    }

    @Override
    public int getWorldViewId() {
        return ReflectionManager.getField(wrapped, "TileItem", "worldViewId");
    }

    @Override
    public String[] getInventoryActions() {
        return composition.getInventoryActions();
    }

    @Override
    public AutomatedMenu generateMenu(InteractMethod interactMethod, int actionIndex) {
        var loc = tile.getSceneLocation();
        return MENU_FACTORY.tileItem(getId(), loc.getX(), loc.getY())
                .interactMethod(interactMethod)
                .actionIndex(actionIndex)
                .worldViewId(getWorldView().getId())
                .build(getClickPoint());
    }

    @Override
    public AutomatedMenu generateMenu(InteractMethod interactMethod, MenuAction opcode) {
        var loc = tile.getSceneLocation();
        return MENU_FACTORY.tileItem(getId(), loc.getX(), loc.getY())
                .interactMethod(interactMethod)
                .opcode(opcode)
                .worldViewId(getWorldView().getId())
                .build(getClickPoint());
    }

    @Override
    public AutomatedMenu generateMenu(InteractMethod interactMethod, Spell spell) {
        var loc = tile.getSceneLocation();
        return MENU_FACTORY.tileItem(getId(), loc.getX(), loc.getY())
                .interactMethod(interactMethod)
                .opcode(MenuAction.WIDGET_TARGET_ON_GROUND_ITEM)
                .castSpell(spell)
                .identifier(spell.getMenuIdentifier() != -1 ? spell.getMenuIdentifier() : 0)
                .worldViewId(getWorldView().getId())
                .build(getClickPoint());
    }

    @Override
    public AutomatedMenu generateMenu(InteractMethod interactMethod, IInventoryItem item) {
        var loc = tile.getSceneLocation();
        return MENU_FACTORY.tileItem(getId(), loc.getX(), loc.getY())
                .interactMethod(interactMethod)
                .useItem(item.getId(), item.getSlot())
                .opcode(MenuAction.WIDGET_TARGET_ON_GROUND_ITEM)
                .worldViewId(getWorldView().getId())
                .build(getClickPoint());
    }

    @Override
    public void pickup() {
        interact(MenuAction.GROUND_ITEM_THIRD_OPTION);
    }

    @Override
    public Coordinate getClickPoint() {
        var canvasTilePoly = Perspective.getCanvasTilePoly(client.getWrapped(), getLocalLocation());
        return Randomizer.getRandomPointIn(canvasTilePoly);
    }

    @Override
    public WorldPoint getWorldLocation() {
        return tile.getWorldLocation();
    }

    @Override
    public LocalPoint getLocalLocation() {
        return tile.getLocalLocation();
    }

    @Override
    public int getPlane() {
        return tile.getPlane();
    }

    @Override
    public boolean canPick() {
        var itemContainer = client.getItemContainer(InventoryID.INV);
        if (itemContainer == null) {
            log.warn("Item container is null, cannot pick up item: {}", getName());
            return false;
        }

        return itemContainer.count() < 28 || (itemContainer.count(getId()) > 0 && composition.isStackable());
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
    public Model getModel() {
        return wrapped.getModel();
    }

    @Override
    public int getModelHeight() {
        return wrapped.getModelHeight();
    }

    @Override
    public void setModelHeight(int modelHeight) {
        wrapped.setModelHeight(modelHeight);
    }

    @Override
    public int getAnimationHeightOffset() {
        return wrapped.getAnimationHeightOffset();
    }

    @Override
    public Node getNext() {
        return wrapped.getNext();
    }

    @Override
    public Node getPrevious() {
        return wrapped.getPrevious();
    }

    @Override
    public long getHash() {
        return wrapped.getHash();
    }

    @Override
    public boolean isInteractable() {
        return isInteractable(client.getLocalPlayer());
    }

    @Override
    public WorldView getWorldView() {
        return client.getWrapped().getWorldView(getWorldViewId());
    }
}
