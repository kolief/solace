package net.solace.impl.domain.game;

import lombok.RequiredArgsConstructor;
import net.runelite.api.events.VarbitChanged;
import net.runelite.client.eventbus.Subscribe;
import net.solace.api.domain.game.IClient;
import net.solace.api.game.IVars;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class VarsImpl implements IVars {
    private static final Map<Integer, Integer> VARBIT_CACHE = new HashMap<>();

    private final IClient client;

    @Override
    public int getBit(int id) {
        if (VARBIT_CACHE.containsKey(id)) {
            return VARBIT_CACHE.get(id);
        }

        var varbitValue = client.getVarbitValue(client.getVarps(), id);
        VARBIT_CACHE.put(id, varbitValue);
        return varbitValue;
    }

    @Override
    public void setBit(int id, int value) {
        client.getWrapped().setVarbit(id, value);
        VARBIT_CACHE.put(id, value);
    }

    @Override
    public int getVarp(int id) {
        return client.getVarpValue(id);
    }

    @Override
    public int getVarcInt(int varClientInt) {
        return client.getVarcIntValue(varClientInt);
    }

    @Override
    public String getVarcStr(int varClientStr) {
        return client.getVarcStrValue(varClientStr);
    }

    @Subscribe
    private void onVarbitChanged(VarbitChanged e) {
        int varbitId = e.getVarbitId();
        if (varbitId == -1) {
            return;
        }

        var cached = VARBIT_CACHE.get(varbitId);
        if (cached == null) {
            return;
        }

        var value = e.getValue();
        if (value == cached) {
            return;
        }

        VARBIT_CACHE.remove(varbitId);
    }
}
