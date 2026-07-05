package net.solace.sdk.script.paint;

import net.runelite.api.Skill;
import net.solace.sdk.game.Skills;

public final class ExperienceTracker {
    private final Skill skill;
    private final int startExp;
    private final int startLevel;

    public int getExperienceGained() {
        return Skills.getExperience(this.skill) - this.startExp;
    }

    public int getLevelsGained() {
        return Skills.getLevel(this.skill) - this.startLevel;
    }

    public ExperienceTracker(Skill skill, int startExp, int startLevel) {
        this.skill = skill;
        this.startExp = startExp;
        this.startLevel = startLevel;
    }

    public Skill getSkill() {
        return this.skill;
    }

    public int getStartExp() {
        return this.startExp;
    }

    public int getStartLevel() {
        return this.startLevel;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ExperienceTracker)) {
            return false;
        }
        ExperienceTracker other = (ExperienceTracker)o;
        if (this.getStartExp() != other.getStartExp()) {
            return false;
        }
        if (this.getStartLevel() != other.getStartLevel()) {
            return false;
        }
        Skill this$skill = this.getSkill();
        Skill other$skill = other.getSkill();
        return !(this$skill == null ? other$skill != null : !this$skill.equals(other$skill));
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getStartExp();
        result = result * 59 + this.getStartLevel();
        Skill $skill = this.getSkill();
        result = result * 59 + ($skill == null ? 43 : $skill.hashCode());
        return result;
    }

    public String toString() {
        return "ExperienceTracker(skill=" + String.valueOf(this.getSkill()) + ", startExp=" + this.getStartExp() + ", startLevel=" + this.getStartLevel() + ")";
    }
}

