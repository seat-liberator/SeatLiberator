package com.seatliberator.seatliberator.reservation.application.reservation.contract;

import com.seatliberator.seatliberator.identity.core.actor.Actor;
import com.seatliberator.seatliberator.reservation.application.shared.configuration.ReservationCapability;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationPolicyException;
import com.seatliberator.seatliberator.reservation.application.shared.policy.PolicyResult;
import com.seatliberator.seatliberator.reservation.application.shared.policy.SimplePolicyResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateAuthorizer {
    public PolicyResult evaluate(Actor actor) {
        var capabilities = actor.capabilities();

        if (capabilities.contains(ReservationCapability.BOOKING_MANAGE))
            return SimplePolicyResult.accept(ReservationPolicyReason.RESERVATION_MANAGER);

        if (capabilities.contains(ReservationCapability.BOOKING_CREATE))
            return SimplePolicyResult.reject(ReservationPolicyReason.UNAUTHORIZED_RESERVATION_CREATE);

        return SimplePolicyResult.accept(ReservationPolicyReason.AUTHORIZED_RESERVATION_CREATE);
    }

    public void ensureAuthorized(Actor actor) {
        var capabilities = actor.capabilities();

        if (capabilities.contains(ReservationCapability.BOOKING_MANAGE)) return;
        if (capabilities.contains(ReservationCapability.BOOKING_CREATE)) return;

        throw new ReservationApplicationPolicyException(ReservationPolicyReason.UNAUTHORIZED_RESERVATION_CREATE);
    }
}
