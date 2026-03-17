package com.seatliberator.seatliberator.reservation.application.service;

import com.seatliberator.seatliberator.reservation.application.exception.ApplicationErrorCode;
import com.seatliberator.seatliberator.reservation.application.exception.ApplicationException;
import com.seatliberator.seatliberator.reservation.application.port.in.ReservationReader;
import com.seatliberator.seatliberator.reservation.application.port.in.command.IdBasedReservationLocator;
import com.seatliberator.seatliberator.reservation.application.port.in.command.ReservationLocator;
import com.seatliberator.seatliberator.reservation.application.port.in.command.SeatBasedReservationLocator;
import com.seatliberator.seatliberator.reservation.application.port.in.entry.ReservationEntry;
import com.seatliberator.seatliberator.reservation.application.port.out.ReservationStore;
import com.seatliberator.seatliberator.reservation.domain.Reservation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DefaultReservationReader implements ReservationReader {
    private final ReservationStore reservationStore;

    @Override
    public ReservationEntry read(ReservationLocator reservationLocator) {
        Optional<Reservation> optReservation = Optional.empty();

        if (reservationLocator instanceof IdBasedReservationLocator(Long reservationId)) {
            optReservation = reservationStore.findById(reservationId);
        }

        if (reservationLocator instanceof SeatBasedReservationLocator(String roomId, String seatId)) {
            optReservation = reservationStore.findByRoomIdAndSeatId(roomId, seatId);
        }

        return optReservation
                .map(ReservationEntry::of)
                .orElseThrow(() -> new ApplicationException(ApplicationErrorCode.RESERVATION_NOT_FOUND));
    }
}
