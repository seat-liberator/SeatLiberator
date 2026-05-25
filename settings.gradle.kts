pluginManagement {
    includeBuild("build-include")

    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

    repositories {
        mavenCentral()
    }
}

rootProject.name = "seatliberator"

include("kernel:kernel-core")
include("kernel:kernel-test")

include("bootstrap:application-starter")
include("bootstrap:security-starter")

include("gateway")

include("identity")
include("identity:identity-core")
include("identity:identity-application-starter")
include("identity:identity-security-starter")
include("identity:identity-server")
include("identity:identity-server:identity-server-api")
include("identity:identity-server:identity-server-application")
include("identity:identity-server:identity-server-domain")
include("identity:identity-server:identity-server-persistence")
include("identity:identity-server:identity-server-webmvc")
include("identity:identity-server:identity-server-security")

include("idempotency:idempotency-core")

include("event-relay:event-relay-core")
include("event-relay:event-relay-test")
include("event-relay:event-relay-support-jpa")
include("event-relay:event-relay-support-kafka")
include("event-relay:event-relay-starter")

include("reservation")
include("reservation:reservation-api")
include("reservation:reservation-application")
include("reservation:reservation-domain")
include("reservation:reservation-web-mvc")
include("reservation:reservation-persistence")

include("board")
include("board:board-api")
include("board:board-application")
include("board:board-domain")
include("board:board-web-mvc")
include("board:board-persistence")

include("notification")
include("notification:notification-api")
include("notification:notification-application")