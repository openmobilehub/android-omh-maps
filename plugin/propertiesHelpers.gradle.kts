import java.util.Properties

/**
 * Get a value from a properties file.
 * 
 * @param name the name of the property to resolve
 * @param propertiesFile the properties file to look in for the property
 * 
 * @return the value of the property, or `null` if it was not found or the file does not exist
 */
fun getValueFromProperties(name: String, propertiesFile: File?): String? {
    if (propertiesFile?.exists() === true) {
        val properties = Properties()
        properties.load(propertiesFile.inputStream())
        return properties[name]?.toString()
    } else {
        return null
    }
}

/**
 * Get a value from environment variables, from a properties file or gradle properties file.
 * Order of resolution stops as soon as a non-null value is resolved and is as follows:
 * - environment variables
 * - [propertiesFile] passed as parameter
 * - local.properties of the project
 * - local.properties of the root project
 * - gradle properties (via `providers.gradleProperty`)
 * 
 * @param name the name of the property to resolve
 * @param propertiesFile the properties file to look in for the property
 * 
 * @return the value of the property, or `null` if it was not found
 */
fun getValueFromEnvOrProperties(name: String, propertiesFile: File? = null): String? {
    var properties = Properties()

    val projectLocalProperties = project.file("local.properties")
    val rootProjectLocalProperties = project.rootProject.file("local.properties")

    return System.getenv(name)
        ?: getValueFromProperties(name, propertiesFile)
        ?: getValueFromProperties(name, projectLocalProperties)
        ?: getValueFromProperties(name, rootProjectLocalProperties)
        ?: providers.gradleProperty(name).orNull
}

/**
 * Get a required value from environment variables, from a properties file or gradle properties file.
 * If a property is not found, a [GradleException] is thrown.
 * 
 * @see getValueFromEnvOrProperties
 * 
 * @param name the name of the property to resolve
 * @param propertiesFile the properties file to look in for the property
 * 
 * @return the value of the property
 */
fun getRequiredValueFromEnvOrProperties(name: String, propertiesFile: File? = null): String {
    return getValueFromEnvOrProperties(name, propertiesFile) ?: throw GradleException(
        "$name was not found in environment variables, nor in any local.properties. ".plus(
            "Did you forget to set it?"
        )
    )
}

/**
 * Get a boolean value from environment variables, from a properties file or gradle properties file.
 * 
 * @see getValueFromEnvOrProperties
 * 
 * @param name the name of the property to resolve
 * @param propertiesFile the properties file to look in for the property
 * 
 * @return the value of the property, or `false` if it was not found
 */
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
