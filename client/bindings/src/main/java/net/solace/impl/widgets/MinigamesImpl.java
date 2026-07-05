package net.solace.impl.widgets;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.gameval.VarPlayerID;
import net.solace.api.commons.Predicates;
import net.solace.api.domain.game.IClient;
import net.solace.api.domain.widgets.IWidget;
import net.solace.api.entities.IPlayers;
import net.solace.api.game.IVars;
import net.solace.api.items.IBank;
import net.solace.api.items.IGrandExchange;
import net.solace.api.widgets.IDialog;
import net.solace.api.widgets.IMinigames;
import net.solace.api.widgets.ITabs;
import net.solace.api.widgets.IWidgets;
import net.solace.api.widgets.MinigameTeleport;
import net.solace.api.widgets.Tab;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

@RequiredArgsConstructor
@Slf4j
public class MinigamesImpl implements IMinigames {
    private final IDialog dialog;
    private final IWidgets widgets;
    private final IBank bank;
    private final IGrandExchange grandExchange;
    private final IClient client;
    private final IVars vars;
    private final IPlayers players;
    private final ITabs tabs;

    @Override
    public boolean canTeleport() {
        return getLastMinigameTeleportUsage().plus(20, ChronoUnit.MINUTES).isBefore(Instant.now());
    }

    @Override
    public boolean teleport(MinigameTeleport destination) {
        if (!canTeleport()) {
            log.warn("Tried to minigame teleport, but it's on cooldown.");
            return false;
        }

        if (dialog.canContinue()) {
            dialog.continueSpace();
            return true;
        }

        IWidget bookWidget = widgets.get(392, 6);
        if (bank.isOpen() || grandExchange.isOpen() || widgets.isVisible(bookWidget)) {
            widgets.closeInterfaces();
            return true;
        }

        IWidget minigamesTeleportButton = widgets.get(76, 32);
        if (isOpen() && minigamesTeleportButton != null) {
            if (!Objects.equals(MinigameTeleport.getCurrent().getName(), destination.getName())) {
                log.debug("Setting minigame teleport from {} to destination {}", MinigameTeleport.getCurrent().getName(), destination.getName());
                client.runScript(124, destination.getInterfaceId());
                return true;
            }

            if (destination.getDialogs() != null) {
                for (String dialog : destination.getDialogs()) {
                    if (this.dialog.chooseOption(dialog)) {
                        log.debug("Chose minigame teleport dialog option: {}", dialog);
                        return true;
                    }
                }
            }

            if (players.getLocal().getSpotAnimationCount() > 0) {
                return true;
            }

            IWidget button = minigamesTeleportButton.getChild(destination.getInterfaceId());
            if (widgets.isVisible(button)) {
                if (dialog.isOpen() && dialog.isViewingOptions()) {
                    log.warn("Unhandled dialog options");
                    dialog.forceClose();
                    return true;
                }

                button.interact(Predicates.textContains("Teleport to"));
                return true;
            }
        } else {
            open();
        }

        return true;
    }

    @Override
    public boolean open() {
        if (!isTabOpen()) {
            tabs.open(Tab.CLAN_CHAT);
            return false;
        }

        if (!isOpen()) {
            IWidget closeButton = widgets.get(762, 2);
            if (widgets.isVisible(closeButton)) {
                closeButton.interact("Close");
                return false;
            }

            IWidget widget = widgets.get(707, x -> x.hasAction("Grouping"));
            if (widgets.isVisible(widget)) {
                widget.interact("Grouping");
                return false;
            }

            IWidget ironWidget = widgets.get(727, x -> x.hasAction("Grouping"));
            if (widgets.isVisible(ironWidget)) {
                ironWidget.interact("Grouping");
                return false;
            }
        }

        return isOpen();
    }

    @Override
    public boolean isOpen() {
        return widgets.isVisible(widgets.get(76, 30));
    }

    @Override
    public boolean isTabOpen() {
        return tabs.isOpen(Tab.CLAN_CHAT);
    }

    @Override
    public Instant getLastMinigameTeleportUsage() {
        return Instant.ofEpochSecond(vars.getVarp(VarPlayerID.SLUG2_REGIONUID) * 60L);
    }
}
