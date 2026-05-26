package com.seatliberator.seatliberator.reservation.domain.shared;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static com.seatliberator.seatliberator.kernel.test.assertion.DomainAssertions.assertThatDomainThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public interface SeatLocatorContractTest<T extends SeatLocator> {

    T create(String roomCode, String seatCode);

    String getRoomCode();

    String getSeatCode();

    @Test
    @DisplayName("문자열 Room Code 및 Seat Code를 넘겨서 Locator를 만들 수 있다")
    default void create_locator_when_arguments_are_valid() {
        var roomCode = getRoomCode();
        var seatCode = getSeatCode();
        var locator = create(roomCode, seatCode);

        assertThat(locator.roomCode()).isEqualTo(roomCode);
        assertThat(locator.seatCode()).isEqualTo(seatCode);
    }

    @Test
    @DisplayName("roomCode가 비어 있으면 예외를 던진다")
    default void throw_exception_when_room_id_is_blank() {
        assertThatDomainThrownBy(() -> new SimpleSeatLocator(" ", getSeatCode()))
                .hasNonBlankMessageFor("roomCode");
    }

    @Test
    @DisplayName("seatCode가 비어 있으면 예외를 던진다")
    default void throw_exception_when_seat_id_is_blank() {
        assertThatDomainThrownBy(() -> new SimpleSeatLocator(getRoomCode(), " "))
                .hasNonBlankMessageFor("seatCode");
    }

    @Test
    @DisplayName("from 팩토리로 생성할 수 있다")
    default void create_locator_with_from_factory() {
        var roomCode = getRoomCode();
        var seatCode = getSeatCode();
        var locator = SimpleSeatLocator.of(roomCode, seatCode);

        assertThat(locator.roomCode()).isEqualTo(roomCode);
        assertThat(locator.seatCode()).isEqualTo(seatCode);
    }

    @Test
    @DisplayName("of 팩토리로 다른 SeatLocator를 복사할 수 있다")
    default void copy_locator_with_of_factory() {
        var roomCode = getRoomCode();
        var seatCode = getSeatCode();
        var source = SimpleSeatLocator.of(roomCode, seatCode);

        var copied = SimpleSeatLocator.from(source);

        assertThat(copied.roomCode()).isEqualTo(roomCode);
        assertThat(copied.seatCode()).isEqualTo(seatCode);
    }

    @Test
    @DisplayName("동일한 값이면 isSame은 true")
    default void is_same_with_same_value() {
        var roomCode = getRoomCode();
        var seatCode = getSeatCode();
        var locator = create(roomCode, seatCode);
        var other = create(roomCode, seatCode);

        assertThat(locator.isSame(other)).isTrue();
    }

    @Test
    @DisplayName("다른 값이면 isSame은 false다")
    default void is_not_same_with_diff_value() {
        var roomCode = getRoomCode();
        var seatCode = getSeatCode();
        var locator = create(roomCode, seatCode);
        var other = create("other-" + roomCode, "other-" + seatCode);

        assertThat(locator.isSame(other)).isFalse();
    }

    @Test
    @DisplayName("정상적인 roomCode와 seatCode로 SeatLocatorKey 생성 가능")
    default void create_key_when_args_are_valid() {
        var roomCode = getRoomCode();
        var seatCode = getSeatCode();
        var key = new SeatLocatorKey(roomCode, seatCode);

        assertThat(key.roomCode()).isEqualTo(roomCode);
        assertThat(key.seatCode()).isEqualTo(seatCode);
    }

    @Test
    @DisplayName("SeatLocatorKey의 roomCode가 비어있으면 예외")
    default void throw_exception_when_room_id_of_key_is_blank() {
        var seatCode = getSeatCode();

        assertThatThrownBy(() -> new SeatLocatorKey(" ", seatCode))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("roomCode must not be blank.");
    }

    @Test
    @DisplayName("SeatLocatorKey의 seatCode가 비어있으면 예외")
    default void throw_exception_when_seat_id_of_key_is_blank() {
        var roomCode = getRoomCode();

        assertThatThrownBy(() -> new SeatLocatorKey(roomCode, " "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("seatCode must not be blank.");
    }

    @Test
    @DisplayName("SeatLocator로부터 생성 가능")
    default void create_from_seat_locator() {
        var roomCode = getRoomCode();
        var seatCode = getSeatCode();
        var locator = create(roomCode, seatCode);

        var key = SeatLocatorKey.from(locator);

        assertThat(key.roomCode()).isEqualTo(roomCode);
        assertThat(key.seatCode()).isEqualTo(seatCode);
    }

    @Test
    @DisplayName("SeatLocatorKey가 같은 roomCode, seatCode면 동등하다")
    default void equal_when_same_value() {
        var roomCode = getRoomCode();
        var seatCode = getSeatCode();
        var key1 = new SeatLocatorKey(roomCode, seatCode);
        var key2 = new SeatLocatorKey(roomCode, seatCode);

        assertThat(key1).isEqualTo(key2);
        assertThat(key1.hashCode()).isEqualTo(key2.hashCode());
    }

    @Test
    @DisplayName("SeatLocatorKey의 roomCode 또는 seatCode가 다르면 동등하지 않다")
    default void not_equal_when_diff_value() {
        var roomCode = getRoomCode();
        var seatCode = getSeatCode();
        var key1 = new SeatLocatorKey(roomCode, seatCode);
        var key2 = new SeatLocatorKey("other-" + roomCode, "other-" + seatCode);

        assertThat(key1).isNotEqualTo(key2);
    }

    @Test
    @DisplayName("roomCode 가 다르면 roomCode 사전순으로 비교")
    default void compare_by_room_id_first() {
        var smaller = new SeatLocatorKey("room-1", "seat-9");
        var larger = new SeatLocatorKey("room-2", "seat-1");

        assertThat(smaller.compareTo(larger)).isNegative();
        assertThat(larger.compareTo(smaller)).isPositive();
    }

    @Test
    @DisplayName("roomCode가 같으면 seatCode 사전순으로 비교")
    default void compare_by_seat_id_when_room_id_is_same() {
        var smaller = new SeatLocatorKey("room-1", "seat-1");
        var larger = new SeatLocatorKey("room-1", "seat-2");

        assertThat(smaller.compareTo(larger)).isNegative();
        assertThat(larger.compareTo(smaller)).isPositive();
    }

    @Test
    @DisplayName("같은 값이면 compareTo 결과는 0이다")
    default void compare_zero_when_same_value() {
        var key1 = new SeatLocatorKey("room-1", "seat-1");
        var key2 = new SeatLocatorKey("room-1", "seat-1");

        assertThat(key1.compareTo(key2)).isZero();
    }

    @Test
    @DisplayName("SeatLocator의 key()는 동일한 SeatLocatorKey를 반환한다")
    default void return_key_from_locator_default_method() {
        SeatLocator locator = SimpleSeatLocator.of("room-1", "seat-1");

        var key = locator.key();

        assertThat(key).isEqualTo(new SeatLocatorKey("room-1", "seat-1"));
    }

    @Test
    @DisplayName("서로 다른 SeatLocator 구현체라도 같은 값이면 같은 key를 만든다")
    default void create_same_key_across_different_implementations() {
        SeatLocator simple = SimpleSeatLocator.of("room-1", "seat-1");
        SeatLocator embeddable = EmbeddableSeatLocator.from("room-1", "seat-1");

        assertThat(simple.key()).isEqualTo(embeddable.key());
    }

    @Test
    @DisplayName("컬렉션에서 같은 key를 찾을 수 있다")
    default void same_locator_can_found_in_collection_when_compared_by_key() {
        var keys = Set.of(
                EmbeddableSeatLocator.from("room-1", "seat-1").key()
        );

        assertThat(keys).contains(
                SimpleSeatLocator.of("room-1", "seat-1").key()
        );
    }

    @Test
    @DisplayName("compareTo가 0이면 equals도 true다")
    default void compare_to_zero_and_equals_are_consistent() {
        var key1 = new SeatLocatorKey("room-1", "seat-1");
        var key2 = new SeatLocatorKey("room-1", "seat-1");

        assertThat(key1.compareTo(key2)).isZero();
        assertThat(key1).isEqualTo(key2);
    }

    @Test
    @DisplayName("정렬 시 roomCode, seatCode 순으로 오름차순 정렬된다")
    default void sort_in_natural_order() {
        var keys = java.util.List.of(
                new SeatLocatorKey("room-2", "seat-1"),
                new SeatLocatorKey("room-1", "seat-2"),
                new SeatLocatorKey("room-1", "seat-1")
        );

        var sorted = keys.stream().sorted().toList();

        assertThat(sorted).containsExactly(
                new SeatLocatorKey("room-1", "seat-1"),
                new SeatLocatorKey("room-1", "seat-2"),
                new SeatLocatorKey("room-2", "seat-1")
        );
    }
}
