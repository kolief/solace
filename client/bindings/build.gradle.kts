version = rootProject.version

dependencies {
    compileOnly(libs.runelite.client) {
        exclude(group = "com.squareup.okhttp3", module = "okhttp")
    }
    compileOnly(libs.runelite.api)
    compileOnly(libs.guice)
    compileOnly(libs.jetbrains.annotations)
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    compileOnly(projects.solaceApi)
    compileOnly(libs.pf4j)
    compileOnly(libs.asm.util)
    compileOnly(libs.reactivex.rxjava3)
    compileOnly(libs.eclipse.collections)

    compileOnly(projects.hub)
}