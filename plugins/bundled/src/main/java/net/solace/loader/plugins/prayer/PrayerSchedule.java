package net.solace.loader.plugins.prayer;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.solace.api.domain.actors.INPC;

@Data
@AllArgsConstructor
public class PrayerSchedule {
    private PrayerNpc.Attack attack;
    private INPC npc;
    private int nextAttackTick;
}
