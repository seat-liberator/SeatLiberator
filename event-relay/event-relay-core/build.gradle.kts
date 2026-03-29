plugins {
    id("java")
}

group = "com.seatliberator.seatliberator"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    api(project(":kernel"))

    implementation("org.springframework:spring-context")
    implementation("tools.jackson.core:jackson-databind")
    implementation("org.springframework.boot:spring-boot-autoconfigure")

    implementation("org.slf4j:slf4j-api")

    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}