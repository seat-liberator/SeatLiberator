package com.seatliberator.seatliberator.reservation.book.domain;

import com.seatliberator.seatliberator.reservation.shared.domain.EmbeddableSeatLocator;
import com.seatliberator.seatliberator.reservation.shared.domain.SeatLocator;
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

    @Embedded
    private EmbeddableSeatLocator locator;

    private Seat(EmbeddableSeatLocator locator) {
        this.locator = locator;
    }

    public static Seat create(String roomId, String seatId) {
        return new Seat(EmbeddableSeatLocator.from(roomId, seatId));
    }

    public static Seat create(SeatLocator locator) {
        return new Seat(EmbeddableSeatLocator.of(locator));
    }

    public void update(String roomId, String seatId) {
        this.locator.setLocate(roomId, seatId);
    }
}
