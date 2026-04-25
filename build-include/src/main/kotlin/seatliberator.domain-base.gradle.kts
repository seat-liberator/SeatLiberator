plugins {
    id("seatliberator.module-base")
    id("java-test-fixtures")
}

val libs = extensions
    .getByType<VersionCatalogsExtension>()
    .named("libs")

dependencies {
    add("compileOnly", platform(libs.findLibrary("spring-boot-dependencies").get()))
    add("compileOnly", libs.findLibrary("jakarta-persistence-api").get())
    add("compileOnly", libs.findLibrary("spring-data-commons").get())

    add("testFixturesCompileOnly", platform(libs.findLibrary("spring-boot-dependencies").get()))
    add("testFixturesCompileOnly", libs.findLibrary("jakarta-persistence-api").get())

    add("testFixturesImplementation", libs.findLibrary("assertj-core").get())
}
