package com.seatliberator.seatliberator.reservation.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Domain: Simple Seat Locator")
public class SimpleSeatLocatorTest {

    String roomId = "room-1";
    String seatId = "seat-1";

    @Nested
    @DisplayName("Creation")
    class Creation {
        @Test
        @DisplayName("정상적인 좌석 위치로 생성할 수 있다")
        void create_locator_when_arguments_are_valid() {
            var locator = new SimpleSeatLocator(roomId, seatId);

            assertThat(locator.roomId()).isEqualTo(roomId);
            assertThat(locator.seatId()).isEqualTo(seatId);
        }

        @Test
        @DisplayName("roomId가 비어 있으면 예외를 던진다")
        void throw_exception_when_room_id_is_blank() {
            assertThatThrownBy(() -> new SimpleSeatLocator(" ", seatId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("roomId must not be null or blank.");
        }

        @Test
        @DisplayName("seatId가 비어 있으면 예외를 던진다")
        void throw_exception_when_seat_id_is_blank() {
            assertThatThrownBy(() -> new SimpleSeatLocator(roomId, " "))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("seatId must not be null or blank.");
        }

        @Test
        @DisplayName("from 팩토리로 생성할 수 있다")
        void create_locator_with_from_factory() {
            var locator = SimpleSeatLocator.from(roomId, seatId);

            assertThat(locator.roomId()).isEqualTo(roomId);
            assertThat(locator.seatId()).isEqualTo(seatId);
        }

        @Test
        @DisplayName("of 팩토리로 다른 SeatLocator를 복사할 수 있다")
        void copy_locator_with_of_factory() {
            var source = SimpleSeatLocator.from(roomId, seatId);

            var copied = SimpleSeatLocator.of(source);

            assertThat(copied.roomId()).isEqualTo(roomId);
            assertThat(copied.seatId()).isEqualTo(seatId);
        }
    }

    @Nested
    @DisplayName("Equality")
    class Equality {
        @Test
        @DisplayName("같은 값, 같은 타입끼리는 인터페이스의 동등성 검사 통과")
        void is_equal_with_same_type_and_value() {
            var locator = SimpleSeatLocator.from(roomId, seatId);
            var other = SimpleSeatLocator.from(roomId, seatId);

            assertThat(locator).isEqualTo(other);
            assertThat(locator.isSame(other)).isTrue();
        }

        @Test
        @DisplayName("다른 값, 같은 타입끼리는 인터페이스의 동등성 검사 실패")
        void is_not_equal_with_same_type_and_diff_value() {
            var locator = SimpleSeatLocator.from(roomId, seatId);
            var other = SimpleSeatLocator.from("other-" + roomId, "other-" + seatId);

            assertThat(locator).isNotEqualTo(other);
            assertThat(locator.isSame(other)).isFalse();
        }
    }
}
