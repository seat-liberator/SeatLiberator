plugins {
    id("seatliberator.spring.platform")
}

val libs = extensions
    .getByType<VersionCatalogsExtension>()
    .named("libs")

dependencies {
    add("implementation", libs.findLibrary("jakarta-persistence-api").get())

    add("testImplementation", libs.findLibrary("jakarta-persistence-api").get())

    plugins.withId("java-library") {
        add("compileOnlyApi", libs.findLibrary("jakarta-persistence-api").get())
    }

    plugins.withId("java-test-fixtures") {
        add("testFixturesCompileOnlyApi", platform(libs.findLibrary("spring-boot-dependencies").get()))
        add("testFixturesCompileOnlyApi", libs.findLibrary("jakarta-persistence-api").get())
    }
}
