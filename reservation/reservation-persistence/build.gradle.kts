plugins {
    id("seatliberator.persistence-base")
}

group = "com.seatliberator.seatliberator"
version = "0.0.1-SNAPSHOT"

dependencies {
    implementation(project(":notification:notification-api"))
    implementation(project(":reservation:reservation-application"))
    implementation(project(":reservation:reservation-domain"))

    testImplementation(project(":kernel:kernel-test"))
    testImplementation(testFixtures(project(":reservation:reservation-domain")))
}

tasks.test {
    useJUnitPlatform()
}
