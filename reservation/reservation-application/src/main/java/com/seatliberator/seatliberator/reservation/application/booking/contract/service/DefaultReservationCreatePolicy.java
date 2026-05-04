package com.seatliberator.seatliberator.reservation.application.booking.contract.service;

import com.seatliberator.seatliberator.reservation.application.booking.contract.ReservationCreatePolicy;
import com.seatliberator.seatliberator.reservation.application.booking.contract.command.ReservationCreatePolicyCommand;
import com.seatliberator.seatliberator.reservation.application.booking.contract.result.ReservationPolicyReason;
import com.seatliberator.seatliberator.reservation.application.room.contract.RoomOperationReservationPolicy;
import com.seatliberator.seatliberator.reservation.application.shared.configuration.ReservationCapability;
import com.seatliberator.seatliberator.reservation.application.shared.policy.SimplePolicyResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DefaultReservationCreatePolicy implements ReservationCreatePolicy {
    private final RoomOperationReservationPolicy roomOperationReservationPolicy;

    @Override
    public SimplePolicyResult evaluate(ReservationCreatePolicyCommand command) {
        var requester = command.request();
        if (!requester.capabilities().contains(ReservationCapability.BOOKING_CREATE)) {
            return SimplePolicyResult.reject(ReservationPolicyReason.UNAUTHORIZED_RESERVATION_CREATE);
        }

        var roomPolicyResult = roomOperationReservationPolicy.evaluate(command.locator(), command.range());
        if (roomPolicyResult.rejected()) return SimplePolicyResult.reject(roomPolicyResult.reason());

        return SimplePolicyResult.accept(ReservationPolicyReason.RESERVATION_CREATABLE);
    }
}
