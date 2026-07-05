package net.solace.impl.domain.items;

import lombok.Getter;
import lombok.Setter;
import net.runelite.api.Item;
import net.runelite.api.ItemComposition;
import net.runelite.api.ItemContainer;
import net.runelite.api.MenuAction;
import net.runelite.api.coords.WorldPoint;
import net.solace.api.coords.Coordinate;
import net.solace.api.domain.game.IClient;
import net.solace.api.domain.items.IInventoryItem;
import net.solace.api.domain.items.IItem;
import net.solace.api.domain.widgets.IWidget;
import net.solace.api.interact.AutomatedMenu;
import net.solace.api.interact.InteractMethod;
import net.solace.api.magic.Spell;
import net.solace.api.util.Randomizer;
import net.solace.impl.domain.AbstractInteractable;
import net.solace.impl.util.RuneLiteWrapperUtil;

import java.util.Arrays;
import java.awt.Shape;

@Getter
public abstract class ItemImpl extends AbstractInteractable implements IItem {
    protected final IClient client;
    private final Item wrapped;
    private final int slot;
    private final ItemComposition composition;
    private final ItemContainer itemContainer;
    @Setter
    private IWidget widget;
    private IWidget childWidget;

    protected ItemImpl(Item wrapped, int slot, IWidget widget, IClient client, int inventoryID) {
        super(client);
        this.wrapped = wrapped;
        this.slot = slot;
        this.widget = widget;
        if (widget != null) {
            childWidget = widget.getChild(slot);
        }

        this.composition = client.getItemComposition(wrapped.getId());
        this.client = client;
        this.itemContainer = client.getItemContainer(inventoryID);
    }

    @Override
    public boolean isInteractable(WorldPoint from) {
        return true;
    }

    @Override
    public boolean isInteractable() {
        return true;
    }

    @Override
    public int getId() {
        return wrapped.getId();
    }

    @Override
    public int getQuantity() {
        return wrapped.getQuantity();
    }

    @Override
    public boolean isPlaceholder() {
        return composition.getPlaceholderTemplateId() != -1;
    }

    @Override
    public int getNotedId() {
        return composition.getLinkedNoteId();
    }

    @Override
    public boolean isNoted() {
        return composition.getNote() != -1;
    }

    @Override
    public boolean isStackable() {
        return composition.isStackable();
    }

    @Override
    public String getName() {
        return composition.getName();
    }

    @Override
    public String[] getActions() {
        if (childWidget != null) {
            return childWidget.getActions();
        }

        return widget.getActions();
    }

    @Override
    public int getActionIndex(String action) {
        if (getActions() == null) {
            return -1;
        }

        var index = Arrays.asList(getActions()).indexOf(action);

        if (index != -1) {
            return index;
        }

        var options = getSubOptions();
        if (options != null) {
            var subIndex = getSubOptionIndex(action);
            if (subIndex != null) {
                return getIdentifier(subIndex) - 1;
            }
        }

        return -1;
    }

    private int getIdentifier(int[] indexes) {
        if (indexes == null) {
            return -1;
        }

        var actionIndex = indexes[0];
        var subOpIndex = indexes[1];

        var actions = getActions();
        var inventoryActions = getComposition().getInventoryActions();

        if (actions == null || inventoryActions == null || actionIndex >= inventoryActions.length) {
            return -1;
        }

        var parentAction = inventoryActions[actionIndex];
        if (parentAction == null) {
            return -1;
        }

        var widgetIndex = Arrays.asList(actions).indexOf(parentAction);
        if (widgetIndex == -1) {
            return -1;
        }

        return ((subOpIndex + 1 << 16 | widgetIndex + 1));
    }

    @Override
    public String[][] getSubOptions() {
        return getComposition().getSubops();
    }

    @Override
    public boolean hasSubOption(String option) {
        return getSubOptionIndex(option) != null;
    }

    @Override
    public int[] getSubOptionIndex(String action) {
        var options = getSubOptions();
        if (options == null) {
            return null;
        }

        for (int i = 0; i < options.length; i++) {
            var subOptions = options[i];
            if (subOptions == null) {
                continue;
            }

            for (int j = 0; j < subOptions.length; j++) {
                var option = subOptions[j];
                if (option == null) {
                    continue;
                }

                if (option.equalsIgnoreCase(action)) {
                    return new int[]{i, j};
                }
            }
        }

        return null;
    }

    @Override
    public boolean isTradable() {
        return composition.isTradeable();
    }

    @Override
    public boolean isMembers() {
        return composition.isMembers();
    }

    @Override
    public Shape getClickArea() {
        if (childWidget != null) {
            return childWidget.getBounds();
        }

        if (widget != null) {
            return widget.getBounds();
        }

        return null;
    }

    @Override
    public Coordinate getClickPoint() {
        if (childWidget != null) {
            return Randomizer.getRandomPointIn(childWidget.getBounds());
        }

        return Randomizer.getRandomPointIn(widget.getBounds());
    }

    @Override
    public AutomatedMenu generateMenu(InteractMethod interactMethod, Spell spell) {
        if (this instanceof IInventoryItem) {
            return MENU_FACTORY.inventoryItem(getId(), getSlot())
                    .interactMethod(interactMethod)
                    .opcode(MenuAction.WIDGET_TARGET_ON_WIDGET)
                    .castSpell(spell)
                    .identifier(spell.getMenuIdentifier() != -1 ? spell.getMenuIdentifier() : 0)
                    .build(getClickPoint());
        }

        throw new UnsupportedOperationException("Cannot cast spell on non-inventory item");
    }

    @Override
    public AutomatedMenu generateMenu(InteractMethod interactMethod, IInventoryItem iInventoryItem) {
        if (this instanceof IInventoryItem) {
            return MENU_FACTORY.inventoryItem(getId(), getSlot())
                    .interactMethod(interactMethod)
                    .opcode(MenuAction.WIDGET_TARGET_ON_WIDGET)
                    .useItem(iInventoryItem.getId(), iInventoryItem.getSlot())
                    .build(getClickPoint());
        }

        throw new UnsupportedOperationException("Cannot use item on non-inventory item");
    }

    @Override
    public int hashCode() {
        return RuneLiteWrapperUtil.getHashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
        return RuneLiteWrapperUtil.isEqual(this, obj);
    }

    protected String getBankAction(int amount, Boolean withdraw) {
        var action = withdraw ? "Withdraw" : "Deposit";
        if (amount == 1) {
            action += "-1";
        } else if (amount == 5) {
            action += "-5";
        } else if (amount == 10) {
            action += "-10";
        } else if (withdrawDepositAll(withdraw, amount)) {
            action += "-All";
        } else {
            if (hasAction(action + "-" + amount)) {
                action += "-" + amount;
            } else {
                action += "-X";
            }
        }
        return action;
    }

    private boolean withdrawDepositAll(boolean withdraw, int amount) {
        return (withdraw && amount >= getQuantity()) || (!withdraw && amount >= getInvCount(getId())) || (amount == 0);
    }

    private int getInvCount(int itemId) {
        return itemContainer.count(itemId);
    }

    private String[] getRawActions() {
        return composition.getInventoryActions();
    }

}