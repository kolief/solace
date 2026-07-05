package net.solace.api.movement.pathfinder.model;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import net.runelite.api.coords.WorldPoint;
import net.solace.api.Static;
import net.solace.api.domain.items.IItem;
import net.solace.api.movement.pathfinder.model.Transport;
import net.solace.api.movement.pathfinder.model.requirement.ItemRequirement;
import net.solace.api.movement.pathfinder.model.requirement.QuestRequirement;
import net.solace.api.movement.pathfinder.model.requirement.Reduction;
import net.solace.api.movement.pathfinder.model.requirement.Requirement;
import net.solace.api.movement.pathfinder.model.requirement.Requirements;
import net.solace.api.movement.pathfinder.model.requirement.SkillRequirement;
import net.solace.api.movement.pathfinder.model.requirement.VarRequirement;
import net.solace.api.widgets.EquipmentSlot;

public enum MagicCarpet {
    SHANTAY_PASS_TO_UZER(CarpetLocation.SHANTAY_PASS, CarpetLocation.UZER, Requirement.THE_GOLEM),
    SHANTAY_PASS_TO_BEDABIN_CAMP(CarpetLocation.SHANTAY_PASS, CarpetLocation.BEDABIN_CAMP, new Requirement[0]),
    SHANTAY_PASS_TO_POLLNIVNEACH(CarpetLocation.SHANTAY_PASS, CarpetLocation.NORTH_POLLNIVNEACH, new Requirement[0]),
    UZER_TO_SHANTAY_PASS(CarpetLocation.UZER, CarpetLocation.SHANTAY_PASS, Requirement.THE_GOLEM),
    BEDABIN_CAMP_TO_SHANTAY_PASS(CarpetLocation.BEDABIN_CAMP, CarpetLocation.SHANTAY_PASS, new Requirement[0]),
    NORTH_POLLNIVNEACH_TO_SHANTAY_PASS(CarpetLocation.NORTH_POLLNIVNEACH, CarpetLocation.SHANTAY_PASS, new Requirement[0]),
    SOUTH_POLLNIVNEACH_TO_SOPHANEM(CarpetLocation.SOUTH_POLLNIVNEACH, CarpetLocation.SOPHANEM, Requirement.ICTHLARINS_LITTLE_HELPER),
    SOUTH_POLLNIVNEACH_TO_MENAPHOS(CarpetLocation.SOUTH_POLLNIVNEACH, CarpetLocation.MENAPHOS, Requirement.ICTHLARINS_LITTLE_HELPER),
    SOUTH_POLLNIVNEACH_TO_NARDAH(CarpetLocation.SOUTH_POLLNIVNEACH, CarpetLocation.NARDAH, new Requirement[0]),
    SOPHANEM_TO_SOUTH_POLLNIVNEACH(CarpetLocation.SOPHANEM, CarpetLocation.SOUTH_POLLNIVNEACH, Requirement.ICTHLARINS_LITTLE_HELPER),
    MENAPHOS_TO_SOUTH_POLLNIVNEACH(CarpetLocation.MENAPHOS, CarpetLocation.SOUTH_POLLNIVNEACH, Requirement.ICTHLARINS_LITTLE_HELPER),
    NARDAH_TO_SOUTH_POLLNIVNEACH(CarpetLocation.NARDAH, CarpetLocation.SOUTH_POLLNIVNEACH, new Requirement[0]);

    private final CarpetLocation origin;
    private final CarpetLocation destination;
    private final Requirement[] requirements;
    private static final MagicCarpet[] VALUES;

    private MagicCarpet(CarpetLocation origin, CarpetLocation destination, Requirement ... requirements) {
        this.origin = origin;
        this.destination = destination;
        this.requirements = requirements;
    }

    public static int getCarpetCost(WorldPoint source, WorldPoint destination) {
        for (MagicCarpet carpet : VALUES) {
            if (!carpet.getOrigin().getLocation().equals((Object)source) || !carpet.getDestination().getLocation().equals((Object)destination)) continue;
            return carpet.getCost();
        }
        return 0;
    }

    public static List<Transport> getCarpetTransports() {
        return Arrays.stream(VALUES).filter(carpet -> carpet.getRequirements().fulfilled()).map(Static.getTransportLoader()::magicCarpet).collect(Collectors.toList());
    }

    public int getCost() {
        IItem ring = Static.getEquipment().fromSlot(EquipmentSlot.RING);
        if (ring != null && ring.getId() == 6465) {
            return 100;
        }
        if (Static.getVars().getBit(4485) == 1) {
            return 0;
        }
        return 200;
    }

    public MagicCarpet getNearest() {
        WorldPoint playerLocation = Static.getPlayers().getLocal().getWorldLocation();
        MagicCarpet nearest = null;
        int distance = Integer.MAX_VALUE;
        for (MagicCarpet carpet : VALUES) {
            if (carpet.getOrigin().getLocation().distanceTo(playerLocation) >= distance) continue;
            nearest = carpet;
            distance = carpet.getOrigin().getLocation().distanceTo(playerLocation);
        }
        return nearest;
    }

    public Requirements getRequirements() {
        Requirements req = new Requirements();
        if (this.requirements != null) {
            for (Requirement requirement : this.requirements) {
                if (requirement instanceof QuestRequirement) {
                    req.getQuestRequirements().add((QuestRequirement)requirement);
                    continue;
                }
                if (requirement instanceof ItemRequirement) {
                    req.getItemRequirements().add((ItemRequirement)requirement);
                    continue;
                }
                if (requirement instanceof SkillRequirement) {
                    req.getSkillRequirements().add((SkillRequirement)requirement);
                    continue;
                }
                if (requirement instanceof VarRequirement) {
                    req.getVarRequirements().add((VarRequirement)requirement);
                    continue;
                }
                req.addRequirement(requirement);
            }
        }
        req.getItemRequirements().add(new ItemRequirement(Reduction.AND, List.of(Integer.valueOf(995)), this.getCost()));
        return req;
    }

    public CarpetLocation getOrigin() {
        return this.origin;
    }

    public CarpetLocation getDestination() {
        return this.destination;
    }

    static {
        VALUES = MagicCarpet.values();
    }

    public static enum CarpetLocation {
        SHANTAY_PASS(new WorldPoint(3309, 3110, 0), "Shantay Pass"),
        NORTH_POLLNIVNEACH(new WorldPoint(3351, 3003, 0), "Pollnivneach"),
        SOUTH_POLLNIVNEACH(new WorldPoint(3351, 2943, 0), "Pollnivneach"),
        BEDABIN_CAMP(new WorldPoint(3181, 3045, 0), "Bedabin camp"),
        UZER(new WorldPoint(3470, 3113, 0), "Uzer"),
        NARDAH(new WorldPoint(3402, 2916, 0), "Nardah"),
        SOPHANEM(new WorldPoint(3286, 2813, 0), "Sophanem"),
        MENAPHOS(new WorldPoint(3246, 2815, 0), "Menaphos");

        private final WorldPoint location;
        private final String name;

        private CarpetLocation(WorldPoint location, String name) {
            this.location = location;
            this.name = name;
        }

        public WorldPoint getLocation() {
            return this.location;
        }

        public String getName() {
            return this.name;
        }
    }
}

