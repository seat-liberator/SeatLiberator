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
        var seat = seatReader.findByLocator(query.locator())
                .orElseThrow(() -> new ReservationApplicationException(ReservationApplicationErrorCode.SEAT_NOT_FOUND));

        return seatTimeSlotReader.findBySeatId(seat.getId()).stream()
                .map(SeatTimeSlotResult::from)
                .toList();
    }
}
