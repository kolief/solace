package net.solace.impl.widgets;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ScriptID;
import net.runelite.api.gameval.InterfaceID;
import net.solace.api.domain.game.IClient;
import net.solace.api.domain.widgets.IWidget;
import net.solace.api.input.IKeyboard;
import net.solace.api.interact.InputDialogAction;
import net.solace.api.interact.InteractManager;
import net.solace.api.items.IGrandExchange;
import net.solace.api.widgets.IDialog;
import net.solace.api.widgets.IWidgets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class DialogImpl implements IDialog {
    private final IWidgets widgets;
    private final IKeyboard keyboard;
    private final IClient client;
    private final IGrandExchange grandExchange;
    private final InteractManager interactManager;

    @Override
    public void continueTutorial() {
        client.runScript(299, 1, 1, 1);
    }

    @Override
    public boolean isEnterInputOpen() {
        return widgets.isVisible(InterfaceID.Chatbox.MES_TEXT2) && !grandExchange.isSearchingItem();
    }

    @Override
    public boolean isOpen() {
        return isChatboxVisible() && !isScrollBarVisible();
    }

    @Override
    public void input(int inputType, String value) {
        interactManager.queue(new InputDialogAction(inputType, value));
    }

    @Override
    public boolean chooseOption(int index) {
        if (isViewingOptions()) {
            keyboard.type(index);
            return true;
        }

        return false;
    }

    @Override
    public boolean canContinue() {
        return canContinueNPC() || canContinuePlayer() || canContinueDeath()
               || canSpriteContinue() || canSprite2Continue()
               || canContinue1() || canContinue2()
               || canContinueTutIsland() || canContinueTutIsland2()
               || canContinueTutIsland3() || canLevelUpContinue();
    }

    @Override
    public void continueSpace() {
        if (isOpen()) {
            keyboard.sendSpace();
        }
    }

    @Override
    public IWidget getOptionTitle() {
        var widget = widgets.get(InterfaceID.CHATMENU, 1);

        if (!widgets.isVisible(widget) || widget.getChildren() == null) {
            return null;
        }

        return widget.getChild(0);
    }

    @Override
    public List<IWidget> getOptions() {
        var widget = widgets.get(InterfaceID.CHATMENU, 1);
        if (!widgets.isVisible(widget)) {
            return Collections.emptyList();
        }

        List<IWidget> out = new ArrayList<>();
        var children = widget.getChildren();
        if (children == null) {
            return out;
        }

        // Skip first child, it's not a dialog option
        for (var i = 1; i < children.length; i++) {
            if (children[i].getText().isBlank()) {
                continue;
            }

            out.add(children[i]);
        }

        return out;
    }

    public void forceOpen() {
        client.runScript(ScriptID.MESSAGE_LAYER_OPEN, 0);
    }

    public void forceClose() {
        continueTutorial();
    }

    @Override
    public String getText() {
        IWidget widget = null;

        if (canContinueNPC()) {
            widget = widgets.get(InterfaceID.ChatLeft.TEXT);
        } else if (canContinuePlayer()) {
            widget = widgets.get(InterfaceID.CHAT_RIGHT, 6);
        } else if (canContinueMinigame() || canContinueTutIsland()) {
            widget = widgets.get(229, 1);
            if (widget != null && widget.getText().isBlank()) {
                widget = widgets.get(229, 3);
            }
        } else if (canSpriteContinue()) {
            widget = widgets.get(InterfaceID.Objectbox.TEXT);
        }

        return widget == null ? "" : widget.getText();
    }

    @Override
    public String getName() {
        IWidget widget = null;

        if (canContinueNPC()) {
            widget = widgets.get(InterfaceID.ChatLeft.NAME);
        } else if (canContinuePlayer()) {
            widget = widgets.get(InterfaceID.CHAT_RIGHT, 4);
        }

        return widget == null ? "" : widget.getText();
    }

    private boolean canContinueMinigame() {
        return widgets.isVisible(229, 2);
    }

    private boolean isChatboxVisible() {
        return widgets.isVisible(InterfaceID.Chatbox.CHATAREA);
    }

    private boolean isScrollBarVisible() {
        return widgets.isVisible(162, 557);
    }

    private boolean canLevelUpContinue() {
        return widgets.isVisible(InterfaceID.LevelupDisplay.TEXT2);
    }

    private boolean canSpriteContinue() {
        return widgets.isVisible(InterfaceID.OBJECTBOX, 0);
    }

    private boolean canSprite2Continue() {
        return widgets.isVisible(InterfaceID.OBJECTBOX_DOUBLE, 4);
    }

    private boolean canContinue1() {
        return widgets.isVisible(InterfaceID.OBJECTBOX, 3);
    }

    private boolean canContinue2() {
        return widgets.isVisible(633, 0);
    }

    private boolean canContinueNPC() {
        return widgets.isVisible(InterfaceID.ChatLeft.NAME);
    }

    private boolean canContinuePlayer() {
        return widgets.isVisible(InterfaceID.CHAT_RIGHT, 3);
    }

    private boolean canContinueDeath() {
        return widgets.isVisible(663, 0, 2);
    }

    private boolean canContinueTutIsland() {
        return canContinueMinigame();
    }

    private boolean canContinueTutIsland2() {
        return widgets.isVisible(InterfaceID.OBJECTBOX, 2);
    }

    private boolean canContinueTutIsland3() {
        var widget = widgets.get(InterfaceID.Chatbox.MES_TEXT2);
        return widgets.isVisible(widget) && widget.getText().toLowerCase().contains("continue");
    }
}
