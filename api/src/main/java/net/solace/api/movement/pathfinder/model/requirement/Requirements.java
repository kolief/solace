package net.solace.api.movement.pathfinder.model.requirement;

import java.util.ArrayList;
import java.util.List;
import net.solace.api.movement.pathfinder.model.requirement.ItemRequirement;
import net.solace.api.movement.pathfinder.model.requirement.QuestRequirement;
import net.solace.api.movement.pathfinder.model.requirement.Requirement;
import net.solace.api.movement.pathfinder.model.requirement.SkillRequirement;
import net.solace.api.movement.pathfinder.model.requirement.VarRequirement;

public final class Requirements {
    private final List<ItemRequirement> itemRequirements = new ArrayList<ItemRequirement>();
    private final List<SkillRequirement> skillRequirements = new ArrayList<SkillRequirement>();
    private final List<VarRequirement> varRequirements = new ArrayList<VarRequirement>();
    private final List<QuestRequirement> questRequirements = new ArrayList<QuestRequirement>();
    private final List<Requirement> genericRequirements = new ArrayList<Requirement>();

    public static Requirements of(Requirement ... requirements) {
        Requirements reqs = new Requirements();
        for (Requirement requirement : requirements) {
            reqs.addRequirement(requirement);
        }
        return reqs;
    }

    public boolean fulfilled() {
        for (ItemRequirement itemRequirement : this.itemRequirements) {
            if (itemRequirement.get().booleanValue()) continue;
            return false;
        }
        for (SkillRequirement skillRequirement : this.skillRequirements) {
            if (skillRequirement.get().booleanValue()) continue;
            return false;
        }
        for (VarRequirement varRequirement : this.varRequirements) {
            if (varRequirement.get().booleanValue()) continue;
            return false;
        }
        for (QuestRequirement questRequirement : this.questRequirements) {
            if (questRequirement.get().booleanValue()) continue;
            return false;
        }
        for (Requirement requirement : this.genericRequirements) {
            if (((Boolean)requirement.get()).booleanValue()) continue;
            return false;
        }
        return true;
    }

    public Requirements addRequirement(Requirement requirement) {
        if (requirement instanceof ItemRequirement) {
            this.itemRequirements.add((ItemRequirement)requirement);
        } else if (requirement instanceof SkillRequirement) {
            this.skillRequirements.add((SkillRequirement)requirement);
        } else if (requirement instanceof VarRequirement) {
            this.varRequirements.add((VarRequirement)requirement);
        } else if (requirement instanceof QuestRequirement) {
            this.questRequirements.add((QuestRequirement)requirement);
        } else {
            this.genericRequirements.add(requirement);
        }
        return this;
    }

    public boolean isEmpty() {
        return this.itemRequirements.isEmpty() && this.skillRequirements.isEmpty() && this.varRequirements.isEmpty() && this.questRequirements.isEmpty() && this.genericRequirements.isEmpty();
    }

    public List<ItemRequirement> getItemRequirements() {
        return this.itemRequirements;
    }

    public List<SkillRequirement> getSkillRequirements() {
        return this.skillRequirements;
    }

    public List<VarRequirement> getVarRequirements() {
        return this.varRequirements;
    }

    public List<QuestRequirement> getQuestRequirements() {
        return this.questRequirements;
    }

    public List<Requirement> getGenericRequirements() {
        return this.genericRequirements;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Requirements)) {
            return false;
        }
        Requirements other = (Requirements)o;
        List<ItemRequirement> this$itemRequirements = this.getItemRequirements();
        List<ItemRequirement> other$itemRequirements = other.getItemRequirements();
        if (this$itemRequirements == null ? other$itemRequirements != null : !((Object)this$itemRequirements).equals(other$itemRequirements)) {
            return false;
        }
        List<SkillRequirement> this$skillRequirements = this.getSkillRequirements();
        List<SkillRequirement> other$skillRequirements = other.getSkillRequirements();
        if (this$skillRequirements == null ? other$skillRequirements != null : !((Object)this$skillRequirements).equals(other$skillRequirements)) {
            return false;
        }
        List<VarRequirement> this$varRequirements = this.getVarRequirements();
        List<VarRequirement> other$varRequirements = other.getVarRequirements();
        if (this$varRequirements == null ? other$varRequirements != null : !((Object)this$varRequirements).equals(other$varRequirements)) {
            return false;
        }
        List<QuestRequirement> this$questRequirements = this.getQuestRequirements();
        List<QuestRequirement> other$questRequirements = other.getQuestRequirements();
        if (this$questRequirements == null ? other$questRequirements != null : !((Object)this$questRequirements).equals(other$questRequirements)) {
            return false;
        }
        List<Requirement> this$genericRequirements = this.getGenericRequirements();
        List<Requirement> other$genericRequirements = other.getGenericRequirements();
        return !(this$genericRequirements == null ? other$genericRequirements != null : !((Object)this$genericRequirements).equals(other$genericRequirements));
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        List<ItemRequirement> $itemRequirements = this.getItemRequirements();
        result = result * 59 + ($itemRequirements == null ? 43 : ((Object)$itemRequirements).hashCode());
        List<SkillRequirement> $skillRequirements = this.getSkillRequirements();
        result = result * 59 + ($skillRequirements == null ? 43 : ((Object)$skillRequirements).hashCode());
        List<VarRequirement> $varRequirements = this.getVarRequirements();
        result = result * 59 + ($varRequirements == null ? 43 : ((Object)$varRequirements).hashCode());
        List<QuestRequirement> $questRequirements = this.getQuestRequirements();
        result = result * 59 + ($questRequirements == null ? 43 : ((Object)$questRequirements).hashCode());
        List<Requirement> $genericRequirements = this.getGenericRequirements();
        result = result * 59 + ($genericRequirements == null ? 43 : ((Object)$genericRequirements).hashCode());
        return result;
    }

    public String toString() {
        return "Requirements(itemRequirements=" + String.valueOf(this.getItemRequirements()) + ", skillRequirements=" + String.valueOf(this.getSkillRequirements()) + ", varRequirements=" + String.valueOf(this.getVarRequirements()) + ", questRequirements=" + String.valueOf(this.getQuestRequirements()) + ", genericRequirements=" + String.valueOf(this.getGenericRequirements()) + ")";
    }
}

