plugins {
    id("java")
}

group = "com.seatliberator.seatliberator"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // API
    implementation(project(":reservation:reservation-api"))

    // Web
    implementation("org.springframework.boot:spring-boot-starter-webmvc")

    // Web Security
    implementation(project(":identity:identity-client"))
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    implementation("org.springframework.boot:spring-boot-starter-security")

    // Persistence
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    runtimeOnly("org.postgresql:postgresql")

    // Event
    implementation(project(":event-relay:event-relay-support-jpa"))
    implementation(project(":event-relay:event-relay-support-kafka"))
    implementation("org.springframework.boot:spring-boot-starter-kafka")

    // Validation
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // Lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.testcontainers:testcontainers-junit-jupiter")
    testImplementation("org.testcontainers:postgresql:1.21.3")
    testImplementation("org.testcontainers:jdbc")
    testRuntimeOnly("com.h2database:h2")
}
tasks.test {
    useJUnitPlatform()
}
