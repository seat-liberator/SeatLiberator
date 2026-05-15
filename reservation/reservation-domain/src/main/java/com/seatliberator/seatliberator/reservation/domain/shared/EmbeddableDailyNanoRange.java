package com.seatliberator.seatliberator.reservation.domain.shared;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.Duration;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmbeddableDailyNanoRange implements DailyNanoRange {
    @Column(name = "start_nano_of_day", nullable = false)
    private Long startNanoOfDay;

    @Column(name = "end_nano_of_day", nullable = false)
    private Long endNanoOfDay;

    private EmbeddableDailyNanoRange(long startNanoOfDay, long endNanoOfDay) {
        DailyNanoRange.validate(startNanoOfDay, endNanoOfDay);

        this.startNanoOfDay = startNanoOfDay;
        this.endNanoOfDay = endNanoOfDay;
    }

    public static EmbeddableDailyNanoRange of(long startNanoOfDay, long endNanoOfDay) {
        return new EmbeddableDailyNanoRange(startNanoOfDay, endNanoOfDay);
    }

    public static EmbeddableDailyNanoRange of(long startNanoOfDay, Duration duration) {
        Preconditions.requirePositive(duration, "duration");

        var endNanoOfDay = startNanoOfDay + duration.toNanos();
        return of(startNanoOfDay, endNanoOfDay);
    }

    public static EmbeddableDailyNanoRange from(DailyNanoRange range) {
        Preconditions.requireNonNull(range, "range");

        return of(range.startNanoOfDay(), range.endNanoOfDay());
    }

    @Override
    public long startNanoOfDay() {
        return startNanoOfDay;
    }

    @Override
    public long endNanoOfDay() {
        return endNanoOfDay;
    }

    public void updateStartNanoOfDay(long startNanoOfDay) {
        apply(startNanoOfDay, endNanoOfDay);
    }

    public void updateEndNanoOfDay(long endNanoOfDay) {
        apply(startNanoOfDay, endNanoOfDay);
    }

    public void extendStartNanoOfDay(Duration extension) {
        Preconditions.requireNonNull(extension, "extension");

        var newStartNanoOfDay = startNanoOfDay + extension.toNanos();
        apply(newStartNanoOfDay, endNanoOfDay);
    }

    public void extendEndNanoOfDay(Duration extension) {
        Preconditions.requireNonNull(extension, "extension");

        var newEndNanoOfDay = endNanoOfDay + extension.toNanos();
        apply(startNanoOfDay, newEndNanoOfDay);
    }

    public void adjustOffset(Duration offset) {
        Preconditions.requireNonNull(offset, "offset");

        var offsetNanos = offset.toNanos();
        apply(startNanoOfDay + offsetNanos, endNanoOfDay + offsetNanos);
    }

    public void apply(DailyNanoRange range) {
        Preconditions.requireNonNull(range, "range");
        apply(range.startNanoOfDay(), range.endNanoOfDay());
    }

    private void apply(long startNanoOfDay, long endNanoOfDay) {
        DailyNanoRange.validate(startNanoOfDay, endNanoOfDay);

        this.startNanoOfDay = startNanoOfDay;
        this.endNanoOfDay = endNanoOfDay;
    }
}
