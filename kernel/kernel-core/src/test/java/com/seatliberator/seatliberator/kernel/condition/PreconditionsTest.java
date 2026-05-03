package com.seatliberator.seatliberator.kernel.condition;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Preconditions")
class PreconditionsTest {

    @Test
    @DisplayName("음수 값을 요구할 수 있다")
    void require_negative() {
        assertThat(Preconditions.requireNegative(-1, "value")).isEqualTo(-1);

        assertThatThrownBy(() -> Preconditions.requireNegative(0, "value"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("value must be negative.");
        assertThatThrownBy(() -> Preconditions.requireNegative(1, "value"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("value must be negative.");
    }

    @Test
    @DisplayName("0 이상의 값을 요구할 수 있다")
    void require_non_negative() {
        assertThat(Preconditions.requireNonNegative(0, "value")).isZero();
        assertThat(Preconditions.requireNonNegative(1, "value")).isEqualTo(1);

        assertThatThrownBy(() -> Preconditions.requireNonNegative(-1, "value"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("value must be non-negative.");
    }

    @Test
    @DisplayName("양수 값을 요구할 수 있다")
    void require_positive() {
        assertThat(Preconditions.requirePositive(1, "value")).isEqualTo(1);

        assertThatThrownBy(() -> Preconditions.requirePositive(0, "value"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("value must be positive.");
        assertThatThrownBy(() -> Preconditions.requirePositive(-1, "value"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("value must be positive.");
    }

    @Test
    @DisplayName("0 이하의 값을 요구할 수 있다")
    void require_non_positive() {
        assertThat(Preconditions.requireNonPositive(-1, "value")).isEqualTo(-1);
        assertThat(Preconditions.requireNonPositive(0, "value")).isZero();

        assertThatThrownBy(() -> Preconditions.requireNonPositive(1, "value"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("value must be non-positive.");
    }

    @Test
    @DisplayName("부호 검증 대상이 null이면 예외를 던진다")
    void throw_when_signed_value_is_null() {
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
}
