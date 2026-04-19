package com.seatliberator.seatliberator.reservation.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.NullUnmarked;

import java.time.Instant;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@NullUnmarked
public class EmbeddableTimeRange implements TimeRange {
    @Column(name = "start_at", nullable = false)
    private Instant startAt;

    @Column(name = "end_at", nullable = false)
    private Instant endAt;

    public EmbeddableTimeRange(Instant startAt, Instant endAt) {
        if (startAt == null) {
            throw new IllegalArgumentException("startAt must not be null.");
        }
        if (endAt == null) {
            throw new IllegalArgumentException("endAt must not be null.");
        }
        if (!startAt.isBefore(endAt)) {
            throw new IllegalArgumentException("startAt must be before endAt");
        }
        this.startAt = startAt;
        this.endAt = endAt;
    }

    public static EmbeddableTimeRange from(Instant startAt, Instant endAt) {
        return new EmbeddableTimeRange(startAt, endAt);
    }

    public static EmbeddableTimeRange of(TimeRange range) {
        return new EmbeddableTimeRange(range.startAt(), range.endAt());
    }

    @Override
    public Instant startAt() {
        return startAt;
    }

    @Override
    public Instant endAt() {
        return endAt;
    }

    public void setRange(Instant startAt, Instant endAt) {
        var range = SimpleTimeRange.of(startAt, endAt);
        apply(range);
    }

    private void apply(TimeRange range) {
        this.startAt = range.startAt();
        this.endAt = range.endAt();
    }
}
