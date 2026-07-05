dependencies {
    compileOnly(libs.runelite.api)
    compileOnly(libs.runelite.client)
    compileOnly(libs.guice)
    compileOnly(libs.lombok)
    compileOnly(libs.pf4j)
    compileOnly(libs.pf4j.update)
    compileOnly(libs.findbugs.jsr305)
    compileOnly(libs.reactivex.rxjava3)
    compileOnly(libs.commons.lang3)
    compileOnly(libs.jetbrains.annotations)

    annotationProcessor(libs.lombok)
}
