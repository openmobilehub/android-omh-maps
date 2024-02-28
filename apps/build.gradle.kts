plugins {
    id("org.jetbrains.dokka") version Versions.dokka
}

tasks.dokkaHtmlMultiModule {
    enabled = false
}
