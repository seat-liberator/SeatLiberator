package com.seatliberator.seatliberator.reservation.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public interface SeatLocatorContractTest<T extends SeatLocator> {

    T create(String roomId, String seatId);

    String getRoomId();

    String getSeatId();

    @Test
    @DisplayName("문자열 Room Id 및 Seat Id를 넘겨서 Locator를 만들 수 있다")
    default void create_locator_when_arguments_are_valid() {
        var roomId = getRoomId();
        var seatId = getSeatId();
        var locator = create(roomId, seatId);

        assertThat(locator.roomId()).isEqualTo(roomId);
        assertThat(locator.seatId()).isEqualTo(seatId);
    }

    @Test
    @DisplayName("roomId가 비어 있으면 예외를 던진다")
    default void throw_exception_when_room_id_is_blank() {
        assertThatThrownBy(() -> new SimpleSeatLocator(" ", getSeatId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("roomId must not be null or blank.");
    }

    @Test
    @DisplayName("seatId가 비어 있으면 예외를 던진다")
    default void throw_exception_when_seat_id_is_blank() {
        assertThatThrownBy(() -> new SimpleSeatLocator(getRoomId(), " "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("seatId must not be null or blank.");
    }

    @Test
    @DisplayName("from 팩토리로 생성할 수 있다")
    default void create_locator_with_from_factory() {
        var roomId = getRoomId();
        var seatId = getSeatId();
        var locator = SimpleSeatLocator.from(roomId, seatId);

        assertThat(locator.roomId()).isEqualTo(roomId);
        assertThat(locator.seatId()).isEqualTo(seatId);
    }

    @Test
    @DisplayName("of 팩토리로 다른 SeatLocator를 복사할 수 있다")
    default void copy_locator_with_of_factory() {
        var roomId = getRoomId();
        var seatId = getSeatId();
        var source = SimpleSeatLocator.from(roomId, seatId);

        var copied = SimpleSeatLocator.of(source);

        assertThat(copied.roomId()).isEqualTo(roomId);
        assertThat(copied.seatId()).isEqualTo(seatId);
    }

    @Test
    @DisplayName("동일한 값이면 isSame은 true")
    default void is_same_with_same_value() {
        var roomId = getRoomId();
        var seatId = getSeatId();
        var locator = create(roomId, seatId);
        var other = create(roomId, seatId);

        assertThat(locator.isSame(other)).isTrue();
    }

    @Test
    @DisplayName("다른 값이면 isSame은 false다")
    default void is_not_same_with_diff_value() {
        var roomId = getRoomId();
        var seatId = getSeatId();
        var locator = create(roomId, seatId);
        var other = create("other-" + roomId, "other-" + seatId);

        assertThat(locator.isSame(other)).isFalse();
    }
}
