package net.solace.impl.breaks;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.solace.api.breaks.BreakHandler;
import net.solace.api.commons.IntRandomNumberGenerator;
import net.solace.api.events.ConfigChanged;
import net.solace.api.plugins.Plugin;
import net.solace.api.plugins.config.ConfigManager;
import org.apache.commons.lang3.tuple.Pair;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

@RequiredArgsConstructor
public class BreakHandlerImpl implements BreakHandler {
    private final ConfigManager configManager;

    @Getter
    public final PublishSubject<ConfigChanged> configChanged = PublishSubject.create();
    @Getter
    private final Map<Plugin, Boolean> plugins = new TreeMap<>((o1, o2) -> o1.getName().compareToIgnoreCase(o2.getName()));
    private final PublishSubject<Map<Plugin, Boolean>> pluginsSubject = PublishSubject.create();
    @Getter
    private final Set<Plugin> activePlugins = new HashSet<>();
    private final PublishSubject<Set<Plugin>> activeSubject = PublishSubject.create();
    @Getter
    private final Map<Plugin, Instant> plannedBreaks = new HashMap<>();
    private final PublishSubject<Map<Plugin, Instant>> plannedBreaksSubject = PublishSubject.create();
    @Getter
    private final Map<Plugin, Instant> activeBreaks = new HashMap<>();
    private final PublishSubject<Map<Plugin, Instant>> activeBreaksSubject = PublishSubject.create();
    private final PublishSubject<Pair<Plugin, Instant>> currentActiveBreaksSubject = PublishSubject.create();
    @Getter
    private final Map<Plugin, Instant> startTimes = new HashMap<>();
    @Getter
    private final Map<Plugin, Integer> amountOfBreaks = new HashMap<>();
    private final PublishSubject<Plugin> logoutActionSubject = PublishSubject.create();
    private final Map<Plugin, Map<String, String>> extraData = new HashMap<>();
    private final PublishSubject<Map<Plugin, Map<String, String>>> extraDataSubject = PublishSubject.create();

    @Override
    public void registerPlugin(Plugin plugin) {
        registerPlugin(plugin, true);
    }


    @Override
    public void registerPlugin(Plugin plugin, boolean configurable) {
        plugins.put(plugin, configurable);
        pluginsSubject.onNext(plugins);
    }

    @Override
    public void unregisterPlugin(Plugin plugin) {
        plugins.remove(plugin);
        pluginsSubject.onNext(plugins);
    }

    @Override
    public @NonNull Observable<Map<Plugin, Boolean>> getPluginObservable() {
        return pluginsSubject.hide();
    }

    @Override
    public void startPlugin(Plugin plugin) {
        activePlugins.add(plugin);
        activeSubject.onNext(activePlugins);

        startTimes.put(plugin, Instant.now());
        amountOfBreaks.put(plugin, 0);
    }

    @Override
    public void stopPlugin(Plugin plugin) {
        activePlugins.remove(plugin);
        activeSubject.onNext(activePlugins);

        removePlannedBreak(plugin);
        stopBreak(plugin);

        startTimes.remove(plugin);
        amountOfBreaks.remove(plugin);
    }

    @Override
    public @NonNull Observable<Set<Plugin>> getActiveObservable() {
        return activeSubject.hide();
    }

    @Override
    public void planBreak(Plugin plugin, Instant instant) {
        plannedBreaks.put(plugin, instant);
        plannedBreaksSubject.onNext(plannedBreaks);
    }

    @Override
    public void removePlannedBreak(Plugin plugin) {
        plannedBreaks.remove(plugin);
        plannedBreaksSubject.onNext(plannedBreaks);
    }

    @Override
    public @NonNull Observable<Map<Plugin, Instant>> getPlannedBreaksObservable() {
        return plannedBreaksSubject.hide();
    }

    @Override
    public boolean isBreakPlanned(Plugin plugin) {
        return plannedBreaks.containsKey(plugin);
    }

    @Override
    public Instant getPlannedBreak(Plugin plugin) {
        return plannedBreaks.get(plugin);
    }

    @Override
    public boolean shouldBreak(Plugin plugin) {
        if (!plannedBreaks.containsKey(plugin)) {
            return false;
        }

        return Instant.now().isAfter(getPlannedBreak(plugin));
    }

    @Override
    public void startBreak(Plugin plugin) {
        int from = Integer.parseInt(configManager.getConfiguration(CONFIG_GROUP, BreakHandler.sanitizedName(plugin) + "-breakfrom")) * 60;
        int to = Integer.parseInt(configManager.getConfiguration(CONFIG_GROUP, BreakHandler.sanitizedName(plugin) + "-breakto")) * 60;

        int random = new IntRandomNumberGenerator(from, to).nextInt();

        removePlannedBreak(plugin);

        Instant breakUntil = Instant.now().plus(random, ChronoUnit.SECONDS);

        activeBreaks.put(plugin, breakUntil);
        activeBreaksSubject.onNext(activeBreaks);

        currentActiveBreaksSubject.onNext(Pair.of(plugin, breakUntil));

        if (amountOfBreaks.containsKey(plugin)) {
            amountOfBreaks.put(plugin, amountOfBreaks.get(plugin) + 1);
        } else {
            amountOfBreaks.put(plugin, 1);
        }
    }

    @Override
    public void startBreak(Plugin plugin, Instant instant) {
        removePlannedBreak(plugin);

        activeBreaks.put(plugin, instant);
        activeBreaksSubject.onNext(activeBreaks);

        currentActiveBreaksSubject.onNext(Pair.of(plugin, instant));

        if (amountOfBreaks.containsKey(plugin)) {
            amountOfBreaks.put(plugin, amountOfBreaks.get(plugin) + 1);
        } else {
            amountOfBreaks.put(plugin, 1);
        }
    }

    @Override
    public void stopBreak(Plugin plugin) {
        activeBreaks.remove(plugin);
        activeBreaksSubject.onNext(activeBreaks);
    }

    @Override
    public void setExtraData(Plugin plugin, String key, String value) {
        extraData.putIfAbsent(plugin, new LinkedHashMap<>());
        extraData.get(plugin).put(key, value);

        extraDataSubject.onNext(extraData);
    }

    @Override
    public void setExtraData(Plugin plugin, Map<String, String> data) {
        extraData.putIfAbsent(plugin, new LinkedHashMap<>());

        data.forEach(
                (key, value) -> extraData.get(plugin).merge(key, value, (existingData, newData) -> newData)
        );

        extraDataSubject.onNext(extraData);
    }

    @Override
    public void removeExtraData(Plugin plugin, String key) {
        if (!extraData.containsKey(plugin)) {
            return;
        }

        extraData.get(plugin).remove(key);
        extraDataSubject.onNext(extraData);
    }

    @Override
    public void resetExtraData(Plugin plugin) {
        extraData.remove(plugin);
        extraDataSubject.onNext(extraData);
    }

    @Override
    public @NonNull Observable<Map<Plugin, Map<String, String>>> getExtraDataObservable() {
        return extraDataSubject.hide();
    }

    @Override
    public @NonNull Observable<Map<Plugin, Instant>> getActiveBreaksObservable() {
        return activeBreaksSubject.hide();
    }

    @Override
    public @NonNull Observable<Pair<Plugin, Instant>> getCurrentActiveBreaksObservable() {
        return currentActiveBreaksSubject.hide();
    }

    @Override
    public boolean isBreakActive() {
        return !activeBreaks.isEmpty();
    }

    @Override
    public boolean isBreakActive(Plugin plugin) {
        return activeBreaks.containsKey(plugin);
    }

    @Override
    public Instant getActiveBreak(Plugin plugin) {
        return activeBreaks.get(plugin);
    }

    @Override
    public void logoutNow(Plugin plugin) {
        logoutActionSubject.onNext(plugin);
    }

    @Override
    public @NonNull Observable<Plugin> getlogoutActionObservable() {
        return logoutActionSubject.hide();
    }

    @Override
    public int getTotalAmountOfBreaks() {
        return amountOfBreaks.values().stream().mapToInt(Integer::intValue).sum();
    }
}