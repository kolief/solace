package net.solace.sdk.items.info;

import java.util.Map;

public class ItemInfo {
    private final int id;
    private final double weight;
    private final int slot;
    private final String equipmentType;
    private final EquipmentDefinition equipmentDefinition;

    public int getId() {
        return this.id;
    }

    public double getWeight() {
        return this.weight;
    }

    public int getSlot() {
        return this.slot;
    }

    public String getEquipmentType() {
        return this.equipmentType;
    }

    public EquipmentDefinition getEquipmentDefinition() {
        return this.equipmentDefinition;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ItemInfo)) {
            return false;
        }
        ItemInfo other = (ItemInfo)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (this.getId() != other.getId()) {
            return false;
        }
        if (Double.compare(this.getWeight(), other.getWeight()) != 0) {
            return false;
        }
        if (this.getSlot() != other.getSlot()) {
            return false;
        }
        String this$equipmentType = this.getEquipmentType();
        String other$equipmentType = other.getEquipmentType();
        if (this$equipmentType == null ? other$equipmentType != null : !this$equipmentType.equals(other$equipmentType)) {
            return false;
        }
        EquipmentDefinition this$equipmentDefinition = this.getEquipmentDefinition();
        EquipmentDefinition other$equipmentDefinition = other.getEquipmentDefinition();
        return !(this$equipmentDefinition == null ? other$equipmentDefinition != null : !((Object)this$equipmentDefinition).equals(other$equipmentDefinition));
    }

    protected boolean canEqual(Object other) {
        return other instanceof ItemInfo;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getId();
        long $weight = Double.doubleToLongBits(this.getWeight());
        result = result * 59 + (int)($weight >>> 32 ^ $weight);
        result = result * 59 + this.getSlot();
        String $equipmentType = this.getEquipmentType();
        result = result * 59 + ($equipmentType == null ? 43 : $equipmentType.hashCode());
        EquipmentDefinition $equipmentDefinition = this.getEquipmentDefinition();
        result = result * 59 + ($equipmentDefinition == null ? 43 : ((Object)$equipmentDefinition).hashCode());
        return result;
    }

    public String toString() {
        return "ItemInfo(id=" + this.getId() + ", weight=" + this.getWeight() + ", slot=" + this.getSlot() + ", equipmentType=" + this.getEquipmentType() + ", equipmentDefinition=" + String.valueOf(this.getEquipmentDefinition()) + ")";
    }

    public ItemInfo(int id, double weight, int slot, String equipmentType, EquipmentDefinition equipmentDefinition) {
        this.id = id;
        this.weight = weight;
        this.slot = slot;
        this.equipmentType = equipmentType;
        this.equipmentDefinition = equipmentDefinition;
    }

    public class WeaponDefinition {
        private final boolean twoHanded;
        private final int blockAnimation;
        private final int standAnimation;
        private final int walkAnimation;
        private final int runAnimation;
        private final int standTurnAnimation;
        private final int rotate90Animation;
        private final int rotate180Animation;
        private final int rotate270Animation;
        private final int accurateAnimation;
        private final int aggressiveAnimation;
        private final int controlledAnimation;
        private final int defensiveAnimation;
        private final Integer specialAttackAnimation;
        private final int attackSpeed;
        private final int interfaceVarbit;
        private final int normalAttackDistance;
        private final int longAttackDistance;

        public boolean isTwoHanded() {
            return this.twoHanded;
        }

        public int getBlockAnimation() {
            return this.blockAnimation;
        }

        public int getStandAnimation() {
            return this.standAnimation;
        }

        public int getWalkAnimation() {
            return this.walkAnimation;
        }

        public int getRunAnimation() {
            return this.runAnimation;
        }

        public int getStandTurnAnimation() {
            return this.standTurnAnimation;
        }

        public int getRotate90Animation() {
            return this.rotate90Animation;
        }

        public int getRotate180Animation() {
            return this.rotate180Animation;
        }

        public int getRotate270Animation() {
            return this.rotate270Animation;
        }

        public int getAccurateAnimation() {
            return this.accurateAnimation;
        }

        public int getAggressiveAnimation() {
            return this.aggressiveAnimation;
        }

        public int getControlledAnimation() {
            return this.controlledAnimation;
        }

        public int getDefensiveAnimation() {
            return this.defensiveAnimation;
        }

        public Integer getSpecialAttackAnimation() {
            return this.specialAttackAnimation;
        }

        public int getAttackSpeed() {
            return this.attackSpeed;
        }

        public int getInterfaceVarbit() {
            return this.interfaceVarbit;
        }

        public int getNormalAttackDistance() {
            return this.normalAttackDistance;
        }

        public int getLongAttackDistance() {
            return this.longAttackDistance;
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof WeaponDefinition)) {
                return false;
            }
            WeaponDefinition other = (WeaponDefinition)o;
            if (!other.canEqual(this)) {
                return false;
            }
            if (this.isTwoHanded() != other.isTwoHanded()) {
                return false;
            }
            if (this.getBlockAnimation() != other.getBlockAnimation()) {
                return false;
            }
            if (this.getStandAnimation() != other.getStandAnimation()) {
                return false;
            }
            if (this.getWalkAnimation() != other.getWalkAnimation()) {
                return false;
            }
            if (this.getRunAnimation() != other.getRunAnimation()) {
                return false;
            }
            if (this.getStandTurnAnimation() != other.getStandTurnAnimation()) {
                return false;
            }
            if (this.getRotate90Animation() != other.getRotate90Animation()) {
                return false;
            }
            if (this.getRotate180Animation() != other.getRotate180Animation()) {
                return false;
            }
            if (this.getRotate270Animation() != other.getRotate270Animation()) {
                return false;
            }
            if (this.getAccurateAnimation() != other.getAccurateAnimation()) {
                return false;
            }
            if (this.getAggressiveAnimation() != other.getAggressiveAnimation()) {
                return false;
            }
            if (this.getControlledAnimation() != other.getControlledAnimation()) {
                return false;
            }
            if (this.getDefensiveAnimation() != other.getDefensiveAnimation()) {
                return false;
            }
            if (this.getAttackSpeed() != other.getAttackSpeed()) {
                return false;
            }
            if (this.getInterfaceVarbit() != other.getInterfaceVarbit()) {
                return false;
            }
            if (this.getNormalAttackDistance() != other.getNormalAttackDistance()) {
                return false;
            }
            if (this.getLongAttackDistance() != other.getLongAttackDistance()) {
                return false;
            }
            Integer this$specialAttackAnimation = this.getSpecialAttackAnimation();
            Integer other$specialAttackAnimation = other.getSpecialAttackAnimation();
            return !(this$specialAttackAnimation == null ? other$specialAttackAnimation != null : !((Object)this$specialAttackAnimation).equals(other$specialAttackAnimation));
        }

        protected boolean canEqual(Object other) {
            return other instanceof WeaponDefinition;
        }

        public int hashCode() {
            int PRIME = 59;
            int result = 1;
            result = result * 59 + (this.isTwoHanded() ? 79 : 97);
            result = result * 59 + this.getBlockAnimation();
            result = result * 59 + this.getStandAnimation();
            result = result * 59 + this.getWalkAnimation();
            result = result * 59 + this.getRunAnimation();
            result = result * 59 + this.getStandTurnAnimation();
            result = result * 59 + this.getRotate90Animation();
            result = result * 59 + this.getRotate180Animation();
            result = result * 59 + this.getRotate270Animation();
            result = result * 59 + this.getAccurateAnimation();
            result = result * 59 + this.getAggressiveAnimation();
            result = result * 59 + this.getControlledAnimation();
            result = result * 59 + this.getDefensiveAnimation();
            result = result * 59 + this.getAttackSpeed();
            result = result * 59 + this.getInterfaceVarbit();
            result = result * 59 + this.getNormalAttackDistance();
            result = result * 59 + this.getLongAttackDistance();
            Integer $specialAttackAnimation = this.getSpecialAttackAnimation();
            result = result * 59 + ($specialAttackAnimation == null ? 43 : ((Object)$specialAttackAnimation).hashCode());
            return result;
        }

        public String toString() {
            return "ItemInfo.WeaponDefinition(twoHanded=" + this.isTwoHanded() + ", blockAnimation=" + this.getBlockAnimation() + ", standAnimation=" + this.getStandAnimation() + ", walkAnimation=" + this.getWalkAnimation() + ", runAnimation=" + this.getRunAnimation() + ", standTurnAnimation=" + this.getStandTurnAnimation() + ", rotate90Animation=" + this.getRotate90Animation() + ", rotate180Animation=" + this.getRotate180Animation() + ", rotate270Animation=" + this.getRotate270Animation() + ", accurateAnimation=" + this.getAccurateAnimation() + ", aggressiveAnimation=" + this.getAggressiveAnimation() + ", controlledAnimation=" + this.getControlledAnimation() + ", defensiveAnimation=" + this.getDefensiveAnimation() + ", specialAttackAnimation=" + this.getSpecialAttackAnimation() + ", attackSpeed=" + this.getAttackSpeed() + ", interfaceVarbit=" + this.getInterfaceVarbit() + ", normalAttackDistance=" + this.getNormalAttackDistance() + ", longAttackDistance=" + this.getLongAttackDistance() + ")";
        }

        public WeaponDefinition(boolean twoHanded, int blockAnimation, int standAnimation, int walkAnimation, int runAnimation, int standTurnAnimation, int rotate90Animation, int rotate180Animation, int rotate270Animation, int accurateAnimation, int aggressiveAnimation, int controlledAnimation, int defensiveAnimation, Integer specialAttackAnimation, int attackSpeed, int interfaceVarbit, int normalAttackDistance, int longAttackDistance) {
            this.twoHanded = twoHanded;
            this.blockAnimation = blockAnimation;
            this.standAnimation = standAnimation;
            this.walkAnimation = walkAnimation;
            this.runAnimation = runAnimation;
            this.standTurnAnimation = standTurnAnimation;
            this.rotate90Animation = rotate90Animation;
            this.rotate180Animation = rotate180Animation;
            this.rotate270Animation = rotate270Animation;
            this.accurateAnimation = accurateAnimation;
            this.aggressiveAnimation = aggressiveAnimation;
            this.controlledAnimation = controlledAnimation;
            this.defensiveAnimation = defensiveAnimation;
            this.specialAttackAnimation = specialAttackAnimation;
            this.attackSpeed = attackSpeed;
            this.interfaceVarbit = interfaceVarbit;
            this.normalAttackDistance = normalAttackDistance;
            this.longAttackDistance = longAttackDistance;
        }
    }

    public class EquipmentDefinition {
        private final EquipmentBonuses bonuses;
        private final Map<Integer, Integer> requirements;
        private final WeaponDefinition weaponDefinition;

        public EquipmentBonuses getBonuses() {
            return this.bonuses;
        }

        public Map<Integer, Integer> getRequirements() {
            return this.requirements;
        }

        public WeaponDefinition getWeaponDefinition() {
            return this.weaponDefinition;
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof EquipmentDefinition)) {
                return false;
            }
            EquipmentDefinition other = (EquipmentDefinition)o;
            if (!other.canEqual(this)) {
                return false;
            }
            EquipmentBonuses this$bonuses = this.getBonuses();
            EquipmentBonuses other$bonuses = other.getBonuses();
            if (this$bonuses == null ? other$bonuses != null : !((Object)this$bonuses).equals(other$bonuses)) {
                return false;
            }
            Map<Integer, Integer> this$requirements = this.getRequirements();
            Map<Integer, Integer> other$requirements = other.getRequirements();
            if (this$requirements == null ? other$requirements != null : !((Object)this$requirements).equals(other$requirements)) {
                return false;
            }
            WeaponDefinition this$weaponDefinition = this.getWeaponDefinition();
            WeaponDefinition other$weaponDefinition = other.getWeaponDefinition();
            return !(this$weaponDefinition == null ? other$weaponDefinition != null : !((Object)this$weaponDefinition).equals(other$weaponDefinition));
        }

        protected boolean canEqual(Object other) {
            return other instanceof EquipmentDefinition;
        }

        public int hashCode() {
            int PRIME = 59;
            int result = 1;
            EquipmentBonuses $bonuses = this.getBonuses();
            result = result * 59 + ($bonuses == null ? 43 : ((Object)$bonuses).hashCode());
            Map<Integer, Integer> $requirements = this.getRequirements();
            result = result * 59 + ($requirements == null ? 43 : ((Object)$requirements).hashCode());
            WeaponDefinition $weaponDefinition = this.getWeaponDefinition();
            result = result * 59 + ($weaponDefinition == null ? 43 : ((Object)$weaponDefinition).hashCode());
            return result;
        }

        public String toString() {
            return "ItemInfo.EquipmentDefinition(bonuses=" + String.valueOf(this.getBonuses()) + ", requirements=" + String.valueOf(this.getRequirements()) + ", weaponDefinition=" + String.valueOf(this.getWeaponDefinition()) + ")";
        }

        public EquipmentDefinition(EquipmentBonuses bonuses, Map<Integer, Integer> requirements, WeaponDefinition weaponDefinition) {
            this.bonuses = bonuses;
            this.requirements = requirements;
            this.weaponDefinition = weaponDefinition;
        }
    }

    public class EquipmentBonuses {
        private final int attStab;
        private final int attSlash;
        private final int attCrush;
        private final int attMagic;
        private final int attRange;
        private final int defStab;
        private final int defSlash;
        private final int defCrush;
        private final int defMagic;
        private final int defRange;
        private final int meleeStrength;
        private final int rangedStrength;
        private final int magicDamage;
        private final int prayer;

        public EquipmentBonuses plus(EquipmentBonuses bonuses) {
            return new EquipmentBonuses(this.attStab + bonuses.attStab, this.attSlash + bonuses.attSlash, this.attCrush + bonuses.attCrush, this.attMagic + bonuses.attMagic, this.attRange + bonuses.attRange, this.defStab + bonuses.defStab, this.defSlash + bonuses.defSlash, this.defCrush + bonuses.defCrush, this.defMagic + bonuses.defMagic, this.defRange + bonuses.defRange, this.meleeStrength + bonuses.meleeStrength, this.rangedStrength + bonuses.rangedStrength, this.magicDamage + bonuses.magicDamage, this.prayer + bonuses.prayer);
        }

        public int getAttStab() {
            return this.attStab;
        }

        public int getAttSlash() {
            return this.attSlash;
        }

        public int getAttCrush() {
            return this.attCrush;
        }

        public int getAttMagic() {
            return this.attMagic;
        }

        public int getAttRange() {
            return this.attRange;
        }

        public int getDefStab() {
            return this.defStab;
        }

        public int getDefSlash() {
            return this.defSlash;
        }

        public int getDefCrush() {
            return this.defCrush;
        }

        public int getDefMagic() {
            return this.defMagic;
        }

        public int getDefRange() {
            return this.defRange;
        }

        public int getMeleeStrength() {
            return this.meleeStrength;
        }

        public int getRangedStrength() {
            return this.rangedStrength;
        }

        public int getMagicDamage() {
            return this.magicDamage;
        }

        public int getPrayer() {
            return this.prayer;
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof EquipmentBonuses)) {
                return false;
            }
            EquipmentBonuses other = (EquipmentBonuses)o;
            if (!other.canEqual(this)) {
                return false;
            }
            if (this.getAttStab() != other.getAttStab()) {
                return false;
            }
            if (this.getAttSlash() != other.getAttSlash()) {
                return false;
            }
            if (this.getAttCrush() != other.getAttCrush()) {
                return false;
            }
            if (this.getAttMagic() != other.getAttMagic()) {
                return false;
            }
            if (this.getAttRange() != other.getAttRange()) {
                return false;
            }
            if (this.getDefStab() != other.getDefStab()) {
                return false;
            }
            if (this.getDefSlash() != other.getDefSlash()) {
                return false;
            }
            if (this.getDefCrush() != other.getDefCrush()) {
                return false;
            }
            if (this.getDefMagic() != other.getDefMagic()) {
                return false;
            }
            if (this.getDefRange() != other.getDefRange()) {
                return false;
            }
            if (this.getMeleeStrength() != other.getMeleeStrength()) {
                return false;
            }
            if (this.getRangedStrength() != other.getRangedStrength()) {
                return false;
            }
            if (this.getMagicDamage() != other.getMagicDamage()) {
                return false;
            }
            return this.getPrayer() == other.getPrayer();
        }

        protected boolean canEqual(Object other) {
            return other instanceof EquipmentBonuses;
        }

        public int hashCode() {
            int PRIME = 59;
            int result = 1;
            result = result * 59 + this.getAttStab();
            result = result * 59 + this.getAttSlash();
            result = result * 59 + this.getAttCrush();
            result = result * 59 + this.getAttMagic();
            result = result * 59 + this.getAttRange();
            result = result * 59 + this.getDefStab();
            result = result * 59 + this.getDefSlash();
            result = result * 59 + this.getDefCrush();
            result = result * 59 + this.getDefMagic();
            result = result * 59 + this.getDefRange();
            result = result * 59 + this.getMeleeStrength();
            result = result * 59 + this.getRangedStrength();
            result = result * 59 + this.getMagicDamage();
            result = result * 59 + this.getPrayer();
            return result;
        }

        public EquipmentBonuses(int attStab, int attSlash, int attCrush, int attMagic, int attRange, int defStab, int defSlash, int defCrush, int defMagic, int defRange, int meleeStrength, int rangedStrength, int magicDamage, int prayer) {
            this.attStab = attStab;
            this.attSlash = attSlash;
            this.attCrush = attCrush;
            this.attMagic = attMagic;
            this.attRange = attRange;
            this.defStab = defStab;
            this.defSlash = defSlash;
            this.defCrush = defCrush;
            this.defMagic = defMagic;
            this.defRange = defRange;
            this.meleeStrength = meleeStrength;
            this.rangedStrength = rangedStrength;
            this.magicDamage = magicDamage;
            this.prayer = prayer;
        }

        public String toString() {
            return "ItemInfo.EquipmentBonuses(attStab=" + this.getAttStab() + ", attSlash=" + this.getAttSlash() + ", attCrush=" + this.getAttCrush() + ", attMagic=" + this.getAttMagic() + ", attRange=" + this.getAttRange() + ", defStab=" + this.getDefStab() + ", defSlash=" + this.getDefSlash() + ", defCrush=" + this.getDefCrush() + ", defMagic=" + this.getDefMagic() + ", defRange=" + this.getDefRange() + ", meleeStrength=" + this.getMeleeStrength() + ", rangedStrength=" + this.getRangedStrength() + ", magicDamage=" + this.getMagicDamage() + ", prayer=" + this.getPrayer() + ")";
        }
    }
}

