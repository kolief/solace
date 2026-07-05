package net.solace.api.breaks;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import java.time.Instant;
import java.util.Map;
import java.util.Set;
import net.solace.api.events.ConfigChanged;
import net.solace.api.plugins.Plugin;
import org.apache.commons.lang3.tuple.Pair;

public interface BreakHandler {
    public static final String CONFIG_GROUP = "solacebreakhandler";

    public void registerPlugin(Plugin var1, boolean var2);

    public void registerPlugin(Plugin var1);

    public void unregisterPlugin(Plugin var1);

    public void startPlugin(Plugin var1);

    public void stopPlugin(Plugin var1);

    public boolean isBreakActive(Plugin var1);

    public boolean isBreakActive();

    public boolean shouldBreak(Plugin var1);

    public void startBreak(Plugin var1);

    public Observable<Pair<Plugin, Instant>> getCurrentActiveBreaksObservable();

    public Observable<Set<Plugin>> getActiveObservable();

    public Observable<Plugin> getlogoutActionObservable();

    public PublishSubject<ConfigChanged> getConfigChanged();

    public Observable<Map<Plugin, Boolean>> getPluginObservable();

    public Observable<Map<Plugin, Instant>> getActiveBreaksObservable();

    public Observable<Map<Plugin, Map<String, String>>> getExtraDataObservable();

    public Map<Plugin, Boolean> getPlugins();

    public Set<Plugin> getActivePlugins();

    public Map<Plugin, Instant> getStartTimes();

    public Map<Plugin, Integer> getAmountOfBreaks();

    public boolean isBreakPlanned(Plugin var1);

    public Instant getPlannedBreak(Plugin var1);

    public Instant getActiveBreak(Plugin var1);

    public void planBreak(Plugin var1, Instant var2);

    public void removePlannedBreak(Plugin var1);

    public Observable<Map<Plugin, Instant>> getPlannedBreaksObservable();

    public void startBreak(Plugin var1, Instant var2);

    public void stopBreak(Plugin var1);

    public void setExtraData(Plugin var1, String var2, String var3);

    public void setExtraData(Plugin var1, Map<String, String> var2);

    public void removeExtraData(Plugin var1, String var2);

    public void resetExtraData(Plugin var1);

    public void logoutNow(Plugin var1);

    public int getTotalAmountOfBreaks();

    public Map<Plugin, Instant> getActiveBreaks();

    public static String sanitizedName(Plugin plugin) {
        return plugin.getName().toLowerCase().replace(" ", "");
    }
}

