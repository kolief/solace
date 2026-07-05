package net.solace.mappings.tool.canonical;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CanonicalClass {
    public String name = "";
    @SerializedName("obfuscatedName")
    public String obfuscatedName = "";
    public List<CanonicalField> fields = List.of();
    public List<CanonicalMethod> methods = List.of();
}
