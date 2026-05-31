plugins {
    id("seatliberator.layer.redis")
    id("seatliberator.spring.autoconfigure")
}

group = "com.seatliberator.seatliberator"
version = "0.0.1-SNAPSHOT"

dependencies {
    implementation(project(":identity:identity-server:identity-server-application"))
    implementation(project(":identity:identity-server:identity-server-domain"))
    implementation(project(":identity:identity-core"))
    implementation(project(":kernel:kernel-core"))

    testImplementation(project(":kernel:kernel-test"))
    testImplementation(testFixtures(project(":identity:identity-server:identity-server-domain")))
}