package com.seatliberator.seatliberator.reservation.availability.application.model;

import com.seatliberator.seatliberator.reservation.domain.SeatLocator;
import com.seatliberator.seatliberator.reservation.domain.SimpleSeatLocator;
import com.seatliberator.seatliberator.reservation.domain.persistence.Seat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static com.seatliberator.seatliberator.reservation.domain.fixture.TestSupport.fixedClock;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Application.model: AvailableSeats")
public class AvailableSeatsTest {
    Instant now;

    @BeforeEach
    void run() {
        now = fixedClock.instant();
    }

    @Test
    @DisplayName("예약된 좌석 locator를 제외한 좌석만 남긴다")
    void exclude_reserved_seats() {
        // given
        var seatA = Seat.create("room-1", "A", now);
        var seatB = Seat.create("room-1", "B", now);
        var seatC = Seat.create("room-1", "C", now);

        var reservedLocators = List.<SeatLocator>of(
                SimpleSeatLocator.from("room-1", "A"),
                SimpleSeatLocator.from("room-1", "C")
        );

        // when
        var result = AvailableSeats.from(List.of(seatA, seatB, seatC), reservedLocators);

        assertThat(result.toList())
                .extracting(seat -> seat.getLocator().seatId())
                .containsExactly("B");
    }

    @Test
    @DisplayName("예약된 좌석이 없으면 모든 좌석을 가용 좌석으로 본다")
    void keep_all_seats_when_reserved_locator_is_empty() {
        var seatA = Seat.create("room-1", "A", now);
        var seatB = Seat.create("room-1", "B", now);

        var result = AvailableSeats.from(
                List.of(seatA, seatB),
                List.of()
        );

        assertThat(result.toList())
                .extracting(seat -> seat.getLocator().seatId())
                .containsExactly("A", "B");
    }

    @Test
    @DisplayName("모든 좌석이 예약됐으면 빈 가용 좌석 컬렉션을 반환한다")
    void return_empty_when_all_seats_are_reserved() {
        // given
        var seatA = Seat.create("room-1", "A", now);
        var seatB = Seat.create("room-1", "B", now);

        var reservedLocators = List.<SeatLocator>of(
                SimpleSeatLocator.from("room-1", "A"),
                SimpleSeatLocator.from("room-1", "B")
        );

        // when
        var result = AvailableSeats.from(
                List.of(seatA, seatB),
                reservedLocators
        );

        // then
        assertThat(result.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("예약 locator가 중복되어 있어도 결과는 한 번만 제외된 것처럼 동작한다")
    void handle_duplicate_reserved_locators() {
        // given
        var seatA = Seat.create("room-1", "A", now);
        var seatB = Seat.create("room-1", "B", now);

        var reservedLocators = List.<SeatLocator>of(
                SimpleSeatLocator.from("room-1", "A"),
                SimpleSeatLocator.from("room-1", "A")
        );

        // when
        var result = AvailableSeats.from(
                List.of(seatA, seatB),
                reservedLocators
        );

        // then
        assertThat(result.toList())
                .extracting(seat -> seat.getLocator().seatId())
                .containsExactly("B");
    }
}
