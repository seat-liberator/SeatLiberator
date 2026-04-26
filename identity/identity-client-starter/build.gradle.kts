plugins {
    id("java")
}

group = "com.seatliberator.seatliberator"
version = "0.0.1-SNAPSHOT"

dependencies {
    api(project(":identity:identity-core"))

    // Validation
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // Jwt
    implementation("org.springframework.security:spring-security-oauth2-jose")

    // Web
    implementation("org.springframework:spring-webflux")

    // Logging
    implementation("org.slf4j:slf4j-api")

    // Test
    testImplementation(project(":identity:identity-core"))
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}