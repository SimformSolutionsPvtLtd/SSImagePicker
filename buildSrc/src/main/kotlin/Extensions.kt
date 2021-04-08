import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import java.net.URI

/**
 * Extension to add file tree dependency
 */
fun Project.defaultFileTree() = fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar")))

/**
 * Provides root path of the project and append path.
 */
fun Project.rootPath(appendPath: String = ""): String = rootProject.rootDir.path + appendPath

/**
 * Adds jitpack maven repository
 */
fun RepositoryHandler.jitPack() = maven { url = URI(Urls.JITPACK) }