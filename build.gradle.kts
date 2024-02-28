import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import org.gradle.internal.Cast.uncheckedCast
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
        val mapboxDownloadToken =
            providers.gradleProperty("MAPBOX_DOWNLOADS_TOKEN").orNull ?: throw GradleException(
                "MAPBOX_DOWNLOADS_TOKEN is missing in local.properties. ".plus(
                    "Did you forget to set it?"
                )
            )

        url = uri("https://api.mapbox.com/downloads/v2/releases/maven")
        credentials.username = "mapbox"
        credentials.password = mapboxDownloadToken
        authentication.create<BasicAuthentication>("basic")
    }
}

apply("./plugin/docsTasks.gradle.kts") // applies all tasks related to docs
val discoverImagesInProject =
    uncheckedCast<(project: Project) -> (List<File>?)>(extra["discoverImagesInProject"])
val dokkaDocsOutputDir = uncheckedCast<File>(extra["dokkaDocsOutputDir"])
val copyMarkdownDocsTask = uncheckedCast<TaskProvider<Task>>(extra["copyMarkdownDocsTask"])

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
            discoverImagesInProject!!(project)
        }.flatten()
    }
}
