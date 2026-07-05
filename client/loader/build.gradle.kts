apply<BootstrapPlugin>()

version = rootProject.version

val embed: Configuration by configurations.creating
val remap: Configuration by configurations.creating
val blob: Configuration by configurations.creating
val bootstrap: Configuration by configurations.creating

dependencies {
    embed(projects.solaceApi)
    embed(projects.solaceSdk)
    embed(projects.bindings)
    embed(projects.hub)
    embed(projects.common)
    embed(projects.ui)
    embed(projects.bundled)

    bootstrap(projects.collisionMaps)
    blob(projects.collisionMaps)

    // already in RL's classpath
    compileOnly(libs.runelite.client) {
        exclude(group = "com.squareup.okhttp3", module = "okhttp")
    }
    compileOnly(libs.runelite.api)
    compileOnly(libs.guice)
    compileOnly(libs.jetbrains.annotations)
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    implementation(libs.gson)
    implementation(libs.pf4j) {
        exclude(group = "org.slf4j", module = "slf4j-api")
    }
    implementation(libs.pf4j.update) {
        exclude(group = "com.google.code.gson", module = "gson")
        exclude(group = "org.slf4j", module = "slf4j-api")
    }
    implementation(libs.asm.util)
    implementation(libs.otp)
    implementation(libs.miglayout)
    implementation(libs.kotlin.stdlib)
    implementation(libs.reactivex.rxjava3)
    implementation(libs.eclipse.collections)

    implementation(projects.solaceApi)
    implementation(projects.collisionMaps)
    implementation(projects.bindings)
    implementation(projects.hub)
    implementation(projects.common)
    implementation(projects.ui)

    runtimeOnly(projects.bundled)
    runtimeOnly(projects.solaceSdk)
}

tasks {
    processResources {
        from("${rootProject.projectDir}/mappings/version-package.json") {
            into("net/solace/loader")
        }
    }

    jar {
        dependsOn(embed)
        from(embed.map { if (it.isDirectory) it else zipTree(it) })
    }

    withType<BootstrapTask> {
        mainJarFile.set(file("${project.projectDir}/build/libs/${project.name}-${project.version}.jar"))
    }
}
