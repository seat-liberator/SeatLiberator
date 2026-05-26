package com.seatliberator.seatliberator.reservation.application.booking;

import com.seatliberator.seatliberator.kernel.test.UuidGenerator;
import com.seatliberator.seatliberator.reservation.application.DefaultTestSupport;
import com.seatliberator.seatliberator.reservation.application.booking.port.in.command.CreateBookingCommand;
import com.seatliberator.seatliberator.reservation.application.booking.port.in.query.FindAvailableSlotsBySeatQuery;
import com.seatliberator.seatliberator.reservation.domain.reservation.Reservation;
import com.seatliberator.seatliberator.reservation.domain.reservation.ReservationFixture;
import com.seatliberator.seatliberator.reservation.domain.reservation.SeatOccupancy;
import com.seatliberator.seatliberator.reservation.domain.reservation.SeatOccupancyFixture;
import com.seatliberator.seatliberator.reservation.domain.seat.SeatTimeSlot;
import com.seatliberator.seatliberator.reservation.domain.seat.SeatTimeSlotFixture;
import com.seatliberator.seatliberator.reservation.domain.shared.temporal.DailyNanoRange;
import com.seatliberator.seatliberator.reservation.domain.shared.temporal.SimpleDateRange;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static com.seatliberator.seatliberator.reservation.application.reservation.ReservationTestSupport.stubReservationId;
import static com.seatliberator.seatliberator.reservation.domain.shared.temporal.DailyNanoRangeFixture.*;

public class BookingTestSupport extends DefaultTestSupport {
    public static final UUID SEAT_UUID = UuidGenerator.generate(1);
    public static final UUID MORNING_SLOT_ID = UuidGenerator.generate(2);
    public static final UUID AFTERNOON_SLOT_ID = UuidGenerator.generate(3);
    public static final UUID NIGHT_SLOT_ID = UuidGenerator.generate(4);
    public static final UUID RESERVATION_ID = UuidGenerator.generate(5);

    public static final LocalDate OCCUPANCY_DATE = LocalDate.now(CLOCK);
    public static final LocalDate RANGE_START_DATE = OCCUPANCY_DATE;
    public static final LocalDate RANGE_END_DATE = OCCUPANCY_DATE.plusDays(2);
    public static final SimpleDateRange DATE_RANGE = SimpleDateRange.of(RANGE_START_DATE, RANGE_END_DATE);

    public static final List<UUID> SLOT_IDS = List.of(MORNING_SLOT_ID, AFTERNOON_SLOT_ID);

    public static CreateBookingCommand createBookingCommand() {
        return CreateBookingCommand.of(USER_ID, SLOT_IDS, OCCUPANCY_DATE);
    }

    public static FindAvailableSlotsBySeatQuery findAvailableSlotsBySeatQuery() {
        return FindAvailableSlotsBySeatQuery.of(SEAT_UUID, DATE_RANGE);
    }

    public static Reservation reservation() {
        return ReservationFixture.next();
    }

    public static Reservation reservationWithId() {
        var reservation = reservation();
        stubReservationId(reservation, RESERVATION_ID);
        return reservation;
    }

    public static SeatTimeSlot morningSlot() {
        return slot(MORNING_SLOT_ID, MORNING_SLOT_RANGE);
    }

    public static SeatTimeSlot afternoonSlot() {
        return slot(AFTERNOON_SLOT_ID, AFTERNOON_SLOT_RANGE);
    }

    public static SeatTimeSlot nightSlot() {
        return slot(NIGHT_SLOT_ID, NIGHT_SLOT_RANGE);
    }

    public static List<SeatTimeSlot> slots() {
        return List.of(morningSlot(), afternoonSlot());
    }

    public static SeatOccupancy occupancy(SeatTimeSlot slot, LocalDate occupancyDate) {
        return new SeatOccupancyFixture.Builder()
                .seatTimeSlotId(slot.getId())
                .reservationId(RESERVATION_ID)
                .occupancyDate(occupancyDate)
                .createdAt(NOW)
                .build();
    }

    private static SeatTimeSlot slot(UUID id, DailyNanoRange range) {
        var slot = new SeatTimeSlotFixture.Builder()
                .slotRange(range)
                .createdAt(NOW)
                .build();
        setField(slot, "id", id);
        setField(slot, "seatId", SEAT_UUID);
        return slot;
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
