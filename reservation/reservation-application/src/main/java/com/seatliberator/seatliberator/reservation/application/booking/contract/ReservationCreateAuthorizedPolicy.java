package com.seatliberator.seatliberator.reservation.application.booking.contract;

import com.seatliberator.seatliberator.identity.core.actor.Actor;
import com.seatliberator.seatliberator.reservation.application.shared.policy.SimplePolicyResult;

public interface ReservationCreateAuthorizedPolicy {
    SimplePolicyResult evaluate(Actor requester);
}
