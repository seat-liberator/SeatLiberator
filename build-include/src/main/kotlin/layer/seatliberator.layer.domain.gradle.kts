plugins {
    id("seatliberator.base.library")
    id("seatliberator.spring.jpa")
    id("java-test-fixtures")
}

val libs = extensions
    .getByType<VersionCatalogsExtension>()
    .named("libs")

dependencies {
    add("implementation", libs.findLibrary("spring-data-commons").get())
}