rootProject.name = "seatliberator"

include("board")
include("board:board-authorization-registration")

include("reservation")
include("reservation:reservation-authorization-registration")

include("identity:identity-api")
include("identity:identity-core")
include("identity:identity-application")
include("identity:identity-client")

include("kernel")
include("idempotency:idempotency-core")
include("event-relay:event-relay-core")
include("event-relay:event-relay-test")
include("event-relay:event-relay-support-jpa")
include("event-relay:event-relay-support-kafka")
