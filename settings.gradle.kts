rootProject.name = "solace-loader"

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.9.0"
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include(
    "solace-api",
    "solace-sdk",
    "bindings",
    "loader",
    "bundled",
    "hub",
    "ui",
    "common",
    "collision-maps",
    "cache-tools",
    "mappings-generator",
    "mappings-tool",
)

project(":solace-api").projectDir = file("api")
project(":solace-sdk").projectDir = file("sdk")
project(":bindings").projectDir = file("client/bindings")
project(":loader").projectDir = file("client/loader")
project(":bundled").projectDir = file("plugins/bundled")
project(":hub").projectDir = file("plugins/hub")
project(":ui").projectDir = file("ui")
project(":common").projectDir = file("common")
project(":collision-maps").projectDir = file("data/collision-maps")
project(":cache-tools").projectDir = file("data/cache-tools")
project(":mappings-generator").projectDir = file("mappings/generator")
project(":mappings-tool").projectDir = file("mappings/tool")
