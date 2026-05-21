plugins {
    id("seatliberator.persistence-base")
}

group = "com.seatliberator.seatliberator"
version = "0.0.1-SNAPSHOT"

dependencies {
    implementation(project(":board:board-application"))
    implementation(project(":board:board-domain"))

    testImplementation(project(":kernel:kernel-test"))
    testImplementation(testFixtures(project(":board:board-domain")))
}
