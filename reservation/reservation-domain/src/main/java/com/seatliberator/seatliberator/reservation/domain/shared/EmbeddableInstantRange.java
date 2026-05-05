package com.seatliberator.seatliberator.reservation.domain.shared;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmbeddableInstantRange implements InstantRange {
    @Column(name = "start_at", nullable = false)
    private Instant startAt;

    @Column(name = "end_at", nullable = false)
    private Instant endAt;

    public EmbeddableInstantRange(Instant startAt, Instant endAt) {
        this.startAt = Preconditions.requireNonNull(startAt, "startAt");
        this.endAt = Preconditions.requireNonNull(endAt, "endAt");
        validate(startAt, endAt);
    }

    public static EmbeddableInstantRange of(Instant startAt, Instant endAt) {
        return new EmbeddableInstantRange(startAt, endAt);
    }

    public static EmbeddableInstantRange from(InstantRange range) {
        Preconditions.requireNonNull(range, "range");
        return new EmbeddableInstantRange(range.startAt(), range.endAt());
    }

    @Override
    public Instant startAt() {
        return startAt;
    }

    @Override
    public Instant endAt() {
        return endAt;
    }

    public void updateStartAt(Instant startAt) {
        validate(startAt, endAt);
        this.startAt = startAt;
    }

    public void updateEndAt(Instant endAt) {
        validate(startAt, endAt);
        this.endAt = endAt;
    }

    public void setRange(Instant startAt, Instant endAt) {
        validate(startAt, endAt);
        var range = SimpleInstantRange.of(startAt, endAt);
        apply(range);
    }

    private void apply(InstantRange range) {
        Preconditions.requireNonNull(range, "range");
        this.startAt = range.startAt();
        this.endAt = range.endAt();
    }
}
