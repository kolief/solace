package net.solace.loader.plugins.questhelper.util;

import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.Plugin;

import java.awt.Color;
import java.lang.reflect.InvocationTargetException;

public class ReflectionBridge {
    public static boolean isConditionalStep(Class<?> clazz) {
        return clazz.getSimpleName().endsWith("ConditionalStep");
    }

    public static boolean isNpcStep(Class<?> clazz) {
        return clazz.getSimpleName().endsWith("NpcStep");
    }

    public static boolean isObjectStep(Class<?> clazz) {
        return clazz.getSimpleName().endsWith("ObjectStep");
    }

    public static boolean isDetailedQuestStep(Class<?> clazz) {
        return clazz.getSimpleName().endsWith("DetailedQuestStep");
    }

    public static boolean isQuestHelperPlugin(Plugin plugin) {
        return plugin.getClass().getSimpleName().endsWith("QuestHelperPlugin");
    }

    public static Object getSelectedQuest(Object questHelperPlugin) {
        return invokeMethod(questHelperPlugin, "getSelectedQuest");
    }

    public static Object getCurrentStep(Object questHelper) {
        return invokeMethod(questHelper, "getCurrentStep");
    }

    public static Object getActiveStep(Object conditionalStep) {
        return invokeMethod(conditionalStep, "getActiveStep");
    }

    public static Object getDefinedPoint(Object detailedQuestStep) {
        return invokeMethod(detailedQuestStep, "getDefinedPoint");
    }

    public static WorldPoint getWorldPointFromDefined(Object definedPoint) {
        return invokeMethod(definedPoint, "getWorldPoint");
    }

    public static Object getConfig(Object questHelperPlugin) {
        return invokeMethod(questHelperPlugin, "getConfig");
    }

    public static Color getTextHighlightColor(Object config) {
        return invokeMethod(config, "textHighlightColor");
    }

    @SuppressWarnings("unchecked")
    private static <T> T invokeMethod(Object inst, String methodName) {
        try {
            var method = inst.getClass().getMethod(methodName);
            return (T) method.invoke(inst);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
