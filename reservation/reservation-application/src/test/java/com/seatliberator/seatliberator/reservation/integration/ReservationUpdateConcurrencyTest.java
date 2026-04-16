package com.seatliberator.seatliberator.reservation.integration;

import com.seatliberator.seatliberator.reservation.book.application.port.in.command.CreateReservationCommand;
import com.seatliberator.seatliberator.reservation.book.application.port.in.command.UpdateReservationCommand;
import com.seatliberator.seatliberator.reservation.book.application.port.out.ReservationReader;
import com.seatliberator.seatliberator.reservation.book.application.port.out.ReservationStore;
import com.seatliberator.seatliberator.reservation.book.application.service.ReservationCommandService;
import com.seatliberator.seatliberator.reservation.seat.application.port.in.command.CreateSeatCommand;
import com.seatliberator.seatliberator.reservation.seat.application.service.SeatCommandService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("integration")
@ReservationIntegrationTest
@DisplayName("Integration: Reservation Update Concurrency")
public class ReservationUpdateConcurrencyTest extends ReservationDatabaseCleanupSupport {

    private static final Logger log = LoggerFactory.getLogger(ReservationUpdateConcurrencyTest.class);

    @Autowired
    ReservationStore reservationStore;
    @Autowired
    ReservationReader reservationReader;
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
    @DisplayName("여러 좌석 예약이 동시에 하나의 좌석으로 변경되면 하나만 성공한다")
    void allow_only_one_success_when_multiple_reservations_update_to_same_seat_concurrently() throws Exception {

        // Given
        var givenRoomId = "room-1";
        var givenTargetSeatId = "seat-1";
        var startTime = Instant.parse("2025-06-01T01:00:00Z");
        var endTime = Instant.parse("2025-06-01T02:00:00Z");

        // target 좌석 생성
        seatService.create(new CreateSeatCommand(givenRoomId, givenTargetSeatId));

        List<String> reservationIds = new ArrayList<>();

        // 각기 다른 좌석 + 예약 생성
        for (int i = 0; i < threadCount; i++) {
            String seatId = "seat-" + (i + 2);
            String userId = "user-" + i;

            seatService.create(new CreateSeatCommand(givenRoomId, seatId));

            var command = new CreateReservationCommand(
                    userId,
                    givenRoomId,
                    seatId,
                    startTime,
                    endTime
            );

            reservationCommandService.create(command);

            String reservation = reservationReader.findByUserId(userId).orElseThrow().getUserId();

            reservationIds.add(reservation);
        }

        // Then (동시 실행)
        var ready = new CountDownLatch(threadCount);
        var start = new CountDownLatch(1);
        var done = new CountDownLatch(threadCount);

        var success = new AtomicInteger();
        var fail = new AtomicInteger();

        var threadExecutor = Executors.newFixedThreadPool(threadCount);

        try {
            for (int i = 0; i < threadCount; i++) {
                int idx = i;

                threadExecutor.submit(() -> {
                    ready.countDown();

                    try {
                        start.await();

                        reservationCommandService.update(
                                new UpdateReservationCommand(
                                        reservationIds.get(idx),
                                        givenRoomId,
                                        givenTargetSeatId,        // 하나의 좌석으로 몰림
                                        startTime,
                                        endTime
                                )
                        );

                        log.debug("Thread {} report run command successfully.", idx);
                        success.incrementAndGet();
                    } catch (Exception e) {
                        fail.incrementAndGet();
                    } finally {
                        done.countDown();
                    }
                });
            }

            ready.await();     // 모든 스레드 준비
            start.countDown(); // 동시에 시작
            done.await();      // 종료 대기

        } finally {
            threadExecutor.shutdown();
            threadExecutor.awaitTermination(5, TimeUnit.SECONDS);
        }

        assertThat(success.get())
                .as("예약 수정 성공한 사람 수")
                .isEqualTo(1);
        assertThat(fail.get())
                .as("예약 수정 실패한 사람 수")
                .isEqualTo(threadCount - 1);

    }

}
