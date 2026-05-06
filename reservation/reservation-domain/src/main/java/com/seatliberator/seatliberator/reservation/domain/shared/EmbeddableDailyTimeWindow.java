package com.seatliberator.seatliberator.reservation.domain.shared;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmbeddableDailyTimeWindow implements DailyTimeWindow {
    @Column(name = "start_at", nullable = false)
    private LocalTime startAt;

    @Column(name = "end_at", nullable = false)
    private LocalTime endAt;

    private EmbeddableDailyTimeWindow(LocalTime startAt, LocalTime endAt) {
        this.startAt = Preconditions.requireNonNull(startAt, "startAt");
        this.endAt = Preconditions.requireNonNull(endAt, "endAt");
        validate(startAt, endAt);
    }

    public static EmbeddableDailyTimeWindow of(LocalTime startAt, LocalTime endAt) {
        return new EmbeddableDailyTimeWindow(startAt, endAt);
    }

    public static EmbeddableDailyTimeWindow from(DailyTimeWindow dailyTimeWindow) {
        Preconditions.requireNonNull(dailyTimeWindow, "dailyTimeWindow");
        return of(dailyTimeWindow.startAt(), dailyTimeWindow.endAt());
    }

    @Override
    public LocalTime startAt() {
        return startAt;
    }

    @Override
    public LocalTime endAt() {
        return endAt;
    }

    public void updateStartAt(LocalTime startAt) {
        Preconditions.requireNonNull(startAt, "startAt");
        validate(startAt, endAt);
        this.startAt = startAt;
    }

    public void updateEndAt(LocalTime endAt) {
        Preconditions.requireNonNull(endAt, "endAt");
        validate(startAt, endAt);
        this.endAt = endAt;
    }

    public void apply(DailyTimeWindow dailyTimeWindow) {
        Preconditions.requireNonNull(dailyTimeWindow, "dailyTimeWindow");
        this.startAt = dailyTimeWindow.startAt();
        this.endAt = dailyTimeWindow.endAt();
    }
}