package com.seatliberator.seatliberator.reservation.application.seat;

import com.seatliberator.seatliberator.kernel.test.clock.TestClock;
import com.seatliberator.seatliberator.reservation.application.seat.port.in.command.CreateSeatCommand;
import com.seatliberator.seatliberator.reservation.application.seat.port.in.command.DeleteSeatCommand;
import com.seatliberator.seatliberator.reservation.application.seat.port.in.command.UpdateSeatCodeCommand;
import com.seatliberator.seatliberator.reservation.application.seat.port.in.query.FindSeatQuery;
import com.seatliberator.seatliberator.reservation.application.seat.port.in.query.ListSeatQuery;
import com.seatliberator.seatliberator.reservation.domain.seat.SeatFixture;
import com.seatliberator.seatliberator.reservation.domain.seat.Seat;

import java.time.Clock;
import java.lang.reflect.Field;
import java.util.UUID;

public class SeatTestSupport {
    private final static Clock CLOCK = TestClock.getFixed();

    private final static UUID SEAT_ID = SeatFixture.nextId();
    private final static UUID ROOM_ID = UUID.fromString("00000000-0000-0000-0000-000000000101");
    private final static String SEAT_CODE = "seat-a";
    private final static String NEW_SEAT_CODE = "seat-b";

    public static CreateSeatCommand createSeatCommand() {
        return CreateSeatCommand.of(ROOM_ID, SEAT_CODE);
    }

    public static UpdateSeatCodeCommand updateSeatCodeCommand() {
        return UpdateSeatCodeCommand.of(SEAT_ID, NEW_SEAT_CODE);
    }

    public static DeleteSeatCommand deleteSeatCommand() {
        return DeleteSeatCommand.of(SEAT_ID);
    }

    public static FindSeatQuery findSeatQuery() {
        return FindSeatQuery.of(SEAT_ID);
    }

    public static ListSeatQuery listSeatQuery() {
        return ListSeatQuery.of(ROOM_ID);
    }

    public static Seat seat() {
        return seat(SEAT_ID, ROOM_ID, SEAT_CODE);
    }

    public static Seat seat(UUID seatId, UUID roomId, String seatCode) {
        var seat = Seat.of(roomId, seatCode, CLOCK.instant());
        setField(seat, "id", seatId);
        return seat;
    }

    private static void setField(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("테스트용 필드 설정 실패: " + fieldName, e);
        }
    }
}
