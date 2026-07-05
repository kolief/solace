import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.register

class MapGeneratorPlugin : Plugin<Project> {
    override fun apply(project: Project): Unit = with(project) {
        tasks.register<MapGeneratorTask>("generateMap") {
            group = "solace"
            description = "Generates collision map from RS cache"
        }
    }
}
