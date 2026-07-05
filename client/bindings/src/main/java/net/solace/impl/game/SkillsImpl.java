package net.solace.impl.game;

import lombok.RequiredArgsConstructor;
import net.runelite.api.Skill;
import net.solace.api.domain.game.IClient;
import net.solace.api.game.ISkills;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class SkillsImpl implements ISkills {
    private final IClient client;

    @Override
    public int getBoostedLevel(Skill skill) {
        return client.getBoostedSkillLevel(skill);
    }

    @Override
    public int getLevel(Skill skill) {
        return client.getRealSkillLevel(skill);
    }

    @Override
    public int getExperience(Skill skill) {
        return client.getSkillExperience(skill);
    }

    @Override
    public List<Skill> getReducedSkills() {
        return Arrays.stream(Skill.values())
                .filter(skill -> getBoostedLevel(skill) < getLevel(skill))
                .collect(Collectors.toList());
    }
}
