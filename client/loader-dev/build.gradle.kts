dependencies {
    // provided by loader
    compileOnly(projects.solaceApi)
    compileOnly(libs.pf4j)
    compileOnly(projects.bindings)
    compileOnly(projects.common)
    compileOnly(projects.hub)
    compileOnly(projects.ui)

    // needed because we don't attach to RuneLite when ran from IDE
    implementation(libs.runelite.client)
    implementation(libs.runelite.api)
    implementation(libs.runelite.jshell)
    implementation(libs.guice)
    implementation(libs.jetbrains.annotations)
    implementation(libs.lombok)
    annotationProcessor(libs.lombok)

    // supplies all compileOnly libs
    implementation(projects.loader)
}

tasks.register<JavaExec>("runDev") {
    group = "solace"
    description = "Run Solace loader from IDE"
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("net.solace.loader.SolaceLoaderDev")
    jvmArgs("-Drunelite.launcher.version=dev")
}