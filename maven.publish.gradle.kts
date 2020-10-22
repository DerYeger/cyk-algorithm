// From https://stackoverflow.com/a/63502756/3891161

import org.gradle.api.publish.PublishingExtension
import java.io.FileInputStream
import java.util.*

apply(plugin = "maven-publish")

val localProperties = Properties().apply {
    val file = project.rootProject.file("local.properties")
    if (file.exists()) {
        load(FileInputStream(file))
    }
}
val bintrayUser: String = System.getenv("BINTRAY_USER") ?: localProperties.getProperty("bintrayUser") ?: ""
val bintrayApiKey: String = System.getenv("BINTRAY_API_KEY") ?: localProperties.getProperty("bintrayApiKey") ?: ""
val libraryVersion: String by project
val publishedGroupId: String by project
val artifact: String by project
val bintrayRepo: String by project
val libraryName = project.name
val bintrayName: String by project
val libraryDescription: String by project
val siteUrl: String by project
val gitUrl: String by project
val licenseName: String by project
val licenseUrl: String by project
val developerName: String by project
val developerEmail: String by project
val developerId: String by project

project.group = publishedGroupId
project.version = libraryVersion

afterEvaluate {
    configure<PublishingExtension> {
        publications.all {
            val mavenPublication = this as? MavenPublication
            mavenPublication?.artifactId =
                "${project.name}${"-$name".takeUnless { "kotlinMultiplatform" in name }.orEmpty()}"
        }
    }
}

configure<PublishingExtension> {
    publications {
        withType<MavenPublication> {
            groupId = publishedGroupId
            artifactId = artifact
            version = libraryVersion

            pom {
                name.set(libraryName)
                description.set(libraryDescription)
                url.set(siteUrl)

                licenses {
                    license {
                        name.set(licenseName)
                        url.set(licenseUrl)
                    }
                }
                developers {
                    developer {
                        id.set(developerId)
                        name.set(developerName)
                        email.set(developerEmail)
                    }
                }
                scm {
                    connection.set(gitUrl)
                    developerConnection.set(gitUrl)
                    url.set(siteUrl)
                }
            }
        }
    }

    repositories {
        maven("https://api.bintray.com/maven/$bintrayUser/$bintrayRepo/$artifact/;publish=1") {
            credentials {
                username = bintrayUser
                password = bintrayApiKey
            }
        }
    }
}
