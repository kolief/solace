package net.solace.impl.domain;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import java.awt.Shape;
import net.solace.api.domain.Interactable;
import net.solace.api.domain.game.IClient;
import net.solace.api.interact.AutomatedMenu;
import net.solace.api.interact.builder.IMenuFactory;
import net.solace.impl.interact.builder.MenuFactoryImpl;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractInteractable implements Interactable {
    protected static final IMenuFactory MENU_FACTORY = new MenuFactoryImpl();

    protected final IClient client;

    @Override
    public Shape getClickArea() {
        return null;
    }

    @Override
    public void interact(AutomatedMenu automatedMenu) {
        client.interact(automatedMenu);
    }
}
