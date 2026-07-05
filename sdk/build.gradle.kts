dependencies {
    compileOnly(libs.runelite.client)
    compileOnly(libs.runelite.api)
    compileOnly(libs.guice)
    compileOnly(libs.lombok)
    compileOnly(libs.pf4j)
    compileOnly(libs.otp)

    compileOnly(projects.solaceApi)

    annotationProcessor(libs.lombok)
}
