package com.seatliberator.seatliberator.reservation.application.seat.service;

import com.seatliberator.seatliberator.reservation.application.seat.port.in.CreateSeatUseCase;
import com.seatliberator.seatliberator.reservation.application.seat.port.in.DeleteSeatUseCase;
import com.seatliberator.seatliberator.reservation.application.seat.port.in.UpdateSeatUseCase;
import com.seatliberator.seatliberator.reservation.application.seat.port.in.command.CreateSeatCommand;
import com.seatliberator.seatliberator.reservation.application.seat.port.in.command.DeleteSeatCommand;
import com.seatliberator.seatliberator.reservation.application.seat.port.in.command.UpdateSeatCodeCommand;
import com.seatliberator.seatliberator.reservation.application.seat.port.in.result.SeatResult;
import com.seatliberator.seatliberator.reservation.application.seat.port.out.SeatReader;
import com.seatliberator.seatliberator.reservation.application.seat.port.out.SeatStore;
import com.seatliberator.seatliberator.reservation.application.seat.port.out.criteria.SeatLookupCriteria;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationErrorCode;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationException;
import com.seatliberator.seatliberator.reservation.domain.seat.Seat;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;

@Service
@RequiredArgsConstructor
@Transactional
public class SeatCommandService implements
        CreateSeatUseCase,
        UpdateSeatUseCase,
        DeleteSeatUseCase {

    private final SeatStore store;
    private final SeatReader reader;

    private final Clock clock;

    @Override
    public SeatResult create(CreateSeatCommand command) {
        var roomId = command.roomId();
        var seatCode = command.seatCode();
        var criteria = SeatLookupCriteria.of(roomId, seatCode);
        var existsSeat = reader.existsByCriteria(criteria);
        if (existsSeat) throw new ReservationApplicationException(ReservationApplicationErrorCode.SEAT_ALREADY_EXISTS);

        var now = clock.instant();
        var seat = Seat.of(roomId, seatCode, now);

        var saved = store.save(seat);

        return SeatResult.from(saved);
    }

    @Override
    public SeatResult update(UpdateSeatCodeCommand command) {
        var seatId = command.seatId();
        var seat = reader.findById(seatId)
                .orElseThrow(() -> new ReservationApplicationException(ReservationApplicationErrorCode.SEAT_NOT_FOUND));

        var roomId = seat.getRoomId();
        var newCode = command.newCode();
        var criteria = SeatLookupCriteria.of(roomId, newCode);
        var existsSeat = reader.existsByCriteria(criteria);
        if (existsSeat) throw new ReservationApplicationException(ReservationApplicationErrorCode.SEAT_ALREADY_EXISTS);

        seat.updateCode(newCode);
        var saved = store.save(seat);

        return SeatResult.from(saved);
    }

    @Override
    public void delete(DeleteSeatCommand command) {
        var seatId = command.seatId();
        var seat = reader.findById(seatId)
                .orElseThrow(() -> new ReservationApplicationException(ReservationApplicationErrorCode.SEAT_NOT_FOUND));
        store.delete(seat);
    }
}
