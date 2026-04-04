package com.seatliberator.seatliberator.reservation.book.application.service;

import com.seatliberator.seatliberator.reservation.book.application.port.in.SeatManager;
import com.seatliberator.seatliberator.reservation.book.application.port.in.command.SeatCreateCommand;
import com.seatliberator.seatliberator.reservation.book.application.port.in.command.SeatUpdateCommand;
import com.seatliberator.seatliberator.reservation.book.application.port.out.SeatStore;
import com.seatliberator.seatliberator.reservation.domain.persistence.Seat;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class SeatService implements SeatManager {

    private final SeatStore seatStore;

    @Override
    public boolean create(SeatCreateCommand command) {

        if (seatStore.existsSeatConflict(
                command.roomId(),
                command.seatId()
        )) {
            return false;
        }

        Seat seat = Seat.create(
                command.roomId(),
                command.seatId()
        );

        seatStore.save(seat);

        return true;
    }

    @Override
    public boolean update(SeatUpdateCommand command) {
        Seat seat = seatStore.findByRoomIdAndSeatId(command.oldRoomId(), command.oldSeatId()).orElseThrow();

        if (seatStore.existsSeatConflictExcept(seat.getId(), command.newRoomId(), command.newSeatId())) {
            return false;
        }

        seat.update(command.newRoomId(), command.newSeatId());

        return true;
    }

    @Override
    public boolean delete(String roomId, String seatId) {

        seatStore.deleteByRoomIdAndSeatId(roomId, seatId);

        return true;
    }
}
