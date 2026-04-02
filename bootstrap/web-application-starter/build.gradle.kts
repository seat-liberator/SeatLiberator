plugins {
    id("java")
    id("java-library")
}

group = "com.seatliberator.seatliberator"
version = "0.0.1-SNAPSHOT"

dependencies {
    api(project(":kernel"))

    // Web
    api("org.springframework.boot:spring-boot-starter-webmvc")
    api("org.springframework.boot:spring-boot-starter-validation")

    // Persistence
    api("org.springframework.boot:spring-boot-starter-data-jpa")
    runtimeOnly("org.postgresql:postgresql")
    runtimeOnly("com.h2database:h2")
    testRuntimeOnly("com.h2database:h2")

    // Event
    api(project(":event-relay:event-relay-core"))
    implementation(project(":event-relay:event-relay-support-jpa"))
    implementation(project(":event-relay:event-relay-support-kafka"))
    implementation("org.springframework.boot:spring-boot-starter-kafka")
}
