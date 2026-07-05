import com.google.gson.GsonBuilder
import org.gradle.api.DefaultTask
import org.gradle.api.artifacts.ResolvedArtifact
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.get
import java.io.File
import java.security.MessageDigest
import javax.inject.Inject

open class BootstrapTask @Inject constructor(@Input val type: String) : DefaultTask() {

    @InputFile
    val mainJarFile = project.objects.fileProperty()

    data class Artifact(
        val name: String,
        val path: String,
        val size: Long,
        val hash: String,
        @Transient
        val file: File,
        var blob: Boolean = false,
        var remap: Boolean = false
    )

    data class Bootstrap(val artifacts: List<Artifact>, val commitMessage: String, val commitSha: String)

    @TaskAction
    fun bootstrap() {
        val artifacts = getArtifacts()
        val bootstrap = Bootstrap(artifacts, System.getenv("COMMIT_MESSAGE"), System.getenv("COMMIT_SHA"))

        val prettyJson = GsonBuilder().setPrettyPrinting().create().toJson(bootstrap)

        val bootstrapDir = File("${project.projectDir}/build/bootstrap")
        bootstrapDir.mkdirs()

        File(bootstrapDir, "bootstrap-${type}.json").printWriter().use { out ->
            out.println(prettyJson)
        }

        val bootstrapDirWithType = File(bootstrapDir, type)
        bootstrapDirWithType.mkdirs()

        val depsDir = File("${project.projectDir}/build/libs/dependencies")
        depsDir.mkdirs()

        artifacts.forEach {
            if (it.blob) {
                File(bootstrapDirWithType, it.name).writeBytes(it.file.readBytes())
            } else {
                File(depsDir, it.name).writeBytes(it.file.readBytes())
            }
        }
    }

    private fun getArtifacts(): List<Artifact> {
        val artifacts = mutableListOf<Artifact>()
        val artifactsSet = HashSet<String>()

        project.configurations["runtimeClasspath"].resolvedConfiguration.resolvedArtifacts.forEach {
            if (shouldEmbed(it)) {
                return@forEach
            }

            val module = it.moduleVersion.id.toString()

            val splat = module.split(":")
            val name = splat[1]
            val group = splat[0]
            val version = splat[2]
            lateinit var path: String

            if (shouldBootstrap(it)) {
                path = "AZUREBLOB"
            } else {
                path = "https://repo.maven.apache.org/maven2/" + group.replace(
                    ".",
                    "/"
                ) + "/${name}/$version/${name}-$version"
                if (it.classifier != null && it.classifier != "no_aop") {
                    path += "-${it.classifier}"
                }
                path += ".jar"
            }

            val filePath = it.file.absolutePath
            val artifactFile = File(filePath)

            if (!artifactsSet.contains(filePath)) {
                artifactsSet.add(filePath)

                val sha = hash(artifactFile.readBytes())

                val artifact = Artifact(
                    name = it.file.name,
                    path = path,
                    size = artifactFile.length(),
                    hash = sha,
                    file = it.file
                )

                if (shouldUploadToBlob(it)) {
                    artifact.blob = true
                }

                if (shouldRemap(it)) {
                    artifact.remap = true
                }

                artifacts.add(artifact)
            }
        }

        val loaderJarFile = mainJarFile.get().asFile
        val sha = hash(loaderJarFile.readBytes())
        val artifact = Artifact(
            name = loaderJarFile.name,
            path = "AZUREBLOB",
            size = loaderJarFile.length(),
            hash = sha,
            blob = true,
            file = loaderJarFile,
            remap = true
        )
        artifacts.add(artifact)

        return artifacts
    }

    private fun shouldEmbed(artifact: ResolvedArtifact): Boolean {
        return project.configurations["embed"].resolvedConfiguration.resolvedArtifacts.contains(artifact)
    }

    private fun shouldUploadToBlob(artifact: ResolvedArtifact): Boolean {
        return project.configurations["blob"].resolvedConfiguration.resolvedArtifacts.contains(artifact)
    }

    private fun shouldBootstrap(artifact: ResolvedArtifact): Boolean {
        return project.configurations["bootstrap"].resolvedConfiguration.resolvedArtifacts.contains(artifact)
    }

    private fun shouldRemap(artifact: ResolvedArtifact): Boolean {
        return project.configurations["remap"].resolvedConfiguration.resolvedArtifacts.contains(artifact)
    }

    private fun hash(file: ByteArray): String {
        return MessageDigest.getInstance("SHA-256")
            .digest(file)
            .fold("") { str, it -> str + "%02x".format(it) }
    }
}
