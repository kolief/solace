package net.solace.api.events;

import javax.annotation.Nullable;

public class ConfigChanged {
    private String group;
    @Nullable
    private String profile;
    private String key;
    private String oldValue;
    @Nullable
    private String newValue;

    public String getGroup() {
        return this.group;
    }

    @Nullable
    public String getProfile() {
        return this.profile;
    }

    public String getKey() {
        return this.key;
    }

    public String getOldValue() {
        return this.oldValue;
    }

    @Nullable
    public String getNewValue() {
        return this.newValue;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public void setProfile(@Nullable String profile) {
        this.profile = profile;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    public void setNewValue(@Nullable String newValue) {
        this.newValue = newValue;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ConfigChanged)) {
            return false;
        }
        ConfigChanged other = (ConfigChanged)o;
        if (!other.canEqual(this)) {
            return false;
        }
        String this$group = this.getGroup();
        String other$group = other.getGroup();
        if (this$group == null ? other$group != null : !this$group.equals(other$group)) {
            return false;
        }
        String this$profile = this.getProfile();
        String other$profile = other.getProfile();
        if (this$profile == null ? other$profile != null : !this$profile.equals(other$profile)) {
            return false;
        }
        String this$key = this.getKey();
        String other$key = other.getKey();
        if (this$key == null ? other$key != null : !this$key.equals(other$key)) {
            return false;
        }
        String this$oldValue = this.getOldValue();
        String other$oldValue = other.getOldValue();
        if (this$oldValue == null ? other$oldValue != null : !this$oldValue.equals(other$oldValue)) {
            return false;
        }
        String this$newValue = this.getNewValue();
        String other$newValue = other.getNewValue();
        return !(this$newValue == null ? other$newValue != null : !this$newValue.equals(other$newValue));
    }

    protected boolean canEqual(Object other) {
        return other instanceof ConfigChanged;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $group = this.getGroup();
        result = result * 59 + ($group == null ? 43 : $group.hashCode());
        String $profile = this.getProfile();
        result = result * 59 + ($profile == null ? 43 : $profile.hashCode());
        String $key = this.getKey();
        result = result * 59 + ($key == null ? 43 : $key.hashCode());
        String $oldValue = this.getOldValue();
        result = result * 59 + ($oldValue == null ? 43 : $oldValue.hashCode());
        String $newValue = this.getNewValue();
        result = result * 59 + ($newValue == null ? 43 : $newValue.hashCode());
        return result;
    }

    public String toString() {
        return "ConfigChanged(group=" + this.getGroup() + ", profile=" + this.getProfile() + ", key=" + this.getKey() + ", oldValue=" + this.getOldValue() + ", newValue=" + this.getNewValue() + ")";
    }
}

