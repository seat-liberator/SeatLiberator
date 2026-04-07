package com.seatliberator.seatliberator.reservation.domain.fixture;

import com.seatliberator.seatliberator.reservation.domain.SeatLocator;
import com.seatliberator.seatliberator.reservation.domain.SimpleSeatLocator;
import com.seatliberator.seatliberator.reservation.domain.SimpleTimeRange;
import com.seatliberator.seatliberator.reservation.domain.TimeRange;
import com.seatliberator.seatliberator.reservation.domain.persistence.VacancyAlertRequest;

import java.time.Duration;
import java.time.Instant;

import static com.seatliberator.seatliberator.reservation.domain.fixture.TestSupport.fixedClock;

public class VacancyAlertRequestFixture {
    // TODO: Reservation Test Fixture의 Test Support로 빼야함
    public static final String INITIAL_USER_ID = "user-1";
    public static final String INITIAL_ROOM_ID = "room-1";
    public static final String INITIAL_SEAT_ID = "seat-1";
    public static final Duration INITIAL_DURATION = Duration.ofMinutes(30);

    public static VacancyAlertRequest createAlert() {
        return createAlert(fixedClock.instant());
    }

    public static VacancyAlertRequest createAlert(Instant requestedAt) {
        return VacancyAlertRequest.create(
                INITIAL_USER_ID,
                createLocator(),
                createRange(),
                requestedAt
        );
    }

    // TODO: TimeRange Test Fixture Support로 빼야함
    private static TimeRange createRange() {
        var startAt = fixedClock.instant();
        var endAt = startAt.plus(INITIAL_DURATION);
        return createRange(startAt, endAt);
    }

    // TODO: TimeRange Test Fixture Support로 빼야함
    private static TimeRange createRange(Instant startAt) {
        var endAt = startAt.plus(INITIAL_DURATION);
        return createRange(startAt, endAt);
    }

    // TODO: TimeRange Test Fixture Support로 빼야함
    private static TimeRange createRange(Instant startAt, Instant endAt) {
        return SimpleTimeRange.from(startAt, endAt);
    }

    // TODO: SeatLocator Test Fixture Support로 빼야함
    private static SeatLocator createLocator() {
        return createLocator(INITIAL_ROOM_ID, INITIAL_SEAT_ID);
    }

    // TODO: SeatLocator Test Fixture Support로 빼야함
    private static SeatLocator createLocator(String roomId, String seatId) {
        return SimpleSeatLocator.from(roomId, seatId);
    }
}
