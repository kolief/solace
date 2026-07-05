apply<MapGeneratorPlugin>()

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

dependencies {
    implementation(libs.runelite.cache)
    implementation(libs.runelite.arn)
    implementation(libs.lombok)
    implementation(projects.solaceSdk)
    implementation(projects.solaceApi)
    implementation(libs.slf4j.api)
    implementation(libs.slf4j.simple)
    implementation(libs.guava)

    // we need gson 2.12.1 because the RL version does not support deserializing into java records
    implementation("com.google.code.gson:gson:2.12.1")

    annotationProcessor(libs.lombok)
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(17)
}

tasks.named<JavaExec>("generateMap") {
    javaLauncher.set(
        javaToolchains.launcherFor {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    )
}
