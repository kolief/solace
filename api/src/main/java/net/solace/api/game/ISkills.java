package net.solace.api.game;

import java.util.List;
import net.runelite.api.Skill;

public interface ISkills {
    public int getBoostedLevel(Skill var1);

    public int getLevel(Skill var1);

    public List<Skill> getReducedSkills();

    public int getExperience(Skill var1);
}

