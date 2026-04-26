plugins {
    id("seatliberator.module-base")
    id("seatliberator.spring-module-base")
}

val libs = extensions
    .getByType<VersionCatalogsExtension>()
    .named("libs")
