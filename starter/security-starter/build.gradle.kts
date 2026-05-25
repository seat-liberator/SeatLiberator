plugins {
    id("seatliberator.base.library")
    id("seatliberator.layer.security")
    id("seatliberator.spring.autoconfigure")
}

group = "com.seatliberator.seatliberator"
version = "0.0.1-SNAPSHOT"

dependencies {
    api(libs.spring.security.core)
}