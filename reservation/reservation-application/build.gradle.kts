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

    // Web application
    implementation("org.springframework.boot:spring-boot-starter-webmvc")

    // Util
    compileOnly("org.projectlombok:lombok")
    runtimeOnly("org.postgresql:postgresql")
    annotationProcessor("org.projectlombok:lombok")

    // Spring Data Jpa
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.testcontainers:testcontainers-junit-jupiter")
    testImplementation("org.testcontainers:postgresql:1.21.3")
    testImplementation("org.testcontainers:jdbc")

    // PostgreSQL
    runtimeOnly("org.postgresql:postgresql")
}
tasks.test {
    useJUnitPlatform()
}