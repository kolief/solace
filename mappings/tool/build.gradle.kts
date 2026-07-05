plugins {
    application
}

dependencies {
    implementation(projects.hub)
    implementation(libs.gson)
    implementation(libs.asm.util)
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
}

application {
    mainClass.set("net.solace.mappings.tool.GenerateVersionPackage")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}
