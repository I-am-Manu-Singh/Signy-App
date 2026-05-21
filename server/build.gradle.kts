plugins {
    kotlin("jvm")
    kotlin("plugin.serialization") version "1.9.21"
}

group = "com.myapplication"
version = "1.0-SNAPSHOT"

dependencies {
    implementation("io.ktor:ktor-server-core:2.3.7")
    implementation("io.ktor:ktor-server-netty:2.3.7")
    implementation("io.ktor:ktor-server-content-negotiation:2.3.7")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.7")
    implementation("io.ktor:ktor-server-websockets:2.3.7")
    implementation("io.ktor:ktor-server-auth:2.3.7")
    
    // Shared module for common models (optional, can just use commonMain)
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "com.myapplication.server.ApplicationKt"
    }
}
