plugins {
    id("seatliberator.spring-application-base")
}

dependencies {
    // Web Security
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")

    // Event
    implementation(project(":event-relay:event-relay-support-jpa"))
    implementation(project(":event-relay:event-relay-support-kafka"))
}
