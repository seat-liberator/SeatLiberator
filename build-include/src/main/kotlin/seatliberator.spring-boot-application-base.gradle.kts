plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    id("seatliberator.spring-module-base")
}

val libs = extensions
    .getByType<VersionCatalogsExtension>()
    .named("libs")

dependencies {
    add("implementation", libs.findLibrary("spring-boot-starter-actuator").get())

    add("runtimeOnly", libs.findLibrary("micrometer-registry-prometheus").get())
}
