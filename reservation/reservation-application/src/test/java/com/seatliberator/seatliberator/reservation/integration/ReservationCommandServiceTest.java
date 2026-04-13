package com.seatliberator.seatliberator.reservation.integration;

import com.seatliberator.seatliberator.reservation.book.application.port.in.command.CreateReservationCommand;
import com.seatliberator.seatliberator.reservation.book.application.port.in.command.CreateSeatCommand;
import com.seatliberator.seatliberator.reservation.book.application.port.out.ReservationStore;
import com.seatliberator.seatliberator.reservation.book.application.port.out.SeatReader;
import com.seatliberator.seatliberator.reservation.book.application.port.out.SeatStore;
import com.seatliberator.seatliberator.reservation.book.application.service.ReservationCommandService;
import com.seatliberator.seatliberator.reservation.book.application.service.SeatCommandService;
import com.seatliberator.seatliberator.reservation.domain.SimpleSeatLocator;
import com.seatliberator.seatliberator.reservation.domain.persistence.Reservation;
import com.seatliberator.seatliberator.reservation.domain.persistence.Seat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("integration")
@TransactionalReservationIntegrationTest
@DisplayName("Integration: Reservation Service")
public class ReservationCommandServiceTest extends ReservationDatabaseCleanupSupport {

    @Autowired
    SeatCommandService seatService;
    @Autowired
    ReservationCommandService reservationCommandService;
    @Autowired
    SeatStore seatStore;
    @Autowired
    SeatReader seatReader;
    @Autowired
    ReservationStore reservationStore;


    @Test
    @DisplayName("Seat 생성 시 true 반환한다.")
    void return_true_when_seat_is_created() {
        var givenRoomId = "room-1";
        var givenSeatId = "seat-1";
        var locator = SimpleSeatLocator.from(givenRoomId, givenSeatId);

        var seatCreateCommand = new CreateSeatCommand(
                givenRoomId,
                givenSeatId
        );

        // Then
        boolean result = seatService.create(seatCreateCommand);

        Optional<Seat> optStoreSeat = seatReader.findByLocator(locator);

        assertThat(optStoreSeat.isPresent()).isTrue();

        var storedSeat = optStoreSeat.get();

        assertThat(result).isTrue();
        assertThat(storedSeat.getLocator().roomId()).isEqualTo(givenRoomId);
        assertThat(storedSeat.getLocator().seatId()).isEqualTo(givenSeatId);
    }

    @Test
    @DisplayName("Reservation 생성 시 예약 정보를 반환한다.")
    void return_entry_when_reservation_is_created() {
        var givenRoomId = "room-1";
        var givenSeatId = "seat-1";
        var givenUserId = "user-1";

        var seatCreateCommand = new CreateSeatCommand(
                givenRoomId,
                givenSeatId
        );

        seatService.create(seatCreateCommand);

        var startTime = Instant.parse("2025-06-01T01:00:00Z");
        var endTime = Instant.parse("2025-06-01T02:00:00Z");

        var reservationCreateCommand = new CreateReservationCommand(
                givenUserId,
                givenRoomId,
                givenSeatId,
                startTime,
                endTime
        );

        // Then
        var result = reservationCommandService.create(reservationCreateCommand);

        Optional<Reservation> optStoredReservation = reservationStore.findByUserId(
                givenUserId
        );

        assertThat(optStoredReservation.isPresent()).isTrue();

        var storedReservation = optStoredReservation.get();

        assertThat(result).isNotNull();
        assertThat(result.actorId()).isEqualTo(givenUserId);
        assertThat(result.roomId()).isEqualTo(givenRoomId);
        assertThat(result.seatId()).isEqualTo(givenSeatId);
        assertThat(storedReservation.getUserId()).isEqualTo(givenUserId);
        assertThat(storedReservation.getLocator().roomId()).isEqualTo(givenRoomId);
        assertThat(storedReservation.getLocator().seatId()).isEqualTo(givenSeatId);
    }
}
