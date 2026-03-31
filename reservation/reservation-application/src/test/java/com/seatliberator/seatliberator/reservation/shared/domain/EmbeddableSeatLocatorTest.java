package com.seatliberator.seatliberator.reservation.shared.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Domain: Embeddable Seat Locator")
public class EmbeddableSeatLocatorTest {

    String roomId = "room-1";
    String seatId = "seat-1";

    @Nested
    @DisplayName("Creation")
    class Creation {
        @Test
        @DisplayName("정상적인 좌석 위치로 생성할 수 있다")
        void create_locator_when_arguments_are_valid() {
            var locator = new EmbeddableSeatLocator(roomId, seatId);

            assertThat(locator.roomId()).isEqualTo(roomId);
            assertThat(locator.seatId()).isEqualTo(seatId);
        }

        @Test
        @DisplayName("roomId가 null이면 예외를 던진다")
        void throw_exception_when_room_id_is_null() {
            assertThatThrownBy(() -> new EmbeddableSeatLocator(null, seatId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("roomId must not be null or blank.");
        }

        @Test
        @DisplayName("seatId가 비어 있으면 예외를 던진다")
        void throw_exception_when_seat_id_is_blank() {
            assertThatThrownBy(() -> new EmbeddableSeatLocator(roomId, " "))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("seatId must not be null or blank.");
        }

        @Test
        @DisplayName("of 팩토리로 다른 SeatLocator를 복사할 수 있다")
        void copy_locator_with_of_factory() {
            var source = SimpleSeatLocator.from(roomId, seatId);

            var copied = EmbeddableSeatLocator.of(source);

            assertThat(copied.roomId()).isEqualTo(roomId);
            assertThat(copied.seatId()).isEqualTo(seatId);
        }
    }

    @Nested
    @DisplayName("Update")
    class Update {
        @Test
        @DisplayName("setLocate로 좌석 위치를 변경할 수 있다")
        void update_locator_with_set_locate() {
            var locator = new EmbeddableSeatLocator(roomId, seatId);
            var newRoomId = "room-2";
            var newSeatId = "seat-2";

            locator.setLocate(newRoomId, newSeatId);

            assertThat(locator.roomId()).isEqualTo(newRoomId);
            assertThat(locator.seatId()).isEqualTo(newSeatId);
        }
    }
}
