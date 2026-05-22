plugins {
    id("seatliberator.layer.application")
}

group = "com.seatliberator.seatliberator"
version = "0.0.1-SNAPSHOT"

dependencies {
    implementation(project(":board:board-api"))
    implementation(project(":board:board-domain"))

    implementation(project(":bootstrap:application-starter"))

    testImplementation(project(":kernel:kernel-test"))
    testImplementation(testFixtures(project(":board:board-domain")))
    testImplementation(testFixtures(project(":identity:identity-core")))
}