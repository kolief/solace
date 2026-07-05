package net.solace.api.util;

import java.awt.Point;
import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.DecorativeObject;
import net.runelite.api.GameObject;
import net.runelite.api.GroundObject;
import net.runelite.api.Tile;
import net.runelite.api.TileObject;
import net.runelite.api.WallObject;
import net.runelite.api.WorldView;
import net.solace.api.coords.Coordinate;
import net.solace.api.domain.game.IClient;
import net.solace.api.interact.AutomatedMenu;
import net.solace.api.util.Randomizer;

public class MenuUtils {
    public static TileObject getTileObjectFromMenu(IClient client, AutomatedMenu menu) {
        DecorativeObject decor;
        int x = menu.getParam0();
        int y = menu.getParam1();
        int wvId = menu.getWorldViewId() == 0 ? -1 : menu.getWorldViewId();
        WorldView topLevelWorldView = ((Client)client.getWrapped()).getWorldView(wvId);
        Tile tile = topLevelWorldView.getScene().getTiles()[topLevelWorldView.getPlane()][x][y];
        if (tile == null) {
            return null;
        }
        int id = menu.getIdentifier();
        GameObject[] gameObjects = tile.getGameObjects();
        if (gameObjects != null) {
            for (GameObject gameObject : gameObjects) {
                if (gameObject == null || gameObject.getId() != id) continue;
                return gameObject;
            }
        }
        if ((decor = tile.getDecorativeObject()) != null && decor.getId() == id) {
            return decor;
        }
        WallObject wall = tile.getWallObject();
        if (wall != null && wall.getId() == id) {
            return wall;
        }
        GroundObject ground = tile.getGroundObject();
        if (ground != null && ground.getId() == id) {
            return ground;
        }
        return null;
    }

    public static Actor getActorFromMenu(IClient client, AutomatedMenu menu) {
        int wvId = menu.getWorldViewId() == 0 ? -1 : menu.getWorldViewId();
        WorldView topLevelWorldView = ((Client)client.getWrapped()).getWorldView(menu.getWorldViewId());
        switch (menu.getOpcode()) {
            case PLAYER_FIRST_OPTION: 
            case PLAYER_SECOND_OPTION: 
            case PLAYER_THIRD_OPTION: 
            case PLAYER_FOURTH_OPTION: 
            case PLAYER_FIFTH_OPTION: 
            case PLAYER_SIXTH_OPTION: 
            case PLAYER_SEVENTH_OPTION: 
            case PLAYER_EIGHTH_OPTION: 
            case WIDGET_TARGET_ON_PLAYER: {
                return (Actor)topLevelWorldView.players().byIndex(menu.getIdentifier());
            }
            case NPC_FIRST_OPTION: 
            case NPC_SECOND_OPTION: 
            case NPC_THIRD_OPTION: 
            case NPC_FOURTH_OPTION: 
            case NPC_FIFTH_OPTION: 
            case WIDGET_TARGET_ON_NPC: {
                return (Actor)topLevelWorldView.npcs().byIndex(menu.getIdentifier());
            }
        }
        return null;
    }

    public static Point getClickPointFromAutomatedMenu(IClient client, AutomatedMenu menu) {
        Actor actor;
        TileObject tile;
        Coordinate clickPoint = menu.getClickPoint();
        if (clickPoint == null && (tile = MenuUtils.getTileObjectFromMenu(client, menu)) != null) {
            clickPoint = Randomizer.getRandomPointIn(tile.getClickbox());
        }
        if (clickPoint == null && (actor = MenuUtils.getActorFromMenu(client, menu)) != null) {
            clickPoint = Randomizer.getRandomPointIn(actor.getConvexHull());
        }
        if (clickPoint == null) {
            return null;
        }
        return new Point(clickPoint.getX(), clickPoint.getY());
    }
}

