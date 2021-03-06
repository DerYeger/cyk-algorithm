val kotlinResultVersion: String by project
val libraryVersion: String by project
val publishedGroupId: String by project

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.dokka")
    id("org.jlleitschuh.gradle.ktlint")
}

group = publishedGroupId
version = libraryVersion

repositories {
    mavenCentral()
}

kotlin {
    explicitApi()

    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
    }

    js {
        browser {
            testTask {
                useKarma {
                    useChromeHeadless()
                }
            }
        }
    }

    sourceSets {
        all {
            languageSettings.enableLanguageFeature("InlineClasses")
        }

        val commonMain by getting
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
            }
        }
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
    }
}

tasks {
    build {
        dependsOn(ktlintFormat)
    }
}

apply(from = "maven.publish.gradle.kts")
