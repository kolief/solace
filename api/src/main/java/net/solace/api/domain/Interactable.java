package net.solace.api.domain;

import java.awt.Shape;
import java.util.Arrays;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.runelite.api.MenuAction;
import net.runelite.api.coords.WorldPoint;
import net.solace.api.commons.Predicates;
import net.solace.api.coords.Coordinate;
import net.solace.api.domain.Locatable;
import net.solace.api.domain.items.IInventoryItem;
import net.solace.api.interact.AutomatedMenu;
import net.solace.api.interact.InteractMethod;
import net.solace.api.magic.Spell;
import net.solace.api.util.Randomizer;

public interface Interactable {
    public boolean isInteractable();

    public boolean isInteractable(WorldPoint var1);

    default public boolean isInteractable(Locatable from) {
        return this.isInteractable(from.getWorldLocation());
    }

    @Nullable
    public String[] getActions();

    @Nullable
    public Shape getClickArea();

    @Nullable
    default public Coordinate getClickPoint() {
        return Randomizer.getRandomPointIn(this.getClickArea());
    }

    default public int getActionIndex(String action) {
        if (this.getActions() == null) {
            return -1;
        }
        return Arrays.asList(this.getActions()).indexOf(action);
    }

    default public boolean hasAction(Predicate<String> filter) {
        String[] raw = this.getActions();
        if (raw == null) {
            return false;
        }
        return Arrays.stream(raw).anyMatch(filter);
    }

    default public boolean hasAction(String ... actions) {
        return this.hasAction(Predicates.texts(actions));
    }

    default public boolean hasAction() {
        String[] raw = this.getActions();
        if (raw == null) {
            return false;
        }
        return Arrays.stream(raw).anyMatch(s -> s != null && !s.isEmpty());
    }

    public AutomatedMenu generateMenu(InteractMethod var1, int var2);

    public AutomatedMenu generateMenu(InteractMethod var1, MenuAction var2);

    public AutomatedMenu generateMenu(InteractMethod var1, Spell var2);

    public AutomatedMenu generateMenu(InteractMethod var1, IInventoryItem var2);

    default public AutomatedMenu generateMenu(InteractMethod interactMethod, String action) {
        return this.generateMenu(interactMethod, this.getActionIndex(action));
    }

    default public AutomatedMenu generateMenu(int actionIndex) {
        return this.generateMenu(null, actionIndex);
    }

    default public AutomatedMenu generateMenu(MenuAction opcode) {
        return this.generateMenu(null, opcode);
    }

    default public AutomatedMenu generateMenu(String action) {
        return this.generateMenu(null, action);
    }

    default public AutomatedMenu generateMenu(Spell spell) {
        return this.generateMenu(null, spell);
    }

    default public AutomatedMenu generateMenu(IInventoryItem item) {
        return this.generateMenu(null, item);
    }

    public void interact(AutomatedMenu var1);

    default public void interact(InteractMethod interactMethod, int index) {
        this.interact(this.generateMenu(interactMethod, index));
    }

    default public void interact(InteractMethod interactMethod, MenuAction opcode) {
        this.interact(this.generateMenu(interactMethod, opcode));
    }

    default public void interact(int index) {
        this.interact(null, index);
    }

    default public void interact(MenuAction opcode) {
        this.interact(null, opcode);
    }

    default public void interact(InteractMethod interactMethod, Predicate<String> predicate) {
        String[] raw = this.getActions();
        if (raw == null) {
            return;
        }
        for (int i = 0; i < raw.length; ++i) {
            if (!predicate.test(raw[i])) continue;
            this.interact(interactMethod, i);
            return;
        }
    }

    default public void interact(Predicate<String> predicate) {
        this.interact(null, predicate);
    }

    default public void interact(InteractMethod interactMethod, String action) {
        if (this.getActions() == null) {
            return;
        }
        int index = this.getActionIndex(action);
        if (index == -1) {
            return;
        }
        this.interact(interactMethod, index);
    }

    default public void interact(String action) {
        this.interact((InteractMethod)null, action);
    }

    default public void interact(InteractMethod interactMethod, String ... actions) {
        this.interact(interactMethod, Predicates.texts(actions));
    }

    default public void interact(String ... actions) {
        this.interact((InteractMethod)null, actions);
    }

    default public void interact() {
        this.interact(null, 0);
    }

    default public void interact(InteractMethod interactMethod, Spell spell) {
        this.interact(this.generateMenu(interactMethod, spell));
    }

    default public void interact(Spell spell) {
        this.interact(null, spell);
    }

    default public void interact(InteractMethod interactMethod, IInventoryItem item) {
        this.interact(this.generateMenu(interactMethod, item));
    }

    default public void interact(IInventoryItem item) {
        this.interact(null, item);
    }
}

