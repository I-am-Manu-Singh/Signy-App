plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("org.jetbrains.compose")
    kotlin("plugin.compose")
    kotlin("plugin.serialization")
    id("app.cash.sqldelight")
}

kotlin {
    androidTarget()
    jvm("desktop")
    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "shared"
            isStatic = true
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material)
                implementation(compose.materialIconsExtended)
                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                implementation(compose.components.resources)

                // Navigation
                implementation("cafe.adriel.voyager:voyager-navigator:${project.property("voyager.version")}")
                implementation("cafe.adriel.voyager:voyager-transitions:${project.property("voyager.version")}")
                implementation("cafe.adriel.voyager:voyager-tab-navigator:${project.property("voyager.version")}")

                // DI
                implementation("io.insert-koin:koin-core:${project.property("koin.version")}")
                implementation("io.insert-koin:koin-compose:${project.property("koin.version")}")

                // Networking
                implementation("io.ktor:ktor-client-core:${project.property("ktor.version")}")
                implementation("io.ktor:ktor-client-content-negotiation:${project.property("ktor.version")}")
                implementation("io.ktor:ktor-serialization-kotlinx-json:${project.property("ktor.version")}")
                implementation("io.ktor:ktor-client-websockets:${project.property("ktor.version")}")

                // Supabase (Back-end as a Service) - API access for app-level deep linking
                api(platform("io.github.jan-tennert.supabase:bom:${project.property("supabase.version")}"))
                api("io.github.jan-tennert.supabase:postgrest-kt")
                api("io.github.jan-tennert.supabase:auth-kt")
                api("io.github.jan-tennert.supabase:realtime-kt")

                // WebRTC
                implementation("com.shepeliev:webrtc-kmp:${project.property("webrtc.version")}")
                
                // UUID
                implementation("com.benasher44:uuid:0.8.4")
            }
        }
        val androidMain by getting {
            dependencies {
                api("androidx.activity:activity-compose:1.7.2")
                api("androidx.appcompat:appcompat:1.6.1")
                api("androidx.core:core-ktx:1.10.1")
                implementation("androidx.media3:media3-exoplayer:1.2.0")
                implementation("androidx.media3:media3-exoplayer-rtsp:1.2.0")
                implementation("androidx.media3:media3-ui:1.2.0")
                implementation("io.ktor:ktor-client-okhttp:${project.property("ktor.version")}")
                implementation("com.google.maps.android:maps-compose:4.3.3")
                implementation("com.google.android.gms:play-services-maps:18.2.0")
                implementation("androidx.browser:browser:1.5.0")
            }
        }
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            dependencies {
                implementation("io.ktor:ktor-client-darwin:${project.property("ktor.version")}")
            }
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
        }
        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.common)
            }
        }
    }
}

android {
    compileSdk = (findProperty("android.compileSdk") as String).toInt()
    namespace = "com.myapplication.common"

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        minSdk = (findProperty("android.minSdk") as String).toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        jvmToolchain(17)
    }
}

sqldelight {
    databases {
        create("AppDatabase") {
            packageName.set("com.myapplication.db")
        }
    }
}
