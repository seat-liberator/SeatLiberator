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

    private Reservation(String userId, String roomId, String seatId, Instant startTime, Instant endTime) {
        this.userId = userId;
        this.roomId = roomId;
        this.seatId = seatId;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    private static void validateTime(Instant startTime, Instant endTime) {
        if (!startTime.isBefore(endTime)) {
            throw new IllegalArgumentException("Wrong Argument!");
        }
    }

    public static Reservation create(String userId, String roomId, String seatId, Instant startTime, Instant endTime) {
        validateTime(startTime, endTime);
        return new Reservation(userId, roomId, seatId, startTime, endTime);
    }

    public void update(String userId, String roomId, String seatId, Instant startTime, Instant endTime) {
        validateTime(startTime, endTime);
        this.userId = userId;
        this.roomId = roomId;
        this.seatId = seatId;
        this.startTime = startTime;
        this.endTime = endTime;
    }

}
