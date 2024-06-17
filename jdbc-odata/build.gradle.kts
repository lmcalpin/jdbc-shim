plugins {
    eclipse
    `java-library`
    id("org.jetbrains.dokka") version "1.9.20"
    `maven-publish`
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

repositories {
    mavenCentral()
    mavenLocal() // This is to access the local Maven repository.
}

dependencies {
    // Use JUnit Jupiter for testing.
    testImplementation(platform("org.junit:junit-bom:5.10.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    
    implementation(project(":jdbc-adapter"))

    implementation("org.apache.logging.log4j:log4j-api:2.20.0")
    implementation("org.apache.logging.log4j:log4j-core:2.20.0")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.20.0")
    implementation("com.google.guava:guava:33.2.0-jre")
    implementation("org.apache.olingo:odata-client-core:5.0.0")
    implementation("com.github.jsqlparser:jsqlparser:4.9")
}

// Apply a specific Java toolchain to ease working on different environments.
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

tasks.named<Test>("test") {
    // Use JUnit Platform for unit tests.
    useJUnitPlatform()
}

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}
