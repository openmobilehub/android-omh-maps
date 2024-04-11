import org.jetbrains.dokka.DokkaConfiguration
import org.jetbrains.dokka.base.DokkaBase
import org.jetbrains.dokka.base.DokkaBaseConfiguration
import org.jetbrains.dokka.gradle.DokkaTaskPartial
import java.net.URL

val useMavenLocal by extra(getBooleanFromProperties("useMavenLocal", null))
val useLocalProjects by extra(getBooleanFromProperties("useLocalProjects", null))

if (useLocalProjects) {
    println("OMH Maps project running with useLocalProjects enabled")
}

if (useMavenLocal) {
    println(
        "OMH Maps project running with useMavenLocal enabled${
            if (useLocalProjects) ", but only publishing will be altered since dependencies are overriden by useLocalProjects"
            else ""
        } "
    )
}

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
            maven("https://atlas.microsoft.com/sdk/android")
            configureMapboxMaven()
        }
    } else {
        repositories {
            mavenCentral()
            google()
            maven("https://s01.oss.sonatype.org/content/groups/staging/")
            maven("https://atlas.microsoft.com/sdk/android")
            configureMapboxMaven()
        }
    }
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
        ":packages:plugin-azuremaps:publishToMavenLocal",
        ":packages:plugin-azuremaps:publishToMavenLocal",
    )
}

tasks {
    val installPrePushHook by existing
    val installPreCommitHook by existing
    getByName("prepareKotlinBuildScriptModel").dependsOn(installPrePushHook)
    getByName("prepareKotlinBuildScriptModel").dependsOn(installPreCommitHook)
}

val publishToReleaseRepository =
    getValueFromEnvOrProperties("publishingSonatypeRepository")?.toString() == "release"

if (!useMavenLocal) {
    println(
        "OMH Maps project configured to publish to Sonatype "
                + (if (publishToReleaseRepository) "release" else "snapshot")
                + " repository"
    )

    if (!publishToReleaseRepository) {
        subprojects {
            version = "$version-SNAPSHOT" // required for publishing to the snapshot repository
        }
    }

    val ossrhUsername = getValueFromEnvOrProperties("OSSRH_USERNAME", null)
    val ossrhPassword = getValueFromEnvOrProperties("OSSRH_PASSWORD", null)
    val mStagingProfileId = getValueFromEnvOrProperties("SONATYPE_STAGING_PROFILE_ID", null)
    val signingKeyId by extra(getValueFromEnvOrProperties("SIGNING_KEY_ID", null))
    val signingPassword by extra(getValueFromEnvOrProperties("SIGNING_PASSWORD", null))
    val signingKey by extra(getValueFromEnvOrProperties("SIGNING_KEY", null))

    // Set up Sonatype repository
    afterEvaluate {
        nexusPublishing {
            // fix for nexus publishing plugin not picking up the correct version of the published
            // subproject, since this is happening in the root project;
            // see https://github.com/gradle-nexus/publish-plugin/issues/105
            useStaging.set(provider { publishToReleaseRepository })

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
}

fun RepositoryHandler.configureMapboxMaven() {
    maven {
        val mapboxDownloadToken =
            getRequiredValueFromEnvOrProperties("MAPBOX_DOWNLOADS_TOKEN", null)

        url = uri("https://api.mapbox.com/downloads/v2/releases/maven")
        credentials.username = "mapbox"
        credentials.password = mapboxDownloadToken
        authentication.create<BasicAuthentication>("basic")
    }
}

apply(from = rootProject.file("buildSrc/docs-tasks.gradle.kts")) // registers all tasks related to docs
val dokkaDocsOutputDir = getDokkaDocsOutputDir()

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
            project.discoverImagesInProject()
        }.flatten()
    }
}
