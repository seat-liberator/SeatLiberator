plugins {
    id("seatliberator.base.library")
    id("java-test-fixtures")
}

group = "com.seatliberator.seatliberator"
version = "0.0.1-SNAPSHOT"

dependencies {
    api(project(":kernel:kernel-core"))

    testImplementation(project(":kernel:kernel-test"))
}

tasks.test {
    useJUnitPlatform()
}