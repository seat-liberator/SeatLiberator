package com.seatliberator.seatliberator.reservation.shared.application.seed;

import com.seatliberator.seatliberator.reservation.domain.persistence.Room;
import com.seatliberator.seatliberator.reservation.domain.persistence.Seat;
import com.seatliberator.seatliberator.reservation.room.application.port.out.RoomStore;
import com.seatliberator.seatliberator.reservation.room.application.port.out.SeatStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RoomSeeder {
    public static final String roomIdPrefix = "study-room";
    public static final Integer roomNumber = 5;
    public static final String seatIdPrefix = "seat";
    public static final Integer seatNumber = 10;

    private final RoomStore roomStore;
    private final SeatStore seatStore;
    private final Clock clock;

    public void seed() {
        var rooms = seedRoom();

        for (var room : rooms) {
            seedSeat(room);
        }
    }

    private List<Room> seedRoom() {
        var result = new ArrayList<Room>();
        for (int i = 0; i < roomNumber; i++) {
            var roomId = String.format("%s-%s", roomIdPrefix, i);
            var room = Room.of(roomId, clock.instant());
            roomStore.save(room);
            result.add(room);
        }
        return result;
    }

    private void seedSeat(Room room) {
        for (int i = 0; i < seatNumber; i++) {
            var seatId = String.format("%s-%s", seatIdPrefix, i);
            var seat = Seat.of(room, seatId, clock.instant());
            seatStore.save(seat);
        }
    }
}
