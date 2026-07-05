package net.solace.api.query.entities;

import java.util.List;
import java.util.function.Supplier;
import net.solace.api.domain.actors.INPC;
import net.solace.api.query.entities.ActorQuery;
import net.solace.api.query.results.SceneEntityQueryResults;
import org.apache.commons.lang3.ArrayUtils;

public class NPCQuery
extends ActorQuery<INPC, NPCQuery> {
    private int[] indices = null;

    public NPCQuery(Supplier<List<INPC>> supplier) {
        super(supplier);
    }

    public NPCQuery indices(int ... indices) {
        this.indices = indices;
        return this;
    }

    @Override
    protected SceneEntityQueryResults<INPC> results(List<INPC> list) {
        return new SceneEntityQueryResults<INPC>(list);
    }

    @Override
    public boolean test(INPC npc) {
        if (this.indices != null && !ArrayUtils.contains((int[])this.indices, (int)npc.getIndex())) {
            return false;
        }
        return super.test(npc);
    }
}

