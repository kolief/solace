package net.solace.impl.widgets;

import lombok.RequiredArgsConstructor;
import net.runelite.api.GameState;
import net.runelite.api.VarClientInt;
import net.solace.api.domain.game.IClient;
import net.solace.api.game.IGame;
import net.solace.api.game.IVars;
import net.solace.api.widgets.ITabs;
import net.solace.api.widgets.Tab;

import java.util.Arrays;

@RequiredArgsConstructor
public class TabsImpl implements ITabs {
    private final IGame game;
    private final IClient client;
    private final IVars vars;

    @Override
    public void open(Tab tab) {
        if (game.getState() != GameState.LOGGED_IN) {
            return;
        }

        client.runScript(915, tab.getIndex());
    }

    @Override
    public boolean isOpen(Tab tab) {
        return vars.getVarcInt(VarClientInt.INVENTORY_TAB) == Arrays.asList(Tab.values()).indexOf(tab);
    }
}
