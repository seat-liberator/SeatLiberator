package com.seatliberator.seatliberator.reservation.domain.reservation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.seatliberator.seatliberator.kernel.test.assertion.DomainAssertions.assertThatDomainThrownBy;
import static com.seatliberator.seatliberator.reservation.domain.reservation.SeatOccupancyFixture.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@DisplayName("SeatOccupancy 도메인 테스트")
public class SeatOccupancyTest {

    @Nested
    @DisplayName("생성 테스트")
    class CreationTest {
        static Stream<Arguments> nullArgumentCases() {
            return Stream.of(
                    arguments("seatTimeSlot = null", (Supplier<SeatOccupancy>) () -> SeatOccupancy.of(null, RESERVATION, OCCUPANCY_DATE, CREATED_AT), "seatTimeSlot"),
                    arguments("reservation", (Supplier<SeatOccupancy>) () -> SeatOccupancy.of(SEAT_TIME_SLOT, null, OCCUPANCY_DATE, CREATED_AT), "reservation"),
                    arguments("occupancyDate = null", (Supplier<SeatOccupancy>) () -> SeatOccupancy.of(SEAT_TIME_SLOT, RESERVATION, null, CREATED_AT), "occupancyDate"),
                    arguments("createdAt = null", (Supplier<SeatOccupancy>) () -> SeatOccupancy.of(SEAT_TIME_SLOT, RESERVATION, OCCUPANCY_DATE, null), "createdAt")
            );
        }

        @ParameterizedTest(name = "{0}")
        @MethodSource("nullArgumentCases")
        @DisplayName("인자가 null이면 예외")
        void throw_exception_when_required_argument_is_null(
                String displayName,
                Supplier<SeatOccupancy> supplier,
                String fieldName
        ) {
            assertThatDomainThrownBy(supplier::get)
                    .hasNonNullMessageFor(fieldName);
        }
    }
}
