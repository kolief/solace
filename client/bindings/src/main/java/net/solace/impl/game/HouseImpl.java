package net.solace.impl.game;

import lombok.RequiredArgsConstructor;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.solace.api.domain.game.IClient;
import net.solace.api.domain.items.IItem;
import net.solace.api.domain.tiles.ITileObject;
import net.solace.api.entities.ITileObjects;
import net.solace.api.entities.ITiles;
import net.solace.api.game.HouseLocation;
import net.solace.api.game.IGame;
import net.solace.api.game.IHouse;
import net.solace.api.game.IVars;
import net.solace.api.items.IEquipment;
import net.solace.api.items.IInventory;
import net.solace.api.magic.SpellBook;
import net.solace.api.movement.pathfinder.model.TeleportSpell;

import java.util.Arrays;
import java.util.Objects;

import static net.solace.api.movement.pathfinder.model.MovementConstants.CONSTRUCTION_CAPE;
import static net.solace.api.movement.pathfinder.model.MovementConstants.MAX_CAPE;

@RequiredArgsConstructor
public class HouseImpl implements IHouse {
    private static final int HOUSE_LOCATION_VARBIT = 2187;

    private final IClient client;
    private final IGame game;
    private final IVars vars;
    private final ITileObjects tileObjects;
    private final ITiles tiles;
    private final IInventory inventory;
    private final IEquipment equipment;

    @Override
    public HouseLocation getLocation() {
        if (!game.isLoggedIn()) {
            return null;
        }

        int idx = vars.getBit(HOUSE_LOCATION_VARBIT);

        return Arrays.stream(HouseLocation.values())
                .filter(h -> h.getIndex() == idx)
                .findFirst()
                .orElse(null);
    }

    @Override
    public WorldPoint getOutsideLocation() {
        HouseLocation location = getLocation();

        if (location == null) {
            return null;
        }

        return location.getLocation();
    }

    @Override
    public boolean isInside() {
        var localPlayer = client.getLocalPlayer();
        return localPlayer != null
                && client.isInInstancedRegion()
                && tileObjects.getNearest(localPlayer.getWorldLocation(), x -> Objects.equals(x.getName(), "Portal") && x.hasAction("Lock")) != null;
    }

    @Override
    public boolean canEnter() {
        var outsideLocation = getOutsideLocation();

        if (outsideLocation == null) {
            return false;
        }

        return inventory.contains(ItemID.POH_TABLET_TELEPORTTOHOUSE)
                || inventory.contains(CONSTRUCTION_CAPE)
                || equipment.contains(CONSTRUCTION_CAPE)
                || TeleportSpell.TELEPORT_TO_HOUSE.canCast()
                || equipment.contains(MAX_CAPE)
                || inventory.contains(MAX_CAPE)
                || getPortal() != null;
    }

    @Override
    public void enter() {
        ITileObject portal = getPortal();
        if (portal != null) {
            portal.interact("Home");
            return;
        }

        IItem maxCape = inventory.getFirst(MAX_CAPE);
        if (maxCape == null) {
            maxCape = equipment.getFirst(MAX_CAPE);
        }

        if (maxCape != null) {
            maxCape.interact("Home");
            return;
        }

        IItem consCape = inventory.getFirst(CONSTRUCTION_CAPE);
        if (consCape == null) {
            consCape = equipment.getFirst(CONSTRUCTION_CAPE);
        }

        if (consCape != null) {
            consCape.interact("Tele to POH");
            return;
        }

        if (TeleportSpell.TELEPORT_TO_HOUSE.canCast()) {
            SpellBook.Standard.TELEPORT_TO_HOUSE.cast("Cast");
            return;
        }

        var teleTab = inventory.getFirst(ItemID.POH_TABLET_TELEPORTTOHOUSE);
        if (teleTab != null) {
            teleTab.interact("Break");
        }
    }

    private ITileObject getPortal() {
        var worldPoint = client.getLocalPlayer().getWorldLocation();
        var tile = tiles.getAt(worldPoint);
        var outsideLocation = getOutsideLocation();
        if (tile == null || outsideLocation == null) {
            return null;
        }

        return tileObjects.getSurrounding(
                        outsideLocation,
                        15,
                        obj -> obj.hasAction("Build mode") && obj.distanceTo(worldPoint) <= 20)
                .stream()
                .findFirst()
                .orElse(null);
    }
}
