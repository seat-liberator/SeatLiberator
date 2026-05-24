plugins {
    id("seatliberator.layer.launcher")
    id("seatliberator.layer.persistence")
    id("seatliberator.layer.webmvc")
    id("seatliberator.layer.domain")
    id("seatliberator.layer.security")
}

group = "com.seatliberator.seatliberator"
version = "0.0.1-SNAPSHOT"

dependencies {
    implementation(project(":notification:notification-api"))

    implementation(project(":bootstrap:application-starter"))
    implementation(project(":bootstrap:security-starter"))
}