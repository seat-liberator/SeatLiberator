package com.seatliberator.seatliberator.reservation.application.reservation.contract;

import com.seatliberator.seatliberator.identity.core.actor.Actor;
import com.seatliberator.seatliberator.kernel.condition.Preconditions;
import com.seatliberator.seatliberator.reservation.application.reservation.port.out.ReservationReader;
import com.seatliberator.seatliberator.reservation.application.shared.configuration.ReservationCapability;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationErrorCode;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationException;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationPolicyException;
import com.seatliberator.seatliberator.reservation.application.shared.policy.PolicyResult;
import com.seatliberator.seatliberator.reservation.application.shared.policy.SimplePolicyResult;
import com.seatliberator.seatliberator.reservation.domain.reservation.Reservation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ReservationOwnershipPolicy {
    private final ReservationReader reader;

    public void validate(UUID reservationId, Actor actor) {
        Preconditions.requireNonNull(reservationId, "reservationId");

        var reservation = reader.findById(reservationId)
                .orElseThrow(() -> new ReservationApplicationException(ReservationApplicationErrorCode.RESERVATION_NOT_FOUND));

        validate(reservation, actor);
    }

    public void validate(Reservation reservation, Actor actor) {
        var result = evaluate(reservation, actor);
        if (result.rejected())
            throw new ReservationApplicationPolicyException(result.reason());
    }

    public PolicyResult evaluate(Reservation reservation, Actor actor) {
        Preconditions.requireNonNull(reservation, "reservation");
        Preconditions.requireNonNull(actor, "actor");

        var capabilities = actor.capabilities();

        if (capabilities.contains(ReservationCapability.BOOKING_MANAGE)) {
            return SimplePolicyResult.accept(ReservationPolicyReason.RESERVATION_MANAGER);
        }

        if (reservation.getUserId().equals(actor.subject())) {
            return SimplePolicyResult.accept(ReservationPolicyReason.RESERVATION_OWNER);
        } else {
            return SimplePolicyResult.reject(ReservationPolicyReason.UNAUTHORIZED_RESERVATION_ACCESS);
        }
    }
}
