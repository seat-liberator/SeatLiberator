plugins {
    id("base.seatliberator.spring-application-base")
}

group = "com.seatliberator.seatliberator"
version = "0.0.1-SNAPSHOT"

val springCloudVersion = "2025.1.1"

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:$springCloudVersion")
    }
}

dependencies {
    implementation("org.springframework.cloud:spring-cloud-starter-gateway-server-webflux")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation(project(":identity:identity-application"))
    testImplementation(project(":reservation:reservation-application"))
    testImplementation(project(":board:board-application"))
    testImplementation(project(":notification:notification-application"))
}

tasks.test {
    useJUnitPlatform()
}
