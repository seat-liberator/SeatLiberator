plugins {
    id("java")
}

group = "com.seatliberator.seatliberator"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":event-relay:event-relay-core"))
    implementation("org.springframework.boot:spring-boot-autoconfigure")
    implementation("org.springframework.kafka:spring-kafka")
    implementation("org.slf4j:slf4j-api")
}

tasks.test {
    useJUnitPlatform()
}