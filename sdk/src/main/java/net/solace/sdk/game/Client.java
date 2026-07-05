package net.solace.sdk.game;

import java.awt.Canvas;
import java.util.EnumSet;
import java.util.List;
import net.runelite.api.FriendContainer;
import net.runelite.api.GameState;
import net.runelite.api.GrandExchangeOffer;
import net.runelite.api.ItemComposition;
import net.runelite.api.ItemContainer;
import net.runelite.api.MenuAction;
import net.runelite.api.ObjectComposition;
import net.runelite.api.Preferences;
import net.runelite.api.Skill;
import net.runelite.api.World;
import net.runelite.api.WorldType;
import net.runelite.api.WorldView;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.Widget;
import net.runelite.api.worldmap.WorldMap;
import net.solace.api.Static;
import net.solace.api.coords.Coordinate;
import net.solace.api.domain.SceneEntity;
import net.solace.api.domain.actors.INPC;
import net.solace.api.domain.actors.IPlayer;
import net.solace.api.domain.game.IClient;
import net.solace.api.domain.tiles.ITile;
import net.solace.api.interact.AutomatedMenu;
import net.solace.api.rs.MouseRecorder;

public class Client {
    private static final IClient CLIENT = Static.getClient();

    public static IClient getClient() {
        return CLIENT;
    }

    public static net.runelite.api.Client getWrapped() {
        return (net.runelite.api.Client)CLIENT.getWrapped();
    }

    public static String getBuildId() {
        return CLIENT.getBuildId();
    }

    public static void interact(AutomatedMenu automatedMenu) {
        CLIENT.interact(automatedMenu);
    }

    public static void interact(int identifier, int opcode, int param0, int param1, int clickX, int clickY, SceneEntity sceneEntity) {
        Client.interact(AutomatedMenu.builder().identifier(identifier).opcode(MenuAction.of((int)opcode)).param0(param0).param1(param1).clickPoint(new Coordinate(clickX, clickY)).entity(sceneEntity).build());
    }

    public static void interact(int identifier, int opcode, int param0, int param1, int clickX, int clickY) {
        Client.interact(identifier, opcode, param0, param1, clickX, clickY, null);
    }

    public static void interact(int identifier, int opcode, int param0, int param1) {
        Client.interact(identifier, opcode, param0, param1, -1, -1);
    }

    public static void interact(int x, int y) {
        CLIENT.interact(x, y);
    }

    public static void interact(int x, int y, boolean click) {
        CLIENT.interact(x, y, click);
    }

    public static String[] getPlayerOptions() {
        return CLIENT.getPlayerOptions();
    }

    public static ObjectComposition getObjectComposition(int id) {
        return CLIENT.getObjectComposition(id);
    }

    public static boolean isClientThread() {
        return CLIENT.isClientThread();
    }

    public static ItemComposition getItemComposition(int id) {
        return CLIENT.getItemComposition(id);
    }

    public static ItemContainer getItemContainer(int id) {
        return CLIENT.getItemContainer(id);
    }

    public static WorldView getTopLevelWorldView() {
        return CLIENT.getTopLevelWorldView();
    }

    public static IPlayer getHintArrowPlayer() {
        return CLIENT.getHintArrowPlayer();
    }

    public static WorldPoint getHintArrowPosition() {
        return CLIENT.getHintArrowPosition();
    }

    public static int[] getVarps() {
        return CLIENT.getVarps();
    }

    public static int getVarbitValue(int[] vars, int id) {
        return CLIENT.getVarbitValue(vars, id);
    }

    public static int getVarpValue(int id) {
        return CLIENT.getVarpValue(id);
    }

    public static int getVarcIntValue(int varClientInt) {
        return CLIENT.getVarcIntValue(varClientInt);
    }

    public static String getVarcStrValue(int varClientStr) {
        return CLIENT.getVarcStrValue(varClientStr);
    }

    public static GameState getGameState() {
        return CLIENT.getGameState();
    }

    public static void setGameState(GameState gameState) {
        CLIENT.setGameState(gameState);
    }

    public static void setGameState(int gameState) {
        CLIENT.setGameState(gameState);
    }

    public static int getTickCount() {
        return CLIENT.getTickCount();
    }

    public static void runScript(Object ... args) {
        CLIENT.runScript(args);
    }

    public static int getBoostedSkillLevel(Skill skill) {
        return CLIENT.getBoostedSkillLevel(skill);
    }

    public static int getRealSkillLevel(Skill skill) {
        return CLIENT.getRealSkillLevel(skill);
    }

    public static int getSkillExperience(Skill skill) {
        return CLIENT.getSkillExperience(skill);
    }

    public static boolean isInInstancedRegion() {
        return CLIENT.isInInstancedRegion();
    }

    public static World[] getWorldList() {
        return CLIENT.getWorldList();
    }

    public static int getWorld() {
        return CLIENT.getWorld();
    }

    public static void openWorldHopper() {
        CLIENT.openWorldHopper();
    }

    public static void changeWorld(World world) {
        CLIENT.changeWorld(world);
    }

    public static void hopToWorld(World world) {
        CLIENT.hopToWorld(world);
    }

    public static Canvas getCanvas() {
        return CLIENT.getCanvas();
    }

    public static void setVarcIntValue(int inputType, int inputType1) {
        CLIENT.setVarcIntValue(inputType, inputType1);
    }

    public static void setVarcStrValue(int var, String value) {
        CLIENT.setVarcStrValue(var, value);
    }

    public static GrandExchangeOffer[] getGrandExchangeOffers() {
        return CLIENT.getGrandExchangeOffers();
    }

    public static int[] getMapRegions() {
        return CLIENT.getMapRegions();
    }

    public static EnumSet<WorldType> getWorldType() {
        return CLIENT.getWorldType();
    }

    public static int[] getIntStack() {
        return CLIENT.getIntStack();
    }

    public static int getEnergy() {
        return CLIENT.getEnergy();
    }

    public static FriendContainer getFriendContainer() {
        return CLIENT.getFriendContainer();
    }

    public static boolean isFriended(String name, boolean b) {
        return CLIENT.isFriended(name, b);
    }

    public static int getViewportWidth() {
        return CLIENT.getViewportWidth();
    }

    public static int getViewportHeight() {
        return CLIENT.getViewportHeight();
    }

    public static int getPlane() {
        return CLIENT.getPlane();
    }

    public static boolean isResized() {
        return CLIENT.isResized();
    }

    public static int getMapAngle() {
        return CLIENT.getMapAngle();
    }

    public static WorldMap getWorldMap() {
        return CLIENT.getWorldMap();
    }

    public static World createWorld() {
        return CLIENT.createWorld();
    }

    public static void setSelectedSceneTileX(int sceneX) {
        CLIENT.setSelectedSceneTileX(sceneX);
    }

    public static void setSelectedSceneTileY(int sceneY) {
        CLIENT.setSelectedSceneTileY(sceneY);
    }

    public static void setViewportWalking(boolean b) {
        CLIENT.setViewportWalking(b);
    }

    public static LocalPoint getLocalDestinationLocation() {
        return CLIENT.getLocalDestinationLocation();
    }

    public static void setLastButton(int button) {
        CLIENT.setLastButton(button);
    }

    public static void setCheckClick(boolean checkClick) {
        CLIENT.setCheckClick(checkClick);
    }

    public static INPC getFollower() {
        return CLIENT.getFollower();
    }

    public static INPC getHintArrowNpc() {
        return CLIENT.getHintArrowNpc();
    }

    public static void invokeWidgetAction(int identifier, int param1, int param0, int itemId, String target) {
        CLIENT.invokeWidgetAction(identifier, param1, param0, itemId, target, "");
    }

    public static void invokeWidgetAction(int identifier, int param1, int param0, int itemId, String target, String str) {
        CLIENT.invokeWidgetAction(identifier, param1, param0, itemId, target, str);
    }

    public static String getLoginMessage() {
        return CLIENT.getLoginMessage();
    }

    public static void setLoginIndex(int index) {
        CLIENT.setLoginIndex(index);
    }

    public static void setMouseIdleTicks(int ticks) {
        CLIENT.setMouseIdleTicks(ticks);
    }

    public static void setKeyboardIdleTicks(int ticks) {
        CLIENT.setKeyboardIdleTicks(ticks);
    }

    public static boolean loadWorlds() {
        return CLIENT.loadWorlds();
    }

    public static void promptCredentials(boolean clearPassword) {
        CLIENT.promptCredentials(clearPassword);
    }

    public static boolean isWorldSelectOpen() {
        return CLIENT.isWorldSelectOpen();
    }

    public static void setWorldSelectOpen(boolean open) {
        CLIENT.setWorldSelectOpen(open);
    }

    public static boolean isOAuthCredentialsSet() {
        return CLIENT.isOAuthCredentialsSet();
    }

    public static String getPassword() {
        return CLIENT.getPassword();
    }

    public static void setPassword(String password) {
        CLIENT.setPassword(password);
    }

    public static String getUsername() {
        return CLIENT.getUsername();
    }

    public static void setUsername(String username) {
        CLIENT.setUsername(username);
    }

    public static void setOtp(String now) {
        CLIENT.setOtp(now);
    }

    public static int getWindowedMode() {
        return CLIENT.getWindowedMode();
    }

    public static void setWindowedMode(int i) {
        CLIENT.setWindowedMode(i);
    }

    public static Preferences getPreferences() {
        return Client.getWrapped().getPreferences();
    }

    public static void setMouseIdleCycles(int minValue) {
        CLIENT.setMouseIdleCycles(minValue);
    }

    public static List<? extends SceneEntity> getHoveredEntities() {
        return CLIENT.getHoveredEntities();
    }

    public static ITile getSelectedSceneTile() {
        return CLIENT.getSelectedSceneTile();
    }

    public static void setDraggedWidget(Widget widget) {
        CLIENT.setDraggedWidget(widget);
    }

    public static int getMinimapState() {
        return CLIENT.getMinimapState();
    }

    public static void setMinimapState(int state) {
        CLIENT.setMinimapState(state);
    }

    public static void processDialog(int id, int idx) {
        CLIENT.processDialog(id, idx);
    }

    public static void setMenuOpened(boolean opened) {
        CLIENT.setMenuOpened(opened);
    }

    public static void setOAuthLoginMode() {
        CLIENT.setOAuthLoginMode();
    }

    public static void setNormalLoginMode() {
        CLIENT.setNormalLoginMode();
    }

    public static String getDisplayName() {
        return CLIENT.getDisplayName();
    }

    public static String getSessionId() {
        return CLIENT.getSessionId();
    }

    public static String getCharacterId() {
        return CLIENT.getCharacterId();
    }

    public static void setDisplayName(String displayName) {
        CLIENT.setDisplayName(displayName);
    }

    public static void setSessionId(String sessionId) {
        CLIENT.setSessionId(sessionId);
    }

    public static void setCharacterId(String characterId) {
        CLIENT.setCharacterId(characterId);
    }

    public static String getLoginResponse1() {
        return CLIENT.getLoginResponse1();
    }

    public static String getLoginResponse2() {
        return CLIENT.getLoginResponse2();
    }

    public static String getLoginResponse3() {
        return CLIENT.getLoginResponse3();
    }

    public static void sleep(int cycles) {
        CLIENT.sleep(cycles);
    }

    public static String getDiscordId() {
        return CLIENT.getDiscordId();
    }

    public static String getDiscordUser() {
        return CLIENT.getDiscordUser();
    }

    public static Long getUserId() {
        return CLIENT.getUserId();
    }

    public static void invokeMenuAction(int param0, int param1, int opcode, int id, int itemId, int worldViewId, String option, String target) {
        CLIENT.invokeMenuAction(param0, param1, opcode, id, itemId, worldViewId, option, target);
    }

    public static MouseRecorder getMouseRecorder() {
        return CLIENT.getMouseRecorder();
    }

    public static boolean isFocused() {
        return CLIENT.isFocused();
    }

    public static void setFocused(boolean focused) {
        CLIENT.setFocused(focused);
    }
}

