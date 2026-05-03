package com.seatliberator.seatliberator.reservation.domain.shared;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.NullUnmarked;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@NullUnmarked
public class EmbeddableSeatLocator implements SeatLocator {
    @Column(name = "room_id", nullable = false)
    private String roomId;

    @Column(name = "seat_id", nullable = false)
    private String seatId;

    public EmbeddableSeatLocator(String roomId, String seatId) {
        if (roomId == null || roomId.isBlank()) {
            throw new IllegalArgumentException("roomId must not be null or blank.");
        }
        if (seatId == null || seatId.isBlank()) {
            throw new IllegalArgumentException("seatId must not be null or blank.");
        }
        this.roomId = roomId;
        this.seatId = seatId;
    }

    public static EmbeddableSeatLocator from(String roomId, String seatId) {
        return new EmbeddableSeatLocator(roomId, seatId);
    }

    public static EmbeddableSeatLocator of(SeatLocator locator) {
        return new EmbeddableSeatLocator(locator.roomId(), locator.seatId());
    }

    @Override
    public String roomId() {
        return roomId;
    }

    @Override
    public String seatId() {
        return seatId;
    }

    public void setLocate(String roomId, String seatId) {
        var locator = SimpleSeatLocator.of(roomId, seatId);
        apply(locator);
    }

    private void apply(SeatLocator locator) {
        this.roomId = locator.roomId();
        this.seatId = locator.seatId();
    }
}
