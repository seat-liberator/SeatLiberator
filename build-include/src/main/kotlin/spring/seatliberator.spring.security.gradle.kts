plugins {
    id("seatliberator.spring.module")
}

val libs = extensions
    .getByType<VersionCatalogsExtension>()
    .named("libs")

dependencies {
    add("implementation", libs.findLibrary("spring-security-core").get())
    add("compileOnly", libs.findLibrary("jakarta-servlet-api").get())

    add("testImplementation", libs.findLibrary("spring-security-test").get())
    add("testImplementation", libs.findLibrary("jakarta-servlet-api").get())
}
