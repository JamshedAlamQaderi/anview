plugins {
    id("com.android.application") version "7.3.1" apply false
    id("com.android.library") version "7.3.1" apply false
    kotlin("multiplatform") version "1.7.20" apply false
    kotlin("plugin.serialization") version "1.7.20" apply false
    id("org.jlleitschuh.gradle.ktlint") version "11.0.0" apply false
    id("org.jetbrains.dokka") version "1.7.20" apply false
    id("com.vanniktech.maven.publish") version "0.22.0" apply false
    id("org.jetbrains.kotlinx.kover") version "0.6.1" apply false
}

val projectVersion: String? by project

allprojects {
    group = "com.jamshedalamqaderi"
    version = projectVersion?.replaceFirst("v", "", true) ?: "0.0.1-SNAPSHOT"
    apply {
        plugin("org.jetbrains.kotlinx.kover")
    }
}