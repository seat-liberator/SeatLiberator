package com.seatliberator.seatliberator.reservation.application.room.internal;

import com.seatliberator.seatliberator.reservation.application.room.port.out.RoomReader;
import com.seatliberator.seatliberator.reservation.application.room.port.out.SeatReader;
import com.seatliberator.seatliberator.reservation.application.room.port.out.SeatStore;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationErrorCode;
import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationException;
import com.seatliberator.seatliberator.reservation.domain.SeatLocator;
import com.seatliberator.seatliberator.reservation.domain.SimpleSeatLocator;
import com.seatliberator.seatliberator.reservation.domain.persistence.Room;
import com.seatliberator.seatliberator.reservation.domain.persistence.Seat;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;

@Service
@RequiredArgsConstructor
@Transactional
public class SeatAssignmentService {
    private final RoomReader roomReader;
    private final SeatReader seatReader;
    private final SeatStore seatStore;

    private final Clock clock;

    public Seat createSeat(String roomId, String seatId) {
        var room = tryFindByRoomId(roomId);
        var locator = SimpleSeatLocator.of(roomId, seatId);
        ensureSeatNotExists(locator);

        var seat = Seat.of(room, seatId, clock.instant());

        seatStore.save(seat);

        return seat;
    }

    public Seat moveSeat(String roomId, String newRoomId, String seatId) {
        var currentLocator = SimpleSeatLocator.of(roomId, seatId);
        var newLocator = SimpleSeatLocator.of(newRoomId, seatId);
        var seat = tryFindByLocator(currentLocator);

        if (currentLocator.isSame(newLocator)) return seat;

        ensureSeatNotExists(newLocator);

        var newRoom = tryFindByRoomId(newRoomId);
        seat.updateRoom(newRoom);
        seatStore.save(seat);

        return seat;
    }

    public Seat changeSeatId(String roomId, String seatId, String newSeatId) {
        var currentLocator = SimpleSeatLocator.of(roomId, seatId);
        var newLocator = SimpleSeatLocator.of(roomId, newSeatId);
        var seat = tryFindByLocator(currentLocator);

        if (currentLocator.isSame(newLocator)) return seat;

        ensureSeatNotExists(newLocator);

        seat.updateSeatId(newSeatId);
        seatStore.save(seat);

        return seat;
    }

    private Room tryFindByRoomId(String roomId) {
        return roomReader.findByRoomId(roomId).orElseThrow(() -> new ReservationApplicationException(ReservationApplicationErrorCode.ROOM_NOT_FOUND));
    }

    private Seat tryFindByLocator(SeatLocator locator) {
        return seatReader.findByLocator(locator).orElseThrow(() -> new ReservationApplicationException(ReservationApplicationErrorCode.SEAT_NOT_FOUND));
    }

    private void ensureSeatNotExists(SeatLocator locator) {
        var exists = seatReader.existsByLocator(locator);
        if (exists) throw new ReservationApplicationException(ReservationApplicationErrorCode.SEAT_ALREADY_EXISTS);
    }
}