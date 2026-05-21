plugins {
    id("seatliberator.spring.platform")
}

val libs = extensions
    .getByType<VersionCatalogsExtension>()
    .named("libs")

dependencies {
    add("implementation", libs.findLibrary("spring-boot-autoconfigure").get())
    add("annotationProcessor", libs.findLibrary("spring-boot-configuration-processor").get())
}
