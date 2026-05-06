package com.seatliberator.seatliberator.reservation.application.seat.service;

import com.seatliberator.seatliberator.reservation.application.room.port.out.RoomReader;
import com.seatliberator.seatliberator.reservation.application.seat.port.in.FindSeatUseCase;
import com.seatliberator.seatliberator.reservation.application.seat.port.in.ListSeatUseCase;
import com.seatliberator.seatliberator.reservation.application.seat.port.in.query.FindSeatQuery;
import com.seatliberator.seatliberator.reservation.application.seat.port.in.query.ListSeatQuery;
import com.seatliberator.seatliberator.reservation.application.seat.port.in.result.SeatResult;
import com.seatliberator.seatliberator.reservation.application.seat.port.out.SeatReader;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationErrorCode;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationException;
import com.seatliberator.seatliberator.reservation.domain.shared.SimpleSeatLocator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SeatQueryService implements
        ListSeatUseCase,
        FindSeatUseCase {
    private final RoomReader roomReader;
    private final SeatReader seatReader;

    @Override
    public List<SeatResult> list(ListSeatQuery query) {
        ensureRoomExists(query.roomId());
        return seatReader.findByRoomId(query.roomId()).stream()
                .map(SeatResult::from)
                .toList();
    }

    @Override
    public SeatResult find(FindSeatQuery query) {
        ensureRoomExists(query.roomId());
        var locator = SimpleSeatLocator.of(query.roomId(), query.seatId());
        return seatReader.findByLocator(locator)
                .map(SeatResult::from)
                .orElseThrow(() -> new ReservationApplicationException(ReservationApplicationErrorCode.SEAT_NOT_FOUND));
    }

    private void ensureRoomExists(String roomId) {
        var exists = roomReader.existsByRoomId(roomId);
        if (!exists) throw new ReservationApplicationException(ReservationApplicationErrorCode.ROOM_NOT_FOUND);
    }
}
