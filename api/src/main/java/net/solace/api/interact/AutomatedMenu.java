package net.solace.api.interact;

import java.util.function.Supplier;
import net.runelite.api.MenuAction;
import net.solace.api.Static;
import net.solace.api.coords.Coordinate;
import net.solace.api.domain.SceneEntity;
import net.solace.api.domain.game.IClient;
import net.solace.api.interact.Automation;
import net.solace.api.interact.InteractMethod;
import net.solace.api.magic.Spell;
import net.solace.api.widgets.Tab;

public class AutomatedMenu
implements Automation {
    private InteractMethod interactMethod;
    private String option;
    private String target;
    private int identifier;
    private MenuAction opcode;
    private int param0;
    private int param1;
    private Coordinate clickPoint;
    private int itemId;
    private SceneEntity entity;
    private int worldViewId;
    private Integer useItemId;
    private Integer useItemSlot;
    private Spell castSpell;
    private Supplier<Coordinate> clickPointSupplier;
    private boolean moveMouse;
    private boolean canvasClick;
    private int tickCount;
    private Tab interactionTab;

    public void queue(IClient client) {
        client.interact(this);
    }

    public String toString() {
        return "[AutomatedMenu] P0: " + this.param0 + " P1: " + this.param1 + " ID: " + this.identifier + " OP: " + String.valueOf(this.opcode) + " OPT: " + this.option + " TGT: " + this.target + " " + String.valueOf(this.clickPoint) + " ITEM: " + this.itemId + " WV: " + this.worldViewId;
    }

    private static InteractMethod $default$interactMethod() {
        return null;
    }

    private static String $default$option() {
        return "Automated";
    }

    private static String $default$target() {
        return "";
    }

    private static Coordinate $default$clickPoint() {
        return new Coordinate(-1, -1);
    }

    private static int $default$itemId() {
        return -1;
    }

    private static int $default$worldViewId() {
        return 0;
    }

    private static boolean $default$moveMouse() {
        return true;
    }

    private static boolean $default$canvasClick() {
        return true;
    }

    private static int $default$tickCount() {
        return Static.getClient().getTickCount();
    }

    private static Tab $default$interactionTab() {
        return null;
    }

    AutomatedMenu(InteractMethod interactMethod, String option, String target, int identifier, MenuAction opcode, int param0, int param1, Coordinate clickPoint, int itemId, SceneEntity entity, int worldViewId, Integer useItemId, Integer useItemSlot, Spell castSpell, Supplier<Coordinate> clickPointSupplier, boolean moveMouse, boolean canvasClick, int tickCount, Tab interactionTab) {
        this.interactMethod = interactMethod;
        this.option = option;
        this.target = target;
        this.identifier = identifier;
        this.opcode = opcode;
        this.param0 = param0;
        this.param1 = param1;
        this.clickPoint = clickPoint;
        this.itemId = itemId;
        this.entity = entity;
        this.worldViewId = worldViewId;
        this.useItemId = useItemId;
        this.useItemSlot = useItemSlot;
        this.castSpell = castSpell;
        this.clickPointSupplier = clickPointSupplier;
        this.moveMouse = moveMouse;
        this.canvasClick = canvasClick;
        this.tickCount = tickCount;
        this.interactionTab = interactionTab;
    }

    public static AutomatedMenuBuilder builder() {
        return new AutomatedMenuBuilder();
    }

    public AutomatedMenuBuilder toBuilder() {
        return new AutomatedMenuBuilder().interactMethod(this.interactMethod).option(this.option).target(this.target).identifier(this.identifier).opcode(this.opcode).param0(this.param0).param1(this.param1).clickPoint(this.clickPoint).itemId(this.itemId).entity(this.entity).worldViewId(this.worldViewId).useItemId(this.useItemId).useItemSlot(this.useItemSlot).castSpell(this.castSpell).clickPointSupplier(this.clickPointSupplier).moveMouse(this.moveMouse).canvasClick(this.canvasClick).tickCount(this.tickCount).interactionTab(this.interactionTab);
    }

    public InteractMethod getInteractMethod() {
        return this.interactMethod;
    }

    public String getOption() {
        return this.option;
    }

    public String getTarget() {
        return this.target;
    }

    public int getIdentifier() {
        return this.identifier;
    }

    public MenuAction getOpcode() {
        return this.opcode;
    }

    public int getParam0() {
        return this.param0;
    }

    public int getParam1() {
        return this.param1;
    }

    public Coordinate getClickPoint() {
        return this.clickPoint;
    }

    public int getItemId() {
        return this.itemId;
    }

    public SceneEntity getEntity() {
        return this.entity;
    }

    public int getWorldViewId() {
        return this.worldViewId;
    }

    public Integer getUseItemId() {
        return this.useItemId;
    }

    public Integer getUseItemSlot() {
        return this.useItemSlot;
    }

    public Spell getCastSpell() {
        return this.castSpell;
    }

    public Supplier<Coordinate> getClickPointSupplier() {
        return this.clickPointSupplier;
    }

    public boolean isMoveMouse() {
        return this.moveMouse;
    }

    public boolean isCanvasClick() {
        return this.canvasClick;
    }

    public int getTickCount() {
        return this.tickCount;
    }

    public Tab getInteractionTab() {
        return this.interactionTab;
    }

    public void setInteractMethod(InteractMethod interactMethod) {
        this.interactMethod = interactMethod;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public void setIdentifier(int identifier) {
        this.identifier = identifier;
    }

    public void setOpcode(MenuAction opcode) {
        this.opcode = opcode;
    }

    public void setParam0(int param0) {
        this.param0 = param0;
    }

    public void setParam1(int param1) {
        this.param1 = param1;
    }

    public void setClickPoint(Coordinate clickPoint) {
        this.clickPoint = clickPoint;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public void setEntity(SceneEntity entity) {
        this.entity = entity;
    }

    public void setWorldViewId(int worldViewId) {
        this.worldViewId = worldViewId;
    }

    public void setUseItemId(Integer useItemId) {
        this.useItemId = useItemId;
    }

    public void setUseItemSlot(Integer useItemSlot) {
        this.useItemSlot = useItemSlot;
    }

    public void setCastSpell(Spell castSpell) {
        this.castSpell = castSpell;
    }

    public void setClickPointSupplier(Supplier<Coordinate> clickPointSupplier) {
        this.clickPointSupplier = clickPointSupplier;
    }

    public void setMoveMouse(boolean moveMouse) {
        this.moveMouse = moveMouse;
    }

    public void setCanvasClick(boolean canvasClick) {
        this.canvasClick = canvasClick;
    }

    public void setTickCount(int tickCount) {
        this.tickCount = tickCount;
    }

    public void setInteractionTab(Tab interactionTab) {
        this.interactionTab = interactionTab;
    }

    public static class AutomatedMenuBuilder {
        private boolean interactMethod$set;
        private InteractMethod interactMethod$value;
        private boolean option$set;
        private String option$value;
        private boolean target$set;
        private String target$value;
        private int identifier;
        private MenuAction opcode;
        private int param0;
        private int param1;
        private boolean clickPoint$set;
        private Coordinate clickPoint$value;
        private boolean itemId$set;
        private int itemId$value;
        private SceneEntity entity;
        private boolean worldViewId$set;
        private int worldViewId$value;
        private Integer useItemId;
        private Integer useItemSlot;
        private Spell castSpell;
        private Supplier<Coordinate> clickPointSupplier;
        private boolean moveMouse$set;
        private boolean moveMouse$value;
        private boolean canvasClick$set;
        private boolean canvasClick$value;
        private boolean tickCount$set;
        private int tickCount$value;
        private boolean interactionTab$set;
        private Tab interactionTab$value;

        AutomatedMenuBuilder() {
        }

        public AutomatedMenuBuilder interactMethod(InteractMethod interactMethod) {
            this.interactMethod$value = interactMethod;
            this.interactMethod$set = true;
            return this;
        }

        public AutomatedMenuBuilder option(String option) {
            this.option$value = option;
            this.option$set = true;
            return this;
        }

        public AutomatedMenuBuilder target(String target) {
            this.target$value = target;
            this.target$set = true;
            return this;
        }

        public AutomatedMenuBuilder identifier(int identifier) {
            this.identifier = identifier;
            return this;
        }

        public AutomatedMenuBuilder opcode(MenuAction opcode) {
            this.opcode = opcode;
            return this;
        }

        public AutomatedMenuBuilder param0(int param0) {
            this.param0 = param0;
            return this;
        }

        public AutomatedMenuBuilder param1(int param1) {
            this.param1 = param1;
            return this;
        }

        public AutomatedMenuBuilder clickPoint(Coordinate clickPoint) {
            this.clickPoint$value = clickPoint;
            this.clickPoint$set = true;
            return this;
        }

        public AutomatedMenuBuilder itemId(int itemId) {
            this.itemId$value = itemId;
            this.itemId$set = true;
            return this;
        }

        public AutomatedMenuBuilder entity(SceneEntity entity) {
            this.entity = entity;
            return this;
        }

        public AutomatedMenuBuilder worldViewId(int worldViewId) {
            this.worldViewId$value = worldViewId;
            this.worldViewId$set = true;
            return this;
        }

        public AutomatedMenuBuilder useItemId(Integer useItemId) {
            this.useItemId = useItemId;
            return this;
        }

        public AutomatedMenuBuilder useItemSlot(Integer useItemSlot) {
            this.useItemSlot = useItemSlot;
            return this;
        }

        public AutomatedMenuBuilder castSpell(Spell castSpell) {
            this.castSpell = castSpell;
            return this;
        }

        public AutomatedMenuBuilder clickPointSupplier(Supplier<Coordinate> clickPointSupplier) {
            this.clickPointSupplier = clickPointSupplier;
            return this;
        }

        public AutomatedMenuBuilder moveMouse(boolean moveMouse) {
            this.moveMouse$value = moveMouse;
            this.moveMouse$set = true;
            return this;
        }

        public AutomatedMenuBuilder canvasClick(boolean canvasClick) {
            this.canvasClick$value = canvasClick;
            this.canvasClick$set = true;
            return this;
        }

        public AutomatedMenuBuilder tickCount(int tickCount) {
            this.tickCount$value = tickCount;
            this.tickCount$set = true;
            return this;
        }

        public AutomatedMenuBuilder interactionTab(Tab interactionTab) {
            this.interactionTab$value = interactionTab;
            this.interactionTab$set = true;
            return this;
        }

        public AutomatedMenu build() {
            InteractMethod interactMethod$value = this.interactMethod$value;
            if (!this.interactMethod$set) {
                interactMethod$value = AutomatedMenu.$default$interactMethod();
            }
            String option$value = this.option$value;
            if (!this.option$set) {
                option$value = AutomatedMenu.$default$option();
            }
            String target$value = this.target$value;
            if (!this.target$set) {
                target$value = AutomatedMenu.$default$target();
            }
            Coordinate clickPoint$value = this.clickPoint$value;
            if (!this.clickPoint$set) {
                clickPoint$value = AutomatedMenu.$default$clickPoint();
            }
            int itemId$value = this.itemId$value;
            if (!this.itemId$set) {
                itemId$value = AutomatedMenu.$default$itemId();
            }
            int worldViewId$value = this.worldViewId$value;
            if (!this.worldViewId$set) {
                worldViewId$value = AutomatedMenu.$default$worldViewId();
            }
            boolean moveMouse$value = this.moveMouse$value;
            if (!this.moveMouse$set) {
                moveMouse$value = AutomatedMenu.$default$moveMouse();
            }
            boolean canvasClick$value = this.canvasClick$value;
            if (!this.canvasClick$set) {
                canvasClick$value = AutomatedMenu.$default$canvasClick();
            }
            int tickCount$value = this.tickCount$value;
            if (!this.tickCount$set) {
                tickCount$value = AutomatedMenu.$default$tickCount();
            }
            Tab interactionTab$value = this.interactionTab$value;
            if (!this.interactionTab$set) {
                interactionTab$value = AutomatedMenu.$default$interactionTab();
            }
            return new AutomatedMenu(interactMethod$value, option$value, target$value, this.identifier, this.opcode, this.param0, this.param1, clickPoint$value, itemId$value, this.entity, worldViewId$value, this.useItemId, this.useItemSlot, this.castSpell, this.clickPointSupplier, moveMouse$value, canvasClick$value, tickCount$value, interactionTab$value);
        }

        public String toString() {
            return "AutomatedMenu.AutomatedMenuBuilder(interactMethod$value=" + String.valueOf((Object)this.interactMethod$value) + ", option$value=" + this.option$value + ", target$value=" + this.target$value + ", identifier=" + this.identifier + ", opcode=" + String.valueOf(this.opcode) + ", param0=" + this.param0 + ", param1=" + this.param1 + ", clickPoint$value=" + String.valueOf(this.clickPoint$value) + ", itemId$value=" + this.itemId$value + ", entity=" + String.valueOf(this.entity) + ", worldViewId$value=" + this.worldViewId$value + ", useItemId=" + this.useItemId + ", useItemSlot=" + this.useItemSlot + ", castSpell=" + String.valueOf(this.castSpell) + ", clickPointSupplier=" + String.valueOf(this.clickPointSupplier) + ", moveMouse$value=" + this.moveMouse$value + ", canvasClick$value=" + this.canvasClick$value + ", tickCount$value=" + this.tickCount$value + ", interactionTab$value=" + String.valueOf((Object)this.interactionTab$value) + ")";
        }
    }
}

