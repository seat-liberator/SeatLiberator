package com.seatliberator.seatliberator.reservation.bootstrap.seed;

import com.seatliberator.seatliberator.reservation.book.domain.Seat;

import java.util.ArrayList;
import java.util.List;

public class DevFixture {
    public static final String SEAT_A_1 = "A-1";
    public static final String SEAT_A_2 = "A-2";
    public static final String SEAT_B_1 = "B-1";
    private static final String ROOM_A = "room-a";
    private static final String ROOM_B = "room-b";
    private static final List<String> rooms = List.of(ROOM_A, ROOM_B);
    private static final List<String> seats = List.of(SEAT_A_1, SEAT_A_2, SEAT_B_1);

    public static List<Seat> createSeats() {
        List<Seat> result = new ArrayList<>();
        for (var room : rooms) {
            for (var seat : seats) {
                result.add(Seat.create(room, seat));
            }
        }
        return result;
    }
}
