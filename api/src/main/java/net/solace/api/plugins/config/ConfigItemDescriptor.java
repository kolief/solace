package net.solace.api.plugins.config;

import java.lang.reflect.Type;
import net.solace.api.plugins.config.Alpha;
import net.solace.api.plugins.config.ConfigItem;
import net.solace.api.plugins.config.ConfigObject;
import net.solace.api.plugins.config.Range;
import net.solace.api.plugins.config.Units;

public final class ConfigItemDescriptor
implements ConfigObject {
    private final ConfigItem item;
    private final Type type;
    private final Range range;
    private final Alpha alpha;
    private final Units units;

    @Override
    public String key() {
        return this.item.keyName();
    }

    @Override
    public String name() {
        return this.item.name();
    }

    @Override
    public int position() {
        return this.item.position();
    }

    public ConfigItemDescriptor(ConfigItem item, Type type, Range range, Alpha alpha, Units units) {
        this.item = item;
        this.type = type;
        this.range = range;
        this.alpha = alpha;
        this.units = units;
    }

    public ConfigItem getItem() {
        return this.item;
    }

    public Type getType() {
        return this.type;
    }

    public Range getRange() {
        return this.range;
    }

    public Alpha getAlpha() {
        return this.alpha;
    }

    public Units getUnits() {
        return this.units;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ConfigItemDescriptor)) {
            return false;
        }
        ConfigItemDescriptor other = (ConfigItemDescriptor)o;
        ConfigItem this$item = this.getItem();
        ConfigItem other$item = other.getItem();
        if (this$item == null ? other$item != null : !this$item.equals(other$item)) {
            return false;
        }
        Type this$type = this.getType();
        Type other$type = other.getType();
        if (this$type == null ? other$type != null : !this$type.equals(other$type)) {
            return false;
        }
        Range this$range = this.getRange();
        Range other$range = other.getRange();
        if (this$range == null ? other$range != null : !this$range.equals(other$range)) {
            return false;
        }
        Alpha this$alpha = this.getAlpha();
        Alpha other$alpha = other.getAlpha();
        if (this$alpha == null ? other$alpha != null : !this$alpha.equals(other$alpha)) {
            return false;
        }
        Units this$units = this.getUnits();
        Units other$units = other.getUnits();
        return !(this$units == null ? other$units != null : !this$units.equals(other$units));
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        ConfigItem $item = this.getItem();
        result = result * 59 + ($item == null ? 43 : $item.hashCode());
        Type $type = this.getType();
        result = result * 59 + ($type == null ? 43 : $type.hashCode());
        Range $range = this.getRange();
        result = result * 59 + ($range == null ? 43 : $range.hashCode());
        Alpha $alpha = this.getAlpha();
        result = result * 59 + ($alpha == null ? 43 : $alpha.hashCode());
        Units $units = this.getUnits();
        result = result * 59 + ($units == null ? 43 : $units.hashCode());
        return result;
    }

    public String toString() {
        return "ConfigItemDescriptor(item=" + String.valueOf(this.getItem()) + ", type=" + String.valueOf(this.getType()) + ", range=" + String.valueOf(this.getRange()) + ", alpha=" + String.valueOf(this.getAlpha()) + ", units=" + String.valueOf(this.getUnits()) + ")";
    }
}

