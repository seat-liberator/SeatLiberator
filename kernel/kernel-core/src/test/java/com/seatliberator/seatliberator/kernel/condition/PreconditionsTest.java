package com.seatliberator.seatliberator.kernel.condition;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Preconditions")
class PreconditionsTest {

    @Test
    @DisplayName("null이 아닌 값을 요구할 수 있다")
    void require_non_null() {
        var value = new Object();

        assertThat(Preconditions.requireNonNull(value, "value")).isEqualTo(value);

        assertThatThrownBy(() -> Preconditions.requireNonNull(null, "value"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("value must not be null.");
    }

    @Test
    @DisplayName("빈 문자열이 아닌 값을 요구할 수 있다")
    void require_non_blank() {
        assertThat(Preconditions.requireNonBlank("value", "value")).isEqualTo("value");

        assertThatThrownBy(() -> Preconditions.requireNonBlank(null, "value"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("value must not be null.");
        assertThatThrownBy(() -> Preconditions.requireNonBlank(" ", "value"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("value must not be blank.");
    }

    @Test
    @DisplayName("비어있지 않은 Collection을 요구할 수 있다")
    void require_non_empty_collection() {
        var value = List.of("value");

        assertThat(Preconditions.requireNonEmpty(value, "value")).isSameAs(value);

        assertThatThrownBy(() -> Preconditions.requireNonEmpty(null, "value"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("value must not be null.");
        assertThatThrownBy(() -> Preconditions.requireNonEmpty(List.of(), "value"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("value must not be empty.");
    }

    @Test
    @DisplayName("비어있지 않고 null 원소가 없는 Collection을 요구할 수 있다")
    void require_non_empty_collection_with_non_null_elements() {
        var value = List.of("value");

        assertThat(Preconditions.requireNonEmptyElementsNonNull(value, "value")).isSameAs(value);

        assertThatThrownBy(() -> Preconditions.requireNonEmptyElementsNonNull(null, "value"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("value must not be null.");
        assertThatThrownBy(() -> Preconditions.requireNonEmptyElementsNonNull(List.of(), "value"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("value must not be empty.");
        assertThatThrownBy(() -> Preconditions.requireNonEmptyElementsNonNull(Arrays.asList("value", null), "value"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("value must not contain null.");
    }

    @Test
    @DisplayName("음수 Integer를 요구할 수 있다")
    void require_negative_integer() {
        assertThat(Preconditions.requireNegative(-1, "value")).isEqualTo(-1);

        assertThatThrownBy(() -> Preconditions.requireNegative(0, "value"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("value must be negative.");
        assertThatThrownBy(() -> Preconditions.requireNegative(1, "value"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("value must be negative.");
    }

    @Test
    @DisplayName("0 이상의 Integer를 요구할 수 있다")
    void require_non_negative_integer() {
        assertThat(Preconditions.requireNonNegative(0, "value")).isEqualTo(0);
        assertThat(Preconditions.requireNonNegative(1, "value")).isEqualTo(1);

        assertThatThrownBy(() -> Preconditions.requireNonNegative(-1, "value"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("value must be non-negative.");
    }

    @Test
    @DisplayName("양수 Integer를 요구할 수 있다")
    void require_positive_integer() {
        assertThat(Preconditions.requirePositive(1, "value")).isEqualTo(1);

        assertThatThrownBy(() -> Preconditions.requirePositive(0, "value"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("value must be positive.");
        assertThatThrownBy(() -> Preconditions.requirePositive(-1, "value"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("value must be positive.");
    }

    @Test
    @DisplayName("0 이하의 Integer를 요구할 수 있다")
    void require_non_positive_integer() {
        assertThat(Preconditions.requireNonPositive(-1, "value")).isEqualTo(-1);
        assertThat(Preconditions.requireNonPositive(0, "value")).isEqualTo(0);

        assertThatThrownBy(() -> Preconditions.requireNonPositive(1, "value"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("value must be non-positive.");
    }

    @Test
    @DisplayName("Integer 부호 검증 대상이 null이면 예외를 던진다")
    void throw_when_signed_integer_is_null() {
        assertThatThrownBy(() -> Preconditions.requireNegative((Integer) null, "value"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("value must not be null.");
        assertThatThrownBy(() -> Preconditions.requireNonNegative((Integer) null, "value"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("value must not be null.");
        assertThatThrownBy(() -> Preconditions.requirePositive((Integer) null, "value"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("value must not be null.");
        assertThatThrownBy(() -> Preconditions.requireNonPositive((Integer) null, "value"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("value must not be null.");
    }

    @Test
    @DisplayName("최소 Integer를 요구할 수 있다")
    void require_integer_at_least() {
        assertThat(Preconditions.requireAtLeast(1, 1, "value")).isEqualTo(1);
        assertThat(Preconditions.requireAtLeast(2, 1, "value")).isEqualTo(2);

        assertThatThrownBy(() -> Preconditions.requireAtLeast(0, 1, "value"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("value must be at least 1.");
    }

    @Test
    @DisplayName("최대 Integer를 요구할 수 있다")
    void require_integer_at_most() {
        assertThat(Preconditions.requireAtMost(1, 1, "value")).isEqualTo(1);
        assertThat(Preconditions.requireAtMost(0, 1, "value")).isEqualTo(0);

        assertThatThrownBy(() -> Preconditions.requireAtMost(2, 1, "value"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("value must be at most 1.");
    }

    @Test
    @DisplayName("범위 안의 Integer를 요구할 수 있다")
    void require_integer_between() {
        assertThat(Preconditions.requireBetween(1, 1, 3, "value")).isEqualTo(1);
        assertThat(Preconditions.requireBetween(2, 1, 3, "value")).isEqualTo(2);
        assertThat(Preconditions.requireBetween(3, 1, 3, "value")).isEqualTo(3);
        assertThat(Preconditions.requireBetween(1, 1, 1, "value")).isEqualTo(1);

        assertThatThrownBy(() -> Preconditions.requireBetween(0, 1, 3, "value"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("value must be between 1 and 3.");
        assertThatThrownBy(() -> Preconditions.requireBetween(4, 1, 3, "value"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("value must be between 1 and 3.");
        assertThatThrownBy(() -> Preconditions.requireBetween(1, 3, 1, "value"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("minimum must be less than or equal to maximum.");
    }

    @Test
    @DisplayName("Integer 범위 검증 대상이 null이면 예외를 던진다")
    void throw_when_integer_range_value_is_null() {
        assertThatThrownBy(() -> Preconditions.requireAtLeast((Integer) null, 1, "value"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("value must not be null.");
        assertThatThrownBy(() -> Preconditions.requireAtMost((Integer) null, 1, "value"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("value must not be null.");
        assertThatThrownBy(() -> Preconditions.requireBetween((Integer) null, 1, 3, "value"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("value must not be null.");
        assertThatThrownBy(() -> Preconditions.requireAtLeast(1, (Integer) null, "value"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("minimum must not be null.");
        assertThatThrownBy(() -> Preconditions.requireAtMost(1, (Integer) null, "value"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("maximum must not be null.");
        assertThatThrownBy(() -> Preconditions.requireBetween(1, (Integer) null, 3, "value"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("minimum must not be null.");
        assertThatThrownBy(() -> Preconditions.requireBetween(1, 1, (Integer) null, "value"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("maximum must not be null.");
    }

    @Test
    @DisplayName("음수 Long을 요구할 수 있다")
    void require_negative_long() {
        assertThat(Preconditions.requireNegative(-1L, "value")).isEqualTo(-1L);

        assertThatThrownBy(() -> Preconditions.requireNegative(0L, "value"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("value must be negative.");
        assertThatThrownBy(() -> Preconditions.requireNegative(1L, "value"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("value must be negative.");
    }

    @Test
    @DisplayName("0 이상의 Long을 요구할 수 있다")
    void require_non_negative_long() {
        assertThat(Preconditions.requireNonNegative(0L, "value")).isEqualTo(0L);
        assertThat(Preconditions.requireNonNegative(1L, "value")).isEqualTo(1L);

        assertThatThrownBy(() -> Preconditions.requireNonNegative(-1L, "value"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("value must be non-negative.");
    }

    @Test
    @DisplayName("양수 Long을 요구할 수 있다")
    void require_positive_long() {
        assertThat(Preconditions.requirePositive(1L, "value")).isEqualTo(1L);

        assertThatThrownBy(() -> Preconditions.requirePositive(0L, "value"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("value must be positive.");
        assertThatThrownBy(() -> Preconditions.requirePositive(-1L, "value"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("value must be positive.");
    }

    @Test
    @DisplayName("0 이하의 Long을 요구할 수 있다")
    void require_non_positive_long() {
        assertThat(Preconditions.requireNonPositive(-1L, "value")).isEqualTo(-1L);
        assertThat(Preconditions.requireNonPositive(0L, "value")).isEqualTo(0L);

        assertThatThrownBy(() -> Preconditions.requireNonPositive(1L, "value"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("value must be non-positive.");
    }

    @Test
    @DisplayName("Long 부호 검증 대상이 null이면 예외를 던진다")
    void throw_when_signed_long_is_null() {
        assertThatThrownBy(() -> Preconditions.requireNegative((Long) null, "value"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("value must not be null.");
        assertThatThrownBy(() -> Preconditions.requireNonNegative((Long) null, "value"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("value must not be null.");
        assertThatThrownBy(() -> Preconditions.requirePositive((Long) null, "value"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("value must not be null.");
        assertThatThrownBy(() -> Preconditions.requireNonPositive((Long) null, "value"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("value must not be null.");
    }

    @Test
    @DisplayName("최소 Long을 요구할 수 있다")
    void require_long_at_least() {
        assertThat(Preconditions.requireAtLeast(1L, 1L, "value")).isEqualTo(1L);
        assertThat(Preconditions.requireAtLeast(2L, 1L, "value")).isEqualTo(2L);

        assertThatThrownBy(() -> Preconditions.requireAtLeast(0L, 1L, "value"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("value must be at least 1.");
    }

    @Test
    @DisplayName("최대 Long을 요구할 수 있다")
    void require_long_at_most() {
        assertThat(Preconditions.requireAtMost(1L, 1L, "value")).isEqualTo(1L);
        assertThat(Preconditions.requireAtMost(0L, 1L, "value")).isEqualTo(0L);

        assertThatThrownBy(() -> Preconditions.requireAtMost(2L, 1L, "value"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("value must be at most 1.");
    }

    @Test
    @DisplayName("범위 안의 Long을 요구할 수 있다")
    void require_long_between() {
        assertThat(Preconditions.requireBetween(1L, 1L, 3L, "value")).isEqualTo(1L);
        assertThat(Preconditions.requireBetween(2L, 1L, 3L, "value")).isEqualTo(2L);
        assertThat(Preconditions.requireBetween(3L, 1L, 3L, "value")).isEqualTo(3L);
        assertThat(Preconditions.requireBetween(1L, 1L, 1L, "value")).isEqualTo(1L);

        assertThatThrownBy(() -> Preconditions.requireBetween(0L, 1L, 3L, "value"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("value must be between 1 and 3.");
        assertThatThrownBy(() -> Preconditions.requireBetween(4L, 1L, 3L, "value"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("value must be between 1 and 3.");
        assertThatThrownBy(() -> Preconditions.requireBetween(1L, 3L, 1L, "value"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("minimum must be less than or equal to maximum.");
    }

    @Test
    @DisplayName("Long 범위 검증 대상이 null이면 예외를 던진다")
    void throw_when_long_range_value_is_null() {
        assertThatThrownBy(() -> Preconditions.requireAtLeast((Long) null, 1L, "value"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("value must not be null.");
        assertThatThrownBy(() -> Preconditions.requireAtMost((Long) null, 1L, "value"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("value must not be null.");
        assertThatThrownBy(() -> Preconditions.requireBetween((Long) null, 1L, 3L, "value"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("value must not be null.");
        assertThatThrownBy(() -> Preconditions.requireAtLeast(1L, (Long) null, "value"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("minimum must not be null.");
        assertThatThrownBy(() -> Preconditions.requireAtMost(1L, (Long) null, "value"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("maximum must not be null.");
        assertThatThrownBy(() -> Preconditions.requireBetween(1L, (Long) null, 3L, "value"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("minimum must not be null.");
        assertThatThrownBy(() -> Preconditions.requireBetween(1L, 1L, (Long) null, "value"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("maximum must not be null.");
    }

    @Test
    @DisplayName("음수 Duration을 요구할 수 있다")
    void require_negative_duration() {
        var negative = Duration.ofSeconds(-1);

        assertThat(Preconditions.requireNegative(negative, "duration")).isEqualTo(negative);

        assertThatThrownBy(() -> Preconditions.requireNegative(Duration.ZERO, "duration"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("duration must be negative.");
        assertThatThrownBy(() -> Preconditions.requireNegative(Duration.ofSeconds(1), "duration"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("duration must be negative.");
    }

    @Test
    @DisplayName("0 이상의 Duration을 요구할 수 있다")
    void require_non_negative_duration() {
        assertThat(Preconditions.requireNonNegative(Duration.ZERO, "duration")).isEqualTo(Duration.ZERO);
        assertThat(Preconditions.requireNonNegative(Duration.ofSeconds(1), "duration")).isEqualTo(Duration.ofSeconds(1));

        assertThatThrownBy(() -> Preconditions.requireNonNegative(Duration.ofSeconds(-1), "duration"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("duration must be non-negative.");
    }

    @Test
    @DisplayName("양수 Duration을 요구할 수 있다")
    void require_positive_duration() {
        var positive = Duration.ofSeconds(1);

        assertThat(Preconditions.requirePositive(positive, "duration")).isEqualTo(positive);

        assertThatThrownBy(() -> Preconditions.requirePositive(Duration.ZERO, "duration"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("duration must be positive.");
        assertThatThrownBy(() -> Preconditions.requirePositive(Duration.ofSeconds(-1), "duration"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("duration must be positive.");
    }

    @Test
    @DisplayName("0 이하의 Duration을 요구할 수 있다")
    void require_non_positive_duration() {
        assertThat(Preconditions.requireNonPositive(Duration.ofSeconds(-1), "duration")).isEqualTo(Duration.ofSeconds(-1));
        assertThat(Preconditions.requireNonPositive(Duration.ZERO, "duration")).isEqualTo(Duration.ZERO);

        assertThatThrownBy(() -> Preconditions.requireNonPositive(Duration.ofSeconds(1), "duration"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("duration must be non-positive.");
    }

    @Test
    @DisplayName("Duration 부호 검증 대상이 null이면 예외를 던진다")
    void throw_when_signed_duration_is_null() {
        assertThatThrownBy(() -> Preconditions.requireNegative((Duration) null, "duration"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("duration must not be null.");
        assertThatThrownBy(() -> Preconditions.requireNonNegative((Duration) null, "duration"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("duration must not be null.");
        assertThatThrownBy(() -> Preconditions.requirePositive((Duration) null, "duration"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("duration must not be null.");
        assertThatThrownBy(() -> Preconditions.requireNonPositive((Duration) null, "duration"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("duration must not be null.");
    }

    @Test
    @DisplayName("최소 Duration을 요구할 수 있다")
    void require_duration_at_least() {
        assertThat(Preconditions.requireAtLeast(Duration.ofSeconds(1), Duration.ofSeconds(1), "duration"))
                .isEqualTo(Duration.ofSeconds(1));
        assertThat(Preconditions.requireAtLeast(Duration.ofSeconds(2), Duration.ofSeconds(1), "duration"))
                .isEqualTo(Duration.ofSeconds(2));

        assertThatThrownBy(() -> Preconditions.requireAtLeast(Duration.ZERO, Duration.ofSeconds(1), "duration"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("duration must be at least PT1S.");
    }

    @Test
    @DisplayName("최대 Duration을 요구할 수 있다")
    void require_duration_at_most() {
        assertThat(Preconditions.requireAtMost(Duration.ofSeconds(1), Duration.ofSeconds(1), "duration"))
                .isEqualTo(Duration.ofSeconds(1));
        assertThat(Preconditions.requireAtMost(Duration.ZERO, Duration.ofSeconds(1), "duration"))
                .isEqualTo(Duration.ZERO);

        assertThatThrownBy(() -> Preconditions.requireAtMost(Duration.ofSeconds(2), Duration.ofSeconds(1), "duration"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("duration must be at most PT1S.");
    }

    @Test
    @DisplayName("범위 안의 Duration을 요구할 수 있다")
    void require_duration_between() {
        assertThat(Preconditions.requireBetween(Duration.ofSeconds(1), Duration.ofSeconds(1), Duration.ofSeconds(3), "duration"))
                .isEqualTo(Duration.ofSeconds(1));
        assertThat(Preconditions.requireBetween(Duration.ofSeconds(2), Duration.ofSeconds(1), Duration.ofSeconds(3), "duration"))
                .isEqualTo(Duration.ofSeconds(2));
        assertThat(Preconditions.requireBetween(Duration.ofSeconds(3), Duration.ofSeconds(1), Duration.ofSeconds(3), "duration"))
                .isEqualTo(Duration.ofSeconds(3));
        assertThat(Preconditions.requireBetween(Duration.ofSeconds(1), Duration.ofSeconds(1), Duration.ofSeconds(1), "duration"))
                .isEqualTo(Duration.ofSeconds(1));

        assertThatThrownBy(() -> Preconditions.requireBetween(Duration.ZERO, Duration.ofSeconds(1), Duration.ofSeconds(3), "duration"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("duration must be between PT1S and PT3S.");
        assertThatThrownBy(() -> Preconditions.requireBetween(Duration.ofSeconds(4), Duration.ofSeconds(1), Duration.ofSeconds(3), "duration"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("duration must be between PT1S and PT3S.");
        assertThatThrownBy(() -> Preconditions.requireBetween(Duration.ofSeconds(1), Duration.ofSeconds(3), Duration.ofSeconds(1), "duration"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("minimum must be less than or equal to maximum.");
    }

    @Test
    @DisplayName("Duration 범위 검증 대상이 null이면 예외를 던진다")
    void throw_when_duration_range_value_is_null() {
        assertThatThrownBy(() -> Preconditions.requireAtLeast((Duration) null, Duration.ofSeconds(1), "duration"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("duration must not be null.");
        assertThatThrownBy(() -> Preconditions.requireAtMost((Duration) null, Duration.ofSeconds(1), "duration"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("duration must not be null.");
        assertThatThrownBy(() -> Preconditions.requireBetween((Duration) null, Duration.ofSeconds(1), Duration.ofSeconds(3), "duration"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("duration must not be null.");
        assertThatThrownBy(() -> Preconditions.requireAtLeast(Duration.ofSeconds(1), (Duration) null, "duration"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("minimum must not be null.");
        assertThatThrownBy(() -> Preconditions.requireAtMost(Duration.ofSeconds(1), (Duration) null, "duration"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("maximum must not be null.");
        assertThatThrownBy(() -> Preconditions.requireBetween(Duration.ofSeconds(1), (Duration) null, Duration.ofSeconds(3), "duration"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("minimum must not be null.");
        assertThatThrownBy(() -> Preconditions.requireBetween(Duration.ofSeconds(1), Duration.ofSeconds(1), (Duration) null, "duration"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("maximum must not be null.");
    }
}
