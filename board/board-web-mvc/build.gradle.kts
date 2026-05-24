plugins {
    id("seatliberator.layer.webmvc")
}

group = "com.seatliberator.seatliberator"
version = "0.0.1-SNAPSHOT"

dependencies {
    implementation(project(":board:board-persistence"))
    implementation(project(":board:board-application"))
    implementation(project(":board:board-domain"))

    implementation(project(":identity:identity-client-starter"))

    testImplementation(project(":kernel:kernel-test"))
}
