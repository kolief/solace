package net.solace.impl.quests;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Quest;
import net.runelite.api.QuestState;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.Subscribe;
import net.solace.api.domain.game.IClient;
import net.solace.api.quests.IQuests;
import net.solace.impl.movement.WalkerManager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
@Slf4j
public class QuestsImpl implements IQuests {

    private Map<Quest, QuestState> questStates = new ConcurrentHashMap<>();

    private final IClient client;
    private final WalkerManager walkerManager;

    @Override
    public QuestState getState(Quest quest) {
        return questStates.getOrDefault(quest, QuestState.NOT_STARTED);
    }

    @Override
    public boolean isFinished(Quest quest) {
        return getState(quest) == QuestState.FINISHED;
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        var newStates = getQuestStates();

        if (newStates.equals(questStates)) {
            return;
        }

        log.debug("Quest states have changed, updating...");
        questStates = newStates;
        walkerManager.refresh();
    }

    private Map<Quest, QuestState> getQuestStates() {
        Map<Quest, QuestState> questStates = new ConcurrentHashMap<>();
        for (var quest : Quest.values()) {
            var state = quest.getState(client.getWrapped());
            questStates.put(quest, state);
        }
        return questStates;
    }
}
