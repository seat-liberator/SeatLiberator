plugins {
    id("seatliberator.spring-application-base")
}

dependencies {
    // Web Security
    implementation(project(":identity:identity-client"))
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")

    // Event
    implementation(project(":event-relay:event-relay-support-jpa"))
    implementation(project(":event-relay:event-relay-support-kafka"))
}
