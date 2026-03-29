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
    implementation(project(":identity:identity-api"))
}

tasks.test {
    useJUnitPlatform()
}