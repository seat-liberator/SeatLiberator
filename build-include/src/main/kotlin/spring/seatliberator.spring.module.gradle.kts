plugins {
    id("seatliberator.base.module")
    id("seatliberator.spring.platform")
}

var libs = extensions
    .getByType<VersionCatalogsExtension>()
    .named("libs")

dependencies {
    add("implementation", libs.findLibrary("spring-context").get())
    add("implementation", libs.findLibrary("spring-tx").get())

    add("testImplementation", libs.findLibrary("spring-boot-starter-test").get())
    add("testImplementation", libs.findLibrary("mockito-core").get())
    add("testImplementation", libs.findLibrary("mockito-junit-jupiter").get())
}
