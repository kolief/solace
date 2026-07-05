package net.solace.impl.items;

import lombok.RequiredArgsConstructor;
import net.runelite.api.gameval.InterfaceID;
import net.solace.api.commons.JagStrings;
import net.solace.api.domain.items.IItem;
import net.solace.api.domain.widgets.IWidget;
import net.solace.api.game.IVars;
import net.solace.api.interact.InteractManager;
import net.solace.api.interact.WidgetAction;
import net.solace.api.items.ITrade;
import net.solace.api.items.ITradeInventory;
import net.solace.api.items.ITradeOther;
import net.solace.api.items.ITradeOurs;
import net.solace.api.widgets.IDialog;
import net.solace.api.widgets.IWidgets;
import net.solace.api.widgets.WidgetGroup;

import java.util.List;
import java.util.function.Predicate;

import static net.solace.api.widgets.WidgetGroup.TradeScreen.SECOND_ACCEPT_FUNC;
import static net.solace.api.widgets.WidgetGroup.TradeScreen.SECOND_DECLINE_FUNC;

@RequiredArgsConstructor
public class TradeImpl implements ITrade {
    private static final int DUEL_OPPONENT_NAME = 357;
    private static final int INVENTORY_COMPONENT = 22020096;

    private final ITradeOurs tradeOurs;
    private final ITradeOther tradeOther;
    private final ITradeInventory tradeInventory;
    private final IDialog dialog;
    private final IWidgets widgets;
    private final IVars vars;
    private final InteractManager interactManager;

    @Override
    public boolean isSecondScreenOpen() {
        return widgets.isVisible(WidgetGroup.PLAYER_TRADE_CONFIRM_GROUP_ID, SECOND_ACCEPT_FUNC);
    }

    @Override
    public boolean isFirstScreenOpen() {
        return widgets.isVisible(InterfaceID.TRADEMAIN, 10);
    }

    @Override
    public void acceptFirstScreen() {
        var button = widgets.get(InterfaceID.TRADEMAIN, 10);
        if (widgets.isVisible(button)) {
            button.interact("Accept");
        }
    }

    @Override
    public void acceptSecondScreen() {
        var button = widgets.get(WidgetGroup.PLAYER_TRADE_CONFIRM_GROUP_ID, SECOND_ACCEPT_FUNC);
        if (widgets.isVisible(button)) {
            button.interact("Accept");
        }
    }

    @Override
    public void declineFirstScreen() {
        var button = widgets.get(InterfaceID.TRADEMAIN, 13);
        if (widgets.isVisible(button)) {
            button.interact("Decline");
        }
    }

    @Override
    public void declineSecondScreen() {
        var button = widgets.get(WidgetGroup.PLAYER_TRADE_CONFIRM_GROUP_ID, SECOND_DECLINE_FUNC);
        if (widgets.isVisible(button)) {
            button.interact("Decline");
        }
    }

    @Override
    public boolean hasAcceptedFirstScreen(boolean them) {
        var widget = widgets.get(InterfaceID.TRADEMAIN, 30);
        return widget != null && widget.getText().equals(them ? "Other player has accepted." : "Waiting for other player...");
    }

    @Override
    public boolean hasAcceptedSecondScreen(boolean them) {
        IWidget widget = widgets.get(WidgetGroup.PLAYER_TRADE_CONFIRM_GROUP_ID, 4);
        return widget != null && widget.getText().equals(them ? "Other player has accepted." : "Waiting for other player...");
    }

    @Override
    public void offer(Predicate<IItem> filter, int quantity, boolean quick) {
        List<IItem> items = getInventory(filter);
        if (items.isEmpty()) {
            return;
        }

        IItem item = items.stream().findFirst().orElse(null);
        int count = items.stream().mapToInt(IItem::getQuantity).sum();
        switch (quantity) {
            case 1:
                item.interact("Offer");
                break;
            case 5:
                item.interact("Offer-5");
                break;
            case 10:
                item.interact("Offer-10");
                break;
            default:
                if (quantity > count) {
                    item.interact("Offer-All");
                } else {
                    if (quick) {
                        interactManager.queue(new WidgetAction(5, INVENTORY_COMPONENT, item.getSlot(), item.getId()));
                        dialog.enterAmount(quantity);
                        return;
                    }

                    if (dialog.isOpen()) {
                        dialog.enterAmount(quantity);
                    } else {
                        item.interact("Offer-X");
                    }
                }
                break;
        }
    }

    @Override
    public List<IItem> getAll(boolean theirs, Predicate<? super IItem> filter) {
        return theirs ? tradeOther.getAll(filter) : tradeOurs.getAll(filter);
    }

    @Override
    public List<IItem> getInventory(Predicate<IItem> filter) {
        return tradeInventory.getAll(filter);
    }

    @Override
    public String getTradingPlayer() {
        return JagStrings.standardize(vars.getVarcStr(DUEL_OPPONENT_NAME));
    }
}
