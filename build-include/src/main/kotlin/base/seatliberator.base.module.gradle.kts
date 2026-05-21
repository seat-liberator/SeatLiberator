plugins {
    id("java")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

tasks.register<Test>("unitTest") {
    group = "verification"
    description = "Runs tests tagged without @Tag(\"integration\")"

    testClassesDirs = sourceSets["test"].output.classesDirs
    classpath = sourceSets["test"].runtimeClasspath

    useJUnitPlatform {
        excludeTags("integration")
    }
}

val libs = extensions
    .getByType<VersionCatalogsExtension>()
    .named("libs")

dependencies {
    // Lombok
    add("compileOnly", libs.findLibrary("lombok").get())
    add("annotationProcessor", libs.findLibrary("lombok").get())

    // Testing
    add("testImplementation", platform(libs.findLibrary("junit-bom").get()))
    add("testImplementation", libs.findLibrary("junit-jupiter").get())
    add("testImplementation", libs.findLibrary("assertj-core").get())
    add("testRuntimeOnly", libs.findLibrary("junit-platform-launcher").get())
}
