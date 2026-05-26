package com.seatliberator.seatliberator.reservation.application.seat.service;

import com.seatliberator.seatliberator.reservation.application.seat.port.in.CreateSeatTimeSlotUseCase;
import com.seatliberator.seatliberator.reservation.application.seat.port.in.DeleteSeatTimeSlotUseCase;
import com.seatliberator.seatliberator.reservation.application.seat.port.in.UpdateSeatTimeSlotUseCase;
import com.seatliberator.seatliberator.reservation.application.seat.port.in.command.CreateSeatTimeSlotCommand;
import com.seatliberator.seatliberator.reservation.application.seat.port.in.command.DeleteSeatTimeSlotCommand;
import com.seatliberator.seatliberator.reservation.application.seat.port.in.command.UpdateSeatTimeSlotCommand;
import com.seatliberator.seatliberator.reservation.application.seat.port.in.result.SeatTimeSlotResult;
import com.seatliberator.seatliberator.reservation.application.seat.port.out.SeatReader;
import com.seatliberator.seatliberator.reservation.application.seat.port.out.SeatTimeSlotReader;
import com.seatliberator.seatliberator.reservation.application.seat.port.out.SeatTimeSlotStore;
import com.seatliberator.seatliberator.reservation.application.seat.port.out.filter.SeatTimeSlotFilter;
import com.seatliberator.seatliberator.reservation.application.seat.port.out.filter.SeatTimeSlotRangeOverlapCriteria;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationErrorCode;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationException;
import com.seatliberator.seatliberator.reservation.domain.seat.SeatTimeSlot;
import com.seatliberator.seatliberator.reservation.domain.seat.SeatTimeSlotStatus;
import com.seatliberator.seatliberator.reservation.domain.shared.temporal.SimpleDailyNanoRange;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;

@Service
@RequiredArgsConstructor
@Transactional
public class SeatTimeSlotCommandService implements
        CreateSeatTimeSlotUseCase,
        UpdateSeatTimeSlotUseCase,
        DeleteSeatTimeSlotUseCase {

    private final SeatTimeSlotReader reader;
    private final SeatTimeSlotStore store;

    private final SeatReader seatReader;
    private final Clock clock;

    @Override
    public SeatTimeSlotResult create(CreateSeatTimeSlotCommand command) {
        var seatId = command.seatId();
        var existsSeat = seatReader.existsById(seatId);
        if (!existsSeat) throw new ReservationApplicationException(ReservationApplicationErrorCode.SEAT_NOT_FOUND);

        var slotRange = SimpleDailyNanoRange.of(command.startAt(), command.duration());
        var seatTimeSlot = SeatTimeSlot.of(seatId, slotRange, SeatTimeSlotStatus.ACTIVE, clock.instant());

        var filter = SeatTimeSlotFilter.empty().seatId(seatId);
        var criteria = SeatTimeSlotRangeOverlapCriteria.of(slotRange, filter);

        var existsOverlaps = reader.existsByCriteria(criteria);
        if (existsOverlaps)
            throw new ReservationApplicationException(ReservationApplicationErrorCode.SEAT_TIME_SLOT_RANGE_CONFLICT);

        store.save(seatTimeSlot);

        return SeatTimeSlotResult.from(seatTimeSlot);
    }

    @Override
    public SeatTimeSlotResult update(UpdateSeatTimeSlotCommand command) {
        var slotId = command.seatTimeSlotId();
        var slot = reader.findById(slotId)
                .orElseThrow(() -> new ReservationApplicationException(ReservationApplicationErrorCode.SEAT_TIME_SLOT_NOT_FOUND));

        var slotRange = SimpleDailyNanoRange.of(command.startAt(), command.duration());

        var seatId = slot.getSeatId();
        var filter = SeatTimeSlotFilter.empty().seatId(seatId);
        var criteria = SeatTimeSlotRangeOverlapCriteria.of(slotRange, filter);

        var existsOverlaps = reader.existsByCriteria(criteria);
        if (existsOverlaps)
            throw new ReservationApplicationException(ReservationApplicationErrorCode.SEAT_TIME_SLOT_RANGE_CONFLICT);

        slot.updateSlotRange(slotRange);
        var saved = store.save(slot);

        return SeatTimeSlotResult.from(saved);
    }

    @Override
    public void delete(DeleteSeatTimeSlotCommand command) {
        var slotId = command.seatTimeSlotId();
        var slot = reader.findById(slotId)
                .orElseThrow(() -> new ReservationApplicationException(ReservationApplicationErrorCode.SEAT_TIME_SLOT_NOT_FOUND));
        store.delete(slot);
    }
}
