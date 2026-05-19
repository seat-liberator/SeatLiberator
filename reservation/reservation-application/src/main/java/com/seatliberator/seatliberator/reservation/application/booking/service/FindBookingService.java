package com.seatliberator.seatliberator.reservation.application.booking.service;

import com.seatliberator.seatliberator.reservation.application.booking.port.in.FindBookingUseCase;
import com.seatliberator.seatliberator.reservation.application.booking.port.in.query.FindBookingQuery;
import com.seatliberator.seatliberator.reservation.application.booking.port.in.result.BookingDetailResult;
import com.seatliberator.seatliberator.reservation.application.booking.port.out.BookingDetailReader;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationErrorCode;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FindBookingService implements FindBookingUseCase {
    private final BookingDetailReader bookingDetailReader;

    @Override
    public BookingDetailResult find(FindBookingQuery query) {
        return bookingDetailReader.findByReservationId(query.reservationId())
                .orElseThrow(() -> new ReservationApplicationException(ReservationApplicationErrorCode.RESERVATION_NOT_FOUND));
    }
}
