package net.solace.impl.domain.widgets;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.FontTypeFace;
import net.runelite.api.MenuAction;
import net.runelite.api.Point;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetType;
import net.solace.api.commons.JagStrings;
import net.solace.api.coords.Coordinate;
import net.solace.api.domain.widgets.IWidget;
import net.solace.impl.domain.AbstractInteractable;
import net.solace.api.domain.game.IClient;
import net.solace.api.domain.game.IClientThread;
import net.solace.api.domain.items.IInventoryItem;
import net.solace.api.interact.AutomatedMenu;
import net.solace.api.interact.InteractMethod;
import net.solace.api.magic.Spell;
import net.solace.api.util.Randomizer;
import net.solace.impl.util.RuneLiteWrapperUtil;
import org.jetbrains.annotations.Nullable;

import java.awt.Rectangle;
import java.awt.Shape;

@Slf4j
public class WidgetImpl extends AbstractInteractable implements IWidget {
    @Getter
    private final Widget wrapped;
    private final IClient client;
    private final IClientThread clientThread;

    private boolean hidden;

    private WidgetImpl(Widget wrapped, boolean hidden, IClient client, IClientThread clientThread) {
        super(client);
        this.wrapped = wrapped;
        this.client = client;
        this.clientThread = clientThread;
        this.hidden = hidden;
    }

    public static IWidget of(Widget wrapped, Boolean hidden, IClient client, IClientThread clientThread) {
        if (wrapped == null) {
            return null;
        }

        return clientThread.invokeAndWait(() -> new WidgetImpl(wrapped, hidden != null ? hidden : wrapped.isHidden(), client, clientThread));
    }

    public static IWidget of(Widget wrapped, IClient client, IClientThread clientThread) {
        return of(wrapped, null, client, clientThread);
    }

    @Override
    public IWidget createChild(int index, int type) {
        return WidgetImpl.of(wrapped.createChild(index, type), client, clientThread);
    }

    @Override
    public IWidget createChild(int type) {
        return WidgetImpl.of(wrapped.createChild(type), client, clientThread);
    }

    @Override
    public IWidget[] getNestedChildren() {
        return clientThread.invokeAndWait(() -> {
            var children = wrapped.getNestedChildren();
            if (children == null) {
                return null;
            }

            var wrappedChildren = new IWidget[children.length];
            for (var i = 0; i < children.length; i++) {
                wrappedChildren[i] = WidgetImpl.of(children[i], client, clientThread);
            }

            return wrappedChildren;
        });
    }

    @Override
    public IWidget getParent() {
        return WidgetImpl.of(wrapped.getParent(), client, clientThread);
    }

    @Override
    public IWidget getDragParent() {
        return WidgetImpl.of(wrapped.getDragParent(), client, clientThread);
    }

    @Nullable
    @Override
    public IWidget[] getChildren() {
        return clientThread.invokeAndWait(() -> {
            var children = wrapped.getChildren();
            if (children == null) {
                return null;
            }

            var wrappedChildren = new IWidget[children.length];
            for (var i = 0; i < children.length; i++) {
                wrappedChildren[i] = WidgetImpl.of(children[i], client, clientThread);
            }

            return wrappedChildren;
        });
    }

    @Override
    public void setChildren(Widget[] children) {
        wrapped.setChildren(children);
    }

    @Override
    public IWidget[] getDynamicChildren() {
        return clientThread.invokeAndWait(() -> {
            var children = wrapped.getDynamicChildren();
            if (children == null) {
                return null;
            }

            var wrappedChildren = new IWidget[children.length];
            for (var i = 0; i < children.length; i++) {
                wrappedChildren[i] = WidgetImpl.of(children[i], client, clientThread);
            }

            return wrappedChildren;
        });
    }

    @Override
    public IWidget[] getStaticChildren() {
        return clientThread.invokeAndWait(() -> {
            var children = wrapped.getStaticChildren();
            if (children == null) {
                return null;
            }

            var wrappedChildren = new IWidget[children.length];
            for (var i = 0; i < children.length; i++) {
                wrappedChildren[i] = WidgetImpl.of(children[i], client, clientThread);
            }

            return wrappedChildren;
        });
    }

    @Override
    public int getType() {
        return wrapped.getType();
    }

    @Override
    public void setType(int type) {
        wrapped.setType(type);
    }

    @Override
    public int getContentType() {
        return wrapped.getContentType();
    }

    @Override
    public IWidget setContentType(int contentType) {
        wrapped.setContentType(contentType);
        return this;
    }

    @Override
    public int getClickMask() {
        return wrapped.getClickMask();
    }

    @Override
    public IWidget setClickMask(int mask) {
        wrapped.setClickMask(mask);
        return this;
    }

    @Override
    public int getParentId() {
        return wrapped.getParentId();
    }

    @Nullable
    @Override
    public IWidget getChild(int index) {
        var child = wrapped.getChild(index);
        if (child == null) {
            return null;
        }
        return WidgetImpl.of(child, client, clientThread);
    }

    @Override
    public int getRelativeX() {
        return wrapped.getRelativeX();
    }

    @Override
    public void setRelativeX(int x) {
        wrapped.setRelativeX(x);
    }

    @Override
    public int getRelativeY() {
        return wrapped.getRelativeY();
    }

    @Override
    public void setRelativeY(int y) {
        wrapped.setRelativeY(y);
    }

    @Override
    public void setForcedPosition(int x, int y) {
        wrapped.setForcedPosition(x, y);
    }

    @Override
    public String getText() {
        return wrapped.getText();
    }

    @Override
    public IWidget setText(String text) {
        wrapped.setText(text);
        return this;
    }

    @Override
    public int getTextColor() {
        return wrapped.getTextColor();
    }

    @Override
    public IWidget setTextColor(int textColor) {
        wrapped.setTextColor(textColor);
        return this;
    }

    @Override
    public int getOpacity() {
        return wrapped.getOpacity();
    }

    @Override
    public IWidget setOpacity(int transparency) {
        wrapped.setOpacity(transparency);
        return this;
    }

    @Override
    public String getName() {
        return wrapped.getName();
    }

    @Override
    public IWidget setName(String name) {
        wrapped.setName(name);
        return this;
    }

    @Override
    public int getModelId() {
        return wrapped.getModelId();
    }

    @Override
    public IWidget setModelId(int id) {
        wrapped.setModelId(id);
        return this;
    }

    @Override
    public int getModelType() {
        return wrapped.getModelType();
    }

    @Override
    public IWidget setModelType(int type) {
        wrapped.setModelType(type);
        return this;
    }

    @Override
    public int getAnimationId() {
        return wrapped.getAnimationId();
    }

    @Override
    public IWidget setAnimationId(int animationId) {
        wrapped.setAnimationId(animationId);
        return this;
    }

    @Override
    public int getRotationX() {
        return wrapped.getRotationX();
    }

    @Override
    public IWidget setRotationX(int modelX) {
        wrapped.setRotationX(modelX);
        return this;
    }

    @Override
    public int getRotationY() {
        return wrapped.getRotationY();
    }

    @Override
    public IWidget setRotationY(int modelY) {
        wrapped.setRotationY(modelY);
        return this;
    }

    @Override
    public int getRotationZ() {
        return wrapped.getRotationZ();
    }

    @Override
    public IWidget setRotationZ(int modelZ) {
        wrapped.setRotationZ(modelZ);
        return this;
    }

    @Override
    public int getModelZoom() {
        return wrapped.getModelZoom();
    }

    @Override
    public IWidget setModelZoom(int modelZoom) {
        wrapped.setModelZoom(modelZoom);
        return this;
    }

    @Override
    public int getSpriteId() {
        return wrapped.getSpriteId();
    }

    @Override
    public boolean getSpriteTiling() {
        return wrapped.getSpriteTiling();
    }

    @Override
    public IWidget setSpriteTiling(boolean tiling) {
        wrapped.setSpriteTiling(tiling);
        return this;
    }

    @Override
    public IWidget setSpriteId(int spriteId) {
        wrapped.setSpriteId(spriteId);
        return this;
    }

    @Override
    public boolean isHidden() {
        return hidden;
    }

    @Override
    public boolean isSelfHidden() {
        return wrapped.isSelfHidden();
    }

    @Override
    public IWidget setHidden(boolean hidden) {
        wrapped.setHidden(hidden);
        this.hidden = hidden;
        return this;
    }

    @Override
    public int getIndex() {
        return wrapped.getIndex();
    }

    @Override
    public Point getCanvasLocation() {
        return wrapped.getCanvasLocation();
    }

    @Override
    public int getWidth() {
        return wrapped.getWidth();
    }

    @Override
    public void setWidth(int width) {
        wrapped.setWidth(width);
    }

    @Override
    public int getHeight() {
        return wrapped.getHeight();
    }

    @Override
    public void setHeight(int height) {
        wrapped.setHeight(height);
    }

    @Override
    public Shape getClickArea() {
        return getBounds();
    }

    @Override
    public Rectangle getBounds() {
        return wrapped.getBounds();
    }

    @Override
    public int getItemId() {
        return wrapped.getItemId();
    }

    @Override
    public IWidget setItemId(int itemId) {
        wrapped.setItemId(itemId);
        return this;
    }

    @Override
    public int getItemQuantity() {
        return wrapped.getItemQuantity();
    }

    @Override
    public IWidget setItemQuantity(int quantity) {
        wrapped.setItemQuantity(quantity);
        return this;
    }

    @Override
    public boolean contains(Point point) {
        return wrapped.contains(point);
    }

    @Override
    public int getScrollX() {
        return wrapped.getScrollX();
    }

    @Override
    public IWidget setScrollX(int scrollX) {
        wrapped.setScrollX(scrollX);
        return this;
    }

    @Override
    public int getScrollY() {
        return wrapped.getScrollY();
    }

    @Override
    public IWidget setScrollY(int scrollY) {
        wrapped.setScrollY(scrollY);
        return this;
    }

    @Override
    public int getScrollWidth() {
        return wrapped.getScrollWidth();
    }

    @Override
    public IWidget setScrollWidth(int width) {
        wrapped.setScrollWidth(width);
        return this;
    }

    @Override
    public int getScrollHeight() {
        return wrapped.getScrollHeight();
    }

    @Override
    public IWidget setScrollHeight(int height) {
        wrapped.setScrollHeight(height);
        return this;
    }

    @Override
    public int getOriginalX() {
        return wrapped.getOriginalX();
    }

    @Override
    public IWidget setOriginalX(int originalX) {
        wrapped.setOriginalX(originalX);
        return this;
    }

    @Override
    public int getOriginalY() {
        return wrapped.getOriginalY();
    }

    @Override
    public IWidget setOriginalY(int originalY) {
        wrapped.setOriginalY(originalY);
        return this;
    }

    @Override
    public IWidget setPos(int x, int y) {
        wrapped.setPos(x, y);
        return this;
    }

    @Override
    public IWidget setPos(int x, int y, int xMode, int yMode) {
        wrapped.setPos(x, y, xMode, yMode);
        return this;
    }

    @Override
    public int getOriginalHeight() {
        return wrapped.getOriginalHeight();
    }

    @Override
    public IWidget setOriginalHeight(int originalHeight) {
        wrapped.setOriginalHeight(originalHeight);
        return this;
    }

    @Override
    public int getOriginalWidth() {
        return wrapped.getOriginalWidth();
    }

    @Override
    public IWidget setOriginalWidth(int originalWidth) {
        wrapped.setOriginalWidth(originalWidth);
        return this;
    }

    @Override
    public IWidget setSize(int width, int height) {
        wrapped.setSize(width, height);
        return this;
    }

    @Override
    public IWidget setSize(int width, int height, int widthMode, int heightMode) {
        wrapped.setSize(width, height, widthMode, heightMode);
        return this;
    }

    @Override
    public void deleteAllChildren() {
        wrapped.deleteAllChildren();
    }

    @Override
    public void setAction(int index, String action) {
        wrapped.setAction(index, action);
    }

    @Override
    public void clearActions() {
        wrapped.clearActions();
    }

    @Override
    public void setOnDialogAbortListener(Object... args) {
        wrapped.setOnDialogAbortListener(args);
    }

    @Override
    public void setOnMouseOverListener(Object... args) {
        wrapped.setOnMouseOverListener(args);
    }

    @Override
    public void setOnMouseRepeatListener(Object... args) {
        wrapped.setOnMouseRepeatListener(args);
    }

    @Override
    public void setOnMouseLeaveListener(Object... args) {
        wrapped.setOnMouseLeaveListener(args);
    }

    @Override
    public void setOnTimerListener(Object... args) {
        wrapped.setOnTimerListener(args);
    }

    @Override
    public void setOnTargetEnterListener(Object... args) {
        wrapped.setOnTargetEnterListener(args);
    }

    @Override
    public void setOnTargetLeaveListener(Object... args) {
        wrapped.setOnTargetLeaveListener(args);
    }

    @Override
    public boolean hasListener() {
        return wrapped.hasListener();
    }

    @Override
    public IWidget setHasListener(boolean hasListener) {
        wrapped.setHasListener(hasListener);
        return this;
    }

    @Override
    public boolean isIf3() {
        return wrapped.isIf3();
    }

    @Override
    public void revalidate() {
        wrapped.revalidate();
    }

    @Override
    public void revalidateScroll() {
        wrapped.revalidateScroll();
    }

    @Override
    public Object[] getOnOpListener() {
        return wrapped.getOnOpListener();
    }

    @Override
    public void setOnOpListener(Object... args) {
        wrapped.setOnOpListener(args);
    }

    @Override
    public Object[] getOnKeyListener() {
        return wrapped.getOnKeyListener();
    }

    @Override
    public void setOnKeyListener(Object... args) {
        wrapped.setOnKeyListener(args);
    }

    @Override
    public Object[] getOnLoadListener() {
        return wrapped.getOnLoadListener();
    }

    @Override
    public Object[] getOnInvTransmitListener() {
        return wrapped.getOnInvTransmitListener();
    }

    @Override
    public int getFontId() {
        return wrapped.getFontId();
    }

    @Override
    public IWidget setFontId(int id) {
        wrapped.setFontId(id);
        return this;
    }

    @Override
    public int getBorderType() {
        return wrapped.getBorderType();
    }

    @Override
    public void setBorderType(int thickness) {
        wrapped.setBorderType(thickness);
    }

    @Override
    public boolean isFlippedVertically() {
        return wrapped.isFlippedVertically();
    }

    @Override
    public void setFlippedVertically(boolean b) {
        wrapped.setFlippedVertically(b);
    }

    @Override
    public boolean isFlippedHorizontally() {
        return wrapped.isFlippedHorizontally();
    }

    @Override
    public void setFlippedHorizontally(boolean b) {
        wrapped.setFlippedHorizontally(b);
    }

    @Override
    public boolean getTextShadowed() {
        return wrapped.getTextShadowed();
    }

    @Override
    public IWidget setTextShadowed(boolean shadowed) {
        wrapped.setTextShadowed(shadowed);
        return this;
    }

    @Override
    public int getDragDeadZone() {
        return wrapped.getDragDeadZone();
    }

    @Override
    public void setDragDeadZone(int deadZone) {
        wrapped.setDragDeadZone(deadZone);
    }

    @Override
    public int getDragDeadTime() {
        return wrapped.getDragDeadTime();
    }

    @Override
    public void setDragDeadTime(int deadTime) {
        wrapped.setDragDeadTime(deadTime);
    }

    @Override
    public int getItemQuantityMode() {
        return wrapped.getItemQuantityMode();
    }

    @Override
    public IWidget setItemQuantityMode(int itemQuantityMode) {
        wrapped.setItemQuantityMode(itemQuantityMode);
        return this;
    }

    @Override
    public int getXPositionMode() {
        return wrapped.getXPositionMode();
    }

    @Override
    public IWidget setXPositionMode(int xpm) {
        wrapped.setXPositionMode(xpm);
        return this;
    }

    @Override
    public int getYPositionMode() {
        return wrapped.getYPositionMode();
    }

    @Override
    public IWidget setYPositionMode(int ypm) {
        wrapped.setYPositionMode(ypm);
        return this;
    }

    @Override
    public int getLineHeight() {
        return wrapped.getLineHeight();
    }

    @Override
    public IWidget setLineHeight(int lineHeight) {
        wrapped.setLineHeight(lineHeight);
        return this;
    }

    @Override
    public int getXTextAlignment() {
        return wrapped.getXTextAlignment();
    }

    @Override
    public IWidget setXTextAlignment(int xta) {
        wrapped.setXTextAlignment(xta);
        return this;
    }

    @Override
    public int getYTextAlignment() {
        return wrapped.getYTextAlignment();
    }

    @Override
    public IWidget setYTextAlignment(int yta) {
        wrapped.setYTextAlignment(yta);
        return this;
    }

    @Override
    public int getWidthMode() {
        return wrapped.getWidthMode();
    }

    @Override
    public IWidget setWidthMode(int widthMode) {
        wrapped.setWidthMode(widthMode);
        return this;
    }

    @Override
    public int getHeightMode() {
        return wrapped.getHeightMode();
    }

    @Override
    public IWidget setHeightMode(int heightMode) {
        wrapped.setHeightMode(heightMode);
        return this;
    }

    @Override
    public FontTypeFace getFont() {
        return wrapped.getFont();
    }

    @Override
    public boolean isFilled() {
        return wrapped.isFilled();
    }

    @Override
    public IWidget setFilled(boolean filled) {
        wrapped.setFilled(filled);
        return this;
    }

    @Override
    public String getTargetVerb() {
        return wrapped.getTargetVerb();
    }

    @Override
    public void setTargetVerb(String targetVerb) {
        wrapped.setTargetVerb(targetVerb);
    }

    @Override
    public int getTargetPriority() {
        return wrapped.getTargetPriority();
    }

    @Override
    public void setTargetPriority(int i) {
        wrapped.setTargetPriority(i);
    }

    @Override
    public boolean getNoClickThrough() {
        return wrapped.getNoClickThrough();
    }

    @Override
    public void setNoClickThrough(boolean noClickThrough) {
        wrapped.setNoClickThrough(noClickThrough);
    }

    @Override
    public boolean getNoScrollThrough() {
        return wrapped.getNoScrollThrough();
    }

    @Override
    public void setNoScrollThrough(boolean noScrollThrough) {
        wrapped.setNoScrollThrough(noScrollThrough);
    }

    @Override
    public int[] getVarTransmitTrigger() {
        return wrapped.getVarTransmitTrigger();
    }

    @Override
    public void setVarTransmitTrigger(int... trigger) {
        wrapped.setVarTransmitTrigger(trigger);
    }

    @Override
    public void setOnClickListener(Object... args) {
        wrapped.setOnClickListener(args);
    }

    @Override
    public void setOnHoldListener(Object... args) {
        wrapped.setOnHoldListener(args);
    }

    @Override
    public void setOnReleaseListener(Object... args) {
        wrapped.setOnReleaseListener(args);
    }

    @Override
    public void setOnDragCompleteListener(Object... args) {
        wrapped.setOnDragCompleteListener(args);
    }

    @Override
    public void setOnDragListener(Object... args) {
        wrapped.setOnDragListener(args);
    }

    @Override
    public void setOnScrollWheelListener(Object... args) {
        wrapped.setOnScrollWheelListener(args);
    }

    @Override
    public IWidget setDragParent(Widget dragParent) {
        wrapped.setDragParent(dragParent);
        return this;
    }

    @Override
    public Object[] getOnVarTransmitListener() {
        return wrapped.getOnVarTransmitListener();
    }

    @Override
    public void setOnVarTransmitListener(Object... args) {
        wrapped.setOnVarTransmitListener(args);
    }

    @Override
    public int getId() {
        return wrapped.getId();
    }

    @Override
    public boolean isInteractable(WorldPoint from) {
        return true;
    }

    @Override
    public String[] getActions() {
        var actions = wrapped.getActions();
        if (actions == null) {
            return null;
        }

        var sanitized = new String[actions.length];
        for (var i = 0; i < actions.length; i++) {
            sanitized[i] = JagStrings.standardize(actions[i]);
        }

        return sanitized;
    }

    @Override
    public Coordinate getClickPoint() {
        return Randomizer.getRandomPointIn(getBounds());
    }

    @Override
    public AutomatedMenu generateMenu(InteractMethod interactMethod, int actionIndex) {
        return MENU_FACTORY.widget(getId())
                .interactMethod(interactMethod)
                .childId(getIndex())
                .resume(isContinue())
                .itemId(getItemId())
                .actionIndex(actionIndex)
                .build(getClickPoint());
    }

    @Override
    public AutomatedMenu generateMenu(InteractMethod interactMethod, MenuAction opcode) {
        return MENU_FACTORY.widget(getId())
                .interactMethod(interactMethod)
                .childId(getIndex())
                .resume(isContinue())
                .itemId(getItemId())
                .opcode(opcode)
                .build(getClickPoint());
    }

    @Override
    public AutomatedMenu generateMenu(InteractMethod interactMethod, Spell spell) {
        return MENU_FACTORY.widget(getId())
                .interactMethod(interactMethod)
                .childId(getIndex())
                .identifier(spell.getMenuIdentifier() != -1 ? spell.getMenuIdentifier() : 0)
                .opcode(MenuAction.WIDGET_TARGET_ON_WIDGET)
                .itemId(getItemId())
                .castSpell(spell)
                .build(getClickPoint());
    }

    @Override
    public AutomatedMenu generateMenu(InteractMethod interactMethod, IInventoryItem item) {
        return MENU_FACTORY.widget(getId())
                .interactMethod(interactMethod)
                .childId(getIndex())
                .identifier(0)
                .opcode(MenuAction.WIDGET_TARGET_ON_WIDGET)
                .useItem(item.getId(), item.getSlot())
                .build(getClickPoint());
    }

    private boolean isContinue() {
        return getType() == WidgetType.TEXT;
    }

    @Override
    public boolean isVisible() {
        return !isHidden();
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
    public boolean isInteractable() {
        return true;
    }

    @Override
    public void click() {
        var clickPoint = getClickPoint();
        if (clickPoint != null) {
            client.interact(clickPoint.getX(), clickPoint.getY());
        } else {
            log.warn("Failed to generate click point for widget: {}", this);
        }
    }
}
