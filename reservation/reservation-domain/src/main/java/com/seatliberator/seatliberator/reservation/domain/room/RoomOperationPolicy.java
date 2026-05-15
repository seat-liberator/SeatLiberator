package com.seatliberator.seatliberator.reservation.domain.room;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;
import com.seatliberator.seatliberator.reservation.domain.shared.temporal.DailySchedule;
import com.seatliberator.seatliberator.reservation.domain.shared.temporal.EmbeddableDailyNanoRange;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

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

    @ElementCollection
    @CollectionTable(
            name = "room_operation_schedule",
            joinColumns = @JoinColumn(name = "room_operation_policy_id")
    )
    private List<EmbeddableDailyNanoRange> operationSchedule = new ArrayList<>();

    private RoomOperationPolicy(
            Integer maxReservationPerUser,
            Duration maxReservationDuration,
            RoomOperationStatus operationStatus,
            List<EmbeddableDailyNanoRange> operationSchedule
    ) {
        this.maxReservationDuration = Preconditions.requirePositive(maxReservationDuration, "maxReservationDuration");
        this.maxReservationPerUser = Preconditions.requirePositive(maxReservationPerUser, "maxReservationPerUser");
        this.operationStatus = Preconditions.requireNonNull(operationStatus, "operationStatus");
        this.operationSchedule = Preconditions.requireNonNull(operationSchedule, "operationSchedule");
    }

    public static RoomOperationPolicy of(
            Integer maxReservationPerUser,
            Duration maxReservationDuration,
            RoomOperationStatus operationStatus,
            DailySchedule operationSchedule
    ) {
        Preconditions.requireNonNull(operationSchedule, "operationSchedule");
        var schedule = operationSchedule.ranges().stream()
                .map(EmbeddableDailyNanoRange::from)
                .toList();
        return new RoomOperationPolicy(
                maxReservationPerUser,
                maxReservationDuration,
                operationStatus,
                schedule
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

    public void updateOperationSchedule(DailySchedule operationSchedule) {
        Preconditions.requireNonNull(operationSchedule, "operationSchedule");

        this.operationSchedule.clear();
        this.operationSchedule.addAll(
                operationSchedule.ranges().stream()
                        .map(EmbeddableDailyNanoRange::from)
                        .toList()
        );
    }
}
