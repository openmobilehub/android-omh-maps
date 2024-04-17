val useMavenLocal = project.rootProject.extra["useMavenLocal"] as Boolean

plugins {
    id("com.android.library")
    id("io.gitlab.arturbosch.detekt")
    kotlin("android")
    id("jacoco")
    id("maven-publish")
    id("signing").apply(false)
}

if (!useMavenLocal) {
    apply<SigningPlugin>()
}

android {
    compileSdk = ConfigData.compileSdkVersion
    defaultConfig {
        minSdk = ConfigData.minSdkVersion
        vectorDrawables {
            useSupportLibrary = true
        }
        consumerProguardFiles("consumer-rules.pro")
    }

    packagingOptions {
        resources.excludes.add("META-INF/*")
        resources.excludes.add("**/LICENSE.txt")
        resources.excludes.add("**/README.txt")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    testOptions {
        execution = "ANDROIDX_TEST_ORCHESTRATOR"
        unitTests {
            isReturnDefaultValues = true
            isIncludeAndroidResources = true
        }
    }
}

setupJacoco()

tasks.named("jacocoCoverageVerification").configure { dependsOn("mergeDebugJniLibFolders") }
tasks.named("jacocoCoverageVerification").configure { dependsOn("copyDebugJniLibsProjectAndLocalJars") }
tasks.named("jacocoCoverageVerification").configure { dependsOn("copyDebugJniLibsProjectOnly") }
tasks.named("jacocoCoverageVerification").configure { dependsOn("syncDebugLibJars") }

dependencies {
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:${Versions.detekt}")
}

// Publishing block

val groupProperty = getRequiredValueFromEnvOrProperties("group")
val versionProperty = getRequiredValueFromEnvOrProperties("version")
val artifactId = getRequiredValueFromEnvOrProperties("artifactId")
val mDescription = getRequiredValueFromEnvOrProperties("description")

android {
    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

fun MavenPublication.setupPublication() {
    groupId = groupProperty
    artifactId = artifactId
    version = versionProperty

    if (project.project.plugins.findPlugin("com.android.library") != null) {
        from(project.components["release"])
    } else {
        from(project.components["java"])
    }

    pom {
        name.set(artifactId)
        description.set(mDescription)
        url.set("https://github.com/openmobilehub/omh-maps")
        licenses {
            license {
                name.set("Apache-2.0 License")
                url.set("https://github.com/openmobilehub/omh-maps/blob/main/LICENSE")
            }
        }

        developers {
            developer {
                id.set("hans-hamel")
                name.set("Hans Hamel")
            }
        }

        // Version control info - if you're using GitHub, follow the
        // format as seen here
        scm {
            connection.set("scm:git:github.com/openmobilehub/omh-maps.git")
            developerConnection.set("scm:git:ssh://github.com/openmobilehub/omh-maps.git")
            url.set("https://github.com/openmobilehub/omh-maps/tree/main")
        }
    }
}

if (useMavenLocal) {
    publishing {
        publications {
            register<MavenPublication>("release") {
                group = groupProperty
                artifactId = artifactId
                version = versionProperty

                afterEvaluate {
                    from(components["release"])
                }
            }
        }
    }
} else {
    group = groupProperty
    version = versionProperty

    afterEvaluate {
        publishing {
            publications {
                register("release", MavenPublication::class.java) {
                    setupPublication()
                }
            }
        }
    }

    signing {
        useInMemoryPgpKeys(
            rootProject.ext["signingKeyId"].toString(),
            rootProject.ext["signingKey"].toString(),
            rootProject.ext["signingPassword"].toString(),
        )
        sign(publishing.publications)
    }
}
