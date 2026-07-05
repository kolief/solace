package net.solace.impl.widgets;

import lombok.RequiredArgsConstructor;
import net.solace.api.domain.game.IClient;
import net.solace.api.domain.widgets.IWidget;
import net.solace.api.widgets.IWidgets;
import net.solace.api.widgets.InterfaceAddress;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class WidgetsImpl implements IWidgets {
    private final IClient client;

    @Override
    public List<IWidget> getAll(int group) {
        var widgets = client.getWidgets(group);
        if (widgets == null) {
            return Collections.emptyList();
        }

        return Arrays.asList(widgets);
    }

    @Override
    public IWidget get(int group, int id) {
        return client.getWidget(group, id);
    }

    @Override
    public IWidget getChild(int component, int child) {
        int group = component >> 16;
        int id = component & 0xFFFF;
        return get(group, id, child);
    }

    @Override
    public IWidget get(int group, int id, int child) {
        var widget = get(group, id);
        if (widget == null) {
            return null;
        }

        return widget.getChild(child);
    }

    @Override
    public IWidget get(int component) {
        return client.getWidget(component);
    }

    @Override
    public IWidget get(InterfaceAddress interfaceAddress) {
        return get(interfaceAddress.getGroup(), interfaceAddress.getChild());
    }

    @Override
    public IWidget get(int group, Predicate<? super IWidget> filter) {
        return getAll(group).stream()
                .filter(filter)
                .findFirst()
                .orElse(null);
    }

    @Override
    public boolean isVisible(IWidget widget) {
        return widget != null && !widget.isHidden();
    }

    @Override
    public boolean isVisible(int group, int id) {
        return isVisible(get(group, id));
    }

    @Override
    public boolean isVisible(int group, int id, int child) {
        return isVisible(get(group, id, child));
    }

    @Override
    public boolean isVisible(int component) {
        return isVisible(get(component));
    }

    @Override
    public boolean isVisible(InterfaceAddress interfaceAddress) {
        return isVisible(interfaceAddress.getGroup(), interfaceAddress.getChild());
    }

    @Override
    public List<IWidget> getChildren(IWidget widget, Predicate<IWidget> filter) {
        if (widget == null) {
            return Collections.emptyList();
        }

        var children = widget.getChildren();
        if (children == null) {
            return Collections.emptyList();
        }

        return Stream.of(children)
                .filter(filter)
                .collect(Collectors.toList());
    }

    @Override
    public List<IWidget> getChildren(int group, int id, Predicate<IWidget> filter) {
        return getChildren(get(group, id), filter);
    }

    @Override
    public List<IWidget> getChildren(int group, int id, int child, Predicate<IWidget> filter) {
        return getChildren(get(group, id, child), filter);
    }

    @Override
    public List<IWidget> getChildren(int component, Predicate<IWidget> filter) {
        return getChildren(get(component), filter);
    }

    @Override
    public List<IWidget> getChildren(InterfaceAddress interfaceAddress, Predicate<IWidget> filter) {
        return getChildren(interfaceAddress.getGroup(), interfaceAddress.getChild(), filter);
    }

    @Override
    public void closeInterfaces() {
        client.runScript(29);
    }
}
