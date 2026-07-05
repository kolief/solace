package net.solace.api.plugins.config;

public final class ConfigImageResource {
    private final String resourceName;

    public ConfigImageResource(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getResourceName() {
        return this.resourceName;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ConfigImageResource)) {
            return false;
        }
        ConfigImageResource other = (ConfigImageResource)o;
        String this$resourceName = this.getResourceName();
        String other$resourceName = other.getResourceName();
        return !(this$resourceName == null ? other$resourceName != null : !this$resourceName.equals(other$resourceName));
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $resourceName = this.getResourceName();
        result = result * 59 + ($resourceName == null ? 43 : $resourceName.hashCode());
        return result;
    }

    public String toString() {
        return "ConfigImageResource(resourceName=" + this.getResourceName() + ")";
    }
}

