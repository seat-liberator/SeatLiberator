package com.seatliberator.seatliberator.reservation.application.seat.service;

import com.seatliberator.seatliberator.reservation.application.seat.port.in.FindSeatTimeSlotUseCase;
import com.seatliberator.seatliberator.reservation.application.seat.port.in.ListSeatTimeSlotUseCase;
import com.seatliberator.seatliberator.reservation.application.seat.port.in.query.FindSeatTimeSlotQuery;
import com.seatliberator.seatliberator.reservation.application.seat.port.in.query.ListSeatTimeSlotQuery;
import com.seatliberator.seatliberator.reservation.application.seat.port.in.result.SeatTimeSlotResult;
import com.seatliberator.seatliberator.reservation.application.seat.port.out.SeatReader;
import com.seatliberator.seatliberator.reservation.application.seat.port.out.SeatTimeSlotReader;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationErrorCode;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SeatTimeSlotQueryService implements
        FindSeatTimeSlotUseCase,
        ListSeatTimeSlotUseCase {

    private final SeatReader seatReader;
    private final SeatTimeSlotReader seatTimeSlotReader;

    @Override
    public SeatTimeSlotResult find(FindSeatTimeSlotQuery query) {
        return seatTimeSlotReader.findById(query.seatTimeSlotId())
                .map(SeatTimeSlotResult::from)
                .orElseThrow(() -> new ReservationApplicationException(ReservationApplicationErrorCode.SEAT_TIME_SLOT_NOT_FOUND));
    }

    @Override
    public List<SeatTimeSlotResult> list(ListSeatTimeSlotQuery query) {
        ensureSeatExists(query.seatId());
        return seatTimeSlotReader.findBySeatId(query.seatId()).stream()
                .map(SeatTimeSlotResult::from)
                .toList();
    }

    private void ensureSeatExists(UUID seatId) {
        var exists = seatReader.findById(seatId).isPresent();
        if (!exists) throw new ReservationApplicationException(ReservationApplicationErrorCode.SEAT_NOT_FOUND);
    }
}
