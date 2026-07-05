package net.solace.sdk.widgets;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.solace.api.Static;
import net.solace.api.domain.widgets.IWidget;
import net.solace.api.widgets.IWidgets;
import net.solace.api.widgets.InterfaceAddress;

public class Widgets {
    private static final IWidgets WIDGETS = Static.getWidgets();

    public static List<IWidget> getAll(int group) {
        return WIDGETS.getAll(group);
    }

    public static List<IWidget> getAll(int group, Predicate<? super IWidget> filter) {
        return WIDGETS.getAll(group).stream().filter(filter).collect(Collectors.toList());
    }

    public static IWidget get(int group, int id) {
        return WIDGETS.get(group, id);
    }

    public static IWidget get(int group, int id, int child) {
        return WIDGETS.get(group, id, child);
    }

    public static IWidget get(int component) {
        return WIDGETS.get(component);
    }

    @Deprecated(forRemoval=true)
    public static IWidget get(InterfaceAddress interfaceAddress) {
        return WIDGETS.get(interfaceAddress);
    }

    public static IWidget get(int group, Predicate<? super IWidget> filter) {
        return WIDGETS.get(group, filter);
    }

    public static IWidget getChild(int component, int child) {
        return WIDGETS.getChild(component, child);
    }

    public static boolean isVisible(IWidget widget) {
        return WIDGETS.isVisible(widget);
    }

    public static boolean isVisible(int group, int id) {
        return WIDGETS.isVisible(group, id);
    }

    public static boolean isVisible(int group, int id, int child) {
        return WIDGETS.isVisible(group, id, child);
    }

    public static boolean isVisible(int component) {
        return WIDGETS.isVisible(component);
    }

    @Deprecated(forRemoval=true)
    public static boolean isVisible(InterfaceAddress interfaceAddress) {
        return WIDGETS.isVisible(interfaceAddress);
    }

    public static List<IWidget> getChildren(IWidget widget, Predicate<IWidget> filter) {
        return WIDGETS.getChildren(widget, filter);
    }

    public static List<IWidget> getChildren(int group, int id, Predicate<IWidget> filter) {
        return WIDGETS.getChildren(group, id, filter);
    }

    public static List<IWidget> getChildren(int group, int id, int child, Predicate<IWidget> filter) {
        return WIDGETS.getChildren(group, id, child, filter);
    }

    public static List<IWidget> getChildren(int component, Predicate<IWidget> filter) {
        return WIDGETS.getChildren(component, filter);
    }

    @Deprecated(forRemoval=true)
    public static List<IWidget> getChildren(InterfaceAddress interfaceAddress, Predicate<IWidget> filter) {
        return WIDGETS.getChildren(interfaceAddress, filter);
    }

    public static void closeInterfaces() {
        WIDGETS.closeInterfaces();
    }
}

