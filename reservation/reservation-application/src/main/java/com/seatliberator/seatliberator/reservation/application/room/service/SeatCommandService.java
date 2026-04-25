package com.seatliberator.seatliberator.reservation.application.room.service;

import com.seatliberator.seatliberator.reservation.application.room.internal.SeatAssignmentService;
import com.seatliberator.seatliberator.reservation.application.room.port.in.CreateSeatUseCase;
import com.seatliberator.seatliberator.reservation.application.room.port.in.DeleteSeatUseCase;
import com.seatliberator.seatliberator.reservation.application.room.port.in.MoveSeatUseCase;
import com.seatliberator.seatliberator.reservation.application.room.port.in.UpdateSeatUseCase;
import com.seatliberator.seatliberator.reservation.application.room.port.in.command.CreateSeatCommand;
import com.seatliberator.seatliberator.reservation.application.room.port.in.command.DeleteSeatCommand;
import com.seatliberator.seatliberator.reservation.application.room.port.in.command.MoveSeatCommand;
import com.seatliberator.seatliberator.reservation.application.room.port.in.command.UpdateSeatCommand;
import com.seatliberator.seatliberator.reservation.application.room.port.in.result.SeatResult;
import com.seatliberator.seatliberator.reservation.application.room.port.out.SeatReader;
import com.seatliberator.seatliberator.reservation.application.room.port.out.SeatStore;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationErrorCode;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationException;
import com.seatliberator.seatliberator.reservation.domain.SimpleSeatLocator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class SeatCommandService implements
        CreateSeatUseCase,
        UpdateSeatUseCase,
        MoveSeatUseCase,
        DeleteSeatUseCase {

    private final SeatStore store;
    private final SeatReader reader;
    private final SeatAssignmentService seatAssignmentService;

    @Override
    public SeatResult create(CreateSeatCommand command) {
        var seat = seatAssignmentService.createSeat(command.roomId(), command.seatId());
        return SeatResult.from(seat);
    }

    @Override
    public SeatResult update(UpdateSeatCommand command) {
        var seat = seatAssignmentService.changeSeatId(command.roomId(), command.oldSeatId(), command.newSeatId());
        return SeatResult.from(seat);
    }

    @Override
    public SeatResult move(MoveSeatCommand command) {
        var seat = seatAssignmentService.moveSeat(command.oldRoomId(), command.newRoomId(), command.seatId());
        return SeatResult.from(seat);
    }

    @Override
    public void delete(DeleteSeatCommand command) {
        var locator = SimpleSeatLocator.of(command.roomId(), command.seatId());
        var exists = reader.existsByLocator(locator);
        if (!exists) throw new ReservationApplicationException(ReservationApplicationErrorCode.SEAT_NOT_FOUND);
        store.deleteByLocator(locator);
    }
}
