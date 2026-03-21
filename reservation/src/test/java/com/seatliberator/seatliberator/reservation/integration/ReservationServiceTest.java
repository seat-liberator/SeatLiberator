package com.seatliberator.seatliberator.reservation;

import com.seatliberator.seatliberator.reservation.application.port.in.command.ReservationCreateCommand;
import com.seatliberator.seatliberator.reservation.application.port.in.command.SeatCreateCommand;
import com.seatliberator.seatliberator.reservation.application.port.out.ReservationStore;
import com.seatliberator.seatliberator.reservation.application.port.out.SeatStore;
import com.seatliberator.seatliberator.reservation.application.service.ReservationService;
import com.seatliberator.seatliberator.reservation.application.service.SeatService;
import com.seatliberator.seatliberator.reservation.domain.Reservation;
import com.seatliberator.seatliberator.reservation.domain.Seat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class ReservationTest {

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
    void Seat_생성_시_true_반환한다() {
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
        assertThat(storedSeat.getRoomId()).isEqualTo(givenRoomId);
        assertThat(storedSeat.getSeatId()).isEqualTo(givenSeatId);
    }

    @Test
    @DisplayName("Reservation 생성 시 true 반환한다.")
    void Reservation_생성_시_true_반환한다() {
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
        boolean result = reservationService.create(reservationCreateCommand);

        Optional<Reservation> optStoredReservation = reservationStore.findByUserId(
                givenUserId
        );

        assertThat(optStoredReservation.isPresent()).isTrue();

        var storedReservation = optStoredReservation.get();

        assertThat(result).isTrue();
        assertThat(storedReservation.getUserId()).isEqualTo(givenUserId);
        assertThat(storedReservation.getRoomId()).isEqualTo(givenRoomId);
        assertThat(storedReservation.getSeatId()).isEqualTo(givenSeatId);
    }
}
