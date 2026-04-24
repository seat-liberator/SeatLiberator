plugins {
    id("java")
}

group = "com.seatliberator.seatliberator"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    api(project(":kernel:kernel-core"))

    api(project(":event-relay:event-relay-core"))
    implementation(project(":event-relay:event-relay-support-jpa"))
    implementation(project(":event-relay:event-relay-support-kafka"))

    implementation("org.springframework:spring-context")
    implementation("org.springframework.boot:spring-boot-autoconfigure")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    testImplementation(project(":event-relay:event-relay-test"))
    testImplementation(project(":event-relay:event-relay-support-jpa"))
    testImplementation(project(":event-relay:event-relay-support-kafka"))
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-starter-kafka")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}