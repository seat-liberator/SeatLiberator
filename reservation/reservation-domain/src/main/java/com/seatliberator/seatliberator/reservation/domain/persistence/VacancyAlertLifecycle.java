package com.seatliberator.seatliberator.reservation.domain.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.time.Instant;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class VacancyAlertLifecycle {
    @Column(name = "requested_at", nullable = false, updatable = false)
    private Instant requestedAt;

    @Column(name = "cancelled_at")
    private Instant cancelledAt;

    @Column(name = "expired_at")
    private Instant expiredAt;

    @Column(name = "fulfilled_at")
    private Instant fulfilledAt;

    public static VacancyAlertLifecycle requestedAt(
            @NonNull Instant requestedAt
    ) {
        return new VacancyAlertLifecycle(requestedAt, null, null, null);
    }

    protected void cancel(Instant cancelledAt) {
        this.cancelledAt = cancelledAt;
    }

    protected void expire(Instant expiredAt) {
        this.expiredAt = expiredAt;
    }

    protected void fulfill(Instant fulfilledAt) {
        this.fulfilledAt = fulfilledAt;
    }
}
