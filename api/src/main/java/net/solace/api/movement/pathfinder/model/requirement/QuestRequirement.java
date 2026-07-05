package net.solace.api.movement.pathfinder.model.requirement;

import java.util.Set;
import net.runelite.api.Quest;
import net.runelite.api.QuestState;
import net.solace.api.Static;
import net.solace.api.movement.pathfinder.model.requirement.Requirement;

public final class QuestRequirement
implements Requirement {
    private final Quest quest;
    private final Set<QuestState> states;

    public static QuestRequirement of(Quest quest, QuestState ... states) {
        return new QuestRequirement(quest, Set.of(states));
    }

    public static QuestRequirement finished(Quest quest) {
        return new QuestRequirement(quest, Set.of(QuestState.FINISHED));
    }

    @Override
    public Boolean get() {
        return this.states.contains(Static.getQuests().getState(this.quest));
    }

    public QuestRequirement(Quest quest, Set<QuestState> states) {
        this.quest = quest;
        this.states = states;
    }

    public Quest getQuest() {
        return this.quest;
    }

    public Set<QuestState> getStates() {
        return this.states;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof QuestRequirement)) {
            return false;
        }
        QuestRequirement other = (QuestRequirement)o;
        Quest this$quest = this.getQuest();
        Quest other$quest = other.getQuest();
        if (this$quest == null ? other$quest != null : !this$quest.equals(other$quest)) {
            return false;
        }
        Set<QuestState> this$states = this.getStates();
        Set<QuestState> other$states = other.getStates();
        return !(this$states == null ? other$states != null : !((Object)this$states).equals(other$states));
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Quest $quest = this.getQuest();
        result = result * 59 + ($quest == null ? 43 : $quest.hashCode());
        Set<QuestState> $states = this.getStates();
        result = result * 59 + ($states == null ? 43 : ((Object)$states).hashCode());
        return result;
    }

    public String toString() {
        return "QuestRequirement(quest=" + String.valueOf(this.getQuest()) + ", states=" + String.valueOf(this.getStates()) + ")";
    }
}

