plugins {
    id("seatliberator.base.library")
    id("seatliberator.spring.module")
}

group = "com.seatliberator.seatliberator"
version = "0.0.1-SNAPSHOT"

dependencies {
    api(project(":kernel:kernel-core"))
    api(project(":identity:identity-server:identity-server-api"))
}
