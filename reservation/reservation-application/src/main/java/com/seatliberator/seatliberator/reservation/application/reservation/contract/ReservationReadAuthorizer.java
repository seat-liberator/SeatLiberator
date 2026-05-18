package com.seatliberator.seatliberator.reservation.application.reservation.contract;

import com.seatliberator.seatliberator.identity.core.actor.Actor;
import com.seatliberator.seatliberator.kernel.condition.Preconditions;
import com.seatliberator.seatliberator.reservation.application.shared.configuration.ReservationCapability;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationPolicyException;
import com.seatliberator.seatliberator.reservation.application.shared.policy.PolicyResult;
import com.seatliberator.seatliberator.reservation.application.shared.policy.SimplePolicyResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReservationReadAuthorizer {
    public void validate(Actor actor) {
        var result = evaluate(actor);
        if (result.rejected())
            throw new ReservationApplicationPolicyException(result.reason());
    }

    public PolicyResult evaluate(Actor actor) {
        Preconditions.requireNonNull(actor, "actor");

        var capabilities = actor.capabilities();

        if (capabilities.contains(ReservationCapability.BOOKING_MANAGE))
            return SimplePolicyResult.accept(ReservationPolicyReason.RESERVATION_MANAGER);

        if (capabilities.contains(ReservationCapability.BOOKING_READ))
            return SimplePolicyResult.accept(ReservationPolicyReason.AUTHORIZED_RESERVATION_READ);

        return SimplePolicyResult.reject(ReservationPolicyReason.UNAUTHORIZED_RESERVATION_ACCESS);
    }
}
