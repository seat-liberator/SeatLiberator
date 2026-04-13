rootProject.name = "seatliberator"

pluginManagement {
    includeBuild("build-include")
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

include("kernel")

include("bootstrap:web-application-starter")
include("bootstrap:resource-application-starter")

include("identity:identity-core")
include("identity:identity-client")
include("identity:identity-api")
include("identity:identity-application")

include("idempotency:idempotency-core")

include("event-relay:event-relay-core")
include("event-relay:event-relay-test")
include("event-relay:event-relay-support-jpa")
include("event-relay:event-relay-support-kafka")
include("event-relay:event-relay-starter")

include("reservation:reservation-api")
include("reservation:reservation-application")
include("reservation:reservation-domain")

include("board:board-api")
include("board:board-application")

include("notification:notification-api")
include("notification:notification-application")

include("gateway")
