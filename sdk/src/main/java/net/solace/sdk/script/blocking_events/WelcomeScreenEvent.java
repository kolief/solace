package net.solace.sdk.script.blocking_events;

import net.runelite.api.MenuAction;
import net.solace.api.domain.widgets.IWidget;
import net.solace.sdk.game.Client;
import net.solace.sdk.script.blocking_events.BlockingEvent;
import net.solace.sdk.widgets.Widgets;

public class WelcomeScreenEvent
extends BlockingEvent {
    public static IWidget getPlayButton() {
        IWidget nu = Widgets.get(413, x -> x.hasAction(new String[]{"Play"}));
        if (Widgets.isVisible(nu)) {
            return nu;
        }
        IWidget old = Widgets.get(378, x -> x.hasAction(new String[]{"Play"}));
        if (Widgets.isVisible(old)) {
            return old;
        }
        return null;
    }

    public static boolean isWelcomeScreenOpen() {
        return Widgets.isVisible(Widgets.get(378, x -> x.hasAction(new String[]{"Play"}))) || Widgets.isVisible(Widgets.get(413, x -> x.hasAction(new String[]{"Play"})));
    }

    @Override
    public boolean validate() {
        return WelcomeScreenEvent.isWelcomeScreenOpen();
    }

    @Override
    public int loop() {
        IWidget playButton = WelcomeScreenEvent.getPlayButton();
        if (playButton != null) {
            Client.interact(1, MenuAction.CC_OP.getId(), -1, playButton.getId());
        }
        return -1;
    }
}

