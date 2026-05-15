package com.seatliberator.seatliberator.reservation.persistence.seat.jpa;

import com.seatliberator.seatliberator.reservation.application.seat.port.out.SeatTimeSlotReader;
import com.seatliberator.seatliberator.reservation.application.seat.port.out.SeatTimeSlotStore;
import com.seatliberator.seatliberator.reservation.domain.room.Room;
import com.seatliberator.seatliberator.reservation.domain.seat.Seat;
import com.seatliberator.seatliberator.reservation.domain.seat.SeatTimeSlot;
import com.seatliberator.seatliberator.reservation.domain.seat.SeatTimeSlotStatus;
import com.seatliberator.seatliberator.reservation.domain.shared.temporal.SimpleDailyNanoRange;
import com.seatliberator.seatliberator.reservation.persistence.AbstractPersistenceAdapterTest;
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

import java.time.Duration;
import java.time.LocalTime;
import java.util.UUID;

import static com.seatliberator.seatliberator.reservation.persistence.TestSupport.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Import({JpaSeatTimeSlotPersistenceAdapter.class})
@DisplayName("SeatTimeSlot Persistence")
public class JpaSeatTimeSlotPersistenceAdapterTest extends AbstractPersistenceAdapterTest {
    @Autowired
    SeatTimeSlotReader reader;

    @Autowired
    SeatTimeSlotStore store;

    @Autowired
    SeatTimeSlotRepository seatTimeSlotRepository;

    @Autowired
    SeatRepository seatRepository;

    @Autowired
    RoomRepository roomRepository;

    private Room saveRoom() {
        return roomRepository.save(room());
    }

    private Seat saveSeat(Room room) {
        return seatRepository.save(seat(room));
    }

    private Seat saveOtherSeat(Room room) {
        return seatRepository.save(seat(room, OTHER_SEAT_ID));
    }

    private SeatTimeSlot saveSeatTimeSlot(Seat seat) {
        return seatTimeSlotRepository.save(seatTimeSlot(seat, LocalTime.of(9, 0), Duration.ofHours(2)));
    }

    private SeatTimeSlot saveOtherSeatTimeSlot(Seat seat) {
        return seatTimeSlotRepository.save(seatTimeSlot(seat, LocalTime.of(13, 0), Duration.ofHours(2)));
    }

    private SeatTimeSlot seatTimeSlot(Seat seat, LocalTime startAt, Duration duration) {
        var slotRange = SimpleDailyNanoRange.of(startAt, duration);
        return SeatTimeSlot.of(seat, slotRange, SeatTimeSlotStatus.ACTIVE, now());
    }

    private void assertSameSeatTimeSlot(SeatTimeSlot actual, SeatTimeSlot expected) {
        assertThat(actual.getId()).isEqualTo(expected.getId());
        assertThat(actual.getSeat().getId()).isEqualTo(expected.getSeat().getId());
        assertThat(actual.getSlotRange().startNanoOfDay()).isEqualTo(expected.getSlotRange().startNanoOfDay());
        assertThat(actual.getSlotRange().endNanoOfDay()).isEqualTo(expected.getSlotRange().endNanoOfDay());
        assertThat(actual.getSlotStatus()).isEqualTo(expected.getSlotStatus());
        assertThat(actual.getCreatedAt()).isEqualTo(expected.getCreatedAt());
    }

    @Nested
    @DisplayName("Reader 테스트")
    class ReaderTest {
        @Test
        @DisplayName("existsById는 Id에 해당하는 시간 슬롯이 있으면 True")
        void should_return_true_when_exists_by_id() {
            var room = saveRoom();
            var seat = saveSeat(room);
            var seatTimeSlot = saveSeatTimeSlot(seat);
            flushAndClear();

            var actual = reader.existsById(seatTimeSlot.getId());

            assertThat(actual).isTrue();
        }

        @Test
        @DisplayName("existsById는 Id에 해당하는 시간 슬롯이 없으면 False")
        void should_return_false_when_not_exists_by_id() {
            var actual = reader.existsById(UUID.randomUUID());

            assertThat(actual).isFalse();
        }

        @Test
        @DisplayName("findById는 Id에 해당하는 시간 슬롯을 반환한다")
        void should_find_seat_time_slot_by_id() {
            var room = saveRoom();
            var seat = saveSeat(room);
            var seatTimeSlot = saveSeatTimeSlot(seat);
            flushAndClear();

            var actual = reader.findById(seatTimeSlot.getId());

            assertThat(actual)
                    .isPresent()
                    .get()
                    .satisfies(found -> assertSameSeatTimeSlot(found, seatTimeSlot));
        }

        @Test
        @DisplayName("findById는 Id에 해당하는 시간 슬롯이 없으면 Optional.empty를 반환한다")
        void should_return_empty_when_seat_time_slot_not_found_by_id() {
            var actual = reader.findById(UUID.randomUUID());

            assertThat(actual).isEmpty();
        }

        @Test
        @DisplayName("findBySeatId는 좌석 Id에 해당하는 모든 시간 슬롯을 반환한다")
        void should_find_seat_time_slots_by_seat_id() {
            var room = saveRoom();
            var seat = saveSeat(room);
            var otherSeat = saveOtherSeat(room);
            var seatTimeSlot = saveSeatTimeSlot(seat);
            var otherSeatTimeSlot = saveOtherSeatTimeSlot(seat);
            saveSeatTimeSlot(otherSeat);
            flushAndClear();

            var actual = reader.findBySeatId(seat.getId());

            assertThat(actual)
                    .hasSize(2)
                    .anySatisfy(found -> assertSameSeatTimeSlot(found, seatTimeSlot))
                    .anySatisfy(found -> assertSameSeatTimeSlot(found, otherSeatTimeSlot));
        }
    }

    @Nested
    @DisplayName("Store 테스트")
    class StoreTest {
        @Test
        @DisplayName("save는 시간 슬롯을 저장한다")
        void should_save_seat_time_slot() {
            var room = saveRoom();
            var seat = saveSeat(room);
            var seatTimeSlot = seatTimeSlot(seat, LocalTime.of(9, 0), Duration.ofHours(2));

            var saved = store.save(seatTimeSlot);
            flushAndClear();

            var actual = seatTimeSlotRepository.findById(saved.getId());
            assertThat(actual)
                    .isPresent()
                    .get()
                    .satisfies(found -> assertSameSeatTimeSlot(found, saved));
        }

        @Test
        @DisplayName("delete는 시간 슬롯을 삭제한다")
        void should_delete_seat_time_slot() {
            var room = saveRoom();
            var seat = saveSeat(room);
            var seatTimeSlot = saveSeatTimeSlot(seat);
            var otherSeatTimeSlot = saveOtherSeatTimeSlot(seat);
            flushAndClear();

            store.delete(seatTimeSlot);
            flushAndClear();

            assertThat(seatTimeSlotRepository.existsById(seatTimeSlot.getId())).isFalse();
            assertThat(seatTimeSlotRepository.existsById(otherSeatTimeSlot.getId())).isTrue();
        }

        @Test
        @DisplayName("같은 좌석에 같은 구간 시간 슬롯은 중복 저장할 수 없다")
        void should_throw_exception_when_save_duplicate_slot_range_for_same_seat() {
            var room = saveRoom();
            var seat = saveSeat(room);
            saveSeatTimeSlot(seat);
            flushAndClear();

            var duplicated = seatTimeSlot(seat, LocalTime.of(9, 0), Duration.ofHours(2));

            assertThatThrownBy(() -> {
                store.save(duplicated);
                flushAndClear();
            }).isInstanceOf(ConstraintViolationException.class);
        }

        @Test
        @DisplayName("다른 좌석이면 같은 구간 시간 슬롯을 저장할 수 있다")
        void should_save_same_slot_range_for_different_seat() {
            var room = saveRoom();
            var seat = saveSeat(room);
            var otherSeat = saveOtherSeat(room);
            saveSeatTimeSlot(seat);
            flushAndClear();

            var sameRangeSlot = seatTimeSlot(otherSeat, LocalTime.of(9, 0), Duration.ofHours(2));

            var saved = store.save(sameRangeSlot);
            flushAndClear();

            assertThat(seatTimeSlotRepository.existsById(saved.getId())).isTrue();
        }

        @Test
        @DisplayName("같은 좌석이어도 다른 구간 시간 슬롯은 저장할 수 있다")
        void should_save_different_slot_range_for_same_seat() {
            var room = saveRoom();
            var seat = saveSeat(room);
            saveSeatTimeSlot(seat);
            flushAndClear();

            var differentRangeSlot = seatTimeSlot(seat, LocalTime.of(13, 0), Duration.ofHours(2));

            var saved = store.save(differentRangeSlot);
            flushAndClear();

            assertThat(seatTimeSlotRepository.existsById(saved.getId())).isTrue();
        }
    }
}
