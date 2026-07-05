version = rootProject.version

dependencies {
    compileOnly(libs.runelite.client) {
        exclude(group = "com.squareup.okhttp3", module = "okhttp")
    }
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    compileOnly(projects.solaceApi)
    compileOnly(libs.pf4j)
    compileOnly(libs.pf4j.update)
    compileOnly(libs.fest.reflect)
    compileOnly(libs.json)

    compileOnly(projects.common)
}
