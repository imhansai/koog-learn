plugins {
    id("java")
    kotlin("jvm")
    kotlin("plugin.serialization")
}

group = "dev.fromnowon"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("ai.koog:koog-agents:0.6.0")
    implementation("ch.qos.logback:logback-classic:1.5.21")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(25)
}