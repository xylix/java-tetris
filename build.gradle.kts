plugins {
    java
    `maven-publish`
    id("org.openjfx.javafxplugin") version "0.0.9"
    id("application")
}

repositories {
    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }
}

dependencies {
    implementation("com.google.code.gson:gson:2.8.6")
    // implementation("org.openjfx:javafx-controls:15")
    //implementation("org.openjfx:javafx-graphics:15")
    //implementation("org.openjfx:javafx-graphics:15")
    //implementation("org.openjfx:javafx-graphics:15")
    implementation("org.tinylog:tinylog-api:2.0.1")
    implementation("org.tinylog:tinylog-impl:2.0.1")
    implementation("com.konghq:unirest-java:3.3.00")
    testImplementation("junit:junit:4.13.1")
    testImplementation("org.testfx:openjfx-monocle:jdk-11+26")
    testImplementation("org.testfx:testfx-junit:4.0.15-alpha")
    testImplementation("org.hamcrest:hamcrest-core:2.1")
    testImplementation("com.github.stefanbirkner:system-rules:1.19.0")
}

javafx {
    version = "16"
    modules("javafx.controls", "javafx.fxml", "javafx.media")
}

group = "kaantelypeli"
version = "0.4.0"
description = "kaantelypeli"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(16))
    }
}

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}

tasks.withType<JavaCompile>() {
    options.encoding = "UTF-8"
}

application {
    mainClass.set("tetris.Main")
}
