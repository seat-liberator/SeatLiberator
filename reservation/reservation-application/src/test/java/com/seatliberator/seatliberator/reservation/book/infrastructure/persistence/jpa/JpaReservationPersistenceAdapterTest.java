package com.seatliberator.seatliberator.reservation.book.infrastructure.persistence.jpa;

import com.seatliberator.seatliberator.reservation.book.application.port.out.ReservationReader;
import com.seatliberator.seatliberator.reservation.book.application.port.out.criteria.ReservationFilter;
import com.seatliberator.seatliberator.reservation.book.application.port.out.criteria.ReservationRangeOverlapCriteria;
import com.seatliberator.seatliberator.reservation.book.infrastructure.persistence.jpa.repository.ReservationRepository;
import com.seatliberator.seatliberator.reservation.domain.ReservationStatus;
import com.seatliberator.seatliberator.reservation.domain.SimpleTimeRange;
import com.seatliberator.seatliberator.reservation.domain.persistence.Reservation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Import;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({
        JpaReservationPersistenceAdapter.class
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@EntityScan(basePackages = "com.seatliberator.seatliberator.reservation")
@DisplayName("Jpa Reservation Persistence Adapter")
public class JpaReservationPersistenceAdapterTest {
    @Autowired
    private ReservationRepository repository;

    @Autowired
    private ReservationReader reservationReader;

    @Test
    @DisplayName("기간이 겹치는 예약만 조회한다")
    void find_all_overlapping_by_user() {
        // given
        var queryRange = SimpleTimeRange.from(
                Instant.parse("2026-04-20T10:00:00Z"),
                Instant.parse("2026-04-20T12:00:00Z")
        );

        var target1 = saveReservation(
                "user-1",
                "room-a",
                "seat-1",
                Instant.parse("2026-04-20T09:30:00Z"),
                Instant.parse("2026-04-20T10:30:00Z"),
                ReservationStatus.RESERVED
        );

        var target2 = saveReservation(
                "user-2",
                "room-a",
                "seat-2",
                Instant.parse("2026-04-20T11:00:00Z"),
                Instant.parse("2026-04-20T13:00:00Z"),
                ReservationStatus.RESERVED
        );

        saveReservation(
                "user-3",
                "room-a",
                "seat-3",
                Instant.parse("2026-04-20T07:00:00Z"),
                Instant.parse("2026-04-20T09:00:00Z"),
                ReservationStatus.RESERVED
        ); // 겹치지 않음

        saveReservation(
                "user-4",
                "room-a",
                "seat-4",
                Instant.parse("2026-04-20T12:00:00Z"),
                Instant.parse("2026-04-20T13:30:00Z"),
                ReservationStatus.RESERVED
        ); // 겹치지 않음

        var criteria = ReservationRangeOverlapCriteria.of(queryRange)
                .withFilter(ReservationFilter.empty());

        // when
        List<Reservation> results = reservationReader.findAllOverlapping(criteria);

        // then
        assertThat(results)
                .extracting(Reservation::getId)
                .containsExactlyInAnyOrder(target1.getId(), target2.getId());
    }

    @Test
    @DisplayName("상태 조건이 있으면 해당 상태만 조회한다")
    void find_all_overlapping_with_status_filter() {
        // given
        var queryRange = SimpleTimeRange.from(
                Instant.parse("2026-04-20T10:00:00Z"),
                Instant.parse("2026-04-20T12:00:00Z")
        );

        var active = saveReservation(
                "user-1",
                "room-a",
                "seat-1",
                Instant.parse("2026-04-20T10:30:00Z"),
                Instant.parse("2026-04-20T11:30:00Z"),
                ReservationStatus.RESERVED
        );

        saveReservation(
                "user-2",
                "room-a",
                "seat-2",
                Instant.parse("2026-04-20T10:30:00Z"),
                Instant.parse("2026-04-20T11:30:00Z"),
                ReservationStatus.CANCELED
        );

        var criteria = ReservationRangeOverlapCriteria.of(queryRange)
                .withFilter(
                        ReservationFilter.empty()
                                .withStatuses(ReservationStatus.RESERVED)
                );

        // when
        List<Reservation> results = reservationReader.findAllOverlapping(criteria);

        // then
        assertThat(results)
                .extracting(Reservation::getId)
                .containsExactlyInAnyOrder(active.getId());
    }

    @Test
    @DisplayName("excludedIds에 포함된 예약은 조회에서 제외한다")
    void exclude_ids() {
        // given
        var queryRange = SimpleTimeRange.from(
                Instant.parse("2026-04-20T10:00:00Z"),
                Instant.parse("2026-04-20T12:00:00Z")
        );

        var include = saveReservation(
                "user-1",
                "room-a",
                "seat-1",
                Instant.parse("2026-04-20T10:30:00Z"),
                Instant.parse("2026-04-20T11:30:00Z"),
                ReservationStatus.RESERVED
        );

        var exclude = saveReservation(
                "user-2",
                "room-a",
                "seat-2",
                Instant.parse("2026-04-20T10:40:00Z"),
                Instant.parse("2026-04-20T11:40:00Z"),
                ReservationStatus.RESERVED
        );

        var criteria = ReservationRangeOverlapCriteria.of(queryRange)
                .withFilter(
                        ReservationFilter.empty()
                                .withExcludeIds(exclude.getId())
                );

        // when
        List<Reservation> results = reservationReader.findAllOverlapping(criteria);

        // then
        assertThat(results)
                .extracting(Reservation::getId)
                .containsExactlyInAnyOrder(include.getId());
    }

    private Reservation saveReservation(
            String userId,
            String roomId,
            String seatId,
            Instant startAt,
            Instant endAt,
            ReservationStatus status
    ) {
        var reservation = Reservation.create(
                userId,
                roomId,
                seatId,
                startAt,
                endAt,
                status
        );

        return repository.saveAndFlush(reservation);
    }
}
