package net.solace.api.movement.pathfinder.model.requirement;

import java.util.Arrays;
import net.runelite.api.Skill;
import net.solace.api.Static;
import net.solace.api.movement.pathfinder.model.MovementConstants;
import net.solace.api.movement.pathfinder.model.requirement.Requirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SkillRequirement
implements Requirement {
    private static final Logger log = LoggerFactory.getLogger(SkillRequirement.class);
    Skill skill;
    int level;
    boolean useBoosted;

    public SkillRequirement() {
        this.useBoosted = true;
    }

    public SkillRequirement(Skill skill, int level, boolean useBoosted) {
        this.skill = skill;
        this.level = level;
        this.useBoosted = useBoosted;
    }

    public SkillRequirement(Skill skill, int level) {
        this(skill, level, true);
    }

    @Override
    public Boolean get() {
        int skillLevel = this.useBoosted ? Static.getSkills().getBoostedLevel(this.skill) : Static.getSkills().getLevel(this.skill);
        return skillLevel >= this.level && (Static.getWorlds().inMembersWorld() || Arrays.stream(MovementConstants.MEMBER_SKILLS).noneMatch(x -> x.equals((Object)this.skill)));
    }

    public Skill getSkill() {
        return this.skill;
    }

    public int getLevel() {
        return this.level;
    }

    public boolean isUseBoosted() {
        return this.useBoosted;
    }
}

