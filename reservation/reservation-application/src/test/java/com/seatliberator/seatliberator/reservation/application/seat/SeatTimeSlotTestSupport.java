package com.seatliberator.seatliberator.reservation.application.seat;

import com.seatliberator.seatliberator.kernel.test.clock.TestClock;
import com.seatliberator.seatliberator.reservation.application.seat.port.in.command.CreateSeatTimeSlotCommand;
import com.seatliberator.seatliberator.reservation.application.seat.port.in.command.DeleteSeatTimeSlotCommand;
import com.seatliberator.seatliberator.reservation.application.seat.port.in.command.UpdateSeatTimeSlotCommand;
import com.seatliberator.seatliberator.reservation.application.seat.port.in.query.FindSeatTimeSlotQuery;
import com.seatliberator.seatliberator.reservation.application.seat.port.in.query.ListSeatTimeSlotQuery;
import com.seatliberator.seatliberator.reservation.domain.seat.SeatFixture;
import com.seatliberator.seatliberator.reservation.domain.seat.SeatTimeSlot;
import com.seatliberator.seatliberator.reservation.domain.seat.SeatTimeSlotFixture;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalTime;
import java.util.UUID;

public class SeatTimeSlotTestSupport {
    private final static Clock CLOCK = TestClock.getFixed();

    private final static UUID SEAT_ID = SeatFixture.nextId();
    private final static UUID SEAT_TIME_SLOT_ID = SeatTimeSlotFixture.nextId();
    private final static LocalTime SLOT_START_AT = LocalTime.of(9, 0);
    private final static Duration SLOT_DURATION = Duration.ofHours(2);
    private final static LocalTime UPDATED_SLOT_START_AT = LocalTime.of(13, 0);
    private final static Duration UPDATED_SLOT_DURATION = Duration.ofHours(3);

    public static CreateSeatTimeSlotCommand createSeatTimeSlotCommand() {
        return CreateSeatTimeSlotCommand.of(SEAT_ID, SLOT_START_AT, SLOT_DURATION);
    }

    public static UpdateSeatTimeSlotCommand updateSeatTimeSlotCommand() {
        return new UpdateSeatTimeSlotCommand(SEAT_TIME_SLOT_ID, UPDATED_SLOT_START_AT, UPDATED_SLOT_DURATION);
    }

    public static DeleteSeatTimeSlotCommand deleteSeatTimeSlotCommand() {
        return new DeleteSeatTimeSlotCommand(SEAT_TIME_SLOT_ID);
    }

    public static FindSeatTimeSlotQuery findSeatTimeSlotQuery() {
        return new FindSeatTimeSlotQuery(SEAT_TIME_SLOT_ID);
    }

    public static ListSeatTimeSlotQuery listSeatTimeSlotQuery() {
        return ListSeatTimeSlotQuery.of(SEAT_ID);
    }

    public static SeatTimeSlot seatTimeSlot() {
        return seatTimeSlot(SEAT_ID);
    }

    public static SeatTimeSlot seatTimeSlot(UUID seatId) {
        return new SeatTimeSlotFixture.Builder()
                .seatId(seatId)
                .createdAt(CLOCK.instant())
                .build();
    }
}
