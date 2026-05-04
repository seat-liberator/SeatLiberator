package com.seatliberator.seatliberator.reservation.domain.room;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OperationHours {
    @Column(name = "open_at", nullable = false)
    private LocalTime openAt;

    @Column(name = "close_at", nullable = false)
    private LocalTime closeAt;

    private OperationHours(LocalTime openAt, LocalTime closeAt) {
        this.openAt = Preconditions.requireNonNull(openAt, "openAt");
        this.closeAt = Preconditions.requireNonNull(closeAt, "closeAt");
    }

    public static OperationHours of(LocalTime openAt, LocalTime closeAt) {
        return new OperationHours(openAt, closeAt);
    }

    public void updateOpenAt(LocalTime openAt) {
        this.openAt = Preconditions.requireNonNull(openAt, "openAt");
    }

    public void updateCloseAt(LocalTime closeAt) {
        this.closeAt = Preconditions.requireNonNull(closeAt, "closeAt");
    }
}