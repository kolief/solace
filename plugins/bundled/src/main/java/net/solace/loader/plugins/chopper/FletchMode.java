package net.solace.loader.plugins.chopper;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.solace.api.domain.items.IInventoryItem;
import net.solace.sdk.items.Inventory;

import java.util.List;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public enum FletchMode {
    ARROW_SHAFT("Arrow shaft"),
    JAVELIN_SHAFT("Javelin shaft"),
    SHORTBOW("Shortbow"),
    LONGBOW("Longbow"),
    CROSSBOW_STOCK("Stock"),
    SHIELD("Shield");

    private final String name;

    public List<IInventoryItem> getItems() {
        return Inventory.getAll(x -> x.getName() != null && x.getName().toLowerCase().contains(name.toLowerCase()));
    }
}
