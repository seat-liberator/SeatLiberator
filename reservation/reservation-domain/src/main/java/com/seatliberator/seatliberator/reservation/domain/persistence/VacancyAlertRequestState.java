package com.seatliberator.seatliberator.reservation.domain.persistence;

import com.seatliberator.seatliberator.reservation.domain.VacancyAlertRequestResolution;
import com.seatliberator.seatliberator.reservation.domain.VacancyAlertRequestStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.time.Instant;

import static com.seatliberator.seatliberator.reservation.domain.validator.VacancyAlertRequestStateValidator.ensureStateIn;
import static com.seatliberator.seatliberator.reservation.domain.validator.VacancyAlertRequestStateValidator.validate;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VacancyAlertRequestState {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VacancyAlertRequestStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VacancyAlertRequestResolution resolution;

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

    private VacancyAlertRequestState(
            VacancyAlertRequestStatus status,
            VacancyAlertRequestResolution resolution,
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

    public static VacancyAlertRequestState requestedAt(
            @NonNull Instant requestedAt
    ) {
        return new VacancyAlertRequestState(
                VacancyAlertRequestStatus.ACTIVE,
                VacancyAlertRequestResolution.PENDING,
                requestedAt,
                null,
                null,
                null
        );
    }

    protected void cancel(Instant at) {
        ensureStateIn(this, VacancyAlertRequestStatus.ACTIVE);

        this.status = VacancyAlertRequestStatus.CANCELLED;
        this.cancelledAt = at;

        validate(this);
    }

    protected void expire(Instant at) {
        ensureStateIn(this, VacancyAlertRequestStatus.ACTIVE);

        this.status = VacancyAlertRequestStatus.EXPIRED;
        this.expiredAt = at;

        validate(this);
    }

    protected void fail(Instant at) {
        ensureStateIn(this, VacancyAlertRequestStatus.ACTIVE);

        this.status = VacancyAlertRequestStatus.FAILED;
        this.failedAt = at;

        validate(this);
    }

    protected void completeAsNotified(Instant at) {
        ensureStateIn(this, VacancyAlertRequestStatus.ACTIVE);

        this.status = VacancyAlertRequestStatus.COMPLETED;
        this.resolution = VacancyAlertRequestResolution.NOTIFIED;
        this.completedAt = at;

        validate(this);
    }

    protected void completeAtClaimed(Instant at) {
        ensureStateIn(this, VacancyAlertRequestStatus.ACTIVE);

        this.status = VacancyAlertRequestStatus.COMPLETED;
        this.resolution = VacancyAlertRequestResolution.CLAIMED;
        this.completedAt = at;

        validate(this);
    }
}