package net.solace.api.plugins.config;

import net.solace.api.plugins.config.RuneScapeProfileType;

public class RuneScapeProfile {
    public static final int ACCOUNT_HASH_INVALID = -1;
    private final String displayName;
    private final RuneScapeProfileType type;
    private final long accountHash;
    private final String key;

    public RuneScapeProfile(String displayName, RuneScapeProfileType type, long accountHash, String key) {
        this.displayName = displayName;
        this.type = type;
        this.accountHash = accountHash;
        this.key = key;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public RuneScapeProfileType getType() {
        return this.type;
    }

    public long getAccountHash() {
        return this.accountHash;
    }

    public String getKey() {
        return this.key;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof RuneScapeProfile)) {
            return false;
        }
        RuneScapeProfile other = (RuneScapeProfile)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (this.getAccountHash() != other.getAccountHash()) {
            return false;
        }
        String this$displayName = this.getDisplayName();
        String other$displayName = other.getDisplayName();
        if (this$displayName == null ? other$displayName != null : !this$displayName.equals(other$displayName)) {
            return false;
        }
        RuneScapeProfileType this$type = this.getType();
        RuneScapeProfileType other$type = other.getType();
        if (this$type == null ? other$type != null : !((Object)((Object)this$type)).equals((Object)other$type)) {
            return false;
        }
        String this$key = this.getKey();
        String other$key = other.getKey();
        return !(this$key == null ? other$key != null : !this$key.equals(other$key));
    }

    protected boolean canEqual(Object other) {
        return other instanceof RuneScapeProfile;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        long $accountHash = this.getAccountHash();
        result = result * 59 + (int)($accountHash >>> 32 ^ $accountHash);
        String $displayName = this.getDisplayName();
        result = result * 59 + ($displayName == null ? 43 : $displayName.hashCode());
        RuneScapeProfileType $type = this.getType();
        result = result * 59 + ($type == null ? 43 : ((Object)((Object)$type)).hashCode());
        String $key = this.getKey();
        result = result * 59 + ($key == null ? 43 : $key.hashCode());
        return result;
    }

    public String toString() {
        return "RuneScapeProfile(displayName=" + this.getDisplayName() + ", type=" + String.valueOf((Object)this.getType()) + ", accountHash=" + this.getAccountHash() + ", key=" + this.getKey() + ")";
    }
}

