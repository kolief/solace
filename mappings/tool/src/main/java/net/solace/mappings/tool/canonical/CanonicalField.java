package net.solace.mappings.tool.canonical;

import com.google.gson.annotations.SerializedName;

public class CanonicalField {
    public String name = "";
    @SerializedName("obfuscatedName")
    public String obfuscatedName = "";
    public String owner;
    @SerializedName("ownerObfuscatedName")
    public String ownerObfuscatedName = "";
    public String descriptor = "";
    public Number getter;
    public Number setter;
    public boolean isStatic;
}
