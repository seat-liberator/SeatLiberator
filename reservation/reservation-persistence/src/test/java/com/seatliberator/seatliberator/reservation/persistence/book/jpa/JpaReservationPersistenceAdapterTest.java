package com.seatliberator.seatliberator.reservation.persistence.book.jpa;

import com.seatliberator.seatliberator.reservation.application.booking.port.out.ReservationReader;
import com.seatliberator.seatliberator.reservation.application.booking.port.out.ReservationStore;
import com.seatliberator.seatliberator.reservation.application.booking.port.out.criteria.*;
import com.seatliberator.seatliberator.reservation.domain.reservation.Reservation;
import com.seatliberator.seatliberator.reservation.persistence.AbstractPersistenceAdapterTest;
import com.seatliberator.seatliberator.reservation.persistence.book.jpa.repository.ReservationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.UUID;

import static com.seatliberator.seatliberator.reservation.persistence.TestSupport.*;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({JpaReservationPersistenceAdapter.class})
@DisplayName("Reservation Persistence")
public class JpaReservationPersistenceAdapterTest extends AbstractPersistenceAdapterTest {
    @Autowired
    ReservationReader reader;

    @Autowired
    ReservationStore store;

    @Autowired
    ReservationRepository repository;

    private Reservation saveReservation() {
        return saveReservation(reservation());
    }

    private Reservation saveReservation(Reservation reservation) {
        return repository.save(reservation);
    }

    private ReservationSeatLookupCriteria lookupCriteria(Reservation reservation) {
        return ReservationSeatLookupCriteria.of(reservation.getLocator(), reservation.getRange());
    }

    private void assertSameReservation(Reservation actual, Reservation expected) {
        assertThat(actual.getId()).isEqualTo(expected.getId());
        assertThat(actual.getUserId()).isEqualTo(expected.getUserId());
        assertThat(actual.getLocator().roomId()).isEqualTo(expected.getLocator().roomId());
        assertThat(actual.getLocator().seatId()).isEqualTo(expected.getLocator().seatId());
        assertThat(actual.getRange().startAt()).isEqualTo(expected.getRange().startAt());
        assertThat(actual.getRange().endAt()).isEqualTo(expected.getRange().endAt());
        assertThat(actual.getStatus()).isEqualTo(expected.getStatus());
    }

    @Nested
    @DisplayName("Reader 테스트")
    class ReaderTest {
        @Test
        @DisplayName("findById는 예약 Id에 해당하는 예약을 반환한다")
        void should_find_reservation_by_id() {
            var reservation = saveReservation();
            flushAndClear();

            var actual = reader.findById(reservation.getId());

            assertThat(actual)
                    .isPresent()
                    .get()
                    .satisfies(found -> assertSameReservation(found, reservation));
        }

        @Test
        @DisplayName("findById는 예약 Id에 해당하는 예약이 없으면 Optional.empty를 반환한다")
        void should_return_empty_when_reservation_not_found_by_id() {
            var actual = reader.findById(UUID.randomUUID());

            assertThat(actual).isEmpty();
        }

        @Test
        @DisplayName("findByUserId는 사용자 Id에 해당하는 예약을 반환한다")
        void should_find_reservation_by_user_id() {
            var reservation = saveReservation();
            flushAndClear();

            var actual = reader.findByUserId(USER_ID);

            assertThat(actual)
                    .isPresent()
                    .get()
                    .satisfies(found -> assertSameReservation(found, reservation));
        }

        @Test
        @DisplayName("existsOne은 Locator와 Range가 같은 예약이 있으면 True")
        void should_return_true_when_exists_one_reservation() {
            var reservation = saveReservation();
            flushAndClear();

            var actual = reader.existsOne(lookupCriteria(reservation));

            assertThat(actual).isTrue();
        }

        @Test
        @DisplayName("existsOne은 제외 대상 예약이면 False")
        void should_return_false_when_existing_reservation_is_excluded() {
            var reservation = saveReservation();
            flushAndClear();

            var criteria = lookupCriteria(reservation)
                    .withFilter(ReservationFilter.empty().withExcludeIds(reservation.getId()));
            var actual = reader.existsOne(criteria);

            assertThat(actual).isFalse();
        }

        @Test
        @DisplayName("findOne은 Locator와 Range가 같은 예약을 반환한다")
        void should_find_one_reservation() {
            var reservation = saveReservation();
            flushAndClear();

            var actual = reader.findOne(lookupCriteria(reservation));

            assertThat(actual)
                    .isPresent()
                    .get()
                    .satisfies(found -> assertSameReservation(found, reservation));
        }

        @Test
        @DisplayName("existsOverlapping은 같은 좌석에 시간이 겹치는 예약이 있으면 True")
        void should_return_true_when_seat_reservation_overlaps() {
            saveReservation();
            flushAndClear();

            var criteria = ReservationSeatOverlapCriteria.of(locator(), overlappingRange());
            var actual = reader.existsOverlapping(criteria);

            assertThat(actual).isTrue();
        }

        @Test
        @DisplayName("existsOverlapping은 같은 좌석이어도 시간이 겹치지 않으면 False")
        void should_return_false_when_seat_reservation_does_not_overlap() {
            saveReservation();
            flushAndClear();

            var criteria = ReservationSeatOverlapCriteria.of(locator(), nonOverlappingRange());
            var actual = reader.existsOverlapping(criteria);

            assertThat(actual).isFalse();
        }

        @Test
        @DisplayName("existsOverlapping은 같은 방에 시간이 겹치는 예약이 있으면 True")
        void should_return_true_when_room_reservation_overlaps() {
            saveReservation();
            flushAndClear();

            var criteria = ReservationRoomOverlapCriteria.of(ROOM_ID, overlappingRange());
            var actual = reader.existsOverlapping(criteria);

            assertThat(actual).isTrue();
        }

        @Test
        @DisplayName("findAllOverlapping은 같은 좌석에 시간이 겹치는 예약만 반환한다")
        void should_find_all_overlapping_by_seat() {
            var reservation = saveReservation();
            saveReservation(reservation(OTHER_USER_ID, locator(ROOM_ID, OTHER_SEAT_ID), reservationRange()));
            flushAndClear();

            var criteria = ReservationSeatOverlapCriteria.of(locator(), overlappingRange());
            var actual = reader.findAllOverlapping(criteria);

            assertThat(actual)
                    .hasSize(1)
                    .anySatisfy(found -> assertSameReservation(found, reservation));
        }

        @Test
        @DisplayName("findAllOverlapping은 시간이 겹치는 모든 예약을 반환한다")
        void should_find_all_overlapping_by_range() {
            var reservation = saveReservation();
            var otherReservation = saveReservation(reservation(OTHER_USER_ID, locator(OTHER_ROOM_ID, SEAT_ID), reservationRange()));
            saveReservation(reservation(USER_ID, locator(ROOM_ID, OTHER_SEAT_ID), nonOverlappingRange()));
            flushAndClear();

            var criteria = ReservationRangeOverlapCriteria.of(overlappingRange());
            var actual = reader.findAllOverlapping(criteria);

            assertThat(actual)
                    .hasSize(2)
                    .anySatisfy(found -> assertSameReservation(found, reservation))
                    .anySatisfy(found -> assertSameReservation(found, otherReservation));
        }

        @Test
        @DisplayName("findAllOverlapping은 같은 방에 시간이 겹치는 예약만 반환한다")
        void should_find_all_overlapping_by_room() {
            var reservation = saveReservation();
            var otherReservation = saveReservation(reservation(OTHER_USER_ID, locator(ROOM_ID, OTHER_SEAT_ID), reservationRange()));
            saveReservation(reservation(USER_ID, locator(OTHER_ROOM_ID, SEAT_ID), reservationRange()));
            flushAndClear();

            var criteria = ReservationRoomOverlapCriteria.of(ROOM_ID, overlappingRange());
            var actual = reader.findAllOverlapping(criteria);

            assertThat(actual)
                    .hasSize(2)
                    .anySatisfy(found -> assertSameReservation(found, reservation))
                    .anySatisfy(found -> assertSameReservation(found, otherReservation));
        }
    }

    @Nested
    @DisplayName("Store 테스트")
    class StoreTest {
        @Test
        @DisplayName("save는 예약을 저장한다")
        void should_save_reservation() {
            var reservation = reservation();

            var savedReservation = store.save(reservation);
            flushAndClear();

            var actual = repository.findById(savedReservation.getId());
            assertThat(actual)
                    .isPresent()
                    .get()
                    .satisfies(found -> assertSameReservation(found, savedReservation));
        }

        @Test
        @DisplayName("delete는 예약을 삭제한다")
        void should_delete_reservation() {
            var reservation = saveReservation();
            flushAndClear();

            store.delete(reservation);
            flushAndClear();

            assertThat(repository.existsById(reservation.getId())).isFalse();
        }
    }
}
