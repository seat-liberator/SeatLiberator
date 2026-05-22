plugins {
    id("seatliberator.layer.domain")
}

group = "com.seatliberator.seatliberator"
version = "0.0.1-SNAPSHOT"

dependencies {
    implementation(project(":kernel:kernel-core"))
    testImplementation(project(":kernel:kernel-test"))
}