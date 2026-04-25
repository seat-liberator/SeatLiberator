plugins {
    id("java")
}

group = "com.seatliberator.seatliberator"
version = "0.0.1-SNAPSHOT"

dependencies {
    api(project(":kernel:kernel-core"))
    api(project(":identity:identity-api"))
}

tasks.test {
    useJUnitPlatform()
}
