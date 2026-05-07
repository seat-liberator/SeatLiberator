package com.seatliberator.seatliberator.reservation.domain.shared;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalTime;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmbeddableDailyTimeSegment implements DailyTimeSegment {
    @Column(name = "start_nano_of_day", nullable = false)
    private Long startNanoOfDay;

    @Column(name = "end_nano_of_day", nullable = false)
    private Long endNanoOfDay;

    private EmbeddableDailyTimeSegment(Long startNanoOfDay, Long endNanoOfDay) {
        Preconditions.requireNonNull(startNanoOfDay, "startNanoOfDay");
        Preconditions.requireNonNull(endNanoOfDay, "endNanoOfDay");
        validate(startNanoOfDay, endNanoOfDay);
        this.startNanoOfDay = startNanoOfDay;
        this.endNanoOfDay = endNanoOfDay;
    }

    public static EmbeddableDailyTimeSegment of(Long startNanoOfDay, Long endNanoOfDay) {
        return new EmbeddableDailyTimeSegment(startNanoOfDay, endNanoOfDay);
    }

    public static EmbeddableDailyTimeSegment of(LocalTime startAt, Duration duration) {
        Preconditions.requireNonNull(startAt, "startAt");
        Preconditions.requireNonNull(duration, "duration");
        var segment = of(startAt.toNanoOfDay(), startAt.toNanoOfDay() + duration.toNanos());
        segment.validateDuration(duration);
        return segment;
    }

    public static EmbeddableDailyTimeSegment of(LocalTime startAt, LocalTime endAt) {
        Preconditions.requireNonNull(startAt, "startAt");
        Preconditions.requireNonNull(endAt, "endAt");
        return of(startAt, durationBetween(startAt, endAt));
    }

    public static EmbeddableDailyTimeSegment from(DailyTimeSegment segment) {
        Preconditions.requireNonNull(segment, "segment");
        return of(segment.startNanoOfDay(), segment.endNanoOfDay());
    }

    private static Duration durationBetween(LocalTime startAt, LocalTime endAt) {
        var startNanoOfDay = startAt.toNanoOfDay();
        var endNanoOfDay = endAt.toNanoOfDay();

        if (startNanoOfDay >= endNanoOfDay) throw new IllegalArgumentException("startAt must be before endAt.");

        return Duration.ofNanos(endNanoOfDay - startNanoOfDay);
    }

    @Override
    public long startNanoOfDay() {
        return startNanoOfDay;
    }

    @Override
    public long endNanoOfDay() {
        return endNanoOfDay;
    }

    public void updateStartNanoOfDay(Long startNanoOfDay) {
        Preconditions.requireNonNull(startNanoOfDay, "startNanoOfDay");
        validate(startNanoOfDay, endNanoOfDay);
        this.startNanoOfDay = startNanoOfDay;
    }

    public void updateEndNanoOfDay(Long endNanoOfDay) {
        Preconditions.requireNonNull(endNanoOfDay, "endNanoOfDay");
        validate(startNanoOfDay, endNanoOfDay);
        this.endNanoOfDay = endNanoOfDay;
    }

    public void apply(DailyTimeSegment segment) {
        Preconditions.requireNonNull(segment, "segment");
        validate(segment.startNanoOfDay(), segment.endNanoOfDay());
        this.startNanoOfDay = segment.startNanoOfDay();
        this.endNanoOfDay = segment.endNanoOfDay();
    }

    public void updateStartAt(LocalTime startAt) {
        Preconditions.requireNonNull(startAt, "startAt");
        updateStartNanoOfDay(startAt.toNanoOfDay());
    }

    public void updateDuration(Duration duration) {
        Preconditions.requireNonNull(duration, "duration");
        validateDuration(duration);
        var newEndNanoOfDay = startNanoOfDay + duration.toNanos();
        validate(startNanoOfDay, newEndNanoOfDay);
        this.endNanoOfDay = newEndNanoOfDay;
    }
}
