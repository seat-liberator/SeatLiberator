plugins {
    id("java")
}

group = "com.seatliberator.seatliberator"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    api(project(":kernel"))
    api(project(":event-relay:event-relay-core"))
    implementation(project(":identity:identity-api"))

    implementation("org.springframework:spring-context")
}

tasks.test {
    useJUnitPlatform()
}