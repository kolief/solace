package net.solace.api.plugins.config;

import java.util.List;
import net.solace.api.plugins.config.ConfigProfile;

public class Profiles {
    private List<ConfigProfile> profiles;

    public List<ConfigProfile> getProfiles() {
        return this.profiles;
    }

    public void setProfiles(List<ConfigProfile> profiles) {
        this.profiles = profiles;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Profiles)) {
            return false;
        }
        Profiles other = (Profiles)o;
        if (!other.canEqual(this)) {
            return false;
        }
        List<ConfigProfile> this$profiles = this.getProfiles();
        List<ConfigProfile> other$profiles = other.getProfiles();
        return !(this$profiles == null ? other$profiles != null : !((Object)this$profiles).equals(other$profiles));
    }

    protected boolean canEqual(Object other) {
        return other instanceof Profiles;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        List<ConfigProfile> $profiles = this.getProfiles();
        result = result * 59 + ($profiles == null ? 43 : ((Object)$profiles).hashCode());
        return result;
    }

    public String toString() {
        return "Profiles(profiles=" + String.valueOf(this.getProfiles()) + ")";
    }
}

