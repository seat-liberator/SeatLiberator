package com.seatliberator.seatliberator.reservation.domain.reservation;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;
import com.seatliberator.seatliberator.reservation.domain.seat.SeatTimeSlot;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(
        name = "seat_time_slot_occupancy",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_seat_time_slot_occupancy_seat_time_slot_id_occupancy_date",
                        columnNames = {"seat_time_slot_id", "occupancy_date"}
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SeatOccupancy {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "seat_time_slot_id", nullable = false, updatable = false)
    private UUID seatTimeSlotId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "seat_time_slot_id",
            nullable = false,
            insertable = false,
            updatable = false
    )
    private SeatTimeSlot seatTimeSlot;

    @Column(name = "reservation_id", nullable = false, updatable = false)
    private UUID reservationId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "reservation_id",
            nullable = false,
            insertable = false,
            updatable = false
    )
    private Reservation reservation;

    @Column(name = "occupancy_date", nullable = false)
    private LocalDate occupancyDate;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    private SeatOccupancy(UUID seatTimeSlotId, UUID reservationId, LocalDate occupancyDate, Instant createdAt) {
        this.seatTimeSlotId = Preconditions.requireNonNull(seatTimeSlotId, "seatTimeSlotId");
        this.reservationId = Preconditions.requireNonNull(reservationId, "reservationId");
        this.occupancyDate = Preconditions.requireNonNull(occupancyDate, "occupancyDate");
        this.createdAt = Preconditions.requireNonNull(createdAt, "createdAt");
    }

    public static SeatOccupancy of(UUID seatTimeSlotId, UUID reservationId, LocalDate occupancyDate, Instant createdAt) {
        return new SeatOccupancy(seatTimeSlotId, reservationId, occupancyDate, createdAt);
    }
}
