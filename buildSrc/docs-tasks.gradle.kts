import java.io.File

val docsOutputDirBase = DocsUtils.getDocsOutputDirBase(rootProject)
val dokkaDocsOutputDir = DocsUtils.getDokkaDocsOutputDir(rootProject)
val markdownDocsOutputDirBase = DocsUtils.getMarkdownDocsOutputDirBase(rootProject)

val copyMarkdownDocsTask = tasks.register("copyMarkdownDocs") {
    group = "documentation"
    description =
        "Copies docs/**/*.md files from all subprojects to the root project's docs/markdown/...".plus(
            "directory, cleaning it beforehand"
        )

    doLast {
        (setOf(rootProject) union subprojects).forEach { project ->
            val projectDocsDestDir = project.let ProjectDocsDestDir@{
                val projectDestPathTreeFragment = project.let ProjectPartsAndName@{
                    val allPathParts = project.path.split(":").filter { it.isNotEmpty() }

                    if (allPathParts.isNotEmpty()) {
                        if (project.path.contains("packages") && allPathParts.size != 1) {
                            // :packages:...:name -> name
                            return@ProjectPartsAndName allPathParts.slice(
                                IntRange(
                                    allPathParts.indexOf("packages") + 1,
                                    allPathParts.size - 1
                                )
                            ).joinToString("/")
                        } else {
                            // :packages:name -> name
                            return@ProjectPartsAndName allPathParts.last()
                        }
                    } else {
                        // :name -> name
                        return@ProjectPartsAndName project.name
                    }
                }

                if (project.rootProject == project) {
                    // this is the root project
                    return@ProjectDocsDestDir markdownDocsOutputDirBase
                } else {
                    // this is not the root project
                    val dir = DocsUtils.prefixFilename(
                        "_",
                        File(
                            markdownDocsOutputDirBase,
                            projectDestPathTreeFragment
                        )
                    )

                    if (dir.exists()) {
                        dir.deleteRecursively()
                    }

                    return@ProjectDocsDestDir dir
                }
            }

            // copy the top-level README for that module to _README_ORIGINAL.md that can be Jekyll-included
            val srcReadmeFile = project.file("README.md")
            if (srcReadmeFile.exists() && project != rootProject) {
                val destReadmeFile = File(projectDocsDestDir, "_README_ORIGINAL.md")
                srcReadmeFile.copyTo(destReadmeFile, true)
                DocsUtils.sanitizeLinksInMdFile(destReadmeFile)
            }

            // copy custom markdown docs
            val docsSrcDir = project.file("docs")
            val allMdFiles = docsSrcDir.walkTopDown()
                .filter {
                    // walk only Markdown files
                    it.isFile && it.extension.equals("md", ignoreCase = true)
                            // ensure we are not looping a child of the output directory
                            && !it.canonicalPath.startsWith(docsOutputDirBase.canonicalPath)
                }.toList()

            if (allMdFiles.isNotEmpty()) {
                if (!projectDocsDestDir.exists()) {
                    projectDocsDestDir.mkdir()
                }

                val initialIgnoredPathComponent = "${project.name}/docs/"
                allMdFiles.forEach { srcMdFile ->
                    val fileRelativePathInProject = srcMdFile.path.slice(
                        IntRange(
                            srcMdFile.path.indexOf(initialIgnoredPathComponent) + initialIgnoredPathComponent.length,
                            srcMdFile.path.length - 1
                        )
                    )
                    val destMdFile = File(projectDocsDestDir, fileRelativePathInProject)

                    srcMdFile.copyTo(destMdFile, true)
                    DocsUtils.sanitizeLinksInMdFile(destMdFile)
                }
            }

            val imagesDestDir = File(projectDocsDestDir, "images")

            if (imagesDestDir.exists()) {
                imagesDestDir.deleteRecursively()
            }

            val images = DocsUtils.discoverImagesInProject(rootProject, project)
            if (images?.isNotEmpty() == true) {
                imagesDestDir.mkdir()
                images.forEach { srcImageFile ->
                    srcImageFile.copyTo(File(imagesDestDir, srcImageFile.name))
                }
            }
        }
    }
}

val buildDocsTask = tasks.register("buildDocs") {
    group = "documentation"
    description = "Runs dokkaHtmlMultiModule and copyMarkdownDocs tasks"
    dependsOn(
        "dokkaHtmlMultiModule",
        copyMarkdownDocsTask
    )
}
