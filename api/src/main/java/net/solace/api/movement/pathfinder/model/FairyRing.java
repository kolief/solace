package net.solace.api.movement.pathfinder.model;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import net.runelite.api.MenuAction;
import net.runelite.api.Quest;
import net.runelite.api.QuestState;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.util.Text;
import net.solace.api.Static;
import net.solace.api.domain.game.IClient;
import net.solace.api.domain.widgets.IWidget;
import net.solace.api.interact.AutomatedMenu;
import net.solace.api.movement.pathfinder.model.MovementConstants;
import net.solace.api.movement.pathfinder.model.Transport;
import net.solace.api.movement.pathfinder.model.requirement.ItemRequirement;
import net.solace.api.movement.pathfinder.model.requirement.QuestRequirement;
import net.solace.api.movement.pathfinder.model.requirement.Reduction;
import net.solace.api.movement.pathfinder.model.requirement.Requirement;
import net.solace.api.movement.pathfinder.model.requirement.Requirements;
import net.solace.api.widgets.IWidgets;

public enum FairyRing {
    AIQ("AIQ", new WorldPoint(2996, 3114, 0)),
    AIR("AIR", new WorldPoint(2700, 3247, 0)),
    AIS("AIS", new WorldPoint(1429, 3324, 0), Requirements.of(Requirement.VISITED_VARLAMORE)),
    AJP("AJP", new WorldPoint(1651, 3010, 0), new Requirements().addRequirement(Requirement.VISITED_VARLAMORE)),
    AJQ("AJQ", new WorldPoint(2735, 5221, 0), new Requirements().addRequirement(new QuestRequirement(Quest.DEATH_TO_THE_DORGESHUUN, Set.of(QuestState.FINISHED))).addRequirement(new ItemRequirement(Reduction.OR, Arrays.stream(MovementConstants.LIGHT_SOURCES).boxed().collect(Collectors.toList()), ItemRequirement.Location.EITHER, 1))),
    AJR("AJR", new WorldPoint(2780, 3613, 0)),
    AJS("AJS", new WorldPoint(2500, 3896, 0)),
    AKP("AKP", new WorldPoint(3284, 2706, 0)),
    AKQ("AKQ", new WorldPoint(2319, 3619, 0)),
    AKS("AKS", new WorldPoint(2571, 2956, 0)),
    AKR("AKR", new WorldPoint(1826, 3540, 0)),
    ALP("ALP", new WorldPoint(2503, 3636, 0)),
    ALQ("ALQ", new WorldPoint(3597, 3495, 0)),
    ALR("ALR", new WorldPoint(3059, 4875, 0)),
    ALS("ALS", new WorldPoint(2644, 3495, 0)),
    BIP("BIP", new WorldPoint(3410, 3324, 0)),
    BIQ("BIQ", new WorldPoint(3251, 3095, 0)),
    BIS("BIS", new WorldPoint(2635, 3266, 0)),
    BJP("BJP", new WorldPoint(2267, 2976, 0)),
    BJR("BJR", new WorldPoint(2650, 4730, 0)),
    BJS("BJS", new WorldPoint(2150, 3070, 0)),
    BKP("BKP", new WorldPoint(2385, 3035, 0)),
    BKQ("BKQ", new WorldPoint(3041, 4532, 0)),
    BKR("BKR", new WorldPoint(3469, 3431, 0)),
    BLP("BLP", new WorldPoint(2437, 5126, 0)),
    BLR("BLR", new WorldPoint(2740, 3351, 0)),
    BLS("BLS", new WorldPoint(1295, 3493, 0)),
    CIP("CIP", new WorldPoint(2513, 3884, 0)),
    CIS("CIS", new WorldPoint(1639, 3868, 0)),
    CIR("CIR", new WorldPoint(1302, 3762, 0)),
    CIQ("CIQ", new WorldPoint(2528, 3127, 0)),
    CJQ("CJQ", new WorldPoint(3178, 2447, 0), new Requirements().addRequirement(new QuestRequirement(Quest.TROUBLED_TORTUGANS, Set.of(QuestState.IN_PROGRESS, QuestState.FINISHED)))),
    CJR("CJR", new WorldPoint(2705, 3576, 0)),
    CKP("CKP", new WorldPoint(2075, 4848, 0)),
    CKQ("CKQ", new WorldPoint(1359, 2941, 0), new Requirements().addRequirement(new QuestRequirement(Quest.CHILDREN_OF_THE_SUN, Set.of(QuestState.FINISHED)))),
    CKR("CKR", new WorldPoint(2801, 3003, 0)),
    CKS("CKS", new WorldPoint(3447, 3470, 0)),
    CLP("CLP", new WorldPoint(3082, 3206, 0)),
    CLR("CLR", new WorldPoint(2740, 2738, 0)),
    CLS("CLS", new WorldPoint(2682, 3081, 0)),
    DIP("DIP", new WorldPoint(3037, 4763, 0)),
    DIS("DIS", new WorldPoint(3108, 3149, 0)),
    DIR("DIR", new WorldPoint(3038, 5348, 0)),
    DJP("DJP", new WorldPoint(2658, 3230, 0)),
    DJR("DJR", new WorldPoint(1455, 3658, 0)),
    DKP("DKP", new WorldPoint(2900, 3111, 0)),
    DKR("DKR", new WorldPoint(3129, 3496, 0)),
    DKS("DKS", new WorldPoint(2744, 3719, 0)),
    DLP("DLS", new WorldPoint(3447, 9824, 0)),
    DLS("DLP", new WorldPoint(2926, 10455, 0)),
    DLQ("DLQ", new WorldPoint(3423, 3016, 0)),
    DLR("DLR", new WorldPoint(2213, 3099, 0)),
    ZANARIS("Zanaris", new WorldPoint(2412, 4434, 0));

    private static final String[][] CODES;
    private static final int[][] TURN_INDICES;
    private final String code;
    private final WorldPoint location;
    private final Requirements requirements;
    private static final FairyRing[] VALUES;

    private FairyRing(String code, WorldPoint location, Requirements requirements) {
        this.code = code;
        this.location = location;
        this.requirements = requirements;
    }

    private FairyRing(String code, WorldPoint location) {
        this(code, location, new Requirements());
    }

    public static String getCurrentCode() {
        return Static.getGameThread().invokeAndWait(() -> CODES[0][Static.getVars().getBit(3985)] + CODES[1][Static.getVars().getBit(3986)] + CODES[2][Static.getVars().getBit(3987)]);
    }

    public static FairyRing getNearest() {
        return Arrays.stream(VALUES).min(Comparator.comparingInt(a -> a.getLocation().distanceTo(Static.getPlayers().getLocal().getWorldLocation()))).orElse(null);
    }

    public static FairyRing[] getAll() {
        return VALUES;
    }

    public static Set<FairyRing> getAllWithNoRequirements() {
        return Arrays.stream(VALUES).filter(fairyRing -> fairyRing.getRequirements().isEmpty()).collect(Collectors.toSet());
    }

    public static List<Transport> getTransports() {
        return Static.getSolaceConfig().fairyRings().stream().filter(Objects::nonNull).filter(source -> source.getRequirements().fulfilled()).flatMap(sourceRing -> Static.getSolaceConfig().fairyRings().stream().filter(Objects::nonNull).filter(destRing -> !sourceRing.equals(destRing)).map(destRing -> Static.getTransportLoader().fairyRingTransport((FairyRing)((Object)sourceRing), (FairyRing)((Object)((Object)destRing))))).collect(Collectors.toList());
    }

    public boolean validate() {
        return FairyRing.getCurrentCode().equalsIgnoreCase(this.getCode());
    }

    public static boolean validateCode(String code) {
        return FairyRing.getCurrentCode().equalsIgnoreCase(code);
    }

    public void setCode() {
        FairyRing.setCode(this.getCode());
    }

    public static void setCode(String targetCode) {
        String currentCode;
        List<IWidget> items;
        IWidgets widgets = Static.getWidgets();
        IWidget travelLog = widgets.get(24969224);
        IClient client = Static.getClient();
        if (widgets.isVisible(travelLog) && !(items = widgets.getAll(381)).isEmpty()) {
            for (IWidget item : items) {
                String code;
                if (!item.hasAction(new String[]{"Use code"}) || !(code = Text.standardize((String)item.getName()).replace(" ", "")).equalsIgnoreCase(targetCode)) continue;
                AutomatedMenu menu = AutomatedMenu.builder().identifier(1).opcode(MenuAction.CC_OP).param0(-1).param1(item.getId()).clickPoint(item.getClickPoint()).build();
                client.interact(menu);
                return;
            }
        }
        if ((currentCode = FairyRing.getCurrentCode()).charAt(0) != targetCode.charAt(0)) {
            widgets.get(TURN_INDICES[0][0]).interact(0);
            return;
        }
        if (currentCode.charAt(1) != targetCode.charAt(1)) {
            widgets.get(TURN_INDICES[1][0]).interact(0);
            return;
        }
        if (currentCode.charAt(2) != targetCode.charAt(2)) {
            widgets.get(TURN_INDICES[2][0]).interact(0);
        }
    }

    public void travel() {
        FairyRing.travel(this.getCode());
    }

    public static void travel(String code) {
        if (FairyRing.validateCode(code)) {
            IWidget confirm = Static.getWidgets().get(26083354);
            if (!Static.getWidgets().isVisible(confirm)) {
                return;
            }
            AutomatedMenu menu = AutomatedMenu.builder().identifier(1).opcode(MenuAction.CC_OP).param0(-1).param1(confirm.getId()).clickPoint(confirm.getClickPoint()).build();
            Static.getClient().interact(menu);
        } else {
            FairyRing.setCode(code);
        }
    }

    public String toString() {
        if (this.code.length() == 3) {
            if (this.code.equals("DIQ")) {
                return "D I Q (House)";
            }
            return this.code.charAt(0) + " " + this.code.charAt(1) + " " + this.code.charAt(2);
        }
        return this.code;
    }

    public String getCode() {
        return this.code;
    }

    public WorldPoint getLocation() {
        return this.location;
    }

    public Requirements getRequirements() {
        return this.requirements;
    }

    static {
        CODES = new String[][]{{"A", "D", "C", "B"}, {"I", "L", "K", "J"}, {"P", "S", "R", "Q"}};
        TURN_INDICES = new int[][]{{26083347, 26083348}, {26083349, 26083350}, {26083351, 26083352}};
        VALUES = FairyRing.values();
    }
}

