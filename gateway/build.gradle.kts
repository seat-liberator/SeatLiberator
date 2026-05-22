plugins {
    id("seatliberator.layer.launcher")
}

group = "com.seatliberator.seatliberator"
version = "0.0.1-SNAPSHOT"

dependencies {
    implementation(platform(libs.spring.cloud.dependencies))
    implementation(libs.spring.cloud.starter.gateway.server.webflux)

    testImplementation(project(":identity:identity-server:identity-server-webmvc"))
    testImplementation(project(":reservation:reservation-web-mvc"))
    testImplementation(project(":board:board-web-mvc"))
    testImplementation(project(":notification:notification-application"))
}

tasks.test {
    useJUnitPlatform()
}
