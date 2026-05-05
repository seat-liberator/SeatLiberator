package com.seatliberator.seatliberator.reservation.domain.room;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;
import com.seatliberator.seatliberator.reservation.domain.shared.DailyTimeWindow;
import com.seatliberator.seatliberator.reservation.domain.shared.EmbeddableDailyTimeWindow;
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
    private EmbeddableDailyTimeWindow operationHours;

    private RoomOperationPolicy(
            Integer maxReservationPerUser,
            Duration maxReservationDuration,
            RoomOperationStatus operationStatus,
            EmbeddableDailyTimeWindow operationHours
    ) {
        this.maxReservationDuration = Preconditions.requirePositive(maxReservationDuration, "maxReservationDuration");
        this.maxReservationPerUser = Preconditions.requirePositive(maxReservationPerUser, "maxReservationPerUser");
        this.operationStatus = Preconditions.requireNonNull(operationStatus, "operationStatus");
        this.operationHours = Preconditions.requireNonNull(operationHours, "operationHours");
    }

    public static RoomOperationPolicy of(
            Integer maxReservationPerUser,
            Duration maxReservationDuration,
            RoomOperationStatus operationStatus,
            DailyTimeWindow operationHours
    ) {
        Preconditions.requireNonNull(operationHours, "operationHours");
        return new RoomOperationPolicy(
                maxReservationPerUser,
                maxReservationDuration,
                operationStatus,
                EmbeddableDailyTimeWindow.from(operationHours)
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

    public void updateOperationHours(DailyTimeWindow operationHours) {
        Preconditions.requireNonNull(operationHours, "operationHours");
        this.operationHours.apply(operationHours);
    }
}
