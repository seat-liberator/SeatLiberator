plugins {
    id("seatliberator.spring-boot-application-base")
    id("seatliberator.web-mvc-base")
}

group = "com.seatliberator.seatliberator"
version = "0.0.1-SNAPSHOT"

dependencies {
    implementation(project(":reservation:reservation-persistence"))
    implementation(project(":reservation:reservation-application"))
    implementation(project(":reservation:reservation-domain"))

    implementation(project(":bootstrap:resource-application-starter"))

    testImplementation(testFixtures(project(":reservation:reservation-domain")))
}
