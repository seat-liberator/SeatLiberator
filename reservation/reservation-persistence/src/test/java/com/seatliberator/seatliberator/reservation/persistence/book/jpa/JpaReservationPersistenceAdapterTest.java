package com.seatliberator.seatliberator.reservation.persistence.book.jpa;

import com.seatliberator.seatliberator.reservation.application.reservation.port.out.ReservationReader;
import com.seatliberator.seatliberator.reservation.application.reservation.port.out.ReservationStore;
import com.seatliberator.seatliberator.reservation.application.reservation.port.out.filter.ReservationFilter;
import com.seatliberator.seatliberator.reservation.domain.reservation.Reservation;
import com.seatliberator.seatliberator.reservation.domain.reservation.ReservationStatus;
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

    private void assertSameReservation(Reservation actual, Reservation expected) {
        assertThat(actual.getId()).isEqualTo(expected.getId());
        assertThat(actual.getUserId()).isEqualTo(expected.getUserId());
        assertThat(actual.getState().getStatus()).isEqualTo(expected.getState().getStatus());
        assertThat(actual.getState().getReservedAt()).isEqualTo(expected.getState().getReservedAt());
        assertThat(actual.getState().getUsedAt()).isEqualTo(expected.getState().getUsedAt());
        assertThat(actual.getState().getCancelledAt()).isEqualTo(expected.getState().getCancelledAt());
        assertThat(actual.getState().getExpiredAt()).isEqualTo(expected.getState().getExpiredAt());
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
        @DisplayName("findByUserId는 사용자 Id에 해당하는 예약 목록을 반환한다")
        void should_find_reservations_by_user_id() {
            var reservation = saveReservation();
            var otherReservation = saveReservation(reservation(USER_ID, ReservationStatus.USED));
            saveReservation(reservation(OTHER_USER_ID));
            flushAndClear();

            var actual = reader.findByUserId(USER_ID);

            assertThat(actual)
                    .hasSize(2)
                    .anySatisfy(found -> assertSameReservation(found, reservation))
                    .anySatisfy(found -> assertSameReservation(found, otherReservation));
        }

        @Test
        @DisplayName("findByFilter는 빈 필터이면 저장된 모든 예약을 반환한다")
        void should_find_all_reservations_when_filter_empty() {
            var reservation = saveReservation();
            var otherReservation = saveReservation(reservation(OTHER_USER_ID));
            flushAndClear();

            var actual = reader.findByFilter(ReservationFilter.empty());

            assertThat(actual)
                    .hasSize(2)
                    .anySatisfy(found -> assertSameReservation(found, reservation))
                    .anySatisfy(found -> assertSameReservation(found, otherReservation));
        }

        @Test
        @DisplayName("findByFilter는 사용자 Id와 상태가 일치하는 예약 목록을 반환한다")
        void should_find_reservations_by_user_id_and_status_filter() {
            var reservation = saveReservation();
            saveReservation(reservation(USER_ID, ReservationStatus.USED));
            saveReservation(reservation(OTHER_USER_ID, ReservationStatus.RESERVED));
            flushAndClear();

            var filter = ReservationFilter.empty()
                    .userId(USER_ID)
                    .status(ReservationStatus.RESERVED);
            var actual = reader.findByFilter(filter);

            assertThat(actual)
                    .hasSize(1)
                    .anySatisfy(found -> assertSameReservation(found, reservation));
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
