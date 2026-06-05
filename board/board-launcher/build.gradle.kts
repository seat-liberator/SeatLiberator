plugins {
    id("seatliberator.layer.launcher")
}

group = "com.seatliberator.seatliberator"
version = "0.0.1-SNAPSHOT"

dependencies {
    implementation(project(":board:board-web-mvc"))
    implementation(project(":board:board-persistence"))
    implementation(project(":board:board-application"))
    implementation(project(":board:board-domain"))

    implementation(project(":starter:spring-application-launcher"))

    implementation(libs.spring.boot.starter.validation)
}
