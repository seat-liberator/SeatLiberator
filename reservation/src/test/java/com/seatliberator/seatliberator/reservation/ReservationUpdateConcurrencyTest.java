package com.seatliberator.seatliberator.reservation;

import com.seatliberator.seatliberator.reservation.application.port.in.command.ReservationCreateCommand;
import com.seatliberator.seatliberator.reservation.application.port.in.command.ReservationUpdateCommand;
import com.seatliberator.seatliberator.reservation.application.port.in.command.SeatCreateCommand;
import com.seatliberator.seatliberator.reservation.application.port.out.ReservationStore;
import com.seatliberator.seatliberator.reservation.application.service.ReservationService;
import com.seatliberator.seatliberator.reservation.application.service.SeatService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class ReservationUpdateConcurrencyTest {

    private static final Logger log = LoggerFactory.getLogger(ReservationUpdateConcurrencyTest.class);

    @Autowired
    ReservationStore reservationStore;
    @Autowired
    ReservationService reservationService;
    @Autowired
    SeatService seatService;
    @Autowired
    EntityManager em;

    int threadCount;

    @BeforeEach
    void run() {
        this.threadCount = 500;
    }

    @AfterEach
    void cleanUp() {
        em.createQuery("DELETE FROM Reservation").executeUpdate();
        em.createQuery("DELETE FROM Seat").executeUpdate();
    }

    @Test
    @DisplayName("여러 좌석 예약이 동시에 하나의 좌석으로 변경되면 하나만 성공한다")
    void 여러_좌석_예약이_동시에_하나의_좌석으로_변경되면_하나만_성공한다() throws Exception {

        // Given
        var givenRoomId = "room-1";
        var givenTargetSeatId = "seat-1";
        var startTime = Instant.parse("2025-06-01T01:00:00Z");
        var endTime = Instant.parse("2025-06-01T02:00:00Z");

        // target 좌석 생성
        seatService.create(new SeatCreateCommand(givenRoomId, givenTargetSeatId));

        List<String> reservationIds = new ArrayList<>();

        // 각기 다른 좌석 + 예약 생성
        for (int i = 0; i < threadCount; i++) {
            String seatId = "seat-" + (i + 2);
            String userId = "user-" + i;

            seatService.create(new SeatCreateCommand(givenRoomId, seatId));

            var command = new ReservationCreateCommand(
                    userId,
                    givenRoomId,
                    seatId,
                    startTime,
                    endTime

            );

            reservationService.create(command);

            String reservation = reservationStore.findByUserId(command.userId()).orElseThrow().getUserId();

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

                        boolean result = reservationService.update(
                                new ReservationUpdateCommand(
                                        reservationIds.get(idx),   // 각자의 예약
                                        givenRoomId,
                                        givenTargetSeatId,        // 하나의 좌석으로 몰림
                                        startTime,
                                        endTime
                                )
                        );

                        if (result) {
                            log.debug("Thread {} report run command successfully.", idx);
                            success.incrementAndGet();
                        } else {
                            log.debug("Thread {} report exception occurred.", idx);
                            fail.incrementAndGet();
                        }

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
