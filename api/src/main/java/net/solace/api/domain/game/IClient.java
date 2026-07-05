package net.solace.api.domain.game;

import java.awt.Canvas;
import java.util.EnumSet;
import java.util.List;
import javax.annotation.Nullable;
import net.runelite.api.Client;
import net.runelite.api.FriendContainer;
import net.runelite.api.GameState;
import net.runelite.api.GrandExchangeOffer;
import net.runelite.api.InventoryID;
import net.runelite.api.ItemComposition;
import net.runelite.api.ItemContainer;
import net.runelite.api.MenuAction;
import net.runelite.api.ObjectComposition;
import net.runelite.api.Skill;
import net.runelite.api.World;
import net.runelite.api.WorldType;
import net.runelite.api.WorldView;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.Widget;
import net.runelite.api.worldmap.WorldMap;
import net.solace.api.containers.NpcContainer;
import net.solace.api.containers.PlayerContainer;
import net.solace.api.containers.TileContainer;
import net.solace.api.domain.RuneLiteWrapper;
import net.solace.api.domain.SceneEntity;
import net.solace.api.domain.actors.INPC;
import net.solace.api.domain.actors.IPlayer;
import net.solace.api.domain.tiles.ITile;
import net.solace.api.domain.widgets.IWidget;
import net.solace.api.interact.AutomatedMenu;
import net.solace.api.rs.MouseRecorder;

public interface IClient
extends RuneLiteWrapper<Client> {
    public String getBuildId();

    public IPlayer getLocalPlayer();

    public void interact(AutomatedMenu var1);

    default public void interact(int identifier, int opcode, int param0, int param1) {
        this.interact(AutomatedMenu.builder().identifier(identifier).opcode(MenuAction.of((int)opcode)).param0(param0).param1(param1).build());
    }

    public String[] getPlayerOptions();

    public ObjectComposition getObjectComposition(int var1);

    public boolean isClientThread();

    public ItemComposition getItemComposition(int var1);

    public ItemContainer getItemContainer(int var1);

    @Deprecated(forRemoval=true)
    default public ItemContainer getItemContainer(InventoryID inventoryID) {
        return this.getItemContainer(inventoryID.getId());
    }

    public WorldView getTopLevelWorldView();

    public ITile[][][] getTiles();

    public int getPlane();

    public IWidget[] getWidgets(int var1);

    public IWidget getWidget(int var1, int var2);

    public IWidget getWidget(int var1);

    public IPlayer getHintArrowPlayer();

    public WorldPoint getHintArrowPosition();

    public int[] getVarps();

    public int getVarbitValue(int[] var1, int var2);

    public int getVarpValue(int var1);

    public int getVarcIntValue(int var1);

    public String getVarcStrValue(int var1);

    public GameState getGameState();

    public int getTickCount();

    public void runScript(Object ... var1);

    public int getBoostedSkillLevel(Skill var1);

    public int getRealSkillLevel(Skill var1);

    public int getSkillExperience(Skill var1);

    public boolean isInInstancedRegion();

    public World[] getWorldList();

    public int getWorld();

    public void openWorldHopper();

    public void changeWorld(World var1);

    public void hopToWorld(World var1);

    public Canvas getCanvas();

    public void setVarcIntValue(int var1, int var2);

    public void setVarcStrValue(int var1, String var2);

    public GrandExchangeOffer[] getGrandExchangeOffers();

    public int[] getMapRegions();

    public EnumSet<WorldType> getWorldType();

    public int[] getIntStack();

    public int getEnergy();

    public FriendContainer getFriendContainer();

    public boolean isFriended(String var1, boolean var2);

    public int getViewportHeight();

    public int getViewportWidth();

    public boolean isResized();

    public int getMapAngle();

    public WorldMap getWorldMap();

    public World createWorld();

    public void setSelectedSceneTileX(int var1);

    public void setSelectedSceneTileY(int var1);

    public void setViewportWalking(boolean var1);

    public LocalPoint getLocalDestinationLocation();

    public void setLastButton(int var1);

    public void setCheckClick(boolean var1);

    @Nullable
    public INPC getFollower();

    public INPC getHintArrowNpc();

    public void invokeWidgetAction(int var1, int var2, int var3, int var4, String var5, String var6);

    public String getLoginMessage();

    public void setLoginIndex(int var1);

    public void setMouseIdleTicks(int var1);

    public void setKeyboardIdleTicks(int var1);

    public boolean loadWorlds();

    public void promptCredentials(boolean var1);

    public boolean isWorldSelectOpen();

    public void setWorldSelectOpen(boolean var1);

    public boolean isOAuthCredentialsSet();

    public String getPassword();

    public void setPassword(String var1);

    public String getUsername();

    public void setUsername(String var1);

    public void setOtp(String var1);

    public int getWindowedMode();

    public void setWindowedMode(int var1);

    public void setMouseIdleCycles(int var1);

    public List<? extends SceneEntity> getHoveredEntities();

    public ITile getSelectedSceneTile();

    public void setDraggedWidget(Widget var1);

    public int getMinimapState();

    public void setMinimapState(int var1);

    public void processDialog(int var1, int var2);

    public void setMenuOpened(boolean var1);

    default public void interact(int x, int y) {
        this.interact(x, y, true);
    }

    public void interact(int var1, int var2, boolean var3);

    public void setGameState(GameState var1);

    public void setGameState(int var1);

    public void setOAuthLoginMode();

    public void setNormalLoginMode();

    public String getDisplayName();

    public String getSessionId();

    public String getCharacterId();

    public void setDisplayName(String var1);

    public void setSessionId(String var1);

    public void setCharacterId(String var1);

    public String getLoginResponse1();

    public String getLoginResponse2();

    public String getLoginResponse3();

    public PlayerContainer getPlayerContainer();

    public NpcContainer getNpcContainer();

    public TileContainer getTileContainer();

    public void sleep(int var1);

    public String getDiscordId();

    public String getDiscordUser();

    public Long getUserId();

    public void invokeMenuAction(int var1, int var2, int var3, int var4, int var5, int var6, String var7, String var8);

    public MouseRecorder getMouseRecorder();

    public boolean isFocused();

    public void setFocused(boolean var1);
}

