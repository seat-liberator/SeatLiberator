package com.seatliberator.seatliberator.reservation.seat.application.service;

import com.seatliberator.seatliberator.reservation.domain.SimpleSeatLocator;
import com.seatliberator.seatliberator.reservation.domain.persistence.Seat;
import com.seatliberator.seatliberator.reservation.seat.application.port.in.CreateSeatUseCase;
import com.seatliberator.seatliberator.reservation.seat.application.port.in.DeleteSeatUseCase;
import com.seatliberator.seatliberator.reservation.seat.application.port.in.UpdateSeatUseCase;
import com.seatliberator.seatliberator.reservation.seat.application.port.in.command.CreateSeatCommand;
import com.seatliberator.seatliberator.reservation.seat.application.port.in.command.DeleteSeatCommand;
import com.seatliberator.seatliberator.reservation.seat.application.port.in.command.UpdateSeatCommand;
import com.seatliberator.seatliberator.reservation.seat.application.port.out.SeatReader;
import com.seatliberator.seatliberator.reservation.seat.application.port.out.SeatStore;
import com.seatliberator.seatliberator.reservation.seat.application.port.out.criteria.SeatExclusion;
import com.seatliberator.seatliberator.reservation.shared.application.exception.ReservationApplicationErrorCode;
import com.seatliberator.seatliberator.reservation.shared.application.exception.ReservationApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SeatCommandService implements
        CreateSeatUseCase,
        UpdateSeatUseCase,
        DeleteSeatUseCase {

    private final SeatStore store;
    private final SeatReader query;

    private final Clock clock;

    @Override
    public boolean create(CreateSeatCommand command) {
        var locator = SimpleSeatLocator.of(command.roomId(), command.seatId());

        var conflict = query.existsByLocator(locator);
        if (conflict) return false;

        Seat seat = Seat.create(
                command.roomId(),
                command.seatId(),
                clock.instant()
        );

        store.save(seat);

        return true;
    }

    @Override
    public boolean update(UpdateSeatCommand command) {
        var oldLocator = SimpleSeatLocator.of(command.oldRoomId(), command.oldSeatId());
        var newLocator = SimpleSeatLocator.of(command.newRoomId(), command.newSeatId());
        var oldSeat = query.findByLocator(oldLocator)
                .orElseThrow(() -> new ReservationApplicationException(ReservationApplicationErrorCode.SEAT_NOT_FOUND));

        var exclude = SeatExclusion.of(List.of(oldSeat.getId()));
        var conflict = query.existsByLocator(newLocator, exclude);
        if (conflict) return false;

        oldSeat.update(command.newRoomId(), command.newSeatId());

        return true;
    }

    @Override
    public boolean delete(DeleteSeatCommand command) {
        var locator = SimpleSeatLocator.of(command.roomId(), command.seatId());
        store.deleteByLocator(locator);

        return true;
    }
}
