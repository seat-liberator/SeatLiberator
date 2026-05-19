package com.seatliberator.seatliberator.reservation.application.booking.port.out;

import com.seatliberator.seatliberator.reservation.application.booking.port.in.result.BookingDetailResult;

import java.util.Optional;
import java.util.UUID;

public interface BookingDetailReader {
    Optional<BookingDetailResult> findByReservationId(UUID reservationId);
}
