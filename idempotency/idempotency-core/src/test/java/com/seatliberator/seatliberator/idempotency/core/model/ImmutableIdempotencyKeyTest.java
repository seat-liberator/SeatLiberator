package com.seatliberator.seatliberator.idempotency.core.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ImmutableIdempotencyKey")
class ImmutableIdempotencyKeyTest {

    @Test
    @DisplayName("sourceKey와 operation이 유효하면 생성한다")
    void sourceKey와_operation이_유효하면_생성한다() {
        // given
        String sourceKey = "reservation-request-1";
        String operation = "reservation:create";

        // when
        ImmutableIdempotencyKey key = new ImmutableIdempotencyKey(sourceKey, operation);

        // then
        assertAll(
                () -> assertEquals(sourceKey, key.sourceKey()),
                () -> assertEquals(operation, key.operation())
        );
    }

    @Test
    @DisplayName("sourceKey가 공백이면 예외를 던진다")
    void sourceKey가_공백이면_예외를_던진다() {
        // when & then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new ImmutableIdempotencyKey("   ", "reservation:create")
        );

        assertEquals("sourceKey must not be blank.", exception.getMessage());
    }

    @Test
    @DisplayName("operation이 공백이면 예외를 던진다")
    void operation이_공백이면_예외를_던진다() {
        // when & then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new ImmutableIdempotencyKey("reservation-request-1", "   ")
        );

        assertEquals("operation must not be blank.", exception.getMessage());
    }

    @Test
    @DisplayName("IdempotencyKey로부터 불변 Key를 복사 생성한다")
    void IdempotencyKey로부터_불변_Key를_복사_생성한다() {
        // given
        IdempotencyKey source = new IdempotencyKey() {
            @Override
            public String sourceKey() {
                return "reservation-request-1";
            }

            @Override
            public String operation() {
                return "reservation:create";
            }
        };

        // when
        ImmutableIdempotencyKey copied = ImmutableIdempotencyKey.of(source);

        // then
        assertAll(
                () -> assertEquals("reservation-request-1", copied.sourceKey()),
                () -> assertEquals("reservation:create", copied.operation())
        );
    }
}
