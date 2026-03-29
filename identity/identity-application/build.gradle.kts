plugins {
    id("java")
}

group = "com.seatliberator.seatliberator"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":kernel"))

    implementation(project(":identity:identity-api"))
    implementation(project(":identity:identity-core"))

    // Authorization Registration
    implementation(project(":reservation:reservation-api"))
    implementation(project(":board:board-api"))

    // Web
    implementation("org.springframework.boot:spring-boot-starter-webmvc")

    // Persistence
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    // Event
    implementation(project(":event-relay:event-relay-support-jpa"))
    implementation(project(":event-relay:event-relay-support-kafka"))
    implementation("org.springframework.boot:spring-boot-starter-kafka")

    // Security
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    implementation("org.springframework.boot:spring-boot-starter-security")

    // Lombok
    compileOnly("org.projectlombok:lombok")
    runtimeOnly("org.postgresql:postgresql")
    annotationProcessor("org.projectlombok:lombok")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.20.1")

    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-data-jpa-test")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}
