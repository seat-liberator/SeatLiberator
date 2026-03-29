package com.seatliberator.seatliberator.reservation.book.application.service;

import com.seatliberator.seatliberator.reservation.book.application.exception.BookApplicationErrorCode;
import com.seatliberator.seatliberator.reservation.book.application.exception.BookApplicationException;
import com.seatliberator.seatliberator.reservation.book.application.port.in.ReservationReader;
import com.seatliberator.seatliberator.reservation.book.application.port.in.command.IdBasedReservationLocator;
import com.seatliberator.seatliberator.reservation.book.application.port.in.command.ReservationLocator;
import com.seatliberator.seatliberator.reservation.book.application.port.in.command.SeatBasedReservationLocator;
import com.seatliberator.seatliberator.reservation.book.application.port.in.entry.ReservationEntry;
import com.seatliberator.seatliberator.reservation.book.application.port.out.ReservationStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class DefaultReservationReader implements ReservationReader {
    private final ReservationStore reservationStore;

    @Override
    public ReservationEntry read(ReservationLocator reservationLocator) {
        var optReservation = switch (reservationLocator) {
            case IdBasedReservationLocator(Long reservationId) -> reservationStore.findById(reservationId);
            case SeatBasedReservationLocator(String roomId, String seatId, Instant startTime, Instant endTime) ->
                    reservationStore.findReservationBySeatAt(roomId, seatId, startTime, endTime);
        };

        return optReservation
                .map(ReservationEntry::of)
                .orElseThrow(() -> new BookApplicationException(BookApplicationErrorCode.RESERVATION_NOT_FOUND));
    }
}
