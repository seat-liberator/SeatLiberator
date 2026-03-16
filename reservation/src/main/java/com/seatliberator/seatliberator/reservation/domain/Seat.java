package com.seatliberator.seatliberator.reservation.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"room_id", "seat_id"})
        }
)
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String roomId;

    @Column(nullable = false)
    private String seatId;

    private Seat(String roomId, String seatId){
        this.roomId = roomId;
        this.seatId = seatId;
    }

    public static Seat create(String roomId, String seatId){
        return new Seat(roomId, seatId);
    }

    public void update(String roomId, String seatId){
        this.roomId = roomId;
        this.seatId = seatId;
    }
}
