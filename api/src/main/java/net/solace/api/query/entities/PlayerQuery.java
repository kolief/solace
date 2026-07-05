package net.solace.api.query.entities;

import java.util.List;
import java.util.function.Supplier;
import net.solace.api.domain.actors.IPlayer;
import net.solace.api.query.entities.ActorQuery;
import net.solace.api.query.results.SceneEntityQueryResults;
import org.apache.commons.lang3.ArrayUtils;

public class PlayerQuery
extends ActorQuery<IPlayer, PlayerQuery> {
    private int[] pids = null;

    public PlayerQuery(Supplier<List<IPlayer>> supplier) {
        super(supplier);
    }

    public PlayerQuery playerIds(int ... pids) {
        this.pids = pids;
        return this;
    }

    @Override
    protected SceneEntityQueryResults<IPlayer> results(List<IPlayer> list) {
        return new SceneEntityQueryResults<IPlayer>(list);
    }

    @Override
    public boolean test(IPlayer player) {
        if (this.pids != null && !ArrayUtils.contains((int[])this.pids, (int)player.getIndex())) {
            return false;
        }
        return super.test(player);
    }
}

