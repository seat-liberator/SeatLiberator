plugins {
    id("seatliberator.module-base")
}

val libs = extensions
    .getByType<VersionCatalogsExtension>()
    .named("libs")

dependencies {
    add("implementation", libs.findLibrary("spring-context").get())
    add("implementation", libs.findLibrary("spring-tx").get())

    add("implementation", platform(libs.findLibrary("spring-boot-dependencies").get()))
    add("implementation", libs.findLibrary("spring-boot-autoconfigure").get())
    add("implementation", libs.findLibrary("slf4j-api").get())

    add("testImplementation", libs.findLibrary("spring-boot-starter-test").get())
    add("testImplementation", libs.findLibrary("mockito-core").get())
    add("testImplementation", libs.findLibrary("mockito-junit-jupiter").get())
}