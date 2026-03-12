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

    // Web
     implementation("org.springframework:spring-webflux")

    // Logging
    implementation("org.slf4j:slf4j-api")
}

tasks.test {
    useJUnitPlatform()
}