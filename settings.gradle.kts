rootProject.name = "seatliberator"

include("board:board-api")
include("board")

include("reservation:reservation-api")
include("reservation:reservation-application")

include("identity:identity-core")
include("identity:identity-client")
include("identity:identity-api")
include("identity:identity-application")

include("kernel")

include("idempotency:idempotency-core")

include("event-relay:event-relay-core")
include("event-relay:event-relay-test")
include("event-relay:event-relay-support-jpa")
include("event-relay:event-relay-support-kafka")
