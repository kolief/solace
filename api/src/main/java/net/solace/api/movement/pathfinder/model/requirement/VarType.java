package net.solace.api.movement.pathfinder.model.requirement;

import java.util.function.Function;
import net.solace.api.Static;

public enum VarType implements Function<Integer, Integer>
{
    VARBIT,
    VARP;


    @Override
    public Integer apply(Integer index) {
        switch (this) {
            case VARBIT: {
                return Static.getVars().getBit(index);
            }
            case VARP: {
                return Static.getVars().getVarp(index);
            }
        }
        return 0;
    }
}

