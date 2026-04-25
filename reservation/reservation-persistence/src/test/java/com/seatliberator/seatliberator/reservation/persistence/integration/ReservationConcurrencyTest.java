package com.seatliberator.seatliberator.reservation.persistence.integration;

import com.seatliberator.seatliberator.reservation.application.booking.port.in.command.CreateReservationCommand;
import com.seatliberator.seatliberator.reservation.application.booking.service.ReservationCommandService;
import com.seatliberator.seatliberator.reservation.application.room.port.in.CreateRoomUseCase;
import com.seatliberator.seatliberator.reservation.application.room.port.in.command.CreateRoomCommand;
import com.seatliberator.seatliberator.reservation.application.room.port.in.command.CreateSeatCommand;
import com.seatliberator.seatliberator.reservation.application.room.service.SeatCommandService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("integration")
@ReservationPersistenceIntegrationTest
@DisplayName("Integration Reservation Concurrency")
public class ReservationConcurrencyTest extends ReservationDatabaseCleanupSupport {

    private static final Logger log = LoggerFactory.getLogger(ReservationConcurrencyTest.class);

    @Autowired
    CreateRoomUseCase createRoomUseCase;
    @Autowired
    ReservationCommandService reservationCommandService;
    @Autowired
    SeatCommandService seatService;
    int threadCount;

    @BeforeEach
    void run() {
        this.threadCount = 500;
    }

    @Test
    @DisplayName("하나의 좌석에 동시에 여러 명이 같은 시간에 예약해도 한 명만 성공하고 나머지는 실패한다.")
    void allow_only_one_success_when_multiple_users_reserve_same_seat_concurrently() throws Exception {
        // Given

        var givenRoomId = "room-1";
        var givenSeatId = "seat-1";
        var startTime = Instant.parse("2025-06-01T01:00:00Z");
        var endTime = Instant.parse("2025-06-01T02:00:00Z");

        createRoomUseCase.create(new CreateRoomCommand(givenRoomId));

        var seatCreateCommand = new CreateSeatCommand(
                givenRoomId,
                givenSeatId
        );

        seatService.create(seatCreateCommand);

        // Then
        var ready = new CountDownLatch(threadCount);
        var start = new CountDownLatch(1);
        var done = new CountDownLatch(threadCount);

        var success = new AtomicInteger();
        var fail = new AtomicInteger();

        var threadExecutor = Executors.newFixedThreadPool(threadCount);

        try {
            for (int i = 0; i < threadCount; i++) {
                var threadId = i;

                threadExecutor.submit(() -> {
                    ready.countDown();

                    try {
                        start.await();
                        var userId = "user-" + threadId;

                        var reservationCommand = new CreateReservationCommand(
                                userId,
                                givenRoomId,
                                givenSeatId,
                                startTime,
                                endTime
                        );

                        reservationCommandService.create(reservationCommand);
                        log.debug("Thread {} report run command successfully.", threadId);
                        success.incrementAndGet();
                    } catch (Exception e) {
                        fail.incrementAndGet();
                    } finally {
                        done.countDown();
                    }
                });
            }

            ready.await();
            start.countDown();
            done.await();
        } finally {
            threadExecutor.shutdown();
            threadExecutor.awaitTermination(5, TimeUnit.SECONDS);
        }

        assertThat(success.get())
                .as("예약 성공한 사람 수")
                .isEqualTo(1);
        assertThat(fail.get())
                .as("예약 실패한 사람 수")
                .isEqualTo(threadCount - 1);
    }
}
