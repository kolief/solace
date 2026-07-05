package net.solace.api.events;

public class ConfigButtonClicked {
    private String group;
    private String key;

    public String getGroup() {
        return this.group;
    }

    public String getKey() {
        return this.key;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ConfigButtonClicked)) {
            return false;
        }
        ConfigButtonClicked other = (ConfigButtonClicked)o;
        if (!other.canEqual(this)) {
            return false;
        }
        String this$group = this.getGroup();
        String other$group = other.getGroup();
        if (this$group == null ? other$group != null : !this$group.equals(other$group)) {
            return false;
        }
        String this$key = this.getKey();
        String other$key = other.getKey();
        return !(this$key == null ? other$key != null : !this$key.equals(other$key));
    }

    protected boolean canEqual(Object other) {
        return other instanceof ConfigButtonClicked;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $group = this.getGroup();
        result = result * 59 + ($group == null ? 43 : $group.hashCode());
        String $key = this.getKey();
        result = result * 59 + ($key == null ? 43 : $key.hashCode());
        return result;
    }

    public String toString() {
        return "ConfigButtonClicked(group=" + this.getGroup() + ", key=" + this.getKey() + ")";
    }
}

