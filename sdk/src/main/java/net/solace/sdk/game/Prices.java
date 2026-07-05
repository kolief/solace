package net.solace.sdk.game;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import net.solace.api.Static;
import net.solace.sdk.game.GameThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Prices {
    private static final Logger log = LoggerFactory.getLogger(Prices.class);
    private static final LoadingCache<Integer, Integer> CACHE = CacheBuilder.newBuilder().expireAfterWrite(5L, TimeUnit.MINUTES).build((CacheLoader)new CacheLoader<Integer, Integer>(){

        public Integer load(Integer itemId) {
            log.debug("Caching item {} price", (Object)itemId);
            return GameThread.invokeAndWait(() -> Static.getItemManager().getItemPrice(itemId.intValue()));
        }
    });

    public static int getItemPrice(int id) {
        try {
            return CACHE.get(id);
        }
        catch (ExecutionException e) {
            return -1;
        }
    }
}

