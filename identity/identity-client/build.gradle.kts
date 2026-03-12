plugins {
    id("java")
}

group = "com.seatliberator.seatliberator"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":identity:identity-core"))

    // Validation
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // Jwt
    implementation("org.springframework.security:spring-security-oauth2-jose")
}

tasks.test {
    useJUnitPlatform()
}