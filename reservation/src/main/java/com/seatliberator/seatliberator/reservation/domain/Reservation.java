package com.seatliberator.seatliberator.reservation.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String userId;

    @Column(nullable = false)
    private String roomId;

    @Column(nullable = false)
    private String seatId;

    @Column(nullable = false)
    private Instant startTime;

    @Column(nullable = false)
    private Instant endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status;

    private Reservation(String userId, String roomId, String seatId, Instant startTime, Instant endTime, ReservationStatus status) {
        this.userId = userId;
        this.roomId = roomId;
        this.seatId = seatId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
    }

    private static void validateTime(Instant startTime, Instant endTime) {
        if (!startTime.isBefore(endTime)) {
            throw new IllegalArgumentException("Wrong Argument!");
        }
    }

    public static Reservation create(String userId, String roomId, String seatId, Instant startTime, Instant endTime) {
        validateTime(startTime, endTime);
        return new Reservation(userId, roomId, seatId, startTime, endTime, ReservationStatus.RESERVED);
    }

    public void update(String userId, String roomId, String seatId, Instant startTime, Instant endTime) {
        validateTime(startTime, endTime);
        this.userId = userId;
        this.roomId = roomId;
        this.seatId = seatId;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public void markUsed() {
        if (this.status == ReservationStatus.USED) {
            throw new IllegalStateException("이미 사용된 예약입니다.");
        }

        if (this.status == ReservationStatus.EXPIRED) {
            throw new IllegalStateException("만료된 예약입니다.");
        }

        if (!Instant.now().isBefore(this.endTime)) {
            this.status = ReservationStatus.EXPIRED;
            throw new IllegalStateException("만료된 예약입니다.");
        }

        this.status = ReservationStatus.USED;
    }
}
