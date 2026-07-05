package net.solace.api.quests;

import net.runelite.api.Quest;
import net.runelite.api.QuestState;

public interface IQuests {
    public QuestState getState(Quest var1);

    public boolean isFinished(Quest var1);
}

