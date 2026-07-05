version = rootProject.version

dependencies {
    compileOnly(libs.runelite.client) {
        exclude(group = "com.squareup.okhttp3", module = "okhttp")
    }
    compileOnly(libs.runelite.api)
    compileOnly(libs.guice)
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    compileOnly(projects.solaceApi)
    compileOnly(projects.solaceSdk)
    compileOnly(libs.pf4j)
    compileOnly(libs.reactivex.rxjava3)
    compileOnly(libs.otp)

    compileOnly(projects.ui)
    compileOnly(projects.hub)
}