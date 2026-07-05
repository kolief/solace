import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.the

open class MapGeneratorTask : JavaExec() {

    @OutputFile
    val outputFile = project.objects.fileProperty().convention(
        project.layout.buildDirectory.file("map-gen/regions")
    )

    init {
        mainClass.set("net.solace.rscache.map.MapGenerator")

        // Ensure classes are compiled first
        dependsOn("classes")

        // Set classpath to include the compiled classes and runtime dependencies
        val mainSourceSet = project.the<JavaPluginExtension>().sourceSets[SourceSet.MAIN_SOURCE_SET_NAME]
        classpath = mainSourceSet.runtimeClasspath
    }

    @TaskAction
    override fun exec() {
        val outputDir = outputFile.get().asFile.parentFile
        outputDir.mkdirs()

        args(outputDir.absolutePath)

        super.exec()
    }
}
