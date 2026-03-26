rootProject.name = "seatliberator"

include("board")
include("reservation")
include("identity:identity-core")
include("identity:identity-application")
include("identity:identity-client")

include("kernel")
include("event-relay:event-relay-core")
include("event-relay:event-relay-support-jpa")
include("event-relay:event-relay-support-kafka")