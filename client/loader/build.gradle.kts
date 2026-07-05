plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

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

    compileOnly(libs.runelite.client) {
        exclude(group = "com.squareup.okhttp3", module = "okhttp")
    }
    compileOnly(libs.runelite.api)
    compileOnly(libs.guice)
    compileOnly(libs.jetbrains.annotations)
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    // bundled into the production fat jar only (shadowJar), not the library jar
    implementation(libs.runelite.client)
    implementation(libs.runelite.api)
    implementation(libs.runelite.jshell)

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
    implementation(libs.guice)

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
        dependsOn(jar)
    }

    shadowJar {
        group = "solace"
        description = "Build the production Solace fat jar"
        archiveBaseName.set("solace")
        archiveClassifier.set("")
        dependsOn(jar)
        manifest {
            attributes["Main-Class"] = "net.solace.loader.SolaceLauncher"
        }
        mergeServiceFiles()
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        exclude(
            "META-INF/*.SF",
            "META-INF/*.DSA",
            "META-INF/*.RSA",
        )
    }

    register<JavaExec>("runDev") {
        group = "solace"
        description = "Run Solace with RuneLite debug and developer-mode flags"
        dependsOn(classes)
        classpath = sourceSets["main"].runtimeClasspath
        mainClass.set("net.solace.loader.SolaceLoaderDev")
        jvmArgs("-Drunelite.launcher.version=dev")
    }

    named("build") {
        dependsOn(shadowJar)
    }
}
