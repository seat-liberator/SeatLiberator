package com.seatliberator.seatliberator.reservation.domain.room;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;
import com.seatliberator.seatliberator.reservation.domain.EmbeddableTimeRange;
import com.seatliberator.seatliberator.reservation.domain.TimeRange;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Duration;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RoomOperationPolicy {

    @Column(name = "max_reservation_per_user", nullable = false)
    private Integer maxReservationPerUser;

    @Column(name = "max_reservation_duration", nullable = false)
    private Duration maxReservationDuration;

    @Enumerated(EnumType.STRING)
    @Column(name = "operation_status", nullable = false)
    private RoomOperationStatus operationStatus;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "startAt", column = @Column(name = "operation_start_at", nullable = false)),
            @AttributeOverride(name = "endAt", column = @Column(name = "operation_end_at", nullable = false))
    })
    private EmbeddableTimeRange operationRange;

    private RoomOperationPolicy(
            Integer maxReservationPerUser,
            Duration maxReservationDuration,
            RoomOperationStatus operationStatus,
            EmbeddableTimeRange operationRange
    ) {
        this.maxReservationDuration = Preconditions.requirePositive(maxReservationDuration, "maxReservationDuration");
        this.maxReservationPerUser = Preconditions.requirePositive(maxReservationPerUser, "maxReservationPerUser");
        this.operationStatus = Preconditions.requireNonNull(operationStatus, "operationStatus");
        this.operationRange = Preconditions.requireNonNull(operationRange, "operationRange");
    }

    public static RoomOperationPolicy of(Integer maxReservationPerUser, Duration maxReservationDuration, RoomOperationStatus operationStatus, TimeRange operationRange) {
        Preconditions.requireNonNull(operationRange, "operationRange");
        return new RoomOperationPolicy(
                maxReservationPerUser,
                maxReservationDuration,
                operationStatus,
                EmbeddableTimeRange.of(operationRange)
        );
    }

    public void updateMaxReservationDuration(Duration maxReservationDuration) {
        this.maxReservationDuration = Preconditions.requirePositive(maxReservationDuration, "maxReservationDuration");
    }

    public void updateMaxReservationPerUser(Integer maxReservationPerUser) {
        this.maxReservationPerUser = Preconditions.requirePositive(maxReservationPerUser, "maxReservationPerUser");
    }

    public void updateOperationStatus(RoomOperationStatus operationStatus) {
        this.operationStatus = Preconditions.requireNonNull(operationStatus, "operationStatus");
    }

    public void updateOperationRange(TimeRange operationRange) {
        this.operationRange = EmbeddableTimeRange.of(
                Preconditions.requireNonNull(operationRange, "operationRange")
        );
    }
}
