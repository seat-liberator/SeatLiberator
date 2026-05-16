package com.seatliberator.seatliberator.reservation.application.reservation.contract;

import com.seatliberator.seatliberator.identity.core.actor.Actor;
import com.seatliberator.seatliberator.reservation.application.shared.configuration.ReservationCapability;
import com.seatliberator.seatliberator.reservation.application.shared.policy.PolicyResult;
import com.seatliberator.seatliberator.reservation.application.shared.policy.SimplePolicyResult;
import com.seatliberator.seatliberator.reservation.domain.reservation.Reservation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReservationOwnershipPolicy {
    public PolicyResult evaluate(Reservation reservation, Actor requester) {
        if (requester.capabilities().contains(ReservationCapability.BOOKING_MANAGE)) {
            return SimplePolicyResult.accept(ReservationPolicyReason.RESERVATION_MANAGER);
        }

        if (reservation.getUserId().equals(requester.subject())) {
            return SimplePolicyResult.accept(ReservationPolicyReason.RESERVATION_OWNER);
        } else {
            return SimplePolicyResult.reject(ReservationPolicyReason.UNAUTHORIZED_RESERVATION_ACCESS);
        }
    }
}
