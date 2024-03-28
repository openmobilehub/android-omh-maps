import org.gradle.api.Project
import java.io.File

object DocsUtils {
    private const val ADVANCED_DOCS_PREFIX = "advanced-docs"

    /**
     * Constructs a new [File] that has its name prefixed with the given [prefix]
     *
     * @param prefix The prefix to add to the filename
     * @param file The file to prefix
     * @return A new [File] with the prefixed filename
     */
    fun prefixFilename(prefix: String, file: File): File {
        val parentDir = file.parent
        val name = file.name

        return File(parentDir, "$prefix$name")
    }

    /**
     * Sanitize relative links to .md files after the [file] has been copied to the to new files tree
     *
     * @param file The file to be sanitized
     */
    fun sanitizeLinksInMdFile(file: File) {
        file.writeText(
            file.readText()
                // replace all absolute references to the root README file
                .replace(Regex("\\(/README.md\\)"), "(/$ADVANCED_DOCS_PREFIX/)")
                // replace all relative upper-level occurrences of local Md files with new tree relative paths
                .replace(Regex("\\(\\.{2}/(.*\\.md)\\)"), "(../../$1)")
                // replace all relative same-level occurrences of local Md files with new tree relative paths
                .replace(Regex("\\(\\./(.*\\.md)\\)"), "(../$1)")
                // replace all absolute references to packages with new tree relative paths
                .replace(
                    Regex("/packages/([^/]*)/(?:docs/)?(.*\\.md)\\)"),
                    "/$ADVANCED_DOCS_PREFIX/$1/$2)"
                )
                // strip file extension off all non-external links ending with
                // (Jekyll creates directories with index.html files)
                .replace(Regex("\\((?!https?)(.*)\\.md\\)"), "($1)")
        )
    }
}

/**
 * Returns the base of output directory for all docs
 */
fun Project.getDocsOutputDirBase(): File {
    return rootProject.file("docs")
}

/**
 * Returns the output directory for Dokka docs
 */
fun Project.getDokkaDocsOutputDir(): File {
    return File(this.getDocsOutputDirBase(), "generated")
}

/**
 * Returns the output directory for markdown docs
 */
fun Project.getMarkdownDocsOutputDirBase(): File {
    return File(getDocsOutputDirBase(), "markdown")
}

/**
 * Discovers all images in the project's `images/` directory
 *
 * @param rootProject The root project
 * @param project The project to search for images in
 * @return A list of images in the project's `images/` directory
 */
fun Project.discoverImagesInProject(): List<File>? {
    val docsOutputDir = rootProject.file("docs")

    return project.file("${project.projectDir}/images")
        .takeIf {
            // walk all directories & ensure we are not looping a child of the output directory
            it.exists() && it.isDirectory && !it.canonicalPath.startsWith(docsOutputDir.canonicalPath)
        }
        ?.walk()
        ?.filter { it.isFile }?.toList()
}
