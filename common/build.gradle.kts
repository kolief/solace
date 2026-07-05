import org.apache.tools.ant.filters.ReplaceTokens

version = rootProject.version

dependencies {
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    compileOnly(projects.solaceApi)
    compileOnly(libs.pf4j)
    compileOnly(libs.pf4j.update)
    compileOnly(libs.guava)
}

val generateSolaceProperties by tasks.registering(Copy::class) {
    val tokens = mapOf(
        "runelite.version" to libs.versions.rl.get(),
        "solace.version" to project.version.toString(),
        "solace.commit.hash" to (System.getenv("COMMIT_SHA") ?: "unknown"),
        "solace.build.date" to "",
    )

    from("src/main/template") {
        include("**/*.java.template")
        rename { it.removeSuffix(".template") }
        filter<ReplaceTokens>("tokens" to tokens)
    }

    into(layout.buildDirectory.dir("generated/sources/solace"))
    filteringCharset = "UTF-8"
}

tasks {
    compileJava {
        dependsOn(generateSolaceProperties)
    }
}

sourceSets {
    main {
        java {
            srcDir(layout.buildDirectory.dir("generated/sources/solace"))
        }
    }
}
