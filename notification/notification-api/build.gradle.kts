plugins {
    id("java")
}

group = "com.seatliberator.seatliberator"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    api(project(":kernel:kernel-core"))
    api(project(":event-relay:event-relay-core"))

    implementation("org.springframework:spring-context")
}

tasks.test {
    useJUnitPlatform()
}