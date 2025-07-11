plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlin.serialization)
    application
}

application {
    mainClass.set("org.noxfr.MainKt")
}

group = "org.noxfr"
version = "0.1.0"

dependencies {
    implementation(libs.modelcontextprotocol)
    implementation(libs.kotlin.logging)
    implementation(libs.logback)
    
    // Ktor client dependencies
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.jackson.jvm)
    implementation(libs.ktor.client.auth)
    implementation(libs.ktor.client.logging)
    implementation(libs.koin.ktor)
    implementation(libs.koin.core)
}

repositories {
    mavenCentral()
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}