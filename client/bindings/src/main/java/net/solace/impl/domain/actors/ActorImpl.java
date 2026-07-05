package net.solace.impl.domain.actors;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Actor;
import net.runelite.api.ActorSpotAnim;
import net.runelite.api.IterableHashTable;
import net.runelite.api.Model;
import net.runelite.api.NPC;
import net.runelite.api.Node;
import net.runelite.api.Player;
import net.runelite.api.Point;
import net.runelite.api.SpritePixels;
import net.runelite.api.WorldView;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.solace.api.commons.Calculations;
import net.solace.api.commons.JagStrings;
import net.solace.api.coords.Coordinate;
import net.solace.api.domain.actors.IActor;
import net.solace.api.domain.game.IClient;
import net.solace.api.util.Randomizer;
import net.solace.impl.domain.AbstractInteractable;
import net.solace.impl.reflection.ReflectionManager;
import net.solace.impl.util.RuneLiteWrapperUtil;

import javax.annotation.Nullable;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.image.BufferedImage;

import static java.util.Optional.ofNullable;

@Slf4j
@Getter
public abstract class ActorImpl<W extends Actor> extends AbstractInteractable implements IActor {
    protected W wrapped;
    protected Actor wrappedInteracting;
    protected int healthRatio;
    protected int healthScale;
    protected WorldView worldView;
    protected WorldPoint worldLocation;


    public ActorImpl(W wrapped, IClient client) {
        super(client);
        this.wrapped = wrapped;
        this.wrappedInteracting = wrapped.getInteracting();
        this.healthRatio = wrapped.getHealthRatio();
        this.healthScale = wrapped.getHealthScale();
        this.worldView = wrapped.getWorldView();
        this.worldLocation = wrapped.getWorldLocation();
    }

    @Override
    public String getName() {
        return ofNullable(wrapped.getName())
                .map(JagStrings::standardize)
                .orElse(null);
    }

    @Override
    public int getCombatLevel() {
        return wrapped.getCombatLevel();
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
    public int getOrientation() {
        return wrapped.getOrientation();
    }

    @Override
    public int getCurrentOrientation() {
        return wrapped.getCurrentOrientation();
    }

    @Override
    public int getAnimation() {
        return wrapped.getAnimation();
    }

    @Override
    public void setAnimation(int var1) {
        wrapped.setAnimation(var1);
    }

    @Override
    public int getPoseAnimation() {
        return wrapped.getPoseAnimation();
    }

    @Override
    public void setPoseAnimation(int var1) {
        wrapped.setPoseAnimation(var1);
    }

    @Override
    public int getPoseAnimationFrame() {
        return wrapped.getPoseAnimationFrame();
    }

    @Override
    public void setPoseAnimationFrame(int var1) {
        wrapped.setPoseAnimationFrame(var1);
    }

    @Override
    public int getIdlePoseAnimation() {
        return wrapped.getIdlePoseAnimation();
    }

    @Override
    public void setIdlePoseAnimation(int var1) {
        wrapped.setIdlePoseAnimation(var1);
    }

    @Override
    public int getIdleRotateLeft() {
        return wrapped.getIdleRotateLeft();
    }

    @Override
    public void setIdleRotateLeft(int var1) {
        wrapped.setIdleRotateLeft(var1);
    }

    @Override
    public int getIdleRotateRight() {
        return wrapped.getIdleRotateRight();
    }

    @Override
    public void setIdleRotateRight(int var1) {
        wrapped.setIdleRotateRight(var1);
    }

    @Override
    public int getWalkAnimation() {
        return wrapped.getWalkAnimation();
    }

    @Override
    public void setWalkAnimation(int var1) {
        wrapped.setWalkAnimation(var1);
    }

    @Override
    public int getWalkRotateLeft() {
        return wrapped.getWalkRotateLeft();
    }

    @Override
    public void setWalkRotateLeft(int var1) {
        wrapped.setWalkRotateLeft(var1);
    }

    @Override
    public int getWalkRotateRight() {
        return wrapped.getWalkRotateRight();
    }

    @Override
    public void setWalkRotateRight(int var1) {
        wrapped.setWalkRotateRight(var1);
    }

    @Override
    public int getWalkRotate180() {
        return wrapped.getWalkRotate180();
    }

    @Override
    public void setWalkRotate180(int var1) {
        wrapped.setWalkRotate180(var1);
    }

    @Override
    public int getRunAnimation() {
        return wrapped.getRunAnimation();
    }

    @Override
    public void setRunAnimation(int var1) {
        wrapped.setRunAnimation(var1);
    }

    @Override
    public int getAnimationFrame() {
        return wrapped.getAnimationFrame();
    }

    @Override
    public void setAnimationFrame(int var1) {
        wrapped.setAnimationFrame(var1);
    }

    @Override
    public IterableHashTable<ActorSpotAnim> getSpotAnims() {
        return wrapped.getSpotAnims();
    }

    @Override
    public boolean hasSpotAnim(int var1) {
        return wrapped.hasSpotAnim(var1);
    }

    @Override
    public void createSpotAnim(int var1, int var2, int var3, int var4) {
        wrapped.createSpotAnim(var1, var2, var3, var4);
    }

    @Override
    public void removeSpotAnim(int var1) {
        wrapped.removeSpotAnim(var1);
    }

    @Override
    public void clearSpotAnims() {
        wrapped.clearSpotAnims();
    }

    @Override
    public Polygon getCanvasTilePoly() {
        return wrapped.getCanvasTilePoly();
    }

    @Override
    public int getFootprintSize() {
        return wrapped.getFootprintSize();
    }

    @Override
    public int getRenderMode() {
        return wrapped.getRenderMode();
    }

    @Nullable
    @Override
    public Point getCanvasTextLocation(Graphics2D var1, String var2, int var3) {
        return wrapped.getCanvasTextLocation(var1, var2, var3);
    }

    @Override
    public Point getCanvasImageLocation(BufferedImage var1, int var2) {
        return wrapped.getCanvasImageLocation(var1, var2);
    }

    @Override
    public Point getCanvasSpriteLocation(SpritePixels var1, int var2) {
        return wrapped.getCanvasSpriteLocation(var1, var2);
    }

    @Override
    public Point getMinimapLocation() {
        return wrapped.getMinimapLocation();
    }

    @Override
    public int getLogicalHeight() {
        return wrapped.getLogicalHeight();
    }

    @Override
    public WorldArea getWorldArea() {
        return wrapped.getWorldArea();
    }

    @Override
    public String getOverheadText() {
        return wrapped.getOverheadText();
    }

    @Override
    public void setOverheadText(String var1) {
        wrapped.setOverheadText(var1);
    }

    @Override
    public int getOverheadCycle() {
        return wrapped.getOverheadCycle();
    }

    @Override
    public void setOverheadCycle(int var1) {
        wrapped.setOverheadCycle(var1);
    }

    @Override
    public boolean isDead() {
        return wrapped.isDead();
    }

    @Override
    public void setDead(boolean var1) {
        wrapped.setDead(var1);
    }

    @Override
    public void attack() {
        interact("Attack");
    }

    @Override
    public boolean isAnimating() {
        return getAnimation() != -1;
    }

    @Override
    public boolean isIdle() {
        return (getIdlePoseAnimation() == getPoseAnimation() && getAnimation() == -1)
               && (getInteracting() == null || getInteracting().isDead());
    }

    @Override
    public boolean isHealthBarVisible() {
        return healthRatio != -1;
    }

    @Override
    public boolean isMoving() {
        int field = ReflectionManager.getField(wrapped, "Actor", "pathLength");
        return field > 0;
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
    public int getSpotAnimationCount() {
        return (int) getSpotAnims().spliterator().getExactSizeIfKnown();
    }

    @Override
    public IActor getTarget() {
        return getInteracting();
    }

    @Override
    public void setActionFrame(int frame) {
        wrapped.setActionFrame(frame);
    }

    @Override
    public int getGraphic() {
        return wrapped.getGraphic();
    }

    @Override
    public void setGraphic(int graphic) {
        wrapped.setGraphic(graphic);
    }

    @Override
    public int getGraphicHeight() {
        return wrapped.getGraphicHeight();
    }

    @Override
    public void setGraphicHeight(int height) {
        wrapped.setGraphicHeight(height);
    }

    @Override
    public int getSpotAnimFrame() {
        return wrapped.getSpotAnimFrame();
    }

    @Override
    public void setSpotAnimFrame(int spotAnimFrame) {
        wrapped.setSpotAnimFrame(spotAnimFrame);
    }

    @Override
    public LocalPoint getCameraFocus() {
        return wrapped.getCameraFocus();
    }

    @Override
    public boolean isInteracting() {
        return wrapped.isInteracting();
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
    public int getPlane() {
        return worldView.getPlane();
    }

    @Override
    public Coordinate getClickPoint() {
        return Randomizer.getRandomPointIn(getConvexHull());
    }

    @Override
    public boolean isInteractable() {
        return isInteractable(client.getLocalPlayer());
    }

    @Override
    public IActor getInteracting() {
        if (wrappedInteracting == null) {
            return null;
        }

        int index;
        if (wrappedInteracting instanceof Player) {
            index = ((Player) wrappedInteracting).getId();
            return client.getPlayerContainer().get(index);
        } else {
            index = ((NPC) wrappedInteracting).getIndex();
            return client.getNpcContainer().get(index);
        }
    }

    @Override
    public int getAnimationHeightOffset() {
        return wrapped.getAnimationHeightOffset();
    }

    @Override
    public WorldView getWorldView() {
        return client.isClientThread() ? wrapped.getWorldView() : worldView;
    }

    @Override
    public Shape getClickArea() {
        return getConvexHull();
    }

    @Override
    public Shape getConvexHull() {
        if (!client.isClientThread()) {
            log.warn("Convex hull is not available on non-client thread!");
            return null;
        }

        return wrapped.getConvexHull();
    }

    @Override
    public WorldPoint getWorldLocation() {
        return client.isClientThread() ? wrapped.getWorldLocation() : worldLocation;
    }

    @Override
    public int getHealthRatio() {
        return client.isClientThread() ? wrapped.getHealthRatio() : healthRatio;
    }

    @Override
    public int getHealthScale() {
        return client.isClientThread() ? wrapped.getHealthScale() : healthScale;
    }
}
