package com.seatliberator.seatliberator.reservation.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;

import java.time.Instant;

@Entity
@Getter
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;
    private String roomId;
    private String seatId;
    private Instant startTime;
    private Instant endTime;

    protected Reservation() {
    }

    private Reservation(String userId, String roomId, String seatId, Instant startTime, Instant endTime) {
        this.userId = userId;
        this.roomId = roomId;
        this.seatId = seatId;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public static Reservation create(String userId, String roomId, String seatId, Instant startTime, Instant endTime) {
        return new Reservation(userId, roomId, seatId, startTime, endTime);
    }

    public void update(String userId, String roomId, String seatId, Instant startTime, Instant endTime) {
        this.userId = userId;
        this.roomId = roomId;
        this.seatId = seatId;
        this.startTime = startTime;
        this.endTime = endTime;
    }

}
