plugins {
    id("seatliberator.spring-boot-application-base")
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

    testImplementation(project(":identity:identity-application"))
    testImplementation(project(":reservation:reservation-web-mvc"))
    testImplementation(project(":board:board-application"))
    testImplementation(project(":notification:notification-application"))
}

tasks.test {
    useJUnitPlatform()
}
