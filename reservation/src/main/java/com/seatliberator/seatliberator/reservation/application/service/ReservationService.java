package com.seatliberator.seatliberator.reservation.application.service;

import com.seatliberator.seatliberator.reservation.application.port.in.ReservationManager;
import com.seatliberator.seatliberator.reservation.application.port.in.command.ReservationCreateCommand;
import com.seatliberator.seatliberator.reservation.application.port.in.command.ReservationUpdateCommand;
import com.seatliberator.seatliberator.reservation.application.port.out.ReservationStore;
import com.seatliberator.seatliberator.reservation.application.port.out.SeatStore;
import com.seatliberator.seatliberator.reservation.domain.Reservation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationService implements ReservationManager {

    private final ReservationStore reservationStore;
    private final SeatStore seatStore;

    @Override
    public boolean create(ReservationCreateCommand command) {

        seatStore.findForUpdate(
                command.roomId(),
                command.seatId()
        ).orElseThrow();

        if (reservationStore.findByUserId(command.userId()).isPresent()) {
            return false;
        }


        if (reservationStore.existsReservationConflict(
                command.roomId(),
                command.seatId(),
                command.startTime(),
                command.endTime()
        )) {
            return false;
        }

        Reservation reservation = Reservation.create(
                command.userId(),
                command.roomId(),
                command.seatId(),
                command.startTime(),
                command.endTime()
        );

        reservationStore.save(reservation);

        return true;
    }

    @Transactional
    @Override
    public boolean update(ReservationUpdateCommand command) {

        Reservation reservation = reservationStore.findByUserId(command.userId()).orElseThrow();

        lockSeats(
                reservation.getRoomId(),
                reservation.getSeatId(),
                command.roomId(),
                command.seatId()
        );

        if (reservationStore.existsReservationConflictExceptId(
                reservation.getId(),
                command.roomId(),
                command.seatId(),
                command.startTime(),
                command.endTime()
        )) {
            return false;
        }

        reservation.update(command.userId(), command.roomId(), command.seatId(), command.startTime(), command.endTime());

        return true;
    }

    @Override
    public boolean cancel(String userId) {

        Reservation reservation = reservationStore.findByUserId(userId).orElseThrow();

        seatStore.findForUpdate(
                reservation.getRoomId(),
                reservation.getSeatId()
        ).ifPresent(seat -> {});

        reservationStore.delete(reservation);

        return true;
    }

    private void lockSeats(String roomId1, String seatId1, String roomId2, String seatId2) {

        if (roomId1.equals(roomId2) && seatId1.equals(seatId2)) {
            seatStore.findForUpdate(roomId1, seatId1).orElseThrow();
        }

        int roomCompare = roomId1.compareTo(roomId2);

        if (roomCompare < 0 || (roomCompare == 0 && seatId1.compareTo(seatId2) < 0)) {
            seatStore.findForUpdate(roomId1, seatId1).orElseThrow();
            seatStore.findForUpdate(roomId2, seatId2).orElseThrow();
        } else {
            seatStore.findForUpdate(roomId2, seatId2).orElseThrow();
            seatStore.findForUpdate(roomId1, seatId1).orElseThrow();
        }
    }
}
