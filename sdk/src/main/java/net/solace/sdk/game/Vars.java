package net.solace.sdk.game;

import net.solace.api.Static;
import net.solace.api.game.IVars;

public class Vars {
    private static final IVars VARS = Static.getVars();

    public static int getBit(int id) {
        return VARS.getBit(id);
    }

    public static void setBit(int id, int value) {
        VARS.setBit(id, value);
    }

    public static int getVarp(int id) {
        return VARS.getVarp(id);
    }

    public static int getVarcInt(int varClientInt) {
        return VARS.getVarcInt(varClientInt);
    }

    public static String getVarcStr(int varClientStr) {
        return VARS.getVarcStr(varClientStr);
    }
}

