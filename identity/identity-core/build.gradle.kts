plugins {
    id("seatliberator.base.library")
    id("java-test-fixtures")
}

group = "com.seatliberator.seatliberator"
version = "0.0.1-SNAPSHOT"

dependencies {
    api(project(":kernel:kernel-core"))

    implementation("org.springframework:spring-context")
    implementation("org.springframework.boot:spring-boot-autoconfigure")
    implementation("org.slf4j:slf4j-api")

    // Validation
    implementation("org.springframework.boot:spring-boot-starter-validation")

    testImplementation("ch.qos.logback:logback-classic:1.5.18")
    testImplementation(project(":kernel:kernel-test"))
}

tasks.test {
    useJUnitPlatform()
}