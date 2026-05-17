package com.seatliberator.seatliberator.reservation.domain.waitlist;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

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
        this.status = Preconditions.requireNonNull(status, "status");
        this.resolution = Preconditions.requireNonNull(resolution, "resolution");
        this.requestedAt = Preconditions.requireNonNull(requestedAt, "requestedAt");
        this.cancelledAt = cancelledAt;
        this.expiredAt = expiredAt;
        this.completedAt = completedAt;
    }

    public static WaitlistState requestedAt(Instant requestedAt) {
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
        ensureStateIn(WaitlistStatus.ACTIVE);
        ensureNotBeforeRequestedAt(at);

        this.status = WaitlistStatus.CANCELLED;
        this.cancelledAt = at;
    }

    protected void expire(Instant at) {
        ensureStateIn(WaitlistStatus.ACTIVE);
        ensureNotBeforeRequestedAt(at);

        this.status = WaitlistStatus.EXPIRED;
        this.expiredAt = at;
    }

    protected void fail(Instant at) {
        ensureStateIn(WaitlistStatus.ACTIVE);
        ensureNotBeforeRequestedAt(at);

        this.status = WaitlistStatus.FAILED;
        this.failedAt = at;
    }

    protected void completeAsNotified(Instant at) {
        ensureStateIn(WaitlistStatus.ACTIVE);
        ensureNotBeforeRequestedAt(at);

        this.status = WaitlistStatus.COMPLETED;
        this.resolution = WaitlistResolution.NOTIFIED;
        this.completedAt = at;
    }

    protected void completeAtClaimed(Instant at) {
        ensureStateIn(WaitlistStatus.ACTIVE);
        ensureNotBeforeRequestedAt(at);

        this.status = WaitlistStatus.COMPLETED;
        this.resolution = WaitlistResolution.CLAIMED;
        this.completedAt = at;
    }

    private void ensureStateIn(WaitlistStatus... statuses) {
        for (var status : statuses) if (this.status == status) return;

        switch (this.status) {
            case ACTIVE -> throw new IllegalStateException("이미 활성화된 대기열입니다.");
            case CANCELLED -> throw new IllegalStateException("이미 취소된 대기열입니다.");
            case EXPIRED -> throw new IllegalStateException("이미 만료된 대기열입니다.");
            case COMPLETED -> throw new IllegalStateException("이미 완료된 대기열입니다.");
            case FAILED -> throw new IllegalStateException("이미 실패한 대기열입니다.");
        }
    }

    private void ensureResolutionIn(WaitlistResolution... resolutions) {
        for (var resolution : resolutions) if (this.resolution == resolution) return;

        switch (this.resolution) {
            case PENDING -> throw new IllegalStateException("아직 처리 대기 중인 대기열입니다.");
            case CLAIMED -> throw new IllegalStateException("이미 점유 처리된 대기열입니다.");
            case NOTIFIED -> throw new IllegalStateException("이미 알림 처리된 대기열입니다.");
        }
    }

    private void ensureNotBeforeRequestedAt(Instant at) {
        Preconditions.requireNonNull(at, "at");
        if (at.isBefore(requestedAt)) {
            throw new IllegalStateException("must not be before requestedAt.");
        }
    }
}