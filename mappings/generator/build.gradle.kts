plugins {
    application
}

dependencies {
    implementation(libs.gson)
    implementation(libs.asm.util)
}

application {
    mainClass.set("net.solace.mappings.generator.MappingGenerator")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}
