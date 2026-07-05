package net.solace.api.movement.pathfinder.model.requirement;

import net.runelite.api.Skill;
import net.solace.api.Static;
import net.solace.api.movement.pathfinder.model.requirement.Requirement;

public class CompositeSkillRequirement
implements Requirement {
    private final Skill[] skills;
    private final int totalRequired;

    public CompositeSkillRequirement(int totalRequired, Skill ... skills) {
        this.skills = skills;
        this.totalRequired = totalRequired;
    }

    @Override
    public Boolean get() {
        int total = 0;
        for (Skill skill : this.skills) {
            total += Static.getSkills().getLevel(skill);
        }
        return total >= this.totalRequired;
    }
}

