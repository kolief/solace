package net.solace.impl.items;

import lombok.RequiredArgsConstructor;
import net.runelite.api.VarClientInt;
import net.runelite.api.gameval.InterfaceID;
import net.solace.api.game.IVars;
import net.solace.api.items.GrandExchangeState;
import net.solace.api.items.IGrandExchange;
import net.solace.api.widgets.IWidgets;

@RequiredArgsConstructor
public class GrandExchangeImpl implements IGrandExchange {
    private final IWidgets widgets;
    private final IVars vars;

    public GrandExchangeState getState() {
        var setupWindow = widgets.get(InterfaceID.GeOffers.SETUP);
        if (widgets.isVisible(setupWindow)) {
            var text = setupWindow.getChild(20).getText();
            if (text == null || text.isEmpty()) {
                return GrandExchangeState.UNKNOWN;
            }

            if (text.equals("Sell offer")) {
                return GrandExchangeState.SELLING;
            }

            if (text.equals("Buy offer")) {
                return GrandExchangeState.BUYING;
            }

            // Widgets broke
            return GrandExchangeState.UNKNOWN;
        }

        var geWindow = widgets.get(InterfaceID.GeOffers.UNIVERSE);
        if (widgets.isVisible(geWindow)) {
            return GrandExchangeState.OFFERS;
        }

        return GrandExchangeState.CLOSED;
    }

    public boolean isOpen() {
        return getState() != GrandExchangeState.CLOSED && getState() != GrandExchangeState.UNKNOWN;
    }

    @Override
    public boolean isSearchingItem() {
        return vars.getVarcInt(VarClientInt.INPUT_TYPE) == 14;
    }
}
