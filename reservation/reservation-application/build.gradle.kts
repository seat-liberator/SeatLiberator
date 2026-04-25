plugins {
    id("seatliberator.application-base")
}

group = "com.seatliberator.seatliberator"
version = "0.0.1-SNAPSHOT"

dependencies {
    implementation(project(":identity:identity-client"))
    implementation(project(":notification:notification-api"))
    implementation(project(":reservation:reservation-api"))
    implementation(project(":reservation:reservation-domain"))

    implementation(project(":bootstrap:application-starter"))

    testImplementation(testFixtures(project(":reservation:reservation-domain")))
}
