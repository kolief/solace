package net.solace.sdk.game;

import java.util.List;
import net.runelite.api.Skill;
import net.solace.api.Static;
import net.solace.api.game.ISkills;

public class Skills {
    private static final ISkills SKILLS = Static.getSkills();

    public static int getBoostedLevel(Skill skill) {
        return SKILLS.getBoostedLevel(skill);
    }

    public static int getLevel(Skill skill) {
        return SKILLS.getLevel(skill);
    }

    public static int getExperience(Skill skill) {
        return SKILLS.getExperience(skill);
    }

    public static List<Skill> getReducedSkills() {
        return SKILLS.getReducedSkills();
    }
}

