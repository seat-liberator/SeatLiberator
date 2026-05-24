plugins {
    id("seatliberator.layer.application")
}

group = "com.seatliberator.seatliberator"
version = "0.0.1-SNAPSHOT"

dependencies {
    implementation(project(":notification:notification-api"))
    implementation(project(":reservation:reservation-api"))
    implementation(project(":reservation:reservation-domain"))

    implementation(project(":event-relay:event-relay-starter"))
    implementation(project(":identity:identity-application-starter"))

    testImplementation(project(":kernel:kernel-test"))
    testImplementation(testFixtures(project(":reservation:reservation-domain")))
    testImplementation(testFixtures(project(":identity:identity-core")))
}
