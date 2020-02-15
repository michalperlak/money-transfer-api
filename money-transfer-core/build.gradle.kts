plugins {
    kotlin("jvm")
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
