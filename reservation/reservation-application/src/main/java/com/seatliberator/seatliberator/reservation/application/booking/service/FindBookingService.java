package com.seatliberator.seatliberator.reservation.application.booking.service;

import com.seatliberator.seatliberator.identity.core.actor.ActorContextHolder;
import com.seatliberator.seatliberator.reservation.application.booking.port.in.FindBookingUseCase;
import com.seatliberator.seatliberator.reservation.application.booking.port.in.query.FindBookingQuery;
import com.seatliberator.seatliberator.reservation.application.booking.port.in.result.BookingDetailResult;
import com.seatliberator.seatliberator.reservation.application.booking.port.out.BookingDetailReader;
import com.seatliberator.seatliberator.reservation.application.reservation.contract.ReservationOwnershipPolicy;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationErrorCode;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FindBookingService implements FindBookingUseCase {
    private final BookingDetailReader bookingDetailReader;

    private final ReservationOwnershipPolicy ownershipPolicy;
    private final ActorContextHolder actorContextHolder;

    @Override
    public BookingDetailResult find(FindBookingQuery query) {
        var actor = actorContextHolder.getActor();
        var reservationId = query.reservationId();

        ownershipPolicy.validate(reservationId, actor);

        return bookingDetailReader.findByReservationId(query.reservationId())
                .orElseThrow(() -> new ReservationApplicationException(ReservationApplicationErrorCode.RESERVATION_NOT_FOUND));
    }
}
