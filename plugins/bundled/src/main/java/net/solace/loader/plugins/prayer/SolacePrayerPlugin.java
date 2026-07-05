package net.solace.loader.plugins.prayer;

import com.google.inject.Inject;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.Subscribe;
import net.solace.api.domain.actors.INPC;
import net.solace.api.domain.game.IClient;
import net.solace.api.events.AnimationChanged;
import net.solace.api.events.InteractingChanged;
import net.solace.api.interact.InteractMethod;
import net.solace.api.plugins.Plugin;
import net.solace.api.plugins.PluginDescriptor;
import net.solace.api.plugins.config.ConfigManager;
import net.solace.api.prayer.PrayerInfo;
import net.solace.sdk.entities.Players;
import net.solace.sdk.utils.MessageUtils;
import net.solace.sdk.widgets.Prayers;
import net.solace.sdk.widgets.Widgets;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@PluginDescriptor(name = "Solace Prayer")
@Slf4j
public class SolacePrayerPlugin extends Plugin {
    private static final Map<Integer, Weapon> ATTACK_ANIMATIONS = Map.of(
            390, Weapon.DRAGON_SCIMITAR,
            1892, Weapon.DRAGON_SCIMITAR,
            7552, Weapon.BONE_CROSSBOW,
            426, Weapon.MAGIC_SHORTBOW
    );

    private final Map<INPC, PrayerSchedule> schedules = new LinkedHashMap<>();
    private final List<INPC> unscheduleNextTick = new ArrayList<>();

    @Inject
    private SolacePrayerConfig config;

    @Inject
    private IClient client;

    private int lastAttack = -1;

    private static void togglePrayer(PrayerInfo prayer) {
        var widget = Widgets.get(prayer.getInterfaceAddress());
        if (widget != null) {
            widget.interact(InteractMethod.INVOKE, 0);
        }
    }

    @Subscribe
    private void onInteractingChanged(InteractingChanged event) {
        if (event.getTarget() == null) {
            return;
        }

        var local = Players.getLocal();
        if (!event.getTarget().equals(local) && !event.getSource().equals(local)) {
            return;
        }

        for (var prayerNpc : config.npcs()) {
            // Jad's ranged attacks are delayed so check for the animation instead
            if (prayerNpc.isJad()) {
                continue;
            }

            var source = event.getSource();
            if (source instanceof INPC && schedules.containsKey(source)) {
                continue;
            }

            for (var attack : prayerNpc.getAttacks()) {
                for (var npcId : attack.getNpcIds()) {
                    if (source instanceof INPC && npcId == ((INPC) source).getId() && config.turnOnIfTargeted()) {
                        schedules.put(((INPC) source), new PrayerSchedule(attack, ((INPC) source), client.getTickCount() + 1));
                    }

                    if (event.getTarget() instanceof INPC && npcId == ((INPC) event.getTarget()).getId() && config.turnOnIfTargeting()) {
                        var target = (INPC) event.getTarget();
                        schedules.put(target, new PrayerSchedule(attack, target, client.getTickCount() + 1));
                    }
                }
            }
        }
    }

    @Subscribe
    private void onAttack(AnimationChanged event) {
        var actor = event.getActor();
        if (actor == null
                || actor.getInteracting() == null
                || config.npcs().isEmpty()
        ) {
            return;
        }

        var animation = actor.getAnimation();
        if (animation == -1) {
            return;
        }

        var local = Players.getLocal();
        var currentTick = client.getTickCount();
        if (Objects.equals(actor, local)) {
            var weapon = ATTACK_ANIMATIONS.get(animation);
            if (weapon != null) {
                lastAttack = client.getTickCount();
            }
        }

        if (!actor.getInteracting().equals(local) && actor != local) {
            return;
        }

        if (!(actor instanceof INPC)) {
            return;
        }

        var npc = (INPC) actor;
        var schedule = schedules.get(npc);

        if (schedule != null) {
            var npcAttack = schedule.getAttack();

            if (npcAttack.getAnimations().contains(animation)
                    || (lastAttack == currentTick && schedule.getNextAttackTick() == currentTick)) {
                // Don't toggle off if it's jad, schedule upcoming attack instead
                if (npcAttack.isJad()) {
                    var delay = 3;
                    schedule.setNextAttackTick(currentTick + delay);
                    return;
                }

                var protectionPrayer = npcAttack.getProtectionPrayer();
                var nextAttack = currentTick + npcAttack.getSpeed();

                schedule.setNextAttackTick(nextAttack);
                debug("[{}] Scheduling {}'s next attack {} at {}", currentTick, ((INPC) actor).getId(), npcAttack.getAnimations(),
                        nextAttack);

                // Turn off
                if (config.turnOffAfterAttack() && Prayers.isEnabled(protectionPrayer.getPrayer())) {
                    if (isAttackScheduledNextTick()) {
                        debug("Attack scheduled in the next tick, not disabling pray");
                        return;
                    }

                    debug("Turning off {} after attack", protectionPrayer);
                    togglePrayer(protectionPrayer);
                }
            }
        } else {
            for (var prayerNpc : config.npcs()) {
                for (var attack : prayerNpc.getAttacks()) {
                    for (var npcId : attack.getNpcIds()) {
                        var nextAttack = currentTick + attack.getSpeed();
                        var protectionPrayer = attack.getProtectionPrayer().getPrayer();

                        if (attack.getAnimations().contains(animation) && npcId == ((INPC) actor).getId()) {
                            if (prayerNpc.isJad()) {
                                var delay = 3;
                                nextAttack = currentTick + delay;
                            }

                            // Schedule next attack
                            schedules.put(npc, new PrayerSchedule(attack, npc, nextAttack));
                            debug("Adding schedule for {} with {}'s next attack at {}", attack.getProtectionPrayer(), npc.getIndex(), nextAttack);

                            // Don't toggle on if it's jad because its attacks are delayed
                            if (config.turnOnIfTargeted() && !Prayers.isEnabled(protectionPrayer) && !prayerNpc.isJad()) {
                                debug("{} has animation, so we are enabling {}", npcId, protectionPrayer);
                                schedules.put(npc, new PrayerSchedule(attack, npc, currentTick + 1));
                            }
                        }
                    }
                }
            }
        }
    }

    // Handle scheduled attacks
    @Subscribe
    private void onGameTick(GameTick e) {
        var currentTick = client.getTickCount();

        var iterator = unscheduleNextTick.iterator();
        while (iterator.hasNext()) {
            var npc = iterator.next();
            iterator.remove();
            schedules.remove(npc);
        }

        var sortedSchedules = schedules.values().stream()
                .sorted((s1, s2) -> Long.compare(s2.getAttack().getPrio(), s1.getAttack().getPrio()))
                .collect(Collectors.toList());

        for (var schedule : sortedSchedules) {
            var attack = schedule.getAttack();
            var attackTick = schedule.getNextAttackTick();
            if (currentTick + 1 == attackTick) {
                if (attack.isJad()) {
                    unscheduleNextTick.add(schedule.getNpc());
                }
            }
        }

        for (var schedule : sortedSchedules) {
            var attack = schedule.getAttack();
            var attackTick = schedule.getNextAttackTick();

            // Toggle prayer on if next tick will be the attack tick
            if (currentTick + 1 == attackTick) {
                if (Prayers.isEnabled(attack.getProtectionPrayer().getPrayer())) {
                    debug("{}'s attack scheduled at {}, but {} is already on",
                            schedule.getNpc().getIndex(), attackTick,
                            attack.getProtectionPrayer());
                    continue;
                }

                debug("{} is about to attack, turning on {}",
                        schedule.getNpc().getIndex(), attack.getProtectionPrayer());
                togglePrayer(attack.getProtectionPrayer());
                return;
            }
        }
    }

    private boolean isAttackScheduledNextTick() {
        return schedules.values().stream().anyMatch(s -> s.getNextAttackTick() == client.getTickCount() + 1);
    }

    @Provides
    SolacePrayerConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(SolacePrayerConfig.class);
    }

    private void debug(String msg, Object... args) {
        if (config.debug()) {
            var prepend = String.format("[%d] ", client.getTickCount());
            var formatted = String.format(prepend + msg.replace("{}", "%s"), args);
            MessageUtils.addMessage(formatted);
        }
    }
}
