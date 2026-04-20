package com.seatliberator.seatliberator.reservation.availability.application.model;

import com.seatliberator.seatliberator.reservation.domain.SeatLocator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.seatliberator.seatliberator.reservation.domain.fixture.SeatLocatorFixture.createLocator;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Seat Reservation Status Classifier")
public class SeatReservationStatusClassifierTest {
    @Test
    @DisplayName("좌석 locator가 null이면 예외")
    void throw_exception_when_seat_locators_is_null() {
        assertThatThrownBy(() -> SeatReservationStatusClassifier.from(null, List.of()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("seatLocators must not be null.");
    }

    @Test
    @DisplayName("점유 locator가 null이면 예외")
    void throw_exception_when_reserved_locators_is_null() {
        assertThatThrownBy(() -> SeatReservationStatusClassifier.from(List.of(), null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("occupiedLocators must not be null.");
    }

    @Test
    @DisplayName("이미 점유된 locator는 RESERVED, 나머지는 AVAILABLE이다")
    void classified_locator() {
        var locatorA = createLocator("room-1", "A");
        var locatorB = createLocator("room-1", "B");
        var locatorC = createLocator("room-1", "C");

        var seatLocators = List.of(locatorA, locatorB, locatorC);

        var occupiedLocators = seatLocators.stream()
                .filter(locator -> !locator.seatId().equals("B"))
                .toList();

        var result = SeatReservationStatusClassifier.from(seatLocators, occupiedLocators);

        assertThat(result.toMap())
                .hasSize(3)
                .containsEntry(locatorA.key(), SeatReservationStatus.OCCUPIED)
                .containsEntry(locatorB.key(), SeatReservationStatus.AVAILABLE)
                .containsEntry(locatorC.key(), SeatReservationStatus.OCCUPIED);
    }

    @Test
    @DisplayName("점유된 locator가 비어있으면 모든 좌석이 AVAILABLE이다")
    void all_available_when_empty_reserved_locator() {
        var locatorA = createLocator("room-1", "A");
        var locatorB = createLocator("room-1", "B");
        var locatorC = createLocator("room-1", "C");

        var seatLocators = List.of(locatorA, locatorB, locatorC);

        var occupiedLocators = List.<SeatLocator>of();

        var result = SeatReservationStatusClassifier.from(seatLocators, occupiedLocators);

        assertThat(result.toMap())
                .hasSize(seatLocators.size());

        assertThat(result.toMap().values())
                .containsOnly(SeatReservationStatus.AVAILABLE);
    }

    @Test
    @DisplayName("좌석 locator와 예약 locator가 모두 비어있으면 빈 결과")
    void empty_when_no_seats() {
        var result = SeatReservationStatusClassifier.from(List.of(), List.of());

        assertThat(result.toMap()).isEmpty();
    }

    @Test
    @DisplayName("좌석 locator에 없는 예약 locator가 있으면 무시")
    void throw_exception_when_reserved_locator_is_not_subset_of_seat_locator() {
        var locatorA = createLocator("room-1", "A");
        var locatorB = createLocator("room-1", "B");
        var locatorC = createLocator("room-1", "C");

        var seatLocators = List.of(locatorA, locatorB);

        var occupiedLocators = List.of(locatorB, locatorC);

        var result = SeatReservationStatusClassifier.from(seatLocators, occupiedLocators);

        assertThat(result.toMap())
                .hasSize(seatLocators.size())
                .containsEntry(locatorA.key(), SeatReservationStatus.AVAILABLE)
                .containsEntry(locatorB.key(), SeatReservationStatus.OCCUPIED);
    }
}
