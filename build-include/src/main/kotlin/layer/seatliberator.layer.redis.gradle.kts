plugins {
    id("seatliberator.spring.module")
}

val libs = extensions
    .getByType<VersionCatalogsExtension>()
    .named("libs")

dependencies {
    add("implementation", libs.findLibrary("spring-boot-starter-data-redis").get());
    add("implementation", libs.findLibrary("tools-jackson-databind").get())

    add("testImplementation", libs.findLibrary("spring-boot-testcontainers").get())
    add("testImplementation", libs.findLibrary("spring-boot-starter-data-redis-test").get())
}