package com.seatliberator.seatliberator.reservation.persistence.booking.jpa;

import com.seatliberator.seatliberator.reservation.application.booking.port.in.result.BookingDetailResult;
import com.seatliberator.seatliberator.reservation.application.booking.port.out.BookingDetailReader;
import com.seatliberator.seatliberator.reservation.domain.reservation.Reservation;
import com.seatliberator.seatliberator.reservation.domain.reservation.ReservationStatus;
import com.seatliberator.seatliberator.reservation.domain.reservation.SeatOccupancy;
import com.seatliberator.seatliberator.reservation.domain.room.Room;
import com.seatliberator.seatliberator.reservation.domain.seat.Seat;
import com.seatliberator.seatliberator.reservation.domain.seat.SeatTimeSlot;
import com.seatliberator.seatliberator.reservation.domain.seat.SeatTimeSlotStatus;
import com.seatliberator.seatliberator.reservation.domain.shared.temporal.SimpleDailyNanoRange;
import com.seatliberator.seatliberator.reservation.persistence.AbstractPersistenceAdapterTest;
import com.seatliberator.seatliberator.reservation.persistence.book.jpa.repository.ReservationRepository;
import com.seatliberator.seatliberator.reservation.persistence.occupancy.jpa.repository.SeatOccupancyRepository;
import com.seatliberator.seatliberator.reservation.persistence.room.jpa.repository.RoomRepository;
import com.seatliberator.seatliberator.reservation.persistence.seat.jpa.repository.SeatRepository;
import com.seatliberator.seatliberator.reservation.persistence.seat.jpa.repository.SeatTimeSlotRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.lang.reflect.Field;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import static com.seatliberator.seatliberator.reservation.persistence.TestSupport.*;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({JpaBookingDetailPersistenceAdapter.class})
@DisplayName("BookingDetail Persistence")
public class JpaBookingDetailPersistenceAdapterTest extends AbstractPersistenceAdapterTest {
    @Autowired
    BookingDetailReader reader;
    @Autowired
    ReservationRepository reservationRepository;
    @Autowired
    SeatOccupancyRepository seatOccupancyRepository;
    @Autowired
    RoomRepository roomRepository;
    @Autowired
    SeatRepository seatRepository;
    @Autowired
    SeatTimeSlotRepository seatTimeSlotRepository;

    private Room saveRoom() {
        return roomRepository.save(room());
    }

    private Seat saveSeat(Room room) {
        return seatRepository.save(seat(room));
    }

    private SeatTimeSlot saveSeatTimeSlot(Seat seat, LocalTime startAt) {
        var slotRange = SimpleDailyNanoRange.of(startAt, Duration.ofHours(2));
        var seatTimeSlot = SeatTimeSlot.of(seat, slotRange, SeatTimeSlotStatus.ACTIVE, now());
        setField(seatTimeSlot, "seatId", seat.getId());
        return seatTimeSlotRepository.save(seatTimeSlot);
    }

    private Reservation saveReservation() {
        return saveReservation(USER_ID);
    }

    private Reservation saveReservation(String userId) {
        return reservationRepository.save(Reservation.of(userId, now()));
    }

    private SeatOccupancy saveSeatOccupancy(SeatTimeSlot slot, Reservation reservation, LocalDate occupancyDate) {
        return seatOccupancyRepository.save(SeatOccupancy.of(
                slot.getId(),
                reservation.getId(),
                occupancyDate,
                now()
        ));
    }

    private void assertReservation(BookingDetailResult actual, Reservation expected) {
        assertThat(actual.reservationId()).isEqualTo(expected.getId());
        assertThat(actual.userId()).isEqualTo(expected.getUserId());
        assertThat(actual.reservationState().status()).isEqualTo(ReservationStatus.RESERVED);
        assertThat(actual.reservationState().reservedAt()).isEqualTo(expected.getState().getReservedAt());
        assertThat(actual.reservationState().usedAt()).isNull();
        assertThat(actual.reservationState().cancelledAt()).isNull();
        assertThat(actual.reservationState().expiredAt()).isNull();
    }

    private void assertSlot(
            BookingDetailResult.BookingSlotResult actual,
            SeatOccupancy expectedOccupancy,
            SeatTimeSlot expectedSlot,
            LocalTime expectedStartAt
    ) {
        assertThat(actual.seatOccupancyId()).isEqualTo(expectedOccupancy.getId());
        assertThat(actual.seatTimeSlotId()).isEqualTo(expectedSlot.getId());
        assertThat(actual.occupancyDate()).isEqualTo(expectedOccupancy.getOccupancyDate());
        assertThat(actual.roomId()).isEqualTo(ROOM_ID);
        assertThat(actual.seatId()).isEqualTo(SEAT_ID);
        assertThat(actual.startAt()).isEqualTo(expectedStartAt);
        assertThat(actual.duration()).isEqualTo(Duration.ofHours(2));
        assertThat(actual.status()).isEqualTo(SeatTimeSlotStatus.ACTIVE);
    }

    private void setField(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("테스트용 필드 설정 실패: " + fieldName, e);
        }
    }

    @Nested
    @DisplayName("Reader 테스트")
    class ReaderTest {
        @Test
        @DisplayName("findByReservationId는 예약과 점유 슬롯 상세를 함께 반환한다")
        void should_find_booking_detail_by_reservation_id() {
            var room = saveRoom();
            var seat = saveSeat(room);
            var firstSlot = saveSeatTimeSlot(seat, LocalTime.of(9, 0));
            var secondSlot = saveSeatTimeSlot(seat, LocalTime.of(11, 0));
            var reservation = saveReservation();
            var occupancyDate = LocalDate.now(fixedClock);
            var firstOccupancy = saveSeatOccupancy(firstSlot, reservation, occupancyDate);
            var secondOccupancy = saveSeatOccupancy(secondSlot, reservation, occupancyDate);
            saveSeatOccupancy(firstSlot, saveReservation(OTHER_USER_ID), occupancyDate.plusDays(1));
            flushAndClear();

            var actual = reader.findByReservationId(reservation.getId());

            assertThat(actual)
                    .isPresent()
                    .get()
                    .satisfies(detail -> {
                        assertReservation(detail, reservation);
                        assertThat(detail.slots())
                                .extracting(BookingDetailResult.BookingSlotResult::seatTimeSlotId)
                                .containsExactly(firstSlot.getId(), secondSlot.getId());
                        assertSlot(detail.slots().get(0), firstOccupancy, firstSlot, LocalTime.of(9, 0));
                        assertSlot(detail.slots().get(1), secondOccupancy, secondSlot, LocalTime.of(11, 0));
                    });
        }

        @Test
        @DisplayName("findByReservationId는 예약이 있지만 점유 슬롯이 없으면 빈 슬롯 목록을 반환한다")
        void should_return_empty_slots_when_reservation_has_no_occupancy() {
            var reservation = saveReservation();
            flushAndClear();

            var actual = reader.findByReservationId(reservation.getId());

            assertThat(actual)
                    .isPresent()
                    .get()
                    .satisfies(detail -> {
                        assertReservation(detail, reservation);
                        assertThat(detail.slots()).isEmpty();
                    });
        }

        @Test
        @DisplayName("findByReservationId는 예약이 없으면 Optional.empty를 반환한다")
        void should_return_empty_when_reservation_not_found() {
            var actual = reader.findByReservationId(UUID.randomUUID());

            assertThat(actual).isEmpty();
        }
    }
}
