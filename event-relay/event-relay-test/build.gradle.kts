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
    implementation("tools.jackson.core:jackson-databind")
}

tasks.test {
    useJUnitPlatform()
}