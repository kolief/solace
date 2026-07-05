package net.solace.api.query.entities;

import java.util.List;
import java.util.function.Supplier;
import net.solace.api.domain.Interactable;
import net.solace.api.domain.actors.IActor;
import net.solace.api.query.entities.SceneEntityQuery;
import org.apache.commons.lang3.ArrayUtils;

public abstract class ActorQuery<T extends IActor, Q extends ActorQuery<T, Q>>
extends SceneEntityQuery<T, Q> {
    private int[] levels = null;
    private int[] animations = null;
    private Interactable[] targeting = null;
    private Boolean moving = null;
    private Boolean targetless = null;
    private Boolean dead = null;

    protected ActorQuery(Supplier<List<T>> supplier) {
        super(supplier);
    }

    public Q levels(int ... levels) {
        this.levels = levels;
        return (Q)((ActorQuery)this.self());
    }

    public Q animations(int ... animations) {
        this.animations = animations;
        return (Q)((ActorQuery)this.self());
    }

    public Q targeting(Interactable ... targets) {
        this.targeting = targets;
        return (Q)((ActorQuery)this.self());
    }

    public Q targetless() {
        this.targetless = true;
        return (Q)((ActorQuery)this.self());
    }

    public Q moving(Boolean moving) {
        this.moving = moving;
        return (Q)((ActorQuery)this.self());
    }

    public Q dead(Boolean dead) {
        this.dead = dead;
        return (Q)((ActorQuery)this.self());
    }

    @Override
    public boolean test(T t) {
        if (this.levels != null && ArrayUtils.contains((int[])this.levels, (int)t.getCombatLevel())) {
            return false;
        }
        if (this.animations != null && ArrayUtils.contains((int[])this.animations, (int)t.getAnimation())) {
            return false;
        }
        if (this.moving != null && this.moving.booleanValue() != t.isMoving()) {
            return false;
        }
        if (this.targeting != null && !ArrayUtils.contains((Object[])this.targeting, (Object)t.getInteracting())) {
            return false;
        }
        if (this.targetless != null && t.getInteracting() != null) {
            return false;
        }
        if (this.dead != null && this.dead.booleanValue() != t.isDead()) {
            return false;
        }
        return super.test(t);
    }
}

