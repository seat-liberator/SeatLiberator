package com.seatliberator.seatliberator.reservation.domain.waitlist;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.time.Instant;

import static com.seatliberator.seatliberator.reservation.domain.waitlist.validator.WaitlistStateValidator.ensureStateIn;
import static com.seatliberator.seatliberator.reservation.domain.waitlist.validator.WaitlistStateValidator.validate;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WaitlistState {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WaitlistStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WaitlistResolution resolution;

    @Column(name = "requested_at", nullable = false, updatable = false)
    private Instant requestedAt;

    @Column(name = "cancelled_at")
    private Instant cancelledAt;

    @Column(name = "expired_at")
    private Instant expiredAt;

    @Column(name = "failed_at")
    private Instant failedAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    private WaitlistState(
            WaitlistStatus status,
            WaitlistResolution resolution,
            Instant requestedAt,
            Instant cancelledAt,
            Instant expiredAt,
            Instant completedAt
    ) {
        this.status = status;
        this.resolution = resolution;
        this.requestedAt = requestedAt;
        this.cancelledAt = cancelledAt;
        this.expiredAt = expiredAt;
        this.completedAt = completedAt;

        validate(this);
    }

    public static WaitlistState requestedAt(
            @NonNull Instant requestedAt
    ) {
        return new WaitlistState(
                WaitlistStatus.ACTIVE,
                WaitlistResolution.PENDING,
                requestedAt,
                null,
                null,
                null
        );
    }

    protected void cancel(Instant at) {
        ensureStateIn(this, WaitlistStatus.ACTIVE);

        this.status = WaitlistStatus.CANCELLED;
        this.cancelledAt = at;

        validate(this);
    }

    protected void expire(Instant at) {
        ensureStateIn(this, WaitlistStatus.ACTIVE);

        this.status = WaitlistStatus.EXPIRED;
        this.expiredAt = at;

        validate(this);
    }

    protected void fail(Instant at) {
        ensureStateIn(this, WaitlistStatus.ACTIVE);

        this.status = WaitlistStatus.FAILED;
        this.failedAt = at;

        validate(this);
    }

    protected void completeAsNotified(Instant at) {
        ensureStateIn(this, WaitlistStatus.ACTIVE);

        this.status = WaitlistStatus.COMPLETED;
        this.resolution = WaitlistResolution.NOTIFIED;
        this.completedAt = at;

        validate(this);
    }

    protected void completeAtClaimed(Instant at) {
        ensureStateIn(this, WaitlistStatus.ACTIVE);

        this.status = WaitlistStatus.COMPLETED;
        this.resolution = WaitlistResolution.CLAIMED;
        this.completedAt = at;

        validate(this);
    }
}