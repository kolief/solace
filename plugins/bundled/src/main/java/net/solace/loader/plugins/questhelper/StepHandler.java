package net.solace.loader.plugins.questhelper;

import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.GameObject;
import net.runelite.api.NPC;
import net.runelite.api.TileObject;
import net.runelite.api.coords.WorldPoint;
import net.solace.api.domain.Interactable;
import net.solace.api.domain.game.IClient;
import net.solace.api.domain.tiles.IGameObject;
import net.solace.sdk.entities.NPCs;
import net.solace.sdk.entities.Players;
import net.solace.sdk.entities.TileObjects;
import net.solace.sdk.interact.builder.MenuFactory;

import javax.inject.Inject;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

import static net.solace.loader.plugins.questhelper.util.ReflectionBridge.getDefinedPoint;
import static net.solace.loader.plugins.questhelper.util.ReflectionBridge.getWorldPointFromDefined;
import static net.solace.loader.plugins.questhelper.util.ReflectionBridge.isDetailedQuestStep;
import static net.solace.loader.plugins.questhelper.util.ReflectionBridge.isNpcStep;
import static net.solace.loader.plugins.questhelper.util.ReflectionBridge.isObjectStep;

@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
@Slf4j
public class StepHandler {
    private final QuesterUtils questerUtils;
    private final SolaceQuestHelperConfig config;
    private final IClient client;

    public void handleQuestArrow(WorldPoint arrowPoint) {
        if (arrowPoint == null) {
            return;
        }

        if (config.talkNpcs() && !Players.getLocal().isInteracting()) {
            var npc = NPCs.getNearest(n -> n.getWorldLocation().equals(arrowPoint));
            if (npc != null && npc.isInteractable()) {
                questerUtils.resetExplorerDestination();
                npc.interact("Talk-to");
                return;
            }
        }

        if (config.interactObjects()) {
            var object = TileObjects.getFirstAt(arrowPoint, Interactable::hasAction);

            if (object == null) {
                var gameObjects = TileObjects.getAll(x -> x instanceof IGameObject && x.hasAction()).stream().map(IGameObject.class::cast)
                        .collect(Collectors.toList());

                if (!gameObjects.isEmpty()) {
                    object = gameObjects.stream().filter(x -> x.getWorldArea().contains(arrowPoint)).findFirst().orElse(null);
                }
            }

            if (object != null && object.isInteractable()) {
                questerUtils.resetExplorerDestination();
                object.interact(0);
                return;
            }
        }

        if (config.autoWalk()) {
            questerUtils.setExplorerDestination(arrowPoint);
        }
    }

    public void handleQuestStep(Object questStep) {
        if (questStep == null) {
            return;
        }

        var questStepClass = questStep.getClass();
        var local = Players.getLocal();
        if (config.interactObjects() && isObjectStep(questStepClass)) {
            TileObject tileObject = getField(questStepClass, "closestObject", questStep);
            if (tileObject == null) {
                log.warn("ObjectStep closestObject is null");
            } else {
                questerUtils.resetExplorerDestination();
                if (tileObject instanceof GameObject) {
                    var gameObject = (GameObject) tileObject;
                    var sceneMinLocation = gameObject.getSceneMinLocation();
                    MenuFactory.tileObject(gameObject.getId(), sceneMinLocation.getX(), sceneMinLocation.getY())
                            .actionIndex(0)
                            .build(null)
                            .queue(client);
                }
                return;
            }
        } else if (config.talkNpcs() && isNpcStep(questStepClass)) {
            ArrayList<NPC> npcs = getField(questStepClass, "npcs", questStep);
            if (npcs != null && !npcs.isEmpty()) {
                questerUtils.resetExplorerDestination();
                var npc = npcs.stream()
                        .min(Comparator.comparingInt(x -> x.getWorldLocation().distanceTo(local.getWorldLocation())))
                        .orElse(null);
                if (npc != null) {
                    MenuFactory.npc(npc.getIndex())
                            .actionIndex(0)
                            .build(null)
                            .queue(client);
                    return;
                }
            } else {
                log.warn("NpcStep npcs is null or empty");
            }
        }

        if (config.autoWalk() && (isDetailedQuestStep(questStepClass) || isNpcStep(questStepClass) || isObjectStep(questStepClass))) {
            var questHelperArrowPoint = getDefinedPoint(questStep);
            if (questHelperArrowPoint != null) {
                var wp = getWorldPointFromDefined(questHelperArrowPoint);
                if (wp != null && wp.distanceTo(local.getWorldLocation()) > 5) {
                    questerUtils.setExplorerDestination(wp);
                }
            }
        }
    }

    private static <T> T getField(Class<?> clazz, String name, Object object) {
        try {
            Field field = clazz.getDeclaredField(name);
            field.setAccessible(true);
            return (T) field.get(object);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            log.error("Unable to find/access field: " + name, e);
            return null;
        }
    }
}