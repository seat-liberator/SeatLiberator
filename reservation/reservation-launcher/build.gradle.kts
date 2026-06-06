plugins {
    id("seatliberator.layer.launcher")
}

group = "com.seatliberator.seatliberator"
version = "0.0.1-SNAPSHOT"

dependencies {
    implementation(project(":reservation:reservation-web-mvc"))
    implementation(project(":reservation:reservation-persistence"))
    implementation(project(":reservation:reservation-application"))
    implementation(project(":reservation:reservation-domain"))

    implementation(project(":starter:spring-application-launcher"))

    implementation(libs.spring.boot.starter.validation)
}
