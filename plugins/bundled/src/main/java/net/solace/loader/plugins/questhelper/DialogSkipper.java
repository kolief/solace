package net.solace.loader.plugins.questhelper;

import com.google.inject.Inject;
import lombok.RequiredArgsConstructor;
import net.runelite.api.widgets.WidgetUtil;
import net.solace.api.domain.widgets.IWidget;
import net.solace.api.widgets.WidgetGroup;
import net.solace.sdk.game.Client;
import net.solace.sdk.widgets.Widgets;

import java.util.List;

import static net.solace.loader.plugins.questhelper.util.ReflectionBridge.getConfig;
import static net.solace.loader.plugins.questhelper.util.ReflectionBridge.getTextHighlightColor;

@RequiredArgsConstructor(onConstructor_ = @__(@Inject))
public class DialogSkipper {
    private static final List<Integer> DIALOG_GROUPS = List.of(
            WidgetGroup.DIALOG_PLAYER_GROUP_ID,
            WidgetGroup.DIALOG_MINIGAME_GROUP_ID,
            WidgetGroup.DIALOG_NOTIFICATION_GROUP_ID,
            WidgetGroup.DIALOG_NPC_GROUP_ID,
            WidgetGroup.DIALOG_OPTION_GROUP_ID,
            WidgetGroup.DIALOG_SPRITE2_ID,
            WidgetGroup.DIALOG_SPRITE_GROUP_ID
    );

    private final SolaceQuestHelperPlugin plugin;

    public void handleDialogs() {
        for (var dialogGroup : DIALOG_GROUPS) {
            Widgets.getAll(dialogGroup).forEach(this::selectOption);
        }
    }

    private void selectOption(IWidget widget) {
        selectOption(widget, -1);
    }

    private void selectOption(IWidget widget, int childId) {
        if (widget == null || widget.isHidden()) {
            return;
        }

        var group = WidgetUtil.componentToInterface(widget.getId());
        if (!DIALOG_GROUPS.contains(group)) {
            return;
        }

        if (widget.getText().equals("Click here to continue")) {
            var idx = group == WidgetGroup.DIALOG_SPRITE_GROUP_ID ? 0 : childId;
            Client.processDialog(widget.getId(), idx);
        } else {
            if (widget.getTextColor() == getTextHighlightColor(getConfig(plugin.getQuestHelperPlugin())).getRGB() && widget.getIndex() != -1) {
                Client.processDialog(widget.getId(), widget.getIndex());
            }
        }

        checkWidgetChildren(widget.getDynamicChildren());
        checkWidgetChildren(widget.getStaticChildren());
        checkWidgetChildren(widget.getNestedChildren());
    }

    private void checkWidgetChildren(IWidget[] widgets) {
        if (widgets == null) {
            return;
        }

        for (var i = 0; i < widgets.length; i++) {
            var widget = widgets[i];
            selectOption(widget, i);
        }
    }
}
