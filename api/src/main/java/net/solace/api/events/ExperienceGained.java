package net.solace.api.events;

import net.runelite.api.Skill;

public class ExperienceGained {
    private final Skill skill;
    private final int xpGained;
    private final int xp;
    private final int level;

    public ExperienceGained(Skill skill, int xpGained, int xp, int level) {
        this.skill = skill;
        this.xpGained = xpGained;
        this.xp = xp;
        this.level = level;
    }

    public Skill getSkill() {
        return this.skill;
    }

    public int getXpGained() {
        return this.xpGained;
    }

    public int getXp() {
        return this.xp;
    }

    public int getLevel() {
        return this.level;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ExperienceGained)) {
            return false;
        }
        ExperienceGained other = (ExperienceGained)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (this.getXpGained() != other.getXpGained()) {
            return false;
        }
        if (this.getXp() != other.getXp()) {
            return false;
        }
        if (this.getLevel() != other.getLevel()) {
            return false;
        }
        Skill this$skill = this.getSkill();
        Skill other$skill = other.getSkill();
        return !(this$skill == null ? other$skill != null : !this$skill.equals(other$skill));
    }

    protected boolean canEqual(Object other) {
        return other instanceof ExperienceGained;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getXpGained();
        result = result * 59 + this.getXp();
        result = result * 59 + this.getLevel();
        Skill $skill = this.getSkill();
        result = result * 59 + ($skill == null ? 43 : $skill.hashCode());
        return result;
    }

    public String toString() {
        return "ExperienceGained(skill=" + String.valueOf(this.getSkill()) + ", xpGained=" + this.getXpGained() + ", xp=" + this.getXp() + ", level=" + this.getLevel() + ")";
    }
}

