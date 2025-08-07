plugins {
    kotlin("jvm") version "2.0.21"
}

group = "isel.project"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("com.google.code.gson:gson:2.12.1")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}

tasks.jar.configure { manifest { attributes(mapOf("Main-Class" to "org.example.main.MainCoverageReportKt")) }
    configurations["compileClasspath"].forEach { file: File -> from(zipTree(file.absoluteFile)) }
    duplicatesStrategy = DuplicatesStrategy.INCLUDE }