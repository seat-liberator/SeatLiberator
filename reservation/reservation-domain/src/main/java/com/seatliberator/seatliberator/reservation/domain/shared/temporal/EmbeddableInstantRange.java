package com.seatliberator.seatliberator.reservation.domain.shared.temporal;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.Instant;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmbeddableInstantRange implements InstantRange {
    @Column(name = "start_at", nullable = false)
    private Instant startAt;

    @Column(name = "end_at", nullable = false)
    private Instant endAt;

    public EmbeddableInstantRange(Instant startAt, Instant endAt) {
        InstantRange.validate(startAt, endAt);

        this.startAt = startAt;
        this.endAt = endAt;
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

    public void setRange(Instant startAt, Instant endAt) {
        apply(startAt, endAt);
    }

    public void updateStartAt(Instant startAt) {
        apply(startAt, endAt);
    }

    public void updateEndAt(Instant endAt) {
        apply(startAt, endAt);
    }


    public void extendStartAt(Duration extension) {
        Preconditions.requireNonNull(extension, "extension");

        apply(startAt.plus(extension), endAt);
    }

    public void extendEndAt(Duration extension) {
        Preconditions.requireNonNull(extension, "extension");

        apply(startAt, endAt.plus(extension));
    }

    public void adjustOffset(Duration offset) {
        Preconditions.requireNonNull(offset, "offset");

        apply(startAt.plus(offset), endAt.plus(offset));
    }

    private void apply(Instant startAt, Instant endAt) {
        Preconditions.requireNonNull(startAt, "startAt");
        Preconditions.requireNonNull(endAt, "endAt");
        InstantRange.validate(startAt, endAt);

        this.startAt = startAt;
        this.endAt = endAt;
    }
}
