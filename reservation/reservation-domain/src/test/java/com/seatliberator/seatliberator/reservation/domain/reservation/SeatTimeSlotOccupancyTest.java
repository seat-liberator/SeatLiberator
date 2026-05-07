package com.seatliberator.seatliberator.reservation.domain.reservation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.seatliberator.seatliberator.kernel.test.assertion.DomainAssertions.assertThatDomainThrownBy;
import static com.seatliberator.seatliberator.reservation.domain.reservation.ReservationFixture.createReservation;
import static com.seatliberator.seatliberator.reservation.domain.reservation.SeatTimeSlotOccupancyFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@DisplayName("SeatTimeSlotOccupancy 도메인 테스트")
public class SeatTimeSlotOccupancyTest {

    @Nested
    @DisplayName("생성 테스트")
    class CreationTest {
        static Stream<Arguments> nullArgumentCases() {
            return Stream.of(
                    arguments("seatTimeSlot = null", (Supplier<SeatTimeSlotOccupancy>) () -> SeatTimeSlotOccupancy.of(null, RESERVATION, OCCUPANCY_DATE, CREATED_AT), "seatTimeSlot"),
                    arguments("reservation", (Supplier<SeatTimeSlotOccupancy>) () -> SeatTimeSlotOccupancy.of(SEAT_TIME_SLOT, null, OCCUPANCY_DATE, CREATED_AT), "reservation"),
                    arguments("occupancyDate = null", (Supplier<SeatTimeSlotOccupancy>) () -> SeatTimeSlotOccupancy.of(SEAT_TIME_SLOT, RESERVATION, null, CREATED_AT), "occupancyDate"),
                    arguments("createdAt = null", (Supplier<SeatTimeSlotOccupancy>) () -> SeatTimeSlotOccupancy.of(SEAT_TIME_SLOT, RESERVATION, OCCUPANCY_DATE, null), "createdAt")
            );
        }

        @ParameterizedTest(name = "{0}")
        @MethodSource("nullArgumentCases")
        @DisplayName("인자가 null이면 예외")
        void throw_exception_when_required_argument_is_null(
                String displayName,
                Supplier<SeatTimeSlotOccupancy> supplier,
                String fieldName
        ) {
            assertThatDomainThrownBy(supplier::get)
                    .hasNonNullMessageFor(fieldName);
        }

        @Test
        @DisplayName("생성한 점유 슬롯은 예약에서도 조회할 수 있다")
        void add_occupancy_to_reservation_when_created() {
            var reservation = createReservation();

            var occupancy = SeatTimeSlotOccupancy.of(SEAT_TIME_SLOT, reservation, OCCUPANCY_DATE, CREATED_AT);

            assertThat(reservation.getSeatTimeSlotOccupancies()).containsExactly(occupancy);
        }
    }
}
