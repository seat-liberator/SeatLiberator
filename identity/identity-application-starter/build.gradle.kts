plugins {
    id("seatliberator.base.library")
    id("seatliberator.spring.autoconfigure")
}

group = "com.seatliberator.seatliberator"
version = "0.0.1-SNAPSHOT"

dependencies {
    api(project(":identity:identity-core"))
    api(project(":starter:application-starter"))
}
