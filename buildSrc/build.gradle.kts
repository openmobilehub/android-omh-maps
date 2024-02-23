import org.jetbrains.kotlin.konan.properties.hasProperty
import java.util.Properties

var properties = Properties()
var localPropertiesFile = project.file("../local.properties")
if (localPropertiesFile.exists()) {
    properties.load(localPropertiesFile.inputStream())
}
var useMavenLocal =
    (rootProject.ext.has("useMavenLocal") && rootProject.ext.get("useMavenLocal") == "true") || (properties.hasProperty(
        "useMavenLocal"
    ) && properties.getProperty("useMavenLocal") == "true")

plugins {
    `kotlin-dsl`
}

repositories {
    if (useMavenLocal) {
        mavenLocal()
    }
    mavenCentral()
    google()
    gradlePluginPortal()
    maven("https://s01.oss.sonatype.org/content/groups/staging/")
}

configurations.all {
    resolutionStrategy.eachDependency {
        when (requested.name) {
            "javapoet" -> useVersion("1.13.0")
        }
    }
    resolutionStrategy {
        cacheChangingModulesFor(0, TimeUnit.SECONDS)
    }
}

dependencies {
    gradleApi()
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.20")
    implementation("com.android.tools.build:gradle:7.4.1")
    implementation("io.gitlab.arturbosch.detekt:detekt-gradle-plugin:1.22.0")
    implementation("org.jacoco:org.jacoco.core:0.8.8")
}
