package com.seatliberator.seatliberator.reservation.application.service;

import com.seatliberator.seatliberator.reservation.application.out.ReservationStore;
import com.seatliberator.seatliberator.reservation.application.port.in.ReservationManager;
import com.seatliberator.seatliberator.reservation.application.port.in.command.ReservationCreateCommand;
import com.seatliberator.seatliberator.reservation.application.port.in.command.ReservationUpdateCommand;
import com.seatliberator.seatliberator.reservation.domain.Reservation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationService implements ReservationManager {

    private final ReservationStore reservationStore;

    @Override
    public boolean create(ReservationCreateCommand command) {

        if (reservationStore.findByUserId(command.userId()).isPresent()) {
            return false;
        }


        if (reservationStore.existsSeatConflict(
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

    @Override
    public boolean update(ReservationUpdateCommand command) {

        Reservation reservation = reservationStore.findByUserId(command.userId()).orElseThrow();

        if (reservationStore.existsSeatConflictExceptId(
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

        reservationStore.delete(reservation);

        return true;
    }
}
