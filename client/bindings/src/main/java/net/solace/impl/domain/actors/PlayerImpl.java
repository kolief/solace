package net.solace.impl.domain.actors;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.HeadIcon;
import net.runelite.api.MenuAction;
import net.runelite.api.Player;
import net.runelite.api.PlayerComposition;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.solace.api.commons.JagStrings;
import net.solace.api.domain.actors.IPlayer;
import net.solace.api.domain.game.IClient;
import net.solace.api.domain.items.IInventoryItem;
import net.solace.api.interact.AutomatedMenu;
import net.solace.api.interact.InteractMethod;
import net.solace.api.magic.Spell;

import java.awt.Polygon;

@Slf4j
@Getter
public class PlayerImpl extends ActorImpl<Player> implements IPlayer {
    private PlayerImpl(Player wrapped, IClient client) {
        super(wrapped, client);
    }

    public static IPlayer of(Player rlPlayer, IClient client) {
        if (rlPlayer == null) {
            return null;
        }

        return new PlayerImpl(rlPlayer, client);
    }

    @Override
    public int getId() {
        return wrapped.getId();
    }

    @Override
    public int getIndex() {
        return getId();
    }

    @Override
    public String[] getActions() {
        var playerOptions = client.getPlayerOptions();
        var sanitized = new String[playerOptions.length];
        for (var i = 0; i < sanitized.length; i++) {
            sanitized[i] = JagStrings.standardize(playerOptions[i]);
        }

        return sanitized;
    }

    @Override
    public AutomatedMenu generateMenu(InteractMethod interactMethod, int actionIndex) {
        return MENU_FACTORY.player(getIndex())
                .interactMethod(interactMethod)
                .actionIndex(actionIndex)
                .worldViewId(getWorldView().getId())
                .build(null);
    }

    @Override
    public AutomatedMenu generateMenu(InteractMethod interactMethod, MenuAction opcode) {
        return MENU_FACTORY.player(getIndex())
                .interactMethod(interactMethod)
                .opcode(opcode)
                .worldViewId(getWorldView().getId())
                .build(null);
    }

    @Override
    public AutomatedMenu generateMenu(InteractMethod interactMethod, IInventoryItem item) {
        return MENU_FACTORY.player(getIndex())
                .interactMethod(interactMethod)
                .opcode(MenuAction.WIDGET_TARGET_ON_PLAYER)
                .useItem(item.getId(), item.getSlot())
                .worldViewId(getWorldView().getId())
                .build(null);
    }

    @Override
    public AutomatedMenu generateMenu(InteractMethod interactMethod, Spell spell) {
        return MENU_FACTORY.player(getIndex())
                .interactMethod(interactMethod)
                .opcode(MenuAction.WIDGET_TARGET_ON_PLAYER)
                .castSpell(spell)
                .identifier(spell.getMenuIdentifier() != -1 ? spell.getMenuIdentifier() : 0)
                .worldViewId(getWorldView().getId())
                .build(null);
    }

    @Override
    public LocalPoint getLocalLocation() {
        return wrapped.getLocalLocation();
    }

    @Override
    public String getName() {
        return wrapped.getName();
    }

    @Override
    public PlayerComposition getPlayerComposition() {
        return wrapped.getPlayerComposition();
    }

    @Override
    public int getTeam() {
        return wrapped.getTeam();
    }

    @Override
    public boolean isFriendsChatMember() {
        return wrapped.isFriendsChatMember();
    }

    @Override
    public boolean isFriend() {
        return wrapped.isFriend();
    }

    @Override
    public boolean isClanMember() {
        return wrapped.isClanMember();
    }

    @Override
    public HeadIcon getOverheadIcon() {
        return wrapped.getOverheadIcon();
    }

    @Override
    public int getSkullIcon() {
        return wrapped.getSkullIcon();
    }

    @Override
    public void setSkullIcon(int i) {
        wrapped.setSkullIcon(i);
    }

    @Override
    public void update(Player player) {
        wrapped = player;
        wrappedInteracting = player.getInteracting();
        healthRatio = player.getHealthRatio();
        healthScale = player.getHealthScale();
        worldView = player.getWorldView();
        worldLocation = player.getWorldLocation();
    }
}
