plugins {
    java
    `java-library`
    id("org.springframework.boot") version "4.0.2" apply false
    id("io.spring.dependency-management") version "1.1.7" apply false
}

group = "com.seatliberator.seatliberator"
version = "0.0.1-SNAPSHOT"
description = "Seat reservation application"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "java-library")
    apply(plugin = "io.spring.dependency-management")

    dependencies {
        "implementation"(platform("org.springframework.boot:spring-boot-dependencies:4.0.2"))
        "annotationProcessor"(platform("org.springframework.boot:spring-boot-dependencies:4.0.2"))
        "testImplementation"(platform("org.springframework.boot:spring-boot-dependencies:4.0.2"))
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}
