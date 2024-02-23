import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import org.jetbrains.dokka.DokkaConfiguration
import org.jetbrains.dokka.base.DokkaBase
import org.jetbrains.dokka.base.DokkaBaseConfiguration
import org.jetbrains.dokka.gradle.DokkaTaskPartial
import java.net.URL
import java.util.Properties

val properties = Properties()
val localPropertiesFile = project.file("local.properties")
if (localPropertiesFile.exists()) {
    properties.load(localPropertiesFile.inputStream())
}
val useMavenLocal = getBooleanFromProperties("useMavenLocal")
val useLocalProjects = getBooleanFromProperties("useLocalProjects")

if (useLocalProjects) {
    println("OMH Maps project running with useLocalProjects enabled ")
}

if (useMavenLocal) {
    println("OMH Maps project running with useMavenLocal enabled${if (useLocalProjects) ", but only publishing will be altered since dependencies are overriden by useLocalProjects" else ""} ")
}

project.extra.set("useLocalProjects", useLocalProjects)
project.extra.set("useMavenLocal", useMavenLocal)

plugins {
    id("io.github.gradle-nexus.publish-plugin") version "1.1.0"
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin") version "2.0.1" apply false
    id("com.github.hierynomus.license") version "0.16.1"
    id("org.jetbrains.dokka") version Versions.dokka
}

buildscript {
    dependencies {
        classpath("org.jetbrains.dokka:dokka-base:${Versions.dokka}")
    }
}

downloadLicenses {
    includeProjectDependencies = true
    dependencyConfiguration = "debugRuntimeClasspath"
}

subprojects {
    apply(plugin = "org.jetbrains.dokka")

    tasks.withType<DokkaTaskPartial>().configureEach {
        suppressInheritedMembers.set(true)

        dokkaSourceSets.configureEach {
            documentedVisibilities.set(
                setOf(
                    DokkaConfiguration.Visibility.PUBLIC,
                    DokkaConfiguration.Visibility.PROTECTED
                )
            )

            sourceLink {
                val exampleDir = "https://github.com/openmobilehub/android-omh-maps/tree/main"

                localDirectory.set(rootProject.projectDir)
                remoteUrl.set(URL(exampleDir))
                remoteLineSuffix.set("#L")
            }

            // include the top-level README for that module
            val readmeFile = project.file("README.md")
            if (readmeFile.exists()) {
                includes.from(readmeFile.path)
            }
        }
    }

    if (useMavenLocal) {
        repositories {
            mavenLocal()
            gradlePluginPortal()
            google()
            configureMapboxMaven()
        }
    } else {
        repositories {
            mavenCentral()
            google()
            maven("https://s01.oss.sonatype.org/content/groups/staging/")
            configureMapboxMaven()
        }
    }
}

fun prefixFilename(prefix: String, file: File): File {
    val parentDir = file.parent
    val name = file.name

    return File(parentDir, "$prefix$name")
}

val docsOutputDir = rootProject.file("docs")
val dokkaDocsOutputDir = File(docsOutputDir, "generated")
val markdownDocsOutputDirBase = File(docsOutputDir, "markdown")

fun discoverImagesInProject(project: Project): List<File>? {
    return file("${project.projectDir}/images")
        .takeIf {
            // walk all directories & ensure we are not looping a child of the output directory
            it.exists() && it.isDirectory && !it.canonicalPath.startsWith(docsOutputDir.canonicalPath)
        }
        ?.walk()
        ?.filter { it.isFile }?.toList()
}

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
                    val dir = prefixFilename(
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
            val readmeFile = project.file("README.md")
            if (readmeFile.exists() && project != rootProject) {
                readmeFile.copyTo(File(projectDocsDestDir, "_README_ORIGINAL.md"), true)
            }

            // copy custom markdown docs
            val docsSrcDir = project.file("docs")
            val allMdFiles = docsSrcDir.walkTopDown()
                .filter {
                    // walk only Markdown files
                    it.isFile && it.extension.equals("md", ignoreCase = true)
                            // ensure we are not looping a child of the output directory
                            && !it.canonicalPath.startsWith(docsOutputDir.canonicalPath)
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
                    srcMdFile.copyTo(File(projectDocsDestDir, fileRelativePathInProject))
                }
            }

            val imagesDestDir = File(projectDocsDestDir, "images")

            if (imagesDestDir.exists()) {
                imagesDestDir.deleteRecursively()
            }

            val images = discoverImagesInProject(project)
            if (images?.isNotEmpty() == true) {
                imagesDestDir.mkdir()
                images.forEach { srcImageFile ->
                    srcImageFile.copyTo(File(imagesDestDir, srcImageFile.name))
                }
            }
        }
    }
}

tasks.register("cleanDokkaDocsOutputDirectory", Delete::class) {
    group = "other"
    description = "Deletes the Dokka HTML docs output directory in root project"
    delete = setOf(dokkaDocsOutputDir)
}

tasks.dokkaHtmlMultiModule {
    dependsOn("cleanDokkaDocsOutputDirectory")

    moduleName.set("OMH Maps")
    outputDirectory.set(dokkaDocsOutputDir)
    includes.from("README.md")

    // copy assets: images/**/* from the rootProject images directory & all subprojects' images directories
    pluginConfiguration<DokkaBase, DokkaBaseConfiguration> {
        footerMessage = "(c) 2023 Open Mobile Hub"
        separateInheritedMembers = false
        customAssets = (setOf(rootProject) union subprojects).mapNotNull { project ->
            discoverImagesInProject(project)
        }.flatten()
    }
}

tasks.register("buildDocs") {
    group = "documentation"
    description = "Runs dokkaHtmlMultiModule and copyMarkdownDocs tasks"
    dependsOn(
        "dokkaHtmlMultiModule",
        copyMarkdownDocsTask
    )
}

downloadLicenses {
    includeProjectDependencies = true
    dependencyConfiguration = "debugRuntimeClasspath"
}

tasks.register("installPrePushHook", Copy::class) {
    from("tools/scripts/pre-push")
    into(".git/hooks")
    fileMode = 0b000_111_111_111
}

tasks.register("installPreCommitHook", Copy::class) {
    from("tools/scripts/pre-commit")
    into(".git/hooks")
    fileMode = 0b000_111_111_111
}

tasks.register("publishCoreToMavenLocal") {
    dependsOn(
        ":packages:core:assembleRelease",
        ":packages:core:publishToMavenLocal",
    )
}

tasks.register("publishPluginsToMavenLocal") {
    dependsOn(
        ":packages:plugin-googlemaps:assembleRelease",
        ":packages:plugin-googlemaps:publishToMavenLocal",
        ":packages:plugin-openstreetmap:assembleRelease",
        ":packages:plugin-openstreetmap:publishToMavenLocal",
        ":packages:plugin-mapbox:assembleRelease",
        ":packages:plugin-mapbox:publishToMavenLocal",
    )
}

tasks {
    val installPrePushHook by existing
    val installPreCommitHook by existing
    getByName("prepareKotlinBuildScriptModel").dependsOn(installPrePushHook)
    getByName("prepareKotlinBuildScriptModel").dependsOn(installPreCommitHook)
}

if (!useMavenLocal) {
    val ossrhUsername by extra(getValueFromEnvOrProperties("OSSRH_USERNAME"))
    val ossrhPassword by extra(getValueFromEnvOrProperties("OSSRH_PASSWORD"))
    val mStagingProfileId by extra(getValueFromEnvOrProperties("SONATYPE_STAGING_PROFILE_ID"))
    val signingKeyId by extra(getValueFromEnvOrProperties("SIGNING_KEY_ID"))
    val signingPassword by extra(getValueFromEnvOrProperties("SIGNING_PASSWORD"))
    val signingKey by extra(getValueFromEnvOrProperties("SIGNING_KEY"))

    // Set up Sonatype repository
    nexusPublishing {
        repositories {
            sonatype {
                stagingProfileId.set(mStagingProfileId.toString())
                username.set(ossrhUsername.toString())
                password.set(ossrhPassword.toString())
                // Add these lines if using new Sonatype infra
                nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
                snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
            }
        }
    }
}

fun getValueFromEnvOrProperties(name: String): Any? {
    val localProperties = gradleLocalProperties(rootDir)
    return System.getenv(name) ?: localProperties[name]
}

fun getBooleanFromProperties(name: String): Boolean {
    val localProperties = gradleLocalProperties(rootDir)
    return (project.ext.has(name) && project.ext.get(name) == "true") || localProperties[name] == "true"
}

fun RepositoryHandler.configureMapboxMaven() {
    maven {
        url = uri("https://api.mapbox.com/downloads/v2/releases/maven")
        credentials.username = "mapbox"
        credentials.password = providers.gradleProperty("MAPBOX_DOWNLOADS_TOKEN").get()
        authentication.create<BasicAuthentication>("basic")
    }
}
