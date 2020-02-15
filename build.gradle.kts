import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.61"
}

group = "pl.michalperlak"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val arrowVersion: String by project
val junitVersion: String by project
val equalsVerifierVersion: String by project

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("io.arrow-kt", "arrow-core", arrowVersion)

    testImplementation("org.junit.jupiter", "junit-jupiter", junitVersion)
    testImplementation("nl.jqno.equalsverifier", "equalsverifier", equalsVerifierVersion)
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}
