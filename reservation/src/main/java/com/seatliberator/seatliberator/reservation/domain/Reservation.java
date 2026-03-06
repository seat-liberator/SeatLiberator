package com.seatliberator.seatliberator.reservation.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.Instant;

@Entity
@Getter
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
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

    public static Reservation create(String userId, String roomId, String seatId, Instant startTime, Instant endTime) {
        if(!startTime.isBefore(endTime)){
            return new Reservation(userId, roomId, seatId, startTime, endTime);
        } else {
            throw new IllegalArgumentException("Wrong Argument");
        }
    }

    public void update(String userId, String roomId, String seatId, Instant startTime, Instant endTime) {
        this.userId = userId;
        this.roomId = roomId;
        this.seatId = seatId;
        this.startTime = startTime;
        this.endTime = endTime;
    }

}
