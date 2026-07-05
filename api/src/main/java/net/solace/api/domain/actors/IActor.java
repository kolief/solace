package net.solace.api.domain.actors;

import net.runelite.api.Actor;
import net.solace.api.domain.RuneLiteWrapper;
import net.solace.api.domain.SceneEntity;

public interface IActor
extends Actor,
SceneEntity,
RuneLiteWrapper<Actor> {
    public int getIndex();

    public void attack();

    public boolean isAnimating();

    public boolean isIdle();

    public boolean isHealthBarVisible();

    public boolean isMoving();

    public int getSpotAnimationCount();

    public IActor getTarget();

    public IActor getInteracting();
}

