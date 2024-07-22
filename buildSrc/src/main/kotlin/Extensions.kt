import org.gradle.api.Project

/**
 * Extension to add file tree dependency
 */
fun Project.defaultFileTree() = fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar")))