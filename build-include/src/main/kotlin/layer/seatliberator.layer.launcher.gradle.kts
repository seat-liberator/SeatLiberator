plugins {
    id("org.springframework.boot")
    id("seatliberator.spring.module")
}

val libs = extensions
    .getByType<VersionCatalogsExtension>()
    .named("libs")

dependencies {
    add("implementation", libs.findLibrary("spring-boot-starter-actuator").get())

    add("implementation", platform(libs.findLibrary("micrometer-bom").get()))
    add("implementation", platform(libs.findLibrary("micrometer-tracing-bom").get()))
    add("implementation", libs.findLibrary("micrometer-registry-prometheus").get())
    add("implementation", libs.findLibrary("micrometer-tracing-bridge-otel").get())

    add("implementation", platform(libs.findLibrary("opentelemetry-bom").get()))
    add("implementation", libs.findLibrary("opentelemetry-exporter-otlp").get())
}
