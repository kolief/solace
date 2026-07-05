plugins {
    `java-library`
}

group = "net.solace"
version = "0.0.1-SNAPSHOT"

val cacheTools = project(":cache-tools")
val generatedResources = layout.buildDirectory.dir("generated/resources")

tasks.register<Copy>("copyRegions") {
    group = "solace"
    description = "Copy collision map from cache-tools generateMap into collision-maps resources"
    dependsOn(":cache-tools:generateMap")
    from(cacheTools.layout.buildDirectory.file("map-gen/regions"))
    into(generatedResources)
}

sourceSets {
    main {
        resources {
            srcDir(generatedResources)
        }
    }
}

tasks.named("processResources") {
    dependsOn("copyRegions")
}
