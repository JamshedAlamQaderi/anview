plugins {
    id("com.android.library")
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("org.jlleitschuh.gradle.ktlint")
    id("org.jetbrains.dokka")
    id("com.vanniktech.maven.publish")
}

android {
    namespace = "com.jamshedalamqaderi.anview"
    compileSdk = 33
    sourceSets["main"].manifest.srcFile("${project.projectDir}/src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 21
        targetSdk = 33
    }
}

kotlin {
    android {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
        publishLibraryVariants("release")
    }

    jvm()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")
            }
        }
        val commonTest by getting {
            dependencies {}
        }
        val androidMain by getting {
            dependencies {
                implementation("androidx.core:core-ktx:1.9.0")
                implementation("androidx.appcompat:appcompat:1.5.1")
                implementation("com.google.android.material:material:1.7.0")
            }
        }
        val androidTest by getting {
            dependencies {
                implementation("junit:junit:4.13.2")
                implementation("org.mockito:mockito-core:4.9.0")
                implementation("org.mockito.kotlin:mockito-kotlin:4.1.0")
            }
        }
        val androidAndroidTest by getting {
            dependencies {
                implementation("androidx.test.ext:junit:1.1.4")
                implementation("androidx.test.espresso:espresso-core:3.5.0")
            }
        }
    }
}

mavenPublishing {
    pom {
        name.set("anview")
        description.set("An Android library for scraping android view")
        inceptionYear.set("2022")
        url.set("https://github.com/JamshedAlamQaderi/anview")
        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                distribution.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }
        developers {
            developer {
                id.set("JamshedAlamQaderi")
                name.set("Jamshed Alam Qaderi")
                url.set("https://github.com/JamshedAlamQaderi")
            }
        }
        scm {
            url.set("https://github.com/JamshedAlamQaderi/anview")
            connection.set("scm:git:git://github.com/JamshedAlamQaderi/anview.git")
            developerConnection.set("scm:git:ssh://git@github.com/JamshedAlamQaderi/anview.git")
        }
    }
    signAllPublications()
    publishToMavenCentral(com.vanniktech.maven.publish.SonatypeHost.S01, true)
}
