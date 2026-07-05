import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType

class BootstrapPlugin : Plugin<Project> {
    override fun apply(project: Project): Unit = with(project) {
        tasks.register<BootstrapTask>("bootstrapStable", "stable")
        tasks.register<BootstrapTask>("bootstrapDev", "dev")

        tasks.withType<BootstrapTask> {
            this.group = "solace"
        }
    }
}
