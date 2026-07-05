package net.solace.impl.domain.actors;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.HeadIcon;
import net.runelite.api.MenuAction;
import net.runelite.api.NPC;
import net.runelite.api.NPCComposition;
import net.runelite.api.NpcOverrides;
import net.runelite.api.EntityOps;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.util.Text;
import net.solace.api.commons.JagStrings;
import net.solace.api.domain.actors.INPC;
import net.solace.api.domain.game.IClient;
import net.solace.api.domain.items.IInventoryItem;
import net.solace.api.interact.AutomatedMenu;
import net.solace.api.interact.InteractMethod;
import net.solace.api.magic.Spell;

import javax.annotation.Nullable;

@Slf4j
@Getter
public class NPCImpl extends ActorImpl<NPC> implements INPC {
    private NPCComposition transformedComposition;

    private NPCImpl(NPC wrapped, IClient client) {
        super(wrapped, client);
        this.transformedComposition = wrapped.getTransformedComposition();
    }

    public static INPC of(NPC rlNpc, IClient client) {
        if (rlNpc == null) {
            return null;
        }

        return new NPCImpl(rlNpc, client);
    }

    @Override
    public HeadIcon[] getOverheadIcons() {
        short[] indices = getOverheadSpriteIds();
        if (indices == null) {
            return new HeadIcon[0];
        }

        var icons = new HeadIcon[indices.length];
        var headIcons = HeadIcon.values();
        for (int i = 0; i < indices.length; i++) {
            var index = indices[i];
            if (index < 0 || index >= headIcons.length) {
                continue;
            }

            icons[i] = headIcons[index];
        }

        return icons;
    }

    @Nullable
    @Override
    public NpcOverrides getModelOverrides() {
        return wrapped.getModelOverrides();
    }

    @Nullable
    @Override
    public NpcOverrides getChatheadOverrides() {
        return wrapped.getChatheadOverrides();
    }

    @Override
    public int[] getOverheadArchiveIds() {
        return wrapped.getOverheadArchiveIds();
    }

    @Override
    public short[] getOverheadSpriteIds() {
        return wrapped.getOverheadSpriteIds();
    }

    @Nullable
    @Override
    public EntityOps getOps() {
        return null;
    }

    @Override
    public int getId() {
        return getTransformedComposition() != null ? getTransformedComposition().getId() : -1;
    }

    @Override
    public int getCombatLevel() {
        return getTransformedComposition() != null ? getTransformedComposition().getCombatLevel() : -1;
    }

    @Override
    public String[] getActions() {
        if (getTransformedComposition() == null) {
            return null;
        }

        var actions = getTransformedComposition().getActions();
        var sanitizedActions = new String[actions.length];
        for (int i = 0; i < actions.length; i++) {
            sanitizedActions[i] = JagStrings.standardize(actions[i]);
        }

        return sanitizedActions;
    }

    @Override
    public AutomatedMenu generateMenu(InteractMethod interactMethod, int actionIndex) {
        return MENU_FACTORY.npc(getIndex())
                .interactMethod(interactMethod)
                .actionIndex(actionIndex)
                .worldViewId(getWorldView().getId())
                .build(null);
    }

    @Override
    public AutomatedMenu generateMenu(InteractMethod interactMethod, MenuAction opcode) {
        return MENU_FACTORY.npc(getIndex())
                .interactMethod(interactMethod)
                .opcode(opcode)
                .worldViewId(getWorldView().getId())
                .build(null);
    }

    @Override
    public AutomatedMenu generateMenu(InteractMethod interactMethod, Spell spell) {
        return MENU_FACTORY.npc(getIndex())
                .interactMethod(interactMethod)
                .opcode(MenuAction.WIDGET_TARGET_ON_NPC)
                .castSpell(spell)
                .identifier(spell.getMenuIdentifier() != -1 ? spell.getMenuIdentifier() : 0)
                .worldViewId(getWorldView().getId())
                .build(null);
    }

    @Override
    public AutomatedMenu generateMenu(InteractMethod interactMethod, IInventoryItem item) {
        return MENU_FACTORY.npc(getIndex())
                .interactMethod(interactMethod)
                .opcode(MenuAction.WIDGET_TARGET_ON_NPC)
                .useItem(item.getId(), item.getSlot())
                .worldViewId(getWorldView().getId())
                .build(null);
    }

    @Override
    public LocalPoint getLocalLocation() {
        return wrapped.getLocalLocation();
    }

    @Override
    public int getIndex() {
        return wrapped.getIndex();
    }

    @Override
    public NPCComposition getComposition() {
        return wrapped.getComposition();
    }

    @Override
    public String getName() {
        return getTransformedComposition() != null ? Text.removeTags(getTransformedComposition().getName()) : null;
    }

    @Override
    public int getActualId() {
        return getId();
    }

    @Override
    public NPCComposition getTransformedComposition() {
        return client.isClientThread() ? wrapped.getTransformedComposition() : transformedComposition;
    }

    @Override
    public void update(NPC npc) {
        wrapped = npc;
        wrappedInteracting = npc.getInteracting();
        transformedComposition = npc.getTransformedComposition();
        healthRatio = npc.getHealthRatio();
        healthScale = npc.getHealthScale();
        worldView = npc.getWorldView();
        worldLocation = npc.getWorldLocation();
    }
}
