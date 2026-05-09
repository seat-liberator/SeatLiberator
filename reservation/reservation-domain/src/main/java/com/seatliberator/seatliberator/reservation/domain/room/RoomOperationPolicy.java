package com.seatliberator.seatliberator.reservation.domain.room;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;
import com.seatliberator.seatliberator.reservation.domain.shared.DailyTimeSegments;
import com.seatliberator.seatliberator.reservation.domain.shared.EmbeddableDailyTimeSegment;
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
            name = "room_operation_time_segment",
            joinColumns = @JoinColumn(name = "room_operation_policy_id")
    )
    private List<EmbeddableDailyTimeSegment> operationTimeSegments = new ArrayList<>();

    private RoomOperationPolicy(
            Integer maxReservationPerUser,
            Duration maxReservationDuration,
            RoomOperationStatus operationStatus,
            List<EmbeddableDailyTimeSegment> operationTimeSegments
    ) {
        this.maxReservationDuration = Preconditions.requirePositive(maxReservationDuration, "maxReservationDuration");
        this.maxReservationPerUser = Preconditions.requirePositive(maxReservationPerUser, "maxReservationPerUser");
        this.operationStatus = Preconditions.requireNonNull(operationStatus, "operationStatus");
        this.operationTimeSegments = Preconditions.requireNonNull(operationTimeSegments, "operationTimeSegments");
    }

    public static RoomOperationPolicy of(
            Integer maxReservationPerUser,
            Duration maxReservationDuration,
            RoomOperationStatus operationStatus,
            DailyTimeSegments operationTimeSegments
    ) {
        Preconditions.requireNonNull(operationTimeSegments, "operationTimeSegments");
        var segments = operationTimeSegments.segments().stream()
                .map(EmbeddableDailyTimeSegment::from)
                .toList();
        return new RoomOperationPolicy(
                maxReservationPerUser,
                maxReservationDuration,
                operationStatus,
                segments
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

    public void updateOperationTimeSegments(DailyTimeSegments operationTimeSegments) {
        Preconditions.requireNonNull(operationTimeSegments, "operationTimeSegments");
        operationTimeSegments.validate(operationTimeSegments.segments());

        this.operationTimeSegments.clear();
        this.operationTimeSegments.addAll(
                operationTimeSegments.segments().stream()
                        .map(EmbeddableDailyTimeSegment::from)
                        .toList()
        );
    }
}
