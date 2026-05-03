package com.seatliberator.seatliberator.reservation.application.booking.contract.service;

import com.seatliberator.seatliberator.identity.core.actor.Actor;
import com.seatliberator.seatliberator.reservation.application.booking.contract.ReservationOwnershipPolicy;
import com.seatliberator.seatliberator.reservation.application.booking.contract.result.ReservationPolicyReason;
import com.seatliberator.seatliberator.reservation.application.booking.contract.result.ReservationPolicyResult;
import com.seatliberator.seatliberator.reservation.application.shared.configuration.ReservationCapability;
import com.seatliberator.seatliberator.reservation.domain.reservation.Reservation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DefaultReservationOwnershipPolicy implements ReservationOwnershipPolicy {
    @Override
    public ReservationPolicyResult evaluate(Reservation reservation, Actor requester) {
        if (requester.capabilities().contains(ReservationCapability.BOOKING_MANAGE)) {
            return ReservationPolicyResult.accept(ReservationPolicyReason.RESERVATION_MANAGER);
        }

        if (reservation.getUserId().equals(requester.subject())) {
            return ReservationPolicyResult.accept(ReservationPolicyReason.RESERVATION_OWNER);
        } else {
            return ReservationPolicyResult.reject(ReservationPolicyReason.UNAUTHORIZED_RESERVATION_ACCESS);
        }
    }
}
