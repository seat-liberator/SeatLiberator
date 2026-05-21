plugins {
    id("seatliberator.spring.module")
}

val libs = extensions
    .getByType<VersionCatalogsExtension>()
    .named("libs")

dependencies {
    add("implementation", libs.findLibrary("spring-boot-starter-webmvc").get())
    add("implementation", libs.findLibrary("spring-boot-starter-validation").get())
    add("implementation", libs.findLibrary("springdoc-openapi-starter-webmvc-ui").get())
    add("compileOnly", libs.findLibrary("spring-security-core").get())

    add("testImplementation", libs.findLibrary("spring-boot-starter-webmvc-test").get())
}
