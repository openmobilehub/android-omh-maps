import java.util.Properties

fun getValueFromProperties(name: String, propertiesFile: File?): String? {
    if (propertiesFile?.exists() === true) {
        val properties = Properties()
        properties.load(propertiesFile.inputStream())
        return properties[name]?.toString()
    } else {
        return null
    }
}

fun getValueFromEnvOrProperties(name: String, propertiesFile: File? = null): String? {
    var properties = Properties()

    val projectLocalProperties = project.file("local.properties")
    val rootProjectLocalProperties = project.rootProject.file("local.properties")

    return System.getenv(name)
        ?: getValueFromProperties(name, propertiesFile)
        ?: getValueFromProperties(name, projectLocalProperties)
        ?: getValueFromProperties(name, rootProjectLocalProperties)
}

fun getRequiredValueFromEnvOrProperties(name: String, propertiesFile: File? = null): String {
    return getValueFromEnvOrProperties(name, propertiesFile) ?: throw GradleException(
        "$name was not found in environment variables, nor in any local.properties. ".plus(
            "Did you forget to set it?"
        )
    )
}

fun getBooleanFromProperties(name: String, propertiesFile: File? = null): Boolean {
    return getValueFromEnvOrProperties(name, propertiesFile) == "true"
}

extra["getValueFromEnvOrProperties"] =
    { name: String, propertiesFile: File? -> getValueFromEnvOrProperties(name, propertiesFile) }
extra["getRequiredValueFromEnvOrProperties"] =
    { name: String, propertiesFile: File? ->
        getRequiredValueFromEnvOrProperties(
            name,
            propertiesFile
        )
    }
extra["getBooleanFromProperties"] =
    { name: String, propertiesFile: File? -> getBooleanFromProperties(name, propertiesFile) }
