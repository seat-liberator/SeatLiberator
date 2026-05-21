plugins {
    id("seatliberator.base.module")
    id("java-library")
}

val libs = extensions
    .getByType<VersionCatalogsExtension>()
    .named("libs")

dependencies {
    add("compileOnlyApi", libs.findLibrary("jspecify").get())
}