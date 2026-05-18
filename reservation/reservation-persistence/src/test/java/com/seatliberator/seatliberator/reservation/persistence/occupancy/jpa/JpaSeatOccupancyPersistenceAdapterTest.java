package com.seatliberator.seatliberator.reservation.persistence.occupancy.jpa;

import com.seatliberator.seatliberator.kernel.test.clock.TestClock;
import com.seatliberator.seatliberator.reservation.application.occupancy.port.out.SeatOccupancyReader;
import com.seatliberator.seatliberator.reservation.application.occupancy.port.out.SeatOccupancyStore;
import com.seatliberator.seatliberator.reservation.application.occupancy.port.out.criteria.SeatOccupancyFilter;
import com.seatliberator.seatliberator.reservation.application.occupancy.port.out.criteria.SeatOccupancySlotCriteria;
import com.seatliberator.seatliberator.reservation.domain.reservation.Reservation;
import com.seatliberator.seatliberator.reservation.domain.reservation.SeatOccupancy;
import com.seatliberator.seatliberator.reservation.domain.room.Room;
import com.seatliberator.seatliberator.reservation.domain.room.RoomFixture;
import com.seatliberator.seatliberator.reservation.domain.seat.Seat;
import com.seatliberator.seatliberator.reservation.domain.seat.SeatFixture;
import com.seatliberator.seatliberator.reservation.domain.seat.SeatTimeSlot;
import com.seatliberator.seatliberator.reservation.domain.seat.SeatTimeSlotStatus;
import com.seatliberator.seatliberator.reservation.domain.shared.temporal.SimpleDailyNanoRange;
import com.seatliberator.seatliberator.reservation.domain.shared.temporal.SimpleDateRange;
import com.seatliberator.seatliberator.reservation.persistence.AbstractPersistenceAdapterTest;
import com.seatliberator.seatliberator.reservation.persistence.book.jpa.repository.ReservationRepository;
import com.seatliberator.seatliberator.reservation.persistence.occupancy.jpa.repository.SeatOccupancyRepository;
import com.seatliberator.seatliberator.reservation.persistence.room.jpa.repository.RoomRepository;
import com.seatliberator.seatliberator.reservation.persistence.seat.jpa.repository.SeatRepository;
import com.seatliberator.seatliberator.reservation.persistence.seat.jpa.repository.SeatTimeSlotRepository;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;

import java.lang.reflect.Field;
import java.time.*;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Import({JpaSeatOccupancyPersistenceAdapter.class})
@DisplayName("SeatOccupancy Persistence")
public class JpaSeatOccupancyPersistenceAdapterTest extends AbstractPersistenceAdapterTest {
    private static final Clock fixedClock = TestClock.getFixed();
    private static final String ROOM_ID = "study-room-1";
    private static final String SEAT_ID = "seat-a";
    private static final String USER_ID = "user-1";
    private static final String OTHER_USER_ID = "user-2";
    private final LocalDate occupancyDate = LocalDate.now(fixedClock);
    @Autowired
    SeatOccupancyReader reader;
    @Autowired
    SeatOccupancyStore store;
    @Autowired
    SeatOccupancyRepository seatOccupancyRepository;
    @Autowired
    ReservationRepository reservationRepository;
    @Autowired
    RoomRepository roomRepository;
    @Autowired
    SeatRepository seatRepository;
    @Autowired
    SeatTimeSlotRepository seatTimeSlotRepository;

    private Room saveRoom() {
        return roomRepository.save(room());
    }

    private Room room() {
        return new RoomFixture.Builder()
                .roomId(ROOM_ID)
                .build();
    }

    private Seat saveSeat(Room room, String seatId) {
        return seatRepository.save(seat(room, seatId));
    }

    private Seat seat(Room room, String seatId) {
        return SeatFixture.create(room, seatId, now());
    }

    private SeatTimeSlot saveSeatTimeSlot() {
        return saveSeatTimeSlot(saveSeat(saveRoom(), SEAT_ID), LocalTime.of(9, 0));
    }

    private SeatTimeSlot saveSeatTimeSlot(Seat seat, LocalTime startAt) {
        var slotRange = SimpleDailyNanoRange.of(startAt, Duration.ofHours(2));
        var seatTimeSlot = SeatTimeSlot.of(seat, slotRange, SeatTimeSlotStatus.ACTIVE, now());
        setField(seatTimeSlot, "seatId", seat.getId());
        return seatTimeSlotRepository.save(seatTimeSlot);
    }

    private Reservation saveReservation(String userId) {
        return reservationRepository.save(Reservation.of(userId, now()));
    }

    private Instant now() {
        return fixedClock.instant();
    }

    private SeatOccupancy saveSeatOccupancy() {
        return saveSeatOccupancy(occupancyDate);
    }

    private SeatOccupancy saveSeatOccupancy(LocalDate occupancyDate) {
        return saveSeatOccupancy(saveSeatTimeSlot(), occupancyDate);
    }

    private SeatOccupancy saveSeatOccupancy(Reservation reservation, LocalDate occupancyDate) {
        return saveSeatOccupancy(saveSeatTimeSlot(), reservation, occupancyDate);
    }

    private SeatOccupancy saveSeatOccupancy(SeatTimeSlot slot, LocalDate occupancyDate) {
        return saveSeatOccupancy(slot, saveReservation(USER_ID), occupancyDate);
    }

    private SeatOccupancy saveSeatOccupancy(SeatTimeSlot slot, Reservation reservation, LocalDate occupancyDate) {
        return seatOccupancyRepository.save(seatOccupancy(slot, reservation, occupancyDate));
    }

    private SeatOccupancy seatOccupancy(SeatTimeSlot slot, Reservation reservation, LocalDate occupancyDate) {
        return SeatOccupancy.of(slot.getId(), reservation.getId(), occupancyDate, now());
    }

    private void assertSameSeatOccupancy(SeatOccupancy actual, SeatOccupancy expected) {
        assertThat(actual.getId()).isEqualTo(expected.getId());
        assertThat(actual.getSeatTimeSlotId()).isEqualTo(expected.getSeatTimeSlotId());
        assertThat(actual.getReservationId()).isEqualTo(expected.getReservationId());
        assertThat(actual.getOccupancyDate()).isEqualTo(expected.getOccupancyDate());
        assertThat(actual.getCreatedAt()).isEqualTo(expected.getCreatedAt());
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
        @DisplayName("existsById는 Id에 해당하는 점유가 있으면 True")
        void should_return_true_when_exists_by_id() {
            var occupancy = saveSeatOccupancy();
            flushAndClear();

            var actual = reader.existsById(occupancy.getId());

            assertThat(actual).isTrue();
        }

        @Test
        @DisplayName("existsById는 Id에 해당하는 점유가 없으면 False")
        void should_return_false_when_not_exists_by_id() {
            var actual = reader.existsById(UUID.randomUUID());

            assertThat(actual).isFalse();
        }

        @Test
        @DisplayName("findById는 Id에 해당하는 점유를 반환한다")
        void should_find_seat_occupancy_by_id() {
            var occupancy = saveSeatOccupancy();
            flushAndClear();

            var actual = reader.findById(occupancy.getId());

            assertThat(actual)
                    .isPresent()
                    .get()
                    .satisfies(found -> assertSameSeatOccupancy(found, occupancy));
        }

        @Test
        @DisplayName("findById는 Id에 해당하는 점유가 없으면 Optional.empty를 반환한다")
        void should_return_empty_when_seat_occupancy_not_found_by_id() {
            var actual = reader.findById(UUID.randomUUID());

            assertThat(actual).isEmpty();
        }

        @Test
        @DisplayName("findByIds는 요청한 Id에 해당하는 점유 목록을 반환한다")
        void should_find_seat_occupancies_by_ids() {
            var first = saveSeatOccupancy();
            var second = saveSeatOccupancy(occupancyDate.plusDays(1));
            saveSeatOccupancy(occupancyDate.plusDays(2));
            flushAndClear();

            var actual = reader.findByIds(List.of(first.getId(), second.getId()));

            assertThat(actual)
                    .extracting(SeatOccupancy::getId)
                    .containsExactlyInAnyOrder(first.getId(), second.getId());
        }

        @Test
        @DisplayName("findByReservationId는 예약 Id에 해당하는 점유 목록을 반환한다")
        void should_find_seat_occupancies_by_reservation_id() {
            var reservation = saveReservation(USER_ID);
            var otherReservation = saveReservation(OTHER_USER_ID);
            var first = saveSeatOccupancy(reservation, occupancyDate);
            var second = saveSeatOccupancy(reservation, occupancyDate.plusDays(1));
            saveSeatOccupancy(otherReservation, occupancyDate.plusDays(2));
            flushAndClear();

            var actual = reader.findByReservationId(reservation.getId());

            assertThat(actual)
                    .extracting(SeatOccupancy::getId)
                    .containsExactlyInAnyOrder(first.getId(), second.getId());
        }

        @Test
        @DisplayName("findByCriteria는 ANY_OF 슬롯 조건과 날짜 범위 조건에 맞는 점유 목록을 반환한다")
        void should_find_seat_occupancies_by_any_slot_and_date_range() {
            var seat = saveSeat(saveRoom(), SEAT_ID);
            var firstSlot = saveSeatTimeSlot(seat, LocalTime.of(9, 0));
            var secondSlot = saveSeatTimeSlot(seat, LocalTime.of(13, 0));
            var excludedSlot = saveSeatTimeSlot(seat, LocalTime.of(17, 0));

            var startDate = occupancyDate;
            var endDate = occupancyDate.plusDays(2);
            var first = saveSeatOccupancy(firstSlot, startDate);
            var second = saveSeatOccupancy(secondSlot, endDate);
            saveSeatOccupancy(firstSlot, endDate.plusDays(1));
            saveSeatOccupancy(excludedSlot, startDate.plusDays(1));
            flushAndClear();

            var criteria = SeatOccupancySlotCriteria
                    .matchAnyOf(List.of(firstSlot.getId(), secondSlot.getId()))
                    .filter(SeatOccupancyFilter.empty().range(SimpleDateRange.of(startDate, endDate)));

            var actual = reader.findByCriteria(criteria);

            assertThat(actual)
                    .extracting(SeatOccupancy::getId)
                    .containsExactlyInAnyOrder(first.getId(), second.getId());
        }

        @Test
        @DisplayName("findByCriteria는 NONE_OF 슬롯 조건에 해당하는 점유를 제외한다")
        void should_exclude_seat_occupancies_by_none_slot_condition() {
            var seat = saveSeat(saveRoom(), SEAT_ID);
            var includedSlot = saveSeatTimeSlot(seat, LocalTime.of(9, 0));
            var excludedSlot = saveSeatTimeSlot(seat, LocalTime.of(13, 0));
            var included = saveSeatOccupancy(includedSlot, occupancyDate);
            saveSeatOccupancy(excludedSlot, occupancyDate);
            flushAndClear();

            var criteria = SeatOccupancySlotCriteria
                    .matchNoneOf(List.of(excludedSlot.getId()))
                    .filter(SeatOccupancyFilter.empty().range(SimpleDateRange.of(occupancyDate, occupancyDate.plusDays(1))));

            var actual = reader.findByCriteria(criteria);

            assertThat(actual)
                    .extracting(SeatOccupancy::getId)
                    .containsExactly(included.getId());
        }

        @Test
        @DisplayName("findByCriteria는 예약 Id 조건에 맞는 점유 목록을 반환한다")
        void should_find_seat_occupancies_by_reservation_filter() {
            var reservation = saveReservation(USER_ID);
            var otherReservation = saveReservation(OTHER_USER_ID);
            var expected = saveSeatOccupancy(reservation, occupancyDate);
            saveSeatOccupancy(otherReservation, occupancyDate.plusDays(1));
            flushAndClear();

            var criteria = SeatOccupancySlotCriteria
                    .matchAnyOf(List.of(expected.getSeatTimeSlotId()))
                    .filter(SeatOccupancyFilter.empty().reservationId(reservation.getId()));

            var actual = reader.findByCriteria(criteria);

            assertThat(actual)
                    .extracting(SeatOccupancy::getId)
                    .containsExactly(expected.getId());
        }
    }

    @Nested
    @DisplayName("Store 테스트")
    class StoreTest {
        @Test
        @DisplayName("save는 좌석 점유를 저장한다")
        void should_save_seat_occupancy() {
            var slot = saveSeatTimeSlot();
            var reservation = saveReservation(USER_ID);
            var occupancy = seatOccupancy(slot, reservation, occupancyDate);

            var saved = store.save(occupancy);
            flushAndClear();

            var actual = seatOccupancyRepository.findById(saved.getId());
            assertThat(actual)
                    .isPresent()
                    .get()
                    .satisfies(found -> assertSameSeatOccupancy(found, saved));
        }

        @Test
        @DisplayName("saveAll은 좌석 점유 목록을 저장한다")
        void should_save_all_seat_occupancies() {
            var slot = saveSeatTimeSlot();
            var reservation = saveReservation(USER_ID);
            var first = seatOccupancy(slot, reservation, occupancyDate);
            var second = seatOccupancy(slot, reservation, occupancyDate.plusDays(1));

            var saved = store.saveAll(List.of(first, second));
            flushAndClear();

            assertThat(seatOccupancyRepository.findAllById(saved.stream().map(SeatOccupancy::getId).toList()))
                    .extracting(SeatOccupancy::getId)
                    .containsExactlyInAnyOrderElementsOf(saved.stream().map(SeatOccupancy::getId).toList());
        }

        @Test
        @DisplayName("delete는 좌석 점유를 삭제한다")
        void should_delete_seat_occupancy() {
            var occupancy = saveSeatOccupancy();
            var otherOccupancy = saveSeatOccupancy(occupancyDate.plusDays(1));
            flushAndClear();

            store.delete(occupancy);
            flushAndClear();

            assertThat(seatOccupancyRepository.existsById(occupancy.getId())).isFalse();
            assertThat(seatOccupancyRepository.existsById(otherOccupancy.getId())).isTrue();
        }

        @Test
        @DisplayName("deleteAll은 좌석 점유 목록을 삭제한다")
        void should_delete_all_seat_occupancies() {
            var first = saveSeatOccupancy();
            var second = saveSeatOccupancy(occupancyDate.plusDays(1));
            var remaining = saveSeatOccupancy(occupancyDate.plusDays(2));
            flushAndClear();

            store.deleteAll(List.of(first, second));
            flushAndClear();

            assertThat(seatOccupancyRepository.existsById(first.getId())).isFalse();
            assertThat(seatOccupancyRepository.existsById(second.getId())).isFalse();
            assertThat(seatOccupancyRepository.existsById(remaining.getId())).isTrue();
        }

        @Test
        @DisplayName("같은 좌석 슬롯과 같은 점유일은 중복 저장할 수 없다")
        void should_throw_exception_when_save_duplicate_slot_and_occupancy_date() {
            var slot = saveSeatTimeSlot();
            saveSeatOccupancy(slot, occupancyDate);
            flushAndClear();

            var duplicated = seatOccupancy(slot, saveReservation(OTHER_USER_ID), occupancyDate);

            assertThatThrownBy(() -> {
                store.save(duplicated);
                flushAndClear();
            }).isInstanceOfAny(ConstraintViolationException.class, DataIntegrityViolationException.class);
        }
    }
}
