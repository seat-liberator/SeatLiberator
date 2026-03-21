package com.seatliberator.seatliberator.reservation.integration;

import com.seatliberator.seatliberator.reservation.application.port.in.command.ReservationCreateCommand;
import com.seatliberator.seatliberator.reservation.application.port.in.command.SeatCreateCommand;
import com.seatliberator.seatliberator.reservation.application.service.ReservationService;
import com.seatliberator.seatliberator.reservation.application.service.SeatService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@ActiveProfiles("test")
//@Testcontainers
public class ReservationConcurrencyTest {

    private static final Logger log = LoggerFactory.getLogger(ReservationConcurrencyTest.class);

//    private static final PostgreSQLContainer<?> postgres;

//    static {
//        postgres = new PostgreSQLContainer<>("postgres:18-alpine")
//                .withDatabaseName("reservation_test")
//                .withUsername("test_user")
//                .withPassword("test_password");
//        postgres.start();
//    }

    @Autowired
    ReservationService reservationService;
    @Autowired
    SeatService seatService;
    int threadCount;

//    @DynamicPropertySource
//    static void setProperties(DynamicPropertyRegistry registry) {
//        registry.add("spring.datasource.url", postgres::getJdbcUrl);
//        registry.add("spring.datasource.username", postgres::getUsername);
//        registry.add("spring.datasource.password", postgres::getPassword);
//    }

    @BeforeEach
    void run() {
        this.threadCount = 500;
    }

    @Test
    @DisplayName("하나의 좌석에 동시에 여러 명이 같은 시간에 예약해도 한 명만 성공하고 나머지는 실패한다.")
    void 하나의_좌석에_동시에_여러_명이_같은_시간에_예약해도_한_명만_성공하고_나머지는_실패한다() throws Exception {
        // Given

        var givenRoomId = "room-1";
        var givenSeatId = "seat-1";
        var givenUserId = "user-1";
        var startTime = Instant.parse("2025-06-01T01:00:00Z");
        var endTime = Instant.parse("2025-06-01T02:00:00Z");

        var seatCreateCommand = new SeatCreateCommand(
                givenRoomId,
                givenSeatId
        );

        seatService.create(seatCreateCommand);

        var reservationCommand = new ReservationCreateCommand(
                givenUserId,
                givenRoomId,
                givenSeatId,
                startTime,
                endTime
        );

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

                        var isSuccess = reservationService.create(reservationCommand);
                        if (isSuccess) {
                            log.debug("Thread {} report run command successfully.", threadId);
                            success.incrementAndGet();
                        } else {
                            log.debug("Thread {} report exception occurred.", threadId);
                            fail.incrementAndGet();
                        }
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
