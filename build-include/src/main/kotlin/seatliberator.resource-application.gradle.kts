plugins {
    id("seatliberator.web-application")
}

dependencies {
    implementation(project(":bootstrap:resource-application-starter"))
    testImplementation("org.springframework.security:spring-security-test")
}
