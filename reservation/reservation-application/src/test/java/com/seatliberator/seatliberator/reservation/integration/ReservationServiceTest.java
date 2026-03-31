package com.seatliberator.seatliberator.reservation.integration;

import com.seatliberator.seatliberator.reservation.book.application.port.in.command.ReservationCreateCommand;
import com.seatliberator.seatliberator.reservation.book.application.port.in.command.SeatCreateCommand;
import com.seatliberator.seatliberator.reservation.book.application.port.out.ReservationStore;
import com.seatliberator.seatliberator.reservation.book.application.port.out.SeatStore;
import com.seatliberator.seatliberator.reservation.book.application.service.ReservationService;
import com.seatliberator.seatliberator.reservation.book.application.service.SeatService;
import com.seatliberator.seatliberator.reservation.book.domain.Reservation;
import com.seatliberator.seatliberator.reservation.book.domain.Seat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@TransactionalReservationIntegrationTest
@DisplayName("Integration: Reservation Service")
public class ReservationServiceTest extends ReservationDatabaseCleanupSupport {

    @Autowired
    SeatService seatService;
    @Autowired
    ReservationService reservationService;
    @Autowired
    SeatStore seatStore;
    @Autowired
    ReservationStore reservationStore;


    @Test
    @DisplayName("Seat 생성 시 true 반환한다.")
    void return_true_when_seat_is_created() {
        var givenRoomId = "room-1";
        var givenSeatId = "seat-1";

        var seatCreateCommand = new SeatCreateCommand(
                givenRoomId,
                givenSeatId
        );

        // Then
        boolean result = seatService.create(seatCreateCommand);

        Optional<Seat> optStoreSeat = seatStore.findByRoomIdAndSeatId(
                givenRoomId,
                givenSeatId
        );

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

        var seatCreateCommand = new SeatCreateCommand(
                givenRoomId,
                givenSeatId
        );

        seatService.create(seatCreateCommand);

        var startTime = Instant.parse("2025-06-01T01:00:00Z");
        var endTime = Instant.parse("2025-06-01T02:00:00Z");

        var reservationCreateCommand = new ReservationCreateCommand(
                givenUserId,
                givenRoomId,
                givenSeatId,
                startTime,
                endTime
        );

        // Then
        var result = reservationService.create(reservationCreateCommand);

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
