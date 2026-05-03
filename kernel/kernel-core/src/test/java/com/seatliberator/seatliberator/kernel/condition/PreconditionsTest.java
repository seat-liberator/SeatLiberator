package com.seatliberator.seatliberator.kernel.condition;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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
        assertThatThrownBy(() -> Preconditions.requireNegative(null, "value"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("value must not be null.");
        assertThatThrownBy(() -> Preconditions.requireNonNegative(null, "value"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("value must not be null.");
        assertThatThrownBy(() -> Preconditions.requirePositive(null, "value"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("value must not be null.");
        assertThatThrownBy(() -> Preconditions.requireNonPositive(null, "value"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("value must not be null.");
    }
}
