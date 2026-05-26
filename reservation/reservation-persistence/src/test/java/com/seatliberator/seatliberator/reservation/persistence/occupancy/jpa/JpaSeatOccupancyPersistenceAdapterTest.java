package com.seatliberator.seatliberator.reservation.persistence.occupancy.jpa;

import com.seatliberator.seatliberator.reservation.application.occupancy.port.out.SeatOccupancyReader;
import com.seatliberator.seatliberator.reservation.application.occupancy.port.out.SeatOccupancyStore;
import com.seatliberator.seatliberator.reservation.application.occupancy.port.out.criteria.SeatOccupancyFilter;
import com.seatliberator.seatliberator.reservation.application.occupancy.port.out.criteria.SeatOccupancySlotCriteria;
import com.seatliberator.seatliberator.reservation.domain.reservation.Reservation;
import com.seatliberator.seatliberator.reservation.domain.reservation.SeatOccupancy;
import com.seatliberator.seatliberator.reservation.domain.room.Room;
import com.seatliberator.seatliberator.reservation.domain.seat.Seat;
import com.seatliberator.seatliberator.reservation.domain.seat.SeatTimeSlot;
import com.seatliberator.seatliberator.reservation.persistence.AbstractPersistenceAdapterTest;
import com.seatliberator.seatliberator.reservation.persistence.occupancy.jpa.repository.SeatOccupancyRepository;
import com.seatliberator.seatliberator.reservation.persistence.reservation.jpa.repository.ReservationRepository;
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

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static com.seatliberator.seatliberator.reservation.persistence.occupancy.SeatOccupancyTestSupport.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Import({JpaSeatOccupancyPersistenceAdapter.class})
@DisplayName("SeatOccupancy Persistence")
public class JpaSeatOccupancyPersistenceAdapterTest extends AbstractPersistenceAdapterTest {
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

    private Seat saveSeat(Room room) {
        return seatRepository.save(seat(room));
    }

    private SeatTimeSlot saveSeatTimeSlot() {
        return saveSeatTimeSlot(saveSeat(saveRoom()), DEFAULT_SLOT_START_AT);
    }

    private SeatTimeSlot saveSeatTimeSlot(Seat seat, LocalTime startAt) {
        return seatTimeSlotRepository.save(seatTimeSlot(seat, startAt));
    }

    private Reservation saveReservation(String userId) {
        return reservationRepository.save(reservation(userId));
    }

    private SeatOccupancy saveSeatOccupancy() {
        return saveSeatOccupancy(OCCUPANCY_DATE);
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
            var actual = reader.existsById(UNKNOWN_SEAT_OCCUPANCY_ID);

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
            var actual = reader.findById(UNKNOWN_SEAT_OCCUPANCY_ID);

            assertThat(actual).isEmpty();
        }

        @Test
        @DisplayName("findByIds는 요청한 Id에 해당하는 점유 목록을 반환한다")
        void should_find_seat_occupancies_by_ids() {
            var first = saveSeatOccupancy();
            var second = saveSeatOccupancy(OCCUPANCY_DATE.plusDays(1));
            saveSeatOccupancy(OCCUPANCY_DATE.plusDays(2));
            flushAndClear();

            var actual = reader.findByIds(List.of(first.getId(), second.getId(), UNKNOWN_SEAT_OCCUPANCY_ID));

            assertThat(actual)
                    .extracting(SeatOccupancy::getId)
                    .containsExactlyInAnyOrder(first.getId(), second.getId());
        }

        @Test
        @DisplayName("findByReservationId는 예약 Id에 해당하는 점유 목록을 반환한다")
        void should_find_seat_occupancies_by_reservation_id() {
            var reservation = saveReservation(USER_ID);
            var otherReservation = saveReservation(OTHER_USER_ID);
            var first = saveSeatOccupancy(reservation, OCCUPANCY_DATE);
            var second = saveSeatOccupancy(reservation, OCCUPANCY_DATE.plusDays(1));
            saveSeatOccupancy(otherReservation, OCCUPANCY_DATE.plusDays(2));
            flushAndClear();

            var actual = reader.findByReservationId(reservation.getId());

            assertThat(actual)
                    .extracting(SeatOccupancy::getId)
                    .containsExactlyInAnyOrder(first.getId(), second.getId());
        }

        @Test
        @DisplayName("findByCriteria는 ANY_OF 슬롯 조건과 날짜 범위 조건에 맞는 점유 목록을 반환한다")
        void should_find_seat_occupancies_by_any_slot_and_date_range() {
            var seat = saveSeat(saveRoom());
            var firstSlot = saveSeatTimeSlot(seat, LocalTime.of(9, 0));
            var secondSlot = saveSeatTimeSlot(seat, LocalTime.of(13, 0));
            var excludedSlot = saveSeatTimeSlot(seat, LocalTime.of(17, 0));

            var startDate = OCCUPANCY_DATE;
            var endDate = OCCUPANCY_DATE.plusDays(2);
            var first = saveSeatOccupancy(firstSlot, startDate);
            var second = saveSeatOccupancy(secondSlot, endDate);
            saveSeatOccupancy(firstSlot, endDate.plusDays(1));
            saveSeatOccupancy(excludedSlot, startDate.plusDays(1));
            flushAndClear();

            var criteria = SeatOccupancySlotCriteria
                    .matchAnyOf(List.of(firstSlot.getId(), secondSlot.getId()))
                    .filter(SeatOccupancyFilter.empty().range(occupancyDateRange(startDate, endDate)));

            var actual = reader.findByCriteria(criteria);

            assertThat(actual)
                    .extracting(SeatOccupancy::getId)
                    .containsExactlyInAnyOrder(first.getId(), second.getId());
        }

        @Test
        @DisplayName("findByCriteria는 NONE_OF 슬롯 조건에 해당하는 점유를 제외한다")
        void should_exclude_seat_occupancies_by_none_slot_condition() {
            var seat = saveSeat(saveRoom());
            var includedSlot = saveSeatTimeSlot(seat, LocalTime.of(9, 0));
            var excludedSlot = saveSeatTimeSlot(seat, LocalTime.of(13, 0));
            var included = saveSeatOccupancy(includedSlot, OCCUPANCY_DATE);
            saveSeatOccupancy(excludedSlot, OCCUPANCY_DATE);
            flushAndClear();

            var criteria = SeatOccupancySlotCriteria
                    .matchNoneOf(List.of(excludedSlot.getId()))
                    .filter(SeatOccupancyFilter.empty().range(occupancyDateRange(OCCUPANCY_DATE, OCCUPANCY_DATE.plusDays(1))));

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
            var expected = saveSeatOccupancy(reservation, OCCUPANCY_DATE);
            saveSeatOccupancy(otherReservation, OCCUPANCY_DATE.plusDays(1));
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
            var occupancy = seatOccupancy(slot, reservation);

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
            var first = seatOccupancy(slot, reservation);
            var second = seatOccupancy(slot, reservation, OCCUPANCY_DATE.plusDays(1));

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
            var otherOccupancy = saveSeatOccupancy(OCCUPANCY_DATE.plusDays(1));
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
            var second = saveSeatOccupancy(OCCUPANCY_DATE.plusDays(1));
            var remaining = saveSeatOccupancy(OCCUPANCY_DATE.plusDays(2));
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
            saveSeatOccupancy(slot, OCCUPANCY_DATE);
            flushAndClear();

            var duplicated = seatOccupancy(slot, saveReservation(OTHER_USER_ID));

            assertThatThrownBy(() -> {
                store.save(duplicated);
                flushAndClear();
            }).isInstanceOfAny(ConstraintViolationException.class, DataIntegrityViolationException.class);
        }
    }
}
