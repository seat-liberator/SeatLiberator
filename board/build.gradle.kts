plugins {
    id("java")
}

group = "com.seatliberator.seatliberator"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // Identity
    implementation(project(":identity:identity-client"))

    // Spring Web MVC
    implementation("org.springframework.boot:spring-boot-starter-webmvc")

    // Spring Data Jpa
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    // Bean Validation (Jakarta)
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // Spring Security
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")

    // Lombok
    compileOnly("org.projectlombok:lombok")
    runtimeOnly("org.postgresql:postgresql")
    annotationProcessor("org.projectlombok:lombok")
}

tasks.test {
    useJUnitPlatform()
}
