plugins {
    id("seatliberator.resource-application")
}

group = "com.seatliberator.seatliberator"
version = "0.0.1-SNAPSHOT"

dependencies {
    // API
    implementation(project(":reservation:reservation-api"))

    // Domain
    implementation(project(":reservation:reservation-domain"))
    testImplementation(testFixtures(project(":reservation:reservation-domain")))

    // External API
    implementation(project(":notification:notification-api"))
    testImplementation("org.springframework.boot:spring-boot-starter-data-jpa-test")
}

tasks.bootJar {
    enabled = false
}

tasks.jar {
    enabled = true
}
