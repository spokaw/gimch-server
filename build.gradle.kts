import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.22"
    id("io.ktor.plugin") version "2.3.8"
    kotlin("plugin.serialization") version "1.9.22"
}

group = "com.diplom"
version = "0.0.1"

application {
    mainClass.set("io.ktor.server.netty.EngineMain")
}

repositories {
    mavenCentral()
}

dependencies {
    // Ktor Core
    implementation("io.ktor:ktor-server-core-jvm:2.3.8")
    implementation("io.ktor:ktor-server-netty-jvm:2.3.8")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:2.3.8")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:2.3.8")
    implementation("io.ktor:ktor-server-call-logging-jvm:2.3.8")

    // Auth & Security
    implementation("io.ktor:ktor-server-auth-jvm:2.3.8")
    implementation("io.ktor:ktor-server-auth-jwt-jvm:2.3.8")
    implementation("io.ktor:ktor-server-cors-jvm:2.3.8")
    implementation("at.favre.lib:bcrypt:0.10.2")

    // Database
    implementation("org.postgresql:postgresql:42.7.1")
    implementation("com.zaxxer:HikariCP:5.0.1")

    // .env support
    implementation("io.github.cdimascio:dotenv-kotlin:6.4.1")

    // Logging
    implementation("ch.qos.logback:logback-classic:1.4.14")

    // Testing
    testImplementation("io.ktor:ktor-server-tests-jvm:2.3.8")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:1.9.22")
    testImplementation("io.mockk:mockk:1.13.9")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "17"
    }
}

ktor {
    fatJar {
        archiveFileName.set("gimch-server.jar")
    }
}

