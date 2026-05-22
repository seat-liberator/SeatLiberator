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

include("bootstrap:web-application-starter")
include("bootstrap:resource-application-starter")
include("bootstrap:application-starter")

include("identity")
include("identity:identity-core")
include("identity:identity-client-starter")
include("identity:identity-api")
include("identity:identity-application")

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

include("gateway")