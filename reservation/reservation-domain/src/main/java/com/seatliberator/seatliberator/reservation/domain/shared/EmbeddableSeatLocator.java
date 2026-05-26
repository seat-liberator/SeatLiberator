package com.seatliberator.seatliberator.reservation.domain.shared;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.NullUnmarked;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@NullUnmarked
public class EmbeddableSeatLocator implements SeatLocator {
    @Column(name = "room_code", nullable = false)
    private String roomCode;

    @Column(name = "seat_code", nullable = false)
    private String seatCode;

    public EmbeddableSeatLocator(String roomCode, String seatCode) {
        this.roomCode = Preconditions.requireNonBlank(roomCode, "roomCode");
        this.seatCode = Preconditions.requireNonBlank(seatCode, "seatCode");
    }

    public static EmbeddableSeatLocator from(String roomCode, String seatCode) {
        return new EmbeddableSeatLocator(roomCode, seatCode);
    }

    public static EmbeddableSeatLocator of(SeatLocator locator) {
        return new EmbeddableSeatLocator(locator.roomCode(), locator.seatCode());
    }

    @Override
    public String roomCode() {
        return roomCode;
    }

    @Override
    public String seatCode() {
        return seatCode;
    }

    public void setLocate(String roomCode, String seatCode) {
        var locator = SimpleSeatLocator.of(roomCode, seatCode);
        apply(locator);
    }

    private void apply(SeatLocator locator) {
        this.roomCode = locator.roomCode();
        this.seatCode = locator.seatCode();
    }
}
