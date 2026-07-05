package net.solace.api.domain;

import net.runelite.api.WorldView;
import net.solace.api.domain.Identifiable;
import net.solace.api.domain.Interactable;
import net.solace.api.domain.Locatable;
import net.solace.api.domain.Nameable;

public interface SceneEntity
extends Locatable,
Identifiable,
Interactable,
Nameable {
    public WorldView getWorldView();
}

