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
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationErrorCode;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationException;
import com.seatliberator.seatliberator.reservation.domain.seat.SeatTimeSlot;
import com.seatliberator.seatliberator.reservation.domain.seat.SeatTimeSlotStatus;
import com.seatliberator.seatliberator.reservation.domain.shared.SimpleDailyTimeSegment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class SeatTimeSlotCommandService implements
        CreateSeatTimeSlotUseCase,
        UpdateSeatTimeSlotUseCase,
        DeleteSeatTimeSlotUseCase {

    private final SeatReader seatReader;
    private final SeatTimeSlotReader seatTimeSlotReader;
    private final SeatTimeSlotStore seatTimeSlotStore;
    private final Clock clock;

    @Override
    public SeatTimeSlotResult create(CreateSeatTimeSlotCommand command) {
        var seat = seatReader.findByLocator(command.locator())
                .orElseThrow(() -> new ReservationApplicationException(ReservationApplicationErrorCode.SEAT_NOT_FOUND));

        var slotRange = SimpleDailyTimeSegment.of(command.startAt(), command.duration());
        var seatTimeSlot = SeatTimeSlot.of(seat, slotRange, SeatTimeSlotStatus.ACTIVE, clock.instant());

        seatTimeSlotStore.save(seatTimeSlot);

        return SeatTimeSlotResult.from(seatTimeSlot);
    }

    @Override
    public SeatTimeSlotResult update(UpdateSeatTimeSlotCommand command) {
        var seatTimeSlot = findSeatTimeSlot(command.seatTimeSlotId());

        var slotRange = SimpleDailyTimeSegment.of(command.startAt(), command.duration());
        seatTimeSlot.updateSlotRange(slotRange);
        seatTimeSlotStore.save(seatTimeSlot);

        return SeatTimeSlotResult.from(seatTimeSlot);
    }

    @Override
    public void delete(DeleteSeatTimeSlotCommand command) {
        var seatTimeSlot = findSeatTimeSlot(command.seatTimeSlotId());
        seatTimeSlotStore.delete(seatTimeSlot);
    }

    private SeatTimeSlot findSeatTimeSlot(UUID seatTimeSlotId) {
        return seatTimeSlotReader.findById(seatTimeSlotId)
                .orElseThrow(() -> new ReservationApplicationException(ReservationApplicationErrorCode.SEAT_TIME_SLOT_NOT_FOUND));
    }
}
