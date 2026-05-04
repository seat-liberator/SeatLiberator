package com.seatliberator.seatliberator.reservation.application.booking.contract.service;

import com.seatliberator.seatliberator.identity.core.actor.Actor;
import com.seatliberator.seatliberator.reservation.application.booking.contract.ReservationCreateAuthorizedPolicy;
import com.seatliberator.seatliberator.reservation.application.booking.contract.result.ReservationPolicyReason;
import com.seatliberator.seatliberator.reservation.application.shared.configuration.ReservationCapability;
import com.seatliberator.seatliberator.reservation.application.shared.policy.SimplePolicyResult;
import org.springframework.stereotype.Component;

@Component
public class DefaultReservationCreateAuthorizedPolicy implements ReservationCreateAuthorizedPolicy {
    @Override
    public SimplePolicyResult evaluate(Actor requester) {
        if (requester.capabilities().contains(ReservationCapability.BOOKING_MANAGE)) {
            return SimplePolicyResult.accept(ReservationPolicyReason.RESERVATION_MANAGER);
        }

        if (!requester.capabilities().contains(ReservationCapability.BOOKING_CREATE)) {
            return SimplePolicyResult.reject(ReservationPolicyReason.UNAUTHORIZED_RESERVATION_CREATE);
        }

        return SimplePolicyResult.accept(ReservationPolicyReason.AUTHORIZED_RESERVATION_CREATE);
    }
}
