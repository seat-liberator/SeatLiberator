package com.seatliberator.seatliberator.reservation.availability.application.model;

import com.seatliberator.seatliberator.reservation.application.availability.model.AvailableSeats;
import com.seatliberator.seatliberator.reservation.domain.SeatLocator;
import com.seatliberator.seatliberator.reservation.domain.seat.SeatFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static com.seatliberator.seatliberator.reservation.domain.fixture.TestSupport.fixedClock;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Available Seats")
public class AvailableSeatsTest {
    Instant now;

    @BeforeEach
    void run() {
        now = fixedClock.instant();
    }

    @Test
    @DisplayName("점유된 좌석 locator를 제외한 좌석만 남긴다")
    void exclude_occupied_seats() {
        // given
        var seatBuilder = new SeatFixture.Builder().createdAt(now);
        var seatA = seatBuilder.copy().seatId("A").build();
        var seatB = seatBuilder.copy().seatId("B").build();
        var seatC = seatBuilder.copy().seatId("C").build();

        var occupiedLocators = List.<SeatLocator>of(
                seatA.getLocator(),
                seatC.getLocator()
        );

        // when
        var result = AvailableSeats.from(List.of(seatA, seatB, seatC), occupiedLocators);

        assertThat(result.toList())
                .extracting(seat -> seat.getLocator().seatId())
                .containsExactly("B");
    }

    @Test
    @DisplayName("점유된 좌석이 없으면 모든 좌석을 가용 좌석으로 본다")
    void keep_all_seats_when_occupied_locator_is_empty() {
        var seatBuilder = new SeatFixture.Builder().createdAt(now);
        var seatA = seatBuilder.copy().seatId("A").build();
        var seatB = seatBuilder.copy().seatId("B").build();

        var result = AvailableSeats.from(
                List.of(seatA, seatB),
                List.of()
        );

        assertThat(result.toList())
                .extracting(seat -> seat.getLocator().seatId())
                .containsExactly("A", "B");
    }

    @Test
    @DisplayName("모든 좌석이 점유됐으면 빈 가용 좌석 컬렉션을 반환한다")
    void return_empty_when_all_seats_are_occupied() {
        // given
        var seatBuilder = new SeatFixture.Builder().createdAt(now);
        var seatA = seatBuilder.copy().seatId("A").build();
        var seatB = seatBuilder.copy().seatId("B").build();

        var occupiedLocators = List.<SeatLocator>of(
                seatA.getLocator(),
                seatB.getLocator()
        );

        // when
        var result = AvailableSeats.from(
                List.of(seatA, seatB),
                occupiedLocators
        );

        // then
        assertThat(result.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("점유된 locator가 중복되어 있어도 결과는 한 번만 제외된 것처럼 동작한다")
    void handle_duplicate_occupied_locators() {
        // given
        var seatBuilder = new SeatFixture.Builder().createdAt(now);
        var seatA = seatBuilder.copy().seatId("A").build();
        var seatB = seatBuilder.copy().seatId("B").build();

        var occupiedLocators = List.<SeatLocator>of(
                seatA.getLocator(),
                seatA.getLocator()
        );

        // when
        var result = AvailableSeats.from(
                List.of(seatA, seatB),
                occupiedLocators
        );

        // then
        assertThat(result.toList())
                .extracting(seat -> seat.getLocator().seatId())
                .containsExactly("B");
    }
}
