package net.solace.sdk.quests;

import java.util.Objects;
import net.runelite.api.MenuAction;
import net.runelite.api.Quest;
import net.runelite.api.QuestState;
import net.runelite.api.widgets.Widget;
import net.solace.api.Static;
import net.solace.api.domain.widgets.IWidget;
import net.solace.api.quests.IQuests;
import net.solace.sdk.game.Client;
import net.solace.sdk.widgets.Widgets;

public class Quests {
    private static final IQuests QUESTS = Static.getQuests();

    public static QuestState getState(Quest quest) {
        return QUESTS.getState(quest);
    }

    public static boolean isFinished(Quest quest) {
        return QUESTS.isFinished(quest);
    }

    public static boolean isJournalOpen() {
        return Widgets.isVisible(Widgets.get(0x770003));
    }

    public static void openJournal(Quest quest) {
        if (Quests.isJournalOpen()) {
            return;
        }
        IWidget questContainer = Widgets.get(26148871);
        if (questContainer == null) {
            return;
        }
        for (Widget child : Objects.requireNonNull(questContainer.getChildren())) {
            if (!child.getText().equals(quest.getName())) continue;
            Client.interact(2, MenuAction.CC_OP.getId(), child.getIndex(), child.getParentId());
            break;
        }
    }
}

