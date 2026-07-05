package net.solace.api.domain.widgets;

import net.runelite.api.widgets.Widget;
import net.solace.api.domain.Identifiable;
import net.solace.api.domain.Interactable;
import net.solace.api.domain.RuneLiteWrapper;

public interface IWidget
extends Widget,
Interactable,
Identifiable,
RuneLiteWrapper<Widget> {
    public IWidget[] getDynamicChildren();

    public IWidget[] getStaticChildren();

    public IWidget[] getNestedChildren();

    public IWidget setText(String var1);

    public IWidget setTextColor(int var1);

    public IWidget setOpacity(int var1);

    public IWidget setName(String var1);

    public IWidget setModelId(int var1);

    public IWidget setModelType(int var1);

    public IWidget setAnimationId(int var1);

    public IWidget setRotationX(int var1);

    public IWidget setRotationY(int var1);

    public IWidget setRotationZ(int var1);

    public IWidget setModelZoom(int var1);

    public IWidget setSpriteId(int var1);

    public IWidget setSpriteTiling(boolean var1);

    public IWidget setHidden(boolean var1);

    public IWidget setItemId(int var1);

    public IWidget setItemQuantity(int var1);

    public IWidget setScrollX(int var1);

    public IWidget setScrollY(int var1);

    public IWidget setScrollWidth(int var1);

    public IWidget setScrollHeight(int var1);

    public IWidget setOriginalX(int var1);

    public IWidget setOriginalY(int var1);

    public IWidget setPos(int var1, int var2);

    public IWidget setPos(int var1, int var2, int var3, int var4);

    public IWidget setOriginalHeight(int var1);

    public IWidget setOriginalWidth(int var1);

    public IWidget setSize(int var1, int var2);

    public IWidget setSize(int var1, int var2, int var3, int var4);

    public IWidget createChild(int var1, int var2);

    public IWidget createChild(int var1);

    public IWidget setHasListener(boolean var1);

    public IWidget setFontId(int var1);

    public IWidget setTextShadowed(boolean var1);

    public IWidget setItemQuantityMode(int var1);

    public IWidget setXPositionMode(int var1);

    public IWidget setYPositionMode(int var1);

    public IWidget setLineHeight(int var1);

    public IWidget setXTextAlignment(int var1);

    public IWidget setYTextAlignment(int var1);

    public IWidget setWidthMode(int var1);

    public IWidget setHeightMode(int var1);

    public IWidget setFilled(boolean var1);

    public IWidget getParent();

    public IWidget getDragParent();

    public IWidget setDragParent(Widget var1);

    public IWidget getChild(int var1);

    public IWidget[] getChildren();

    public void setChildren(Widget[] var1);

    public boolean isVisible();

    public void click();
}

