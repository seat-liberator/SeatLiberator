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
public class SeatTimeSlotOccupancy {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "seat_time_slot_id", nullable = false)
    private SeatTimeSlot seatTimeSlot;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @Column(name = "occupancy_date", nullable = false)
    private LocalDate occupancyDate;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    private SeatTimeSlotOccupancy(SeatTimeSlot seatTimeSlot, Reservation reservation, LocalDate occupancyDate, Instant createdAt) {
        this.seatTimeSlot = Preconditions.requireNonNull(seatTimeSlot, "seatTimeSlot");
        this.reservation = Preconditions.requireNonNull(reservation, "reservation");
        this.occupancyDate = Preconditions.requireNonNull(occupancyDate, "occupancyDate");
        this.createdAt = Preconditions.requireNonNull(createdAt, "createdAt");
        this.reservation.addSeatTimeSlotOccupancy(this);
    }

    public static SeatTimeSlotOccupancy of(SeatTimeSlot seatTimeSlot, Reservation reservation, LocalDate occupancyDate, Instant createdAt) {
        return new SeatTimeSlotOccupancy(seatTimeSlot, reservation, occupancyDate, createdAt);
    }
}
